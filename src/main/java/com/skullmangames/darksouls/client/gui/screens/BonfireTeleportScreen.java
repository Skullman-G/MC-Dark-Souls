package com.skullmangames.darksouls.client.gui.screens;

import java.util.ArrayList;
import java.util.List;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.config.ConfigManager;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSTeleportPlayer;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BonfireTeleportScreen extends Screen
{
	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/bonfire_teleport.png");
	public static final ResourceLocation DS_TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/ds_bonfire_teleport.png");
	
	private int imageWidth = 235;
	private int imageHeight = 164;
	private int buttonWidth = 220;
	private int buttonHeight = 20;
	private int scrollOff;
	private boolean isDragging;
	
	private final List<Pair<String, BlockPos>> teleports;
	private final List<Button> buttons = new ArrayList<>();
	
	public BonfireTeleportScreen(List<Pair<String, BlockPos>> teleports)
	{
		super(new TranslatableComponent("gui.darksouls.teleport.title"));
		this.teleports = teleports;
	}
	
	@Override
	public boolean isPauseScreen()
	{
		return false;
	}
	
	@Override
	protected void init()
	{
		super.init();
		int x = this.width / 2 - (this.buttonWidth / 2) - 4;
		int y = (this.height - this.imageHeight) / 2;
		int k = y + 16 + 1;

		this.buttons.clear();
		for (int i = 0; i < 7; i++)
		{
			this.buttons.add(this.addRenderableWidget(new Button(x, k + (i * this.buttonHeight), this.buttonWidth, this.buttonHeight, new TextComponent(""), (button) ->
			{
				int bi = this.buttons.indexOf(button);
				if (bi < this.teleports.size())
				{
					BlockPos bonfirePos = this.teleports.get(bi + this.scrollOff).getSecond();
					ModNetworkManager.sendToServer(new CTSTeleportPlayer(bonfirePos));
					this.onClose();
				}
			})));
		}
	}
	
	private void renderScroller(PoseStack poseStack, int x, int y)
	{
		int i = this.teleports.size() + 1 - 7;
		if (i > 1)
		{
			int j = 139 - (27 + (i - 1) * 139 / i);
			int k = 1 + j / i + 139 / i;
			int i1 = Math.min(113, this.scrollOff * k);
			if (this.scrollOff == i - 1)
			{
				i1 = 113;
			}

			blit(poseStack, x, y + 17 + i1, this.getBlitOffset(), 0.0F, 199.0F, 6, 27, 256, 256);
		}
		else
		{
			blit(poseStack, x, y + 17, this.getBlitOffset(), 6.0F, 199.0F, 6, 27, 256, 256);
		}

	}
	
	private boolean canScroll(int size)
	{
		return size > 7;
	}
	
	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialticks)
	{
		super.renderBackground(poseStack);
		this.renderBg(poseStack, partialticks, mouseX, mouseY);
	    super.render(poseStack, mouseX, mouseY, partialticks);
	    
	    int tO = this.font.width(this.title);
	    this.font.draw(poseStack, this.title, this.width / 2 - tO / 2, this.height / 2 - this.imageHeight / 2 + 5, 16777215);
	    
	    if (!this.teleports.isEmpty())
		{
			int j = (this.height - this.imageHeight) / 2;
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			if (ConfigManager.CLIENT_CONFIG.darkSoulsUI.getValue()) RenderSystem.setShaderTexture(0, DS_TEXTURE_LOCATION);
			else RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
			this.renderScroller(poseStack, this.width / 2 + this.imageWidth / 2 - 10, j);
			int i1 = 0;
			int i2 = 0;

			for (Pair<String, BlockPos> bonfire : this.teleports)
			{
				if (this.canScroll(this.teleports.size()) && (i1 < this.scrollOff || i1 >= 7 + this.scrollOff))
				{
					++i1;
				}
				else
				{
					int l0 = this.font.width(bonfire.getFirst());
					this.font.draw(poseStack, bonfire.getFirst(), (float)(this.width / 2 - l0 / 2 - 4), (float)(j + 22.5 + i2 * this.buttonHeight), 16777215);
					++i2;
					++i1;
				}
			}
			
			for (int i = 0; i < this.buttons.size(); i++)
			{
				Button button = this.buttons.get(i);
				button.visible = i < this.teleports.size();
			}

			RenderSystem.enableDepthTest();
		}
	}
	
	private void renderBg(PoseStack matrixstack, float partialticks, int mouseX, int mouseY)
	{
		if (ConfigManager.CLIENT_CONFIG.darkSoulsUI.getValue()) RenderSystem.setShaderTexture(0, DS_TEXTURE_LOCATION);
		else RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
		int x = (this.width - this.imageWidth) / 2;
	    int y = (this.height - this.imageHeight) / 2;
	    this.blit(matrixstack, x, y, 0, 0, this.imageWidth, this.imageHeight);
	}
	
	public boolean mouseScrolled(double p_99127_, double p_99128_, double p_99129_)
	{
		int i = this.teleports.size();
		if (this.canScroll(i))
		{
			int j = i - 7;
			this.scrollOff = Mth.clamp((int) ((double) this.scrollOff - p_99129_), 0, j);
		}

		return true;
	}

	public boolean mouseDragged(double p_99135_, double p_99136_, int p_99137_, double p_99138_, double p_99139_)
	{
		int i = this.teleports.size();
		if (this.isDragging)
		{
			int j = (this.height - this.imageHeight) / 2 + 18;
			int k = j + 139;
			int l = i - 7;
			float f = ((float) p_99136_ - (float) j - 13.5F) / ((float) (k - j) - 27.0F);
			f = f * (float) l + 0.5F;
			this.scrollOff = Mth.clamp((int) f, 0, l);
			return true;
		}
		else
		{
			return super.mouseDragged(p_99135_, p_99136_, p_99137_, p_99138_, p_99139_);
		}
	}

	public boolean mouseClicked(double x, double y, int p_99133_)
	{
		this.isDragging = false;
		int k = this.width / 2 + this.imageWidth / 2 - 10;
		int j = (this.height - this.imageHeight) / 2;
		if (this.canScroll(this.teleports.size())
				&& x > (double) (k)
				&& x < (double) (k + 6)
				&& y > (double) (j + 17)
				&& y <= (double) (j + 17 + 139 + 1))
		{
			this.isDragging = true;
		}

		return super.mouseClicked(x, y, p_99133_);
	}
}
