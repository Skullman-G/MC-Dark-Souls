package com.skullmangames.darksouls.client.gui.screens;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.gui.ScreenManager;
import com.skullmangames.darksouls.client.gui.widget.ScalableButton;

import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.LanguageScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridgeScreen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldSummary;

public class ModMainMenuScreen extends MainMenuScreen
{
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ResourceLocation DS_ACCESSIBILITY_TEXTURE = new ResourceLocation(DarkSouls.MOD_ID,
			"textures/guis/accessibility.png");
	private static final ResourceLocation DS_MINECRAFT_LOGO = new ResourceLocation(DarkSouls.MOD_ID,
			"textures/guis/title/minecraft.png");
	private static final ResourceLocation DS_MINECRAFT_EDITION = new ResourceLocation(DarkSouls.MOD_ID,
			"textures/guis/title/edition.png");
	private net.minecraftforge.client.gui.NotificationModUpdateScreen modUpdateNotification;

	public ModMainMenuScreen(boolean fading)
	{
		super(fading);
	}

	private void confirmDemo(boolean p_213087_1_)
	{
		if (p_213087_1_)
		{
			try (SaveFormat.LevelSave saveformat$levelsave = this.minecraft.getLevelSource().createAccess("Demo_World"))
			{
				saveformat$levelsave.deleteLevel();
			} catch (IOException ioexception)
			{
				SystemToast.onWorldDeleteFailure(this.minecraft, "Demo_World");
				LOGGER.warn("Failed to delete demo world", (Throwable) ioexception);
			}
		}

		this.minecraft.setScreen(this);
	}

	@Override
	public void createNormalMenuOptions(int p_73969_1_, int p_73969_2_)
	{
		this.addButton(new ScalableButton(this.width / 2 - 50, p_73969_1_, 100, 13,
				new TranslationTextComponent("menu.singleplayer"), (p_213089_1_) ->
				{
					this.minecraft.setScreen(new ModWorldSelectionScreen(this));
				}));
		boolean flag = this.minecraft.allowsMultiplayer();
		Button.ITooltip button$itooltip = flag ? Button.NO_TOOLTIP
				: (p_238659_1_, p_238659_2_, p_238659_3_, p_238659_4_) ->
				{
					if (!p_238659_1_.active)
					{
						this.renderTooltip(p_238659_2_,
								this.minecraft.font.split(new TranslationTextComponent("title.multiplayer.disabled"),
										Math.max(this.width / 2 - 43, 170)),
								p_238659_3_, p_238659_4_);
					}

				};
		(this.addButton(new ScalableButton(this.width / 2 - 50, p_73969_1_ + p_73969_2_ * 1, 100, 13,
				new TranslationTextComponent("menu.multiplayer"), (p_213095_1_) ->
				{
					Screen screen = (Screen) (this.minecraft.options.skipMultiplayerWarning
							? new MultiplayerScreen(this)
							: new MultiplayerWarningScreen(this));
					this.minecraft.setScreen(screen);
				}, button$itooltip))).active = flag;
		(this.addButton(new ScalableButton(this.width / 2 - 50, p_73969_1_ + p_73969_2_ * 2, 100, 13,
				new TranslationTextComponent("menu.online"), (p_238661_1_) ->
				{
					this.realmsButtonClicked();
				}, button$itooltip))).active = flag;
	}

	@Override
	public void createDemoMenuOptions(int p_73972_1_, int p_73972_2_)
	{
		boolean flag = this.checkDemoWorldPresence();
		this.addButton(new ScalableButton(this.width / 2 - 50, p_73972_1_, 100, 13,
				new TranslationTextComponent("menu.playdemo"), (p_213091_2_) ->
				{
					if (flag)
					{
						this.minecraft.loadLevel("Demo_World");
					} else
					{
						DynamicRegistries.Impl dynamicregistries$impl = DynamicRegistries.builtin();
						this.minecraft.createLevel("Demo_World", MinecraftServer.DEMO_SETTINGS, dynamicregistries$impl,
								DimensionGeneratorSettings.demoSettings(dynamicregistries$impl));
					}

				}));
		this.resetDemoButton = this.addButton(new ScalableButton(this.width / 2 - 50, p_73972_1_ + p_73972_2_ * 1, 100,
				13, new TranslationTextComponent("menu.resetdemo"), (p_238658_1_) ->
				{
					SaveFormat saveformat = this.minecraft.getLevelSource();

					try (SaveFormat.LevelSave saveformat$levelsave = saveformat.createAccess("Demo_World"))
					{
						WorldSummary worldsummary = saveformat$levelsave.getSummary();
						if (worldsummary != null)
						{
							this.minecraft.setScreen(new ConfirmScreen(this::confirmDemo,
									new TranslationTextComponent("selectWorld.deleteQuestion"),
									new TranslationTextComponent("selectWorld.deleteWarning",
											worldsummary.getLevelName()),
									new TranslationTextComponent("selectWorld.deleteButton"), DialogTexts.GUI_CANCEL));
						}
					} catch (IOException ioexception)
					{
						SystemToast.onWorldAccessFailure(this.minecraft, "Demo_World");
						LOGGER.warn("Failed to access demo world", (Throwable) ioexception);
					}

				}));
		this.resetDemoButton.active = flag;
	}

	@Override
	protected void init()
	{
		this.copyrightWidth = this.font.width("Copyright Mojang AB. Do not distribute!");
		this.copyrightX = this.width - this.copyrightWidth - 2;
		int j = this.height / 2;
		Button modButton = null;
		if (this.minecraft.isDemo())
			this.createDemoMenuOptions(j, 17);
		else
		{
			this.createNormalMenuOptions(j, 17);
			modButton = this.addButton(new ScalableButton(this.width / 2 - 50, j + 17 * this.buttons.size(), 100, 13,
					new TranslationTextComponent("fml.menu.mods"), button ->
					{
						this.minecraft.setScreen(new net.minecraftforge.fml.client.gui.screen.ModListScreen(this));
					}));
		}
		this.modUpdateNotification = net.minecraftforge.client.gui.NotificationModUpdateScreen.init(this, modButton);

		this.addButton(new ScalableButton(this.width / 2 - 50, j + 17 * this.buttons.size(), 100, 13,
				new TranslationTextComponent("menu.options"), (p_213096_1_) ->
				{
					this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
				}));
		this.addButton(new ScalableButton(this.width / 2 - 50, j + 17 * this.buttons.size(), 100, 13,
				new TranslationTextComponent("menu.quit"), (p_213094_1_) ->
				{
					this.minecraft.stop();
				}));
		this.addButton(new ImageButton(this.width / 2 - 124, j + 72 + 12, 20, 20, 0, 106, 20,
				ScreenManager.DS_WIDGETS_PATH, 256, 256, (p_213090_1_) ->
				{
					this.minecraft.setScreen(
							new LanguageScreen(this, this.minecraft.options, this.minecraft.getLanguageManager()));
				}, new TranslationTextComponent("narrator.button.language")));
		this.addButton(new ImageButton(this.width / 2 + 104, j + 72 + 12, 20, 20, 0, 0, 20, DS_ACCESSIBILITY_TEXTURE,
				32, 64, (p_213088_1_) ->
				{
					this.minecraft.setScreen(new AccessibilityScreen(this, this.minecraft.options));
				}, new TranslationTextComponent("narrator.button.accessibility")));
		this.minecraft.setConnectedToRealms(false);
		if (this.minecraft.options.realmsNotifications && !this.realmsNotificationsInitialized)
		{
			RealmsBridgeScreen realmsbridgescreen = new RealmsBridgeScreen();
			this.realmsNotificationsScreen = realmsbridgescreen.getNotificationScreen(this);
			this.realmsNotificationsInitialized = true;
		}

		if (this.realmsNotificationsEnabled())
		{
			this.realmsNotificationsScreen.init(this.minecraft, this.width, this.height);
		}
	}

	@Override
	public void render(MatrixStack matStack, int p_230430_2_, int p_230430_3_, float p_230430_4_)
	{
		if (this.fadeInStart == 0L && this.fading)
			this.fadeInStart = Util.getMillis();

		float f = this.fading ? (float) (Util.getMillis() - this.fadeInStart) / 1000.0F : 1.0F;
		int j = this.width / 2 - 137;
		RenderSystem.enableBlend();

		ScreenManager.renderDarkBackground(this);

		float f1 = this.fading ? MathHelper.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
		int l = MathHelper.ceil(f1 * 255.0F) << 24;
		if ((l & -67108864) != 0)
		{
			this.minecraft.getTextureManager().bind(DS_MINECRAFT_LOGO);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, f1);
			this.blitOutlineBlack(j, 30, (p_238657_2_, p_238657_3_) ->
			{
				this.blit(matStack, p_238657_2_ + 0, p_238657_3_, 0, 0, 155, 44);
				this.blit(matStack, p_238657_2_ + 156, p_238657_3_ - 1, 0, 45, 155, 44);
			});

			this.minecraft.getTextureManager().bind(DS_MINECRAFT_EDITION);
			blit(matStack, j + 88, 80, 0.0F, 0.0F, 98, 14, 128, 16);
			net.minecraftforge.client.ForgeHooksClient.renderMainMenu(this, matStack, this.font, this.width,
					this.height, l);

			String s = "Minecraft " + SharedConstants.getCurrentVersion().getName();
			if (this.minecraft.isDemo())
				s = s + " Demo";
			else
			{
				s = s + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? ""
						: "/" + this.minecraft.getVersionType());
			}

			if (this.minecraft.isProbablyModded())
			{
				s = s + I18n.get("menu.modded");
			}

			net.minecraftforge.fml.BrandingControl.forEachLine(true, true, (brdline, brd) -> drawString(matStack,
					this.font, brd, 2, this.height - (10 + brdline * (this.font.lineHeight + 1)), 11184810 | l));

			net.minecraftforge.fml.BrandingControl.forEachAboveCopyrightLine(
					(brdline, brd) -> drawString(matStack, this.font, brd, this.width - font.width(brd),
							this.height - (10 + (brdline + 1) * (this.font.lineHeight + 1)), 11184810 | l));

			drawString(matStack, this.font, "Copyright Mojang AB. Do not distribute!", this.copyrightX,
					this.height - 10, 11184810 | l);
			if (p_230430_2_ > this.copyrightX && p_230430_2_ < this.copyrightX + this.copyrightWidth
					&& p_230430_3_ > this.height - 10 && p_230430_3_ < this.height)
			{
				fill(matStack, this.copyrightX, this.height - 1, this.copyrightX + this.copyrightWidth, this.height,
						11184810 | l);
			}

			for (Widget widget : this.buttons)
				widget.setAlpha(f1);

			for (int i = 0; i < this.buttons.size(); ++i)
			{
				this.buttons.get(i).render(matStack, p_230430_2_, p_230430_3_, p_230430_4_);
			}
			if (this.realmsNotificationsEnabled() && f1 >= 1.0F)
			{
				this.realmsNotificationsScreen.render(matStack, p_230430_2_, p_230430_3_, p_230430_4_);
			}
			modUpdateNotification.render(matStack, p_230430_2_, p_230430_3_, p_230430_4_);

		}
	}
}
