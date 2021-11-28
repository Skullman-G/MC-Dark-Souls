package com.skullmangames.darksouls.client.gui.screens;

import java.util.Random;
import java.util.StringTokenizer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.core.init.ModItems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.LoadingGui;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistryEntry;

@OnlyIn(Dist.CLIENT)
public class ModLoadingScreen extends LoadingGui
{
	private Minecraft minecraft;
	private static final ResourceLocation ITEM_DESCRIPTION_WINDOW = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/loading.png");
	private static final ResourceLocation BONFIRE_LOADING = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/bonfire_loading.png");
	private static final int BACKGROUND = ColorHelper.PackedColor.color(0, 0, 0, 0);
	private static final int BACKGROUND_NO_ALPHA = BACKGROUND & 16777215;
	private final ItemStack descriptionItem;
	private final String[] description;
	private int loadingGif = 0;

	private boolean canFadeOut = false;
	private long fadeInStart = -1L;
	private long fadeOutStart = -1L;

	public ModLoadingScreen()
	{
		this.minecraft = Minecraft.getInstance();
		Random random = new Random();
		this.descriptionItem = new ItemStack(ModItems.DESCRIPTION_ITEMS.get(random.nextInt(ModItems.DESCRIPTION_ITEMS.size())));

		String languagePath = "tooltip." + DarkSouls.MOD_ID + "."
				+ ((IForgeRegistryEntry<Item>) this.descriptionItem.getItem()).getRegistryName().getPath() + ".extended";
		this.description = this.addLinebreaks(new TranslationTextComponent(languagePath).getString(), 40);
	}

	public String[] addLinebreaks(String input, int maxCharInLine)
	{
		StringTokenizer tok = new StringTokenizer(input, " ");
		StringBuilder output = new StringBuilder(input.length());
		int lineLen = 0;
		while (tok.hasMoreTokens())
		{
			String word = tok.nextToken();

			while (word.length() > maxCharInLine)
			{
				output.append(word.substring(0, maxCharInLine - lineLen) + "\n");
				word = word.substring(maxCharInLine - lineLen);
				lineLen = 0;
			}

			if (lineLen + word.length() > maxCharInLine)
			{
				output.append("\n");
				lineLen = 0;
			}
			output.append(word + " ");

			lineLen += word.length() + 1;
		}
		return output.toString().split("\n");
	}

	@Override
	public void render(MatrixStack matStack, int p_230430_2_, int p_230430_3_, float p_230430_4_)
	{
		int width = this.minecraft.getWindow().getGuiScaledWidth();
		int height = this.minecraft.getWindow().getGuiScaledHeight();

		long millis = Util.getMillis();
		if (this.fadeInStart == -1L)
			this.fadeInStart = millis;

		float outf = this.fadeOutStart > -1L ? (float) (millis - this.fadeOutStart) / 1000.0F : -1.0F;
		float inf = this.fadeInStart > -1L ? (float) (millis - this.fadeInStart) / 500.0F : -1.0F;

		float alpha = 1.0F;

		// Render Background
		if (outf >= 1.0F)
		{
			if (this.minecraft.screen != null)
			{
				this.minecraft.screen.render(matStack, 0, 0, p_230430_4_);
			}

			int l = MathHelper.ceil((1.0F - MathHelper.clamp(outf - 1.0F, 0.0F, 1.0F)) * 255.0F);
			fill(matStack, 0, 0, width, height, BACKGROUND_NO_ALPHA | l << 24);
			alpha = 1.0F - MathHelper.clamp(outf - 1.0F, 0.0F, 1.0F);
		} else
		{
			if (this.minecraft.screen != null && inf < 1.0F)
			{
				this.minecraft.screen.render(matStack, p_230430_2_, p_230430_3_, p_230430_4_);
			}

			int i2 = MathHelper.ceil(MathHelper.clamp((double) inf, 0.15D, 1.0D) * 255.0D);
			fill(matStack, 0, 0, width, height, BACKGROUND_NO_ALPHA | i2 << 24);
			alpha = MathHelper.clamp(inf, 0.0F, 1.0F);
		}

		RenderSystem.enableBlend();

		int l = MathHelper.ceil(alpha * 255.0F) << 24;
		if ((l & -67108864) != 0)
		{
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
			this.renderBg(matStack, width, height);

			this.minecraft.getTextureManager().bind(BONFIRE_LOADING);
			float loadingscale = 1.5F;
			matStack.scale(loadingscale, loadingscale, 1.0F);
			AbstractGui.blit(matStack, (int) (20 / loadingscale), (int) ((height - 40) / loadingscale), 0 + (int) (this.loadingGif++ * 0.25F) * 16, 0,
					16, 16, 48, 16);
			matStack.scale(1.0F / loadingscale, 1.0F / loadingscale, 1.0F);
			if (this.loadingGif * 0.25F > 3)
				this.loadingGif = 0;

			int x = width / 2;
			int y = height / 2;

			if (alpha > 0.5F) this.renderItem(this.descriptionItem, x - 119, y - 58);

			drawString(matStack, this.minecraft.font, this.descriptionItem.getHoverName(), x - 100, y - 50, 16755200 | l);

			float scale = 0.8F;
			matStack.scale(scale, scale, 1.0F);
			for (int i = 0; i < this.description.length; i++)
			{
				drawString(matStack, this.minecraft.font, description[i], (int) ((x - 100) / scale), (int) ((y - 27 + 13 * i) / scale), 16777215 | l);
			}
			matStack.scale(1.0F / scale, 1.0F / scale, 1.0F);
		}

		if (outf >= 2.0F)
		{
			this.minecraft.setOverlay((LoadingGui) null);
		}
		if (this.fadeOutStart == -1L && this.canFadeOut && inf >= 2.0F)
		{
			this.fadeOutStart = Util.getMillis();

			if (this.minecraft.screen != null)
			{
				this.minecraft.screen.init(this.minecraft, this.minecraft.getWindow().getGuiScaledWidth(),
						this.minecraft.getWindow().getGuiScaledHeight());
			}
		}
	}

	public void setCanFadeOut(boolean value)
	{
		this.canFadeOut = value;
	}

	private void renderBg(MatrixStack matrixstack, int width, int height)
	{
		this.minecraft.getTextureManager().bind(ITEM_DESCRIPTION_WINDOW);
		int x = (width - 350) / 2;
		int y = (height - 200) / 2;
		AbstractGui.blit(matrixstack, x, y, 0, 0, 350, 200, 350, 200);
	}

	private void renderItem(ItemStack itemstack, int posX, int yPos)
	{
		ItemRenderer itemRenderer = this.minecraft.getItemRenderer();

		RenderSystem.translatef(0.0F, 0.0F, 32.0F);
		this.setBlitOffset(200);
		itemRenderer.blitOffset = 200.0F;
		net.minecraft.client.gui.FontRenderer font = itemstack.getItem().getFontRenderer(itemstack);
		if (font == null)
			font = this.minecraft.font;
		itemRenderer.renderAndDecorateItem(itemstack, posX, yPos);
		itemRenderer.renderGuiItemDecorations(font, itemstack, posX, yPos, "");
		setBlitOffset(0);
		itemRenderer.blitOffset = 0.0F;
	}
}
