package com.skullmangames.darksouls.core.util.data.pack_resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.FolderPackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;

public class DSDefaultPackResources implements PackResources, ResourceProvider
{
	private static final Logger LOGGER = LogUtils.getLogger();
	public static Class<?> clientObject;
	public static final String ROOT_DIR_NAME = "darksouls_data";
	private static final Path ROOT_DIR = Util.make(() ->
	{
		synchronized (DSDefaultPackResources.class)
		{
			String s = "/"+ROOT_DIR_NAME+"/.mcassetsroot";
			URL url = DSDefaultPackResources.class.getResource(s);
			if (url == null)
			{
				LOGGER.error("File {} does not exist in classpath", (Object) s);
			}
			else
			{
				try
				{
					URI uri = url.toURI();
					String s1 = uri.getScheme();
					if (!"jar".equals(s1) && !"file".equals(s1))
					{
						LOGGER.warn("Assets URL '{}' uses unexpected schema", (Object) uri);
					}

					Path path = safeGetPath(uri);
					return path.getParent();
				}
				catch (Exception exception)
				{
					LOGGER.error("Couldn't resolve path to vanilla assets", (Throwable) exception);
				}
			}
			return null;
		}
	});
	public final PackMetadataSection packMetadata;
	public final Set<String> namespaces;

	private static Path safeGetPath(URI p_182298_) throws IOException
	{
		try
		{
			return Paths.get(p_182298_);
		}
		catch (FileSystemNotFoundException filesystemnotfoundexception) {}
		catch (Throwable throwable)
		{
			LOGGER.warn("Unable to get path for: {}", p_182298_, throwable);
		}

		try
		{
			FileSystems.newFileSystem(p_182298_, Collections.emptyMap());
		}
		catch (FileSystemAlreadyExistsException filesystemalreadyexistsexception) {}

		return Paths.get(p_182298_);
	}

	public DSDefaultPackResources(PackMetadataSection p_143761_, String... p_143762_)
	{
		this.packMetadata = p_143761_;
		this.namespaces = ImmutableSet.copyOf(p_143762_);
	}

	public InputStream getRootResource(String p_10358_) throws IOException
	{
		if (!p_10358_.contains("/") && !p_10358_.contains("\\"))
		{
			return this.getResourceAsStream(p_10358_);
		}
		else
		{
			throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
		}
	}

	public InputStream getResource(@Nullable PackType p_10330_, ResourceLocation p_10331_) throws IOException
	{
		InputStream inputstream = this.getResourceAsStream(p_10331_);
		if (inputstream != null)
		{
			return inputstream;
		}
		else
		{
			throw new FileNotFoundException(p_10331_.getPath());
		}
	}

	public Collection<ResourceLocation> getResources(@Nullable PackType p_10324_, String p_10325_, String p_10326_, int p_10327_, Predicate<String> p_10328_)
	{
		Set<ResourceLocation> set = Sets.newHashSet();

		try
		{
			Path path = ROOT_DIR;
			if (path != null)
			{
				getResources(set, p_10327_, p_10325_, path, p_10326_, p_10328_);
			}
			else
			{
				LOGGER.error("Path to Dark Souls data not found");
			}
		}
		catch (NoSuchFileException | FileNotFoundException filenotfoundexception) {}
		catch (IOException ioexception)
		{
			LOGGER.error("Couldn't get a list of all vanilla resources", (Throwable) ioexception);
		}

		return set;
	}

	private static void getResources(Collection<ResourceLocation> p_10343_, int p_10344_, String p_10345_,
			Path p_10346_, String p_10347_, Predicate<String> p_10348_) throws IOException
	{
		Path path = p_10346_.resolve(p_10345_);
		Stream<Path> stream = Files.walk(path.resolve(p_10347_), p_10344_);

		try
		{
			stream.filter((p_10353_) ->
			{
				return !p_10353_.endsWith(".mcmeta") && Files.isRegularFile(p_10353_)
						&& p_10348_.test(p_10353_.getFileName().toString());
			}).map((p_10341_) ->
			{
				return new ResourceLocation(p_10345_, path.relativize(p_10341_).toString().replaceAll("\\\\", "/"));
			}).forEach(p_10343_::add);
		} catch (Throwable throwable1)
		{
			if (stream != null)
			{
				try
				{
					stream.close();
				} catch (Throwable throwable)
				{
					throwable1.addSuppressed(throwable);
				}
			}

			throw throwable1;
		}

		if (stream != null)
		{
			stream.close();
		}

	}

	@Nullable
	protected InputStream getResourceAsStream(ResourceLocation p_10360_)
	{
		String s = createPath(p_10360_);

		try
		{
			URL url = DSDefaultPackResources.class.getResource(s);
			return isResourceUrlValid(s, url) ? getExtraInputStream(s) : null;
		} catch (IOException ioexception)
		{
			return DSDefaultPackResources.class.getResourceAsStream(s);
		}
	}

	private static String createPath(ResourceLocation p_10364_)
	{
		return "/"+ROOT_DIR_NAME+"/" + p_10364_.getNamespace() + "/" + p_10364_.getPath();
	}

	private static boolean isResourceUrlValid(String p_10336_, @Nullable URL p_10337_) throws IOException
	{
		return p_10337_ != null && (p_10337_.getProtocol().equals("jar")
				|| FolderPackResources.validatePath(new File(p_10337_.getFile()), p_10336_));
	}

	@Nullable
	protected InputStream getResourceAsStream(String p_10334_)
	{
		return getExtraInputStream("/" + p_10334_);
	}

	public boolean hasResource(@Nullable PackType p_10355_, ResourceLocation p_10356_)
	{
		String s = createPath(p_10356_);

		try
		{
			URL url = DSDefaultPackResources.class.getResource(s);
			return isResourceUrlValid(s, url);
		}
		catch (IOException ioexception)
		{
			return false;
		}
	}

	public Set<String> getNamespaces(@Nullable PackType p_10322_)
	{
		return this.namespaces;
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public <T> T getMetadataSection(MetadataSectionSerializer<T> p_10333_) throws IOException
	{
		try
		{
			InputStream inputstream = this.getRootResource("pack.mcmeta");

			Object object;
			label59:
			{
				try
				{
					if (inputstream != null)
					{
						T t = AbstractPackResources.getMetadataFromStream(p_10333_, inputstream);
						if (t != null)
						{
							object = t;
							break label59;
						}
					}
				}
				catch (Throwable throwable1)
				{
					if (inputstream != null)
					{
						try
						{
							inputstream.close();
						}
						catch (Throwable throwable)
						{
							throwable1.addSuppressed(throwable);
						}
					}

					throw throwable1;
				}

				if (inputstream != null)
				{
					inputstream.close();
				}

				return (T) (p_10333_ == PackMetadataSection.SERIALIZER ? this.packMetadata : null);
			}

			if (inputstream != null)
			{
				inputstream.close();
			}

			return (T) object;
		}
		catch (FileNotFoundException | RuntimeException runtimeexception)
		{
			return (T) (p_10333_ == PackMetadataSection.SERIALIZER ? this.packMetadata : null);
		}
	}

	public String getName()
	{
		return "Default";
	}

	public void close() {}

	// Vanilla used to just grab from the classpath, this breaks dev environments,
	// and Forge runtime
	// as forge ships vanilla assets in an 'extra' jar with no classes.
	// So find that extra jar using the .mcassetsroot marker.
	private InputStream getExtraInputStream(String resource)
	{
		try
		{
			Path rootDir = ROOT_DIR;
			if (rootDir != null) return Files.newInputStream(rootDir.resolve(resource));
			return DSDefaultPackResources.class.getResourceAsStream(resource);
		}
		catch (IOException e)
		{
			return DSDefaultPackResources.class.getResourceAsStream(resource);
		}
	}

	public Resource getResource(final ResourceLocation p_143764_) throws IOException
	{
		return new Resource()
		{
			@Nullable
			InputStream inputStream;

			public void close() throws IOException
			{
				if (this.inputStream != null)
				{
					this.inputStream.close();
				}

			}

			public ResourceLocation getLocation()
			{
				return p_143764_;
			}

			public InputStream getInputStream()
			{
				try
				{
					this.inputStream = DSDefaultPackResources.this.getResource(PackType.CLIENT_RESOURCES, p_143764_);
				}
				catch (IOException ioexception)
				{
					throw new UncheckedIOException("Could not get client resource from vanilla pack", ioexception);
				}

				return this.inputStream;
			}

			public boolean hasMetadata()
			{
				return false;
			}

			@Nullable
			public <T> T getMetadata(MetadataSectionSerializer<T> p_143773_)
			{
				return (T) null;
			}

			public String getSourceName()
			{
				return p_143764_.toString();
			}
		};
	}
}
