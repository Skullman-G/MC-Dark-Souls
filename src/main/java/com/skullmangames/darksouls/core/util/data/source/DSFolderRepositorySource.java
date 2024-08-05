package com.skullmangames.darksouls.core.util.data.source;

import java.io.File;
import java.io.FileFilter;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.skullmangames.darksouls.core.util.data.pack_resources.DSFilePackResources;
import com.skullmangames.darksouls.core.util.data.pack_resources.DSFolderPackResources;

import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;

public class DSFolderRepositorySource implements RepositorySource
{
	private static final FileFilter RESOURCEPACK_FILTER = (p_10398_) ->
	{
		boolean flag = p_10398_.isFile() && p_10398_.getName().endsWith(".zip");
		boolean flag1 = p_10398_.isDirectory() && (new File(p_10398_, "pack.mcmeta")).isFile();
		return flag || flag1;
	};
	private final File folder;
	private final PackSource packSource;

	public DSFolderRepositorySource(File p_10386_, PackSource p_10387_)
	{
		this.folder = p_10386_;
		this.packSource = p_10387_;
	}

	public void loadPacks(Consumer<Pack> p_10391_, Pack.PackConstructor p_10392_)
	{
		if (!this.folder.isDirectory())
		{
			this.folder.mkdirs();
		}

		File[] afile = this.folder.listFiles(RESOURCEPACK_FILTER);
		if (afile != null)
		{
			for (File file1 : afile)
			{
				String s = "file/" + file1.getName();
				Pack pack = Pack.create(s, false, this.createSupplier(file1), p_10392_, Pack.Position.TOP,
						this.packSource);
				if (pack != null)
				{
					p_10391_.accept(pack);
				}
			}

		}
	}

	private Supplier<PackResources> createSupplier(File p_10389_)
	{
		return p_10389_.isDirectory() ? () ->
		{
			return new DSFolderPackResources(p_10389_);
		} : () ->
		{
			return new DSFilePackResources(p_10389_);
		};
	}
}
