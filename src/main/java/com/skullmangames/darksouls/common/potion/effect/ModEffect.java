package com.skullmangames.darksouls.common.potion.effect;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModEffect extends Effect {
	protected final ResourceLocation icon;

	public ModEffect(EffectType typeIn, String potionName, int liquidColorIn) {
		super(typeIn, liquidColorIn);
		this.setRegistryName(new ResourceLocation(DarkSouls.MOD_ID, potionName));
		this.icon = new ResourceLocation(DarkSouls.MOD_ID, "textures/mob_effect/" + potionName + ".png");
	}
	
	@SuppressWarnings({ "deprecation", "resource" })
	@OnlyIn(Dist.CLIENT)@Override
	public void renderHUDEffect(EffectInstance effect, AbstractGui gui, MatrixStack mStack, int x, int y, float z, float alpha) {
		GlStateManager._disableTexture();
		GlStateManager._color4f(0.13F, 0.13F, 0.13F, 1.0F);
		AbstractGui.blit(mStack, x+3, y+3, 0, 0, 0, 18, 18, 18, 18);
		GlStateManager._enableTexture();
		GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
    	Minecraft.getInstance().textureManager.bind(this.icon);
    	AbstractGui.blit(mStack, x+3, y+3, 1, 0, 0, 18, 18, 18, 18);
    }
	
	@SuppressWarnings({ "deprecation", "resource" })
	@OnlyIn(Dist.CLIENT)@Override
	public void renderInventoryEffect(EffectInstance effect, DisplayEffectsScreen<?> gui, MatrixStack mStack, int x, int y, float z) {
		GlStateManager._disableTexture();
		GlStateManager._color4f(0.13F, 0.13F, 0.13F, 1.0F);
		AbstractGui.blit(mStack, x+6, y+7, 1, 0, 0, 18, 18, 18, 18);
		GlStateManager._enableTexture();
		GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getInstance().textureManager.bind(this.icon);
    	AbstractGui.blit(mStack, x+6, y+7, 1, 0, 0, 18, 18, 18, 18);
	}
	
	@OnlyIn(Dist.CLIENT)
	public ResourceLocation getIcon() {
		return this.icon;
	}
}