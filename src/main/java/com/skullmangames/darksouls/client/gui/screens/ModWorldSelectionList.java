package com.skullmangames.darksouls.client.gui.screens;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModWorldSelectionList extends ObjectSelectionList<ModWorldSelectionList.Entry>
{
	private static final Logger LOGGER = LogManager.getLogger();
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
	private static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
	private static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation(
			"textures/gui/world_selection.png");
	private static final Component FROM_NEWER_TOOLTIP_1 = (new TranslatableComponent(
			"selectWorld.tooltip.fromNewerVersion1")).withStyle(ChatFormatting.RED);
	private static final Component FROM_NEWER_TOOLTIP_2 = (new TranslatableComponent(
			"selectWorld.tooltip.fromNewerVersion2")).withStyle(ChatFormatting.RED);
	private static final Component SNAPSHOT_TOOLTIP_1 = (new TranslatableComponent("selectWorld.tooltip.snapshot1"))
			.withStyle(ChatFormatting.GOLD);
	private static final Component SNAPSHOT_TOOLTIP_2 = (new TranslatableComponent("selectWorld.tooltip.snapshot2"))
			.withStyle(ChatFormatting.GOLD);
	private static final Component WORLD_LOCKED_TOOLTIP = (new TranslatableComponent("selectWorld.locked"))
			.withStyle(ChatFormatting.RED);
	private final ModWorldSelectionScreen screen;
	@Nullable
	private List<LevelSummary> cachedList;

	public ModWorldSelectionList(ModWorldSelectionScreen p_i49846_1_, Minecraft p_i49846_2_, int p_i49846_3_,
			int p_i49846_4_, int p_i49846_5_, int p_i49846_6_, int p_i49846_7_, Supplier<String> p_i49846_8_,
			@Nullable ModWorldSelectionList p_i49846_9_)
	{
		super(p_i49846_2_, p_i49846_3_, p_i49846_4_, p_i49846_5_, p_i49846_6_, p_i49846_7_);
		this.screen = p_i49846_1_;
		if (p_i49846_9_ != null)
		{
			this.cachedList = p_i49846_9_.cachedList;
		}
		this.refreshList(p_i49846_8_, false);
	}

	public void refreshList(Supplier<String> p_212330_1_, boolean p_212330_2_)
	{
		this.clearEntries();
		LevelStorageSource levelstoragesource = this.minecraft.getLevelSource();
		if (this.cachedList == null || p_212330_2_)
		{
			try
			{
				this.cachedList = levelstoragesource.getLevelList();
			} catch (LevelStorageException levelstorageexception)
			{
				LOGGER.error("Couldn't load level list", (Throwable) levelstorageexception);
				this.minecraft.setScreen(new ErrorScreen(new TranslatableComponent("selectWorld.unable_to_load"),
						new TextComponent(levelstorageexception.getMessage())));
				return;
			}

			Collections.sort(this.cachedList);
		}

		if (this.cachedList.isEmpty())
		{
			this.minecraft.setScreen(CreateWorldScreen.create((Screen) null));
		} else
		{
			String s = p_212330_1_.get().toLowerCase(Locale.ROOT);

			for (LevelSummary levelsummary : this.cachedList)
			{
				if (levelsummary.getLevelName().toLowerCase(Locale.ROOT).contains(s)
						|| levelsummary.getLevelId().toLowerCase(Locale.ROOT).contains(s))
				{
					this.addEntry(new ModWorldSelectionList.Entry(this, levelsummary));
				}
			}

		}
	}

	@Override
	protected int getScrollbarPosition()
	{
		return super.getScrollbarPosition() + 20;
	}

	@Override
	public int getRowWidth()
	{
		return super.getRowWidth() + 50;
	}

	@Override
	protected boolean isFocused()
	{
		return this.screen.getFocused() == this;
	}

	@Override
	public void setSelected(@Nullable ModWorldSelectionList.Entry p_241215_1_)
	{
		super.setSelected(p_241215_1_);
		if (p_241215_1_ != null)
		{
			LevelSummary worldsummary = p_241215_1_.summary;
			NarratorChatListener.INSTANCE.sayNow((new TranslatableComponent("narrator.select",
					new TranslatableComponent("narrator.select.world", worldsummary.getLevelName(),
							new Date(worldsummary.getLastPlayed()),
							worldsummary.isHardcore() ? new TranslatableComponent("gameMode.hardcore")
									: new TranslatableComponent("gameMode." + worldsummary.getGameMode().getName()),
							worldsummary.hasCheats() ? new TranslatableComponent("selectWorld.cheats")
									: TextComponent.EMPTY,
							worldsummary.getWorldVersionName()))).getString());
		}

		this.screen.updateButtonStatus(p_241215_1_ != null && !p_241215_1_.summary.isLocked());
	}

	@Override
	protected void moveSelection(AbstractSelectionList.SelectionDirection p_101673_)
	{
		this.moveSelection(p_101673_, (p_101681_) ->
		{
			return !p_101681_.summary.isDisabled();
		});
	}

	public Optional<ModWorldSelectionList.Entry> getSelectedOpt()
	{
		return Optional.ofNullable(this.getSelected());
	}

	public ModWorldSelectionScreen getScreen()
	{
		return this.screen;
	}

	@OnlyIn(Dist.CLIENT)
	public final class Entry extends ObjectSelectionList.Entry<ModWorldSelectionList.Entry> implements AutoCloseable
	{
		private final Minecraft minecraft;
		private final ModWorldSelectionScreen screen;
		private final LevelSummary summary;
		private final ResourceLocation iconLocation;
		private File iconFile;
		@Nullable
		private final DynamicTexture icon;
		private long lastClickTime;

		@SuppressWarnings("deprecation")
		public Entry(ModWorldSelectionList list, LevelSummary summary)
		{
			this.screen = list.getScreen();
			this.summary = summary;
			this.minecraft = Minecraft.getInstance();
			String s = summary.getLevelId();
			this.iconLocation = new ResourceLocation("minecraft",
					"worlds/" + Util.sanitizeName(s, ResourceLocation::validPathChar) + "/"
							+ Hashing.sha1().hashUnencodedChars(s) + "/icon");
			this.iconFile = summary.getIcon();
			if (!this.iconFile.isFile())
			{
				this.iconFile = null;
			}

			this.icon = this.loadServerIcon();
		}

		@Override
		public void render(PoseStack matStack, int field, int y, int x, int p_230432_5_, int p_230432_6_,
				int p_230432_7_, int p_230432_8_, boolean isMouseOver, float p_230432_10_)
		{
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShaderTexture(0, AbstractWidget.WIDGETS_LOCATION);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();

			int texHeight = isMouseOver || ModWorldSelectionList.this.getSelected() == this ? 0 : 1;

			this.screen.blit(matStack, x - 2, y - 2, 0, 86 - 20 * texHeight, 175, 18);
			this.screen.blit(matStack, x - 2 + 175, y - 2, 104, 86 - 20 * texHeight, 98, 18);

			this.screen.blit(matStack, x - 2, y + 16, 0, 88 - 20 * texHeight, 175, 18);
			this.screen.blit(matStack, x - 2 + 175, y + 16, 104, 88 - 20 * texHeight, 98, 18);

			String s = this.summary.getLevelName();
			String s1 = this.summary.getLevelId() + " ("
					+ ModWorldSelectionList.DATE_FORMAT.format(new Date(this.summary.getLastPlayed())) + ")";
			if (StringUtils.isEmpty(s))
			{
				s = I18n.get("selectWorld.world") + " " + (field + 1);
			}

			Component itextcomponent = this.summary.getInfo();
			this.minecraft.font.draw(matStack, s, (float) (x + 32 + 3), (float) (y + 1), 16777215);
			this.minecraft.font.draw(matStack, s1, (float) (x + 32 + 3), (float) (y + 9 + 3), 16777215);
			this.minecraft.font.draw(matStack, itextcomponent, (float) (x + 32 + 3), (float) (y + 9 + 9 + 3), 16777215);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShaderTexture(0, this.icon != null ? this.iconLocation : ModWorldSelectionList.ICON_MISSING);
			RenderSystem.enableBlend();
			GuiComponent.blit(matStack, x, y, 0.0F, 0.0F, 32, 32, 32, 32);
			RenderSystem.disableBlend();
			if (this.minecraft.options.touchscreen || isMouseOver)
			{
				RenderSystem.setShaderTexture(0, ModWorldSelectionList.ICON_OVERLAY_LOCATION);
				GuiComponent.fill(matStack, x, y, x + 32, y + 32, -1601138544);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				int i = p_230432_7_ - x;
				boolean flag = i < 32;
				int j = flag ? 32 : 0;
				if (this.summary.isLocked())
				{
					GuiComponent.blit(matStack, x, y, 96.0F, (float) j, 32, 32, 256, 256);
					if (flag)
					{
						this.screen
								.setToolTip(this.minecraft.font.split(ModWorldSelectionList.WORLD_LOCKED_TOOLTIP, 175));
					}
				} else if (this.summary.markVersionInList())
				{
					GuiComponent.blit(matStack, x, y, 32.0F, (float) j, 32, 32, 256, 256);
					if (this.summary.askToOpenWorld())
					{
						GuiComponent.blit(matStack, x, y, 96.0F, (float) j, 32, 32, 256, 256);
						if (flag)
						{
							this.screen.setToolTip(
									ImmutableList.of(ModWorldSelectionList.FROM_NEWER_TOOLTIP_1.getVisualOrderText(),
											ModWorldSelectionList.FROM_NEWER_TOOLTIP_2.getVisualOrderText()));
						}
					} else if (!SharedConstants.getCurrentVersion().isStable())
					{
						GuiComponent.blit(matStack, x, y, 64.0F, (float) j, 32, 32, 256, 256);
						if (flag)
						{
							this.screen.setToolTip(
									ImmutableList.of(ModWorldSelectionList.SNAPSHOT_TOOLTIP_1.getVisualOrderText(),
											ModWorldSelectionList.SNAPSHOT_TOOLTIP_2.getVisualOrderText()));
						}
					}
				} else
				{
					GuiComponent.blit(matStack, x, y, 0.0F, (float) j, 32, 32, 256, 256);
				}
			}
		}

		@Override
		public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_)
		{
			if (this.summary.isLocked())
			{
				return true;
			} else
			{
				ModWorldSelectionList.this.setSelected(this);
				this.screen.updateButtonStatus(ModWorldSelectionList.this.getSelectedOpt().isPresent());
				if (p_231044_1_ - (double) ModWorldSelectionList.this.getRowLeft() <= 32.0D)
				{
					this.joinWorld();
					return true;
				} else if (Util.getMillis() - this.lastClickTime < 250L)
				{
					this.joinWorld();
					return true;
				} else
				{
					this.lastClickTime = Util.getMillis();
					return false;
				}
			}
		}

		public void joinWorld()
		{
			if (!this.summary.isDisabled())
			{
				LevelSummary.BackupStatus levelsummary$backupstatus = this.summary.backupStatus();
				if (levelsummary$backupstatus.shouldBackup())
				{
					String s = "selectWorld.backupQuestion." + levelsummary$backupstatus.getTranslationKey();
					String s1 = "selectWorld.backupWarning." + levelsummary$backupstatus.getTranslationKey();
					MutableComponent mutablecomponent = new TranslatableComponent(s);
					if (levelsummary$backupstatus.isSevere())
					{
						mutablecomponent.withStyle(ChatFormatting.BOLD, ChatFormatting.RED);
					}

					Component component = new TranslatableComponent(s1, this.summary.getWorldVersionName(),
							SharedConstants.getCurrentVersion().getName());
					this.minecraft.setScreen(new BackupConfirmScreen(this.screen, (p_101736_, p_101737_) ->
					{
						if (p_101736_)
						{
							String s2 = this.summary.getLevelId();

							try
							{
								LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = this.minecraft
										.getLevelSource().createAccess(s2);

								try
								{
									EditWorldScreen.makeBackupAndShowToast(levelstoragesource$levelstorageaccess);
								} catch (Throwable throwable1)
								{
									if (levelstoragesource$levelstorageaccess != null)
									{
										try
										{
											levelstoragesource$levelstorageaccess.close();
										} catch (Throwable throwable)
										{
											throwable1.addSuppressed(throwable);
										}
									}

									throw throwable1;
								}

								if (levelstoragesource$levelstorageaccess != null)
								{
									levelstoragesource$levelstorageaccess.close();
								}
							} catch (IOException ioexception)
							{
								SystemToast.onWorldAccessFailure(this.minecraft, s2);
								ModWorldSelectionList.LOGGER.error("Failed to backup level {}", s2, ioexception);
							}
						}

						this.loadWorld();
					}, mutablecomponent, component, false));
				} else if (this.summary.askToOpenWorld())
				{
					this.minecraft.setScreen(new ConfirmScreen((p_101741_) ->
					{
						if (p_101741_)
						{
							try
							{
								this.loadWorld();
							} catch (Exception exception)
							{
								ModWorldSelectionList.LOGGER.error("Failure to open 'future world'",
										(Throwable) exception);
								this.minecraft.setScreen(new AlertScreen(() ->
								{
									this.minecraft.setScreen(this.screen);
								}, new TranslatableComponent("selectWorld.futureworld.error.title"),
										new TranslatableComponent("selectWorld.futureworld.error.text")));
							}
						} else
						{
							this.minecraft.setScreen(this.screen);
						}

					}, new TranslatableComponent("selectWorld.versionQuestion"),
							new TranslatableComponent("selectWorld.versionWarning", this.summary.getWorldVersionName()),
							new TranslatableComponent("selectWorld.versionJoinButton"), CommonComponents.GUI_CANCEL));
				} else
				{
					this.loadWorld();
				}

			}
		}

		public void deleteWorld()
		{
			this.minecraft.setScreen(new ConfirmScreen((p_170322_) ->
			{
				if (p_170322_)
				{
					this.minecraft.setScreen(new ProgressScreen(true));
					this.doDeleteWorld();
				}

				this.minecraft.setScreen(this.screen);
			}, new TranslatableComponent("selectWorld.deleteQuestion"),
					new TranslatableComponent("selectWorld.deleteWarning", this.summary.getLevelName()),
					new TranslatableComponent("selectWorld.deleteButton"), CommonComponents.GUI_CANCEL));
		}

		public void doDeleteWorld()
		{
			LevelStorageSource levelstoragesource = this.minecraft.getLevelSource();
			String s = this.summary.getLevelId();

			try
			{
				LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = levelstoragesource
						.createAccess(s);

				try
				{
					levelstoragesource$levelstorageaccess.deleteLevel();
				} catch (Throwable throwable1)
				{
					if (levelstoragesource$levelstorageaccess != null)
					{
						try
						{
							levelstoragesource$levelstorageaccess.close();
						} catch (Throwable throwable)
						{
							throwable1.addSuppressed(throwable);
						}
					}

					throw throwable1;
				}

				if (levelstoragesource$levelstorageaccess != null)
				{
					levelstoragesource$levelstorageaccess.close();
				}
			} catch (IOException ioexception)
			{
				SystemToast.onWorldDeleteFailure(this.minecraft, s);
				ModWorldSelectionList.LOGGER.error("Failed to delete world {}", s, ioexception);
			}

			ModWorldSelectionList.this.refreshList(() ->
			{
				return this.screen.searchBox.getValue();
			}, true);
		}

		public void editWorld()
		{
			String s = this.summary.getLevelId();

			try
			{
				LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = this.minecraft
						.getLevelSource().createAccess(s);
				this.minecraft.setScreen(new EditWorldScreen((p_101719_) ->
				{
					try
					{
						levelstoragesource$levelstorageaccess.close();
					} catch (IOException ioexception1)
					{
						ModWorldSelectionList.LOGGER.error("Failed to unlock level {}", s, ioexception1);
					}

					if (p_101719_)
					{
						ModWorldSelectionList.this.refreshList(() ->
						{
							return this.screen.searchBox.getValue();
						}, true);
					}

					this.minecraft.setScreen(this.screen);
				}, levelstoragesource$levelstorageaccess));
			} catch (IOException ioexception)
			{
				SystemToast.onWorldAccessFailure(this.minecraft, s);
				ModWorldSelectionList.LOGGER.error("Failed to access level {}", s, ioexception);
				ModWorldSelectionList.this.refreshList(() ->
				{
					return this.screen.searchBox.getValue();
				}, true);
			}

		}

		public void recreateWorld()
		{
			this.queueLoadScreen();
			RegistryAccess.RegistryHolder registryaccess$registryholder = RegistryAccess.builtin();

			try
			{
				LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = this.minecraft
						.getLevelSource().createAccess(this.summary.getLevelId());

				try
				{
					Minecraft.ServerStem minecraft$serverstem = this.minecraft.makeServerStem(
							registryaccess$registryholder, Minecraft::loadDataPacks, Minecraft::loadWorldData, false,
							levelstoragesource$levelstorageaccess);

					try
					{
						LevelSettings levelsettings = minecraft$serverstem.worldData().getLevelSettings();
						DataPackConfig datapackconfig = levelsettings.getDataPackConfig();
						WorldGenSettings worldgensettings = minecraft$serverstem.worldData().worldGenSettings();
						Path path = CreateWorldScreen.createTempDataPackDirFromExistingWorld(
								levelstoragesource$levelstorageaccess.getLevelPath(LevelResource.DATAPACK_DIR),
								this.minecraft);
						if (worldgensettings.isOldCustomizedWorld())
						{
							this.minecraft.setScreen(new ConfirmScreen((p_101715_) ->
							{
								this.minecraft.setScreen((Screen) (p_101715_
										? new CreateWorldScreen(this.screen, levelsettings, worldgensettings, path,
												datapackconfig, registryaccess$registryholder)
										: this.screen));
							}, new TranslatableComponent("selectWorld.recreate.customized.title"),
									new TranslatableComponent("selectWorld.recreate.customized.text"),
									CommonComponents.GUI_PROCEED, CommonComponents.GUI_CANCEL));
						} else
						{
							this.minecraft.setScreen(new CreateWorldScreen(this.screen, levelsettings, worldgensettings,
									path, datapackconfig, registryaccess$registryholder));
						}
					} catch (Throwable throwable2)
					{
						if (minecraft$serverstem != null)
						{
							try
							{
								minecraft$serverstem.close();
							} catch (Throwable throwable1)
							{
								throwable2.addSuppressed(throwable1);
							}
						}

						throw throwable2;
					}

					if (minecraft$serverstem != null)
					{
						minecraft$serverstem.close();
					}
				} catch (Throwable throwable3)
				{
					if (levelstoragesource$levelstorageaccess != null)
					{
						try
						{
							levelstoragesource$levelstorageaccess.close();
						} catch (Throwable throwable)
						{
							throwable3.addSuppressed(throwable);
						}
					}

					throw throwable3;
				}

				if (levelstoragesource$levelstorageaccess != null)
				{
					levelstoragesource$levelstorageaccess.close();
				}
			} catch (Exception exception)
			{
				ModWorldSelectionList.LOGGER.error("Unable to recreate world", (Throwable) exception);
				this.minecraft.setScreen(new AlertScreen(() ->
				{
					this.minecraft.setScreen(this.screen);
				}, new TranslatableComponent("selectWorld.recreate.error.title"),
						new TranslatableComponent("selectWorld.recreate.error.text")));
			}

		}

		private void loadWorld()
		{
			this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			if (this.minecraft.getLevelSource().levelExists(this.summary.getLevelId()))
			{
				this.queueLoadScreen();
				this.minecraft.loadLevel(this.summary.getLevelId());
			}

		}

		private void queueLoadScreen()
		{
			this.minecraft
					.forceSetScreen(new GenericDirtMessageScreen(new TranslatableComponent("selectWorld.data_read")));
		}

		@Nullable
		private DynamicTexture loadServerIcon()
		{
			boolean flag = this.iconFile != null && this.iconFile.isFile();
			if (flag)
			{
				try (InputStream inputstream = new FileInputStream(this.iconFile))
				{
					NativeImage nativeimage = NativeImage.read(inputstream);
					Validate.validState(nativeimage.getWidth() == 64, "Must be 64 pixels wide");
					Validate.validState(nativeimage.getHeight() == 64, "Must be 64 pixels high");
					DynamicTexture dynamictexture = new DynamicTexture(nativeimage);
					this.minecraft.getTextureManager().register(this.iconLocation, dynamictexture);
					return dynamictexture;
				} catch (Throwable throwable)
				{
					ModWorldSelectionList.LOGGER.error("Invalid icon for world {}", this.summary.getLevelId(),
							throwable);
					this.iconFile = null;
					return null;
				}
			} else
			{
				this.minecraft.getTextureManager().release(this.iconLocation);
				return null;
			}
		}

		@Override
		public void close()
		{
			if (this.icon != null)
			{
				this.icon.close();
			}

		}

		@Override
		public Component getNarration()
		{
			TranslatableComponent translatablecomponent = new TranslatableComponent("narrator.select.world",
					this.summary.getLevelName(), new Date(this.summary.getLastPlayed()),
					this.summary.isHardcore() ? new TranslatableComponent("gameMode.hardcore")
							: new TranslatableComponent("gameMode." + this.summary.getGameMode().getName()),
					this.summary.hasCheats() ? new TranslatableComponent("selectWorld.cheats") : TextComponent.EMPTY,
					this.summary.getWorldVersionName());
			Component component;
			if (this.summary.isLocked())
			{
				component = CommonComponents.joinForNarration(translatablecomponent,
						ModWorldSelectionList.WORLD_LOCKED_TOOLTIP);
			} else
			{
				component = translatablecomponent;
			}

			return new TranslatableComponent("narrator.select", component);
		}
	}
}
