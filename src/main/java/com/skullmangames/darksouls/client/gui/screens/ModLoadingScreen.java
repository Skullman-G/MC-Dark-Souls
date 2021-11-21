package com.skullmangames.darksouls.client.gui.screens;

import java.util.Random;
import java.util.StringTokenizer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.gui.ScreenManager;
import com.skullmangames.darksouls.core.init.ModItems;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistryEntry;

@OnlyIn(Dist.CLIENT)
public class ModLoadingScreen extends Screen
{
	private static final ResourceLocation ITEM_DESCRIPTION_WINDOW = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/loading.png");
	private static final ResourceLocation BONFIRE_LOADING = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/bonfire_loading.png");
	private final ItemStack descriptionItem;
	private final String[] description;
	private int loadingGif = 0;
	private long fadeInStart;

	public ModLoadingScreen()
	{
		super(NarratorChatListener.NO_TITLE);

		Random random = new Random();
		this.descriptionItem = new ItemStack(
				ModItems.DESCRIPTION_ITEMS.get(random.nextInt(ModItems.DESCRIPTION_ITEMS.size())));

		String languagePath = "tooltip." + DarkSouls.MOD_ID + "."
				+ ((IForgeRegistryEntry<Item>) this.descriptionItem.getItem()).getRegistryName().getPath()
				+ ".extended";
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

	public boolean shouldCloseOnEsc()
	{
		return false;
	}

	public void removed()
	{
		NarratorChatListener.INSTANCE.sayNow((new TranslationTextComponent("narrator.loading.done")).getString());
	}

	@SuppressWarnings("deprecation")
	public void render(MatrixStack matStack, int p_230430_2_, int p_230430_3_, float p_230430_4_)
	{
		if (this.fadeInStart == 0L) this.fadeInStart = Util.getMillis();

		float f = (float)(Util.getMillis() - this.fadeInStart) / 1000.0F;
		RenderSystem.enableBlend();
		
		ScreenManager.renderDarkBackground(this);
		
		float f1 = MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);
		int l = MathHelper.ceil(f1 * 255.0F) << 24;
		if ((l & -67108864) != 0)
		{
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, f1);
			this.renderBg(matStack);
			
			this.minecraft.getTextureManager().bind(BONFIRE_LOADING);
			float loadingscale = 1.5F;
			matStack.scale(loadingscale, loadingscale, 1.0F);
			AbstractGui.blit(matStack, (int)(20 / loadingscale), (int)((this.height - 40) / loadingscale), 0 + (int)(this.loadingGif++ * 0.25F) * 16, 0, 16, 16, 48, 16);
			matStack.scale(1.0F / loadingscale, 1.0F / loadingscale, 1.0F);
			if (this.loadingGif * 0.25F > 3) this.loadingGif = 0;

			int x = this.width / 2;
			int y = this.height / 2;

			this.renderFloatingItem(this.descriptionItem, x - 119, y - 58, 1);

			drawString(matStack, this.font, this.descriptionItem.getHoverName(), x - 100, y - 50, 16755200 | l);

			float scale = 0.8F;
			matStack.scale(scale, scale, 1.0F);
			for (int i = 0; i < this.description.length; i++)
			{
				drawString(matStack, this.font, description[i], (int) ((x - 100) / scale), (int) ((y - 27 + 13 * i) / scale), 16777215 | l);
			}
			matStack.scale(1.0F / scale, 1.0F / scale, 1.0F);
		}
	}

	private void renderBg(MatrixStack matrixstack)
	{
		this.minecraft.getTextureManager().bind(ITEM_DESCRIPTION_WINDOW);
		int x = (this.width - 350) / 2;
		int y = (this.height - 200) / 2;
		AbstractGui.blit(matrixstack, x, y, 0, 0, 350, 200, 350, 200);
	}

	@SuppressWarnings("deprecation")
	private void renderFloatingItem(ItemStack itemstack, int posX, int yPos, int scale)
	{
		RenderSystem.translatef(0.0F, 0.0F, 32.0F);
		this.setBlitOffset(200);
		this.itemRenderer.blitOffset = 200.0F;
		net.minecraft.client.gui.FontRenderer font = itemstack.getItem().getFontRenderer(itemstack);
		if (font == null)
			font = this.font;
		this.itemRenderer.renderAndDecorateItem(itemstack, posX, yPos);
		this.itemRenderer.renderGuiItemDecorations(font, itemstack, posX, yPos, "");
		this.setBlitOffset(0);
		this.itemRenderer.blitOffset = 0.0F;
	}
}
