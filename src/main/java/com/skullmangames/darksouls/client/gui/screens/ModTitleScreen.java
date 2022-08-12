package com.skullmangames.darksouls.client.gui.screens;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.gui.ScreenManager;
import com.skullmangames.darksouls.client.gui.widget.TextButton;

import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.LanguageScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldSummary;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.gui.NotificationModUpdateScreen;
import net.minecraftforge.fml.BrandingControl;

public class ModTitleScreen extends Screen
{
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ResourceLocation DS_ACCESSIBILITY_TEXTURE = new ResourceLocation(DarkSouls.MOD_ID,
			"textures/guis/accessibility.png");
	private static final ResourceLocation DS_MINECRAFT_LOGO = new ResourceLocation(DarkSouls.MOD_ID,
			"textures/guis/title/minecraft.png");
	private static final ResourceLocation DS_MINECRAFT_EDITION = new ResourceLocation(DarkSouls.MOD_ID,
			"textures/guis/title/edition.png");
	private net.minecraftforge.client.gui.NotificationModUpdateScreen modUpdateNotification;
	private long fadeInStart;
	private int copyrightWidth;
	private int copyrightX;
	private final boolean fading;
	private Screen realmsNotificationsScreen;
	private Button resetDemoButton;

	public ModTitleScreen(boolean fading)
	{
		super(new TranslationTextComponent("narrator.screen.title"));
		this.fading = fading;
	}

	@Override
	public boolean isPauseScreen()
	{
		return false;
	}

	@Override
	public boolean shouldCloseOnEsc()
	{
		return false;
	}

	private boolean checkDemoWorldPresence()
	{
		try
		{
			SaveFormat.LevelSave levelstoragesource$levelstorageaccess = this.minecraft
					.getLevelSource().createAccess("Demo_World");

			boolean flag;
			try
			{
				flag = levelstoragesource$levelstorageaccess.getSummary() != null;
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

			return flag;
		} catch (IOException ioexception)
		{
			SystemToast.onWorldAccessFailure(this.minecraft, "Demo_World");
			LOGGER.warn("Failed to read demo world data", (Throwable) ioexception);
			return false;
		}
	}

	private void createNormalMenuOptions(int x, int y, int buttonwidth, int buttonheight)
	{
		this.addButton(new TextButton(x, y, buttonwidth, buttonheight,
				new TranslationTextComponent("menu.singleplayer"), (p_96781_) ->
				{
					this.minecraft.setScreen(new ModWorldSelectionScreen(this));
				}));
		y += buttonheight;
		boolean flag = this.minecraft.allowsMultiplayer();
		Button.ITooltip button$ontooltip = flag ? Button.NO_TOOLTIP : new Button.ITooltip()
		{
			private final ITextComponent text = new TranslationTextComponent("title.multiplayer.disabled");

			public void onTooltip(Button p_169458_, MatrixStack p_169459_, int p_169460_, int p_169461_)
			{
				if (!p_169458_.active)
				{
					ModTitleScreen.this.renderTooltip(p_169459_, ModTitleScreen.this.minecraft.font.split(
							this.text, Math.max(ModTitleScreen.this.width / 2 - 43, 170)), p_169460_, p_169461_);
				}

			}
		};
		(this.addButton(new TextButton(x, y, buttonwidth, buttonheight,
				new TranslationTextComponent("menu.multiplayer"), (p_169450_) ->
				{
					Screen screen = (Screen) (this.minecraft.options.skipMultiplayerWarning
							? new MultiplayerScreen(this)
							: new MultiplayerWarningScreen(this));
					this.minecraft.setScreen(screen);
				}, button$ontooltip))).active = flag;
		y += buttonheight;
		(this.addButton(new TextButton(x, y, buttonwidth, buttonheight,
				new TranslationTextComponent("menu.online"), (p_96771_) ->
				{
					this.realmsButtonClicked();
				}, button$ontooltip))).active = flag;
	}

	private void realmsButtonClicked()
	{
		this.minecraft.setScreen(new RealmsMainScreen(this));
	}

	private void confirmDemo(boolean p_96778_)
	{
		if (p_96778_)
		{
			try
			{
				SaveFormat.LevelSave levelstoragesource$levelstorageaccess = this.minecraft
						.getLevelSource().createAccess("Demo_World");

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
				SystemToast.onWorldDeleteFailure(this.minecraft, "Demo_World");
				LOGGER.warn("Failed to delete demo world", (Throwable) ioexception);
			}
		}

		this.minecraft.setScreen(this);
	}

	private void createDemoMenuOptions(int x, int y, int buttonwidth, int buttonheight)
	{
		boolean flag = this.checkDemoWorldPresence();
		this.addButton(new TextButton(x, y, buttonwidth, buttonheight,
				new TranslationTextComponent("menu.playdemo"), (p_169444_) ->
				{
					if (flag)
					{
						this.minecraft.loadLevel("Demo_World");
					} else
					{
						DynamicRegistries.Impl registryaccess = DynamicRegistries.builtin();
						this.minecraft.createLevel("Demo_World", MinecraftServer.DEMO_SETTINGS,
								registryaccess,
								net.minecraft.world.gen.settings.DimensionGeneratorSettings.demoSettings(registryaccess));
					}

				}));
		y += buttonheight;
		this.resetDemoButton = this.addButton(new TextButton(x, y, buttonwidth,
				buttonheight, new TranslationTextComponent("menu.resetdemo"), (p_169441_) ->
				{
					SaveFormat levelstoragesource = this.minecraft.getLevelSource();

					try
					{
						SaveFormat.LevelSave levelstoragesource$levelstorageaccess = levelstoragesource
								.createAccess("Demo_World");

						try
						{
							WorldSummary levelsummary = levelstoragesource$levelstorageaccess.getSummary();
							if (levelsummary != null)
							{
								this.minecraft.setScreen(new ConfirmScreen(this::confirmDemo,
										new TranslationTextComponent("selectWorld.deleteQuestion"),
										new TranslationTextComponent("selectWorld.deleteWarning",
												levelsummary.getLevelName()),
										new TranslationTextComponent("selectWorld.deleteButton"),
										DialogTexts.GUI_CANCEL));
							}
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
						SystemToast.onWorldAccessFailure(this.minecraft, "Demo_World");
						LOGGER.warn("Failed to access demo world", (Throwable) ioexception);
					}

				}));
		this.resetDemoButton.active = flag;
	}

	@Override
	protected void init()
	{
		super.init();

		this.copyrightWidth = this.font.width("Copyright Mojang AB. Do not distribute!");
		this.copyrightX = this.width - this.copyrightWidth - 2;
		int buttonwidth = 90;
		int buttonheight = 15;
		int x = this.width / 2 - buttonwidth / 2;
		int y = this.height - this.height / 2;
		Button modButton = null;
		
		if (this.minecraft.isDemo())
		{
			this.createDemoMenuOptions(x, y, buttonwidth, buttonheight);
			y += buttonheight * 2;
		} else
		{
			this.createNormalMenuOptions(x, y, buttonwidth, buttonheight);
			y += buttonheight * 3;
			modButton = this.addButton(new TextButton(x, y, buttonwidth, buttonheight,
					new TranslationTextComponent("fml.menu.mods"), button ->
					{
						this.minecraft.setScreen(new net.minecraftforge.fml.client.gui.screen.ModListScreen(this));
					}));
			y += buttonheight;
		}
		modUpdateNotification = this.initNotificationModUpdateScreen(modButton);

		int y1 = this.height - this.height / 4;
		this.addButton(new ImageButton(this.width / 2 - 70, y1, 20, 20, 0, 106, 20,
				Button.WIDGETS_LOCATION, 256, 256, (p_96791_) ->
				{
					this.minecraft.setScreen(new LanguageScreen(this, this.minecraft.options,
							this.minecraft.getLanguageManager()));
				}, new TranslationTextComponent("narrator.button.language")));

		this.addButton(new TextButton(x, y, buttonwidth, buttonheight,
				new TranslationTextComponent("menu.options"), (p_96788_) ->
				{
					this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
				}));
		y += buttonheight;

		this.addButton(new TextButton(x, y, buttonwidth, buttonheight,
				new TranslationTextComponent("menu.quit"), (p_96786_) ->
				{
					this.minecraft.stop();
				}));
		y += buttonheight;

		this.addButton(new ImageButton(this.width / 2 + 50, y1, 20, 20, 0, 0, 20,
				DS_ACCESSIBILITY_TEXTURE, 32, 64, (p_96784_) ->
				{
					this.minecraft.setScreen(new AccessibilityScreen(this, this.minecraft.options));
				}, new TranslationTextComponent("narrator.button.accessibility")));
		this.minecraft.setConnectedToRealms(false);
		if (this.minecraft.options.realmsNotifications && this.realmsNotificationsScreen == null)
		{
			this.realmsNotificationsScreen = new RealmsNotificationsScreen();
		}

		if (this.realmsNotificationsEnabled())
		{
			this.realmsNotificationsScreen.init(this.minecraft, this.width, this.height);
		}
	}

	private NotificationModUpdateScreen initNotificationModUpdateScreen(Button modButton)
	{
		NotificationModUpdateScreen notificationModUpdateScreen = new NotificationModUpdateScreen(modButton);
		notificationModUpdateScreen.resize(this.minecraft, this.width, this.height);
		notificationModUpdateScreen.init();
		return notificationModUpdateScreen;
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
			minecraft.getTextureManager().bind(DS_MINECRAFT_LOGO);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, f1);
			this.blitOutlineBlack(j, 30, (p_238657_2_, p_238657_3_) ->
			{
				this.blit(matStack, p_238657_2_ + 0, p_238657_3_, 0, 0, 155, 44);
				this.blit(matStack, p_238657_2_ + 156, p_238657_3_ - 1, 0, 45, 155, 44);
			});

			minecraft.getTextureManager().bind(DS_MINECRAFT_EDITION);
			blit(matStack, j + 88, 80, 0.0F, 0.0F, 98, 14, 128, 16);
			net.minecraftforge.client.ForgeHooksClient.renderMainMenu(null, matStack, this.font,
					this.width, this.height, l);

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

			BrandingControl.forEachLine(true, true, (brdline, brd) -> drawString(matStack, this.font, brd, 2,
					this.height - (10 + brdline * (this.font.lineHeight + 1)), 11184810 | l));

			BrandingControl.forEachAboveCopyrightLine(
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

			for (Widget widget : this.buttons) widget.setAlpha(f1);
			
			super.render(matStack, p_230430_2_, p_230430_3_, p_230430_4_);
			
			if (this.realmsNotificationsEnabled() && f1 >= 1.0F)
			{
				this.realmsNotificationsScreen.render(matStack, p_230430_2_, p_230430_3_, p_230430_4_);
			}
			modUpdateNotification.render(matStack, p_230430_2_, p_230430_3_, p_230430_4_);
		}
	}

	private boolean realmsNotificationsEnabled()
	{
		return this.minecraft.options.realmsNotifications && this.realmsNotificationsScreen != null;
	}
}
