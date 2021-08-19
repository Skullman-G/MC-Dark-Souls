package com.skullmangames.darksouls.client.gui;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.skullmangames.darksouls.common.capability.entity.ClientPlayerData;
import com.skullmangames.darksouls.common.capability.item.CapabilityItem;
import com.skullmangames.darksouls.common.skill.Skill;
import com.skullmangames.darksouls.common.skill.SkillContainer;
import com.skullmangames.darksouls.common.skill.SkillSlot;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BattleModeGui extends ModIngameGui
{
	private final Map<SkillSlot, Vector3f> screenPositionMap;
	private int guiSlider;
	private boolean guiSliderToggle;
	protected FontRenderer font;
	
	@SuppressWarnings("resource")
	public BattleModeGui() {
		guiSlider = 29;
		guiSliderToggle = false;
		screenPositionMap = new HashMap<SkillSlot, Vector3f> ();
		screenPositionMap.put(SkillSlot.DODGE, new Vector3f(74F, 36F, 0.078F));
		screenPositionMap.put(SkillSlot.WEAPON_HEAVY_ATTACK, new Vector3f(42F, 48F, 0.117F));
		font = Minecraft.getInstance().font;
	}
	
	private static final Vector2f[] vectorz = {
		new Vector2f(0.5F, 0.5F),
		new Vector2f(0.5F, 0.0F),
		new Vector2f(0.0F, 0.0F),
		new Vector2f(0.0F, 1.0F),
		new Vector2f(1.0F, 1.0F),
		new Vector2f(1.0F, 0.0F)
	};
	
	@SuppressWarnings("deprecation")
	public void renderGui(ClientPlayerData playerdata, float partialTicks)
	{
		if (playerdata.getOriginalEntity().getVehicle() != null)
		{
			return;
		}

		if (guiSlider > 28)
		{
			return;
		}
		else if (guiSlider > 0)
		{
			if (this.guiSliderToggle)
			{
				guiSlider -= 2;
			}
			else
			{
				guiSlider += 2;
			}
		}
		
		MainWindow sr = Minecraft.getInstance().getWindow();
		int width = sr.getGuiScaledWidth();
		int height = sr.getGuiScaledHeight();
		
		boolean depthTestEnabled = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
		boolean alphaTestEnabled = GL11.glGetBoolean(GL11.GL_ALPHA_TEST);
		boolean blendEnabled = GL11.glGetBoolean(GL11.GL_BLEND);
		
		if(!depthTestEnabled)
			GlStateManager._enableDepthTest();
		if(!alphaTestEnabled)
			GL11.glEnable(GL11.GL_ALPHA_TEST);
		if(!blendEnabled)
			GlStateManager._enableBlend();
		
		MatrixStack matStack = new MatrixStack();
		
		Minecraft.getInstance().getTextureManager().bind(EntityIndicator.BATTLE_ICON);
		
		float maxStunArmor = playerdata.getMaxStunArmor();
		float stunArmor = playerdata.getStunArmor();
		
		if(maxStunArmor > 0.0F && stunArmor < maxStunArmor)
		{
			float ratio = stunArmor / maxStunArmor;
			matStack.pushPose();
			matStack.translate(0, (float)guiSlider * 0.5F, 0);
			matStack.scale(0.5F, 0.5F, 1.0F);
			GlStateManager._color4f(1.0F, ratio, 0.25F, 1.0F);
			AbstractGui.blit(matStack, (int)((width-120) * 2.0F), (int)((height-10) * 2.0F), 2.0F, 38.0F, 237, 9, 255, 255);
			AbstractGui.blit(matStack, (int)((width-120) * 2.0F), (int)((height-10) * 2.0F), 2.0F, 47.0F, (int)(237*ratio), 9, 255, 255);
			matStack.popPose();
		}
		
		for (int i = 0; i < SkillSlot.values().length; i++)
		{
			SkillContainer container = playerdata.getSkill(i);
			
			if(container != null && !container.isEmpty() && this.screenPositionMap.containsKey(container.getContaining().getSlot())) {
				SkillSlot slot = container.getContaining().getSlot();
				boolean creative = playerdata.getOriginalEntity().isCreative();
				float cooldownRatio = creative ? 1.0F : container.getCooldownRatio(partialTicks);
				float durationRatio = container.getDurationRatio(partialTicks);
				boolean isReady = container.getStack() > 0 || durationRatio > 0 || creative;
				boolean fullstack = container.getStack() >= container.getContaining().getMaxStack();
				int x = (int) this.screenPositionMap.get(slot).x();
				int y = (int) this.screenPositionMap.get(slot).y();
				float scale = this.screenPositionMap.get(slot).z();
				float multiplyScale = 1F / scale;
				
				matStack.pushPose();
				matStack.scale(scale, scale, 1.0F);
				matStack.translate(0, (float)guiSlider * multiplyScale, 0);
				Matrix4f matrix = matStack.last().pose();
				
				if(!isReady) {
					GlStateManager._color4f(0.5F, 0.5F, 0.5F, 0.8F);
				} else {
					GlStateManager._color4f(1F, 1F, 1F, 1F);
				}
				
				if (slot != SkillSlot.WEAPON_HEAVY_ATTACK)
				{
					Minecraft.getInstance().getTextureManager().bind(this.getSkillTexture(container.getContaining()));
					drawTexturedModalRectFixCoord(matrix, (width - x) * multiplyScale, (height - y) * multiplyScale, 0, 0, 255, 255);
					
					if (!(fullstack || creative))
					{
						matStack.scale(multiplyScale, multiplyScale, 1.0F);
						this.font.drawShadow(matStack,
								String.valueOf((int)(1 + container.getCooldownSec() * Math.max(1 / container.getContaining().getRegenTimePerTick(playerdata), 1.0F))),
								((float)width - x+8), ((float)height - y+8), 16777215);
						GL11.glEnable(GL11.GL_ALPHA_TEST);
						GlStateManager._enableBlend();
					}
				}
				else
				{
					CapabilityItem item = playerdata.getHeldItemCapability(Hand.MAIN_HAND);
					boolean isCompatibleWeapon = item != null && item.getSpecialAttack(playerdata) == container.getContaining();
					int vertexNum = 0;
					float iconSize = 32.0F;
					float iconSizeDiv = iconSize*0.5F;
					float top = y;
					float bottom = y - iconSize;
					float left = x;
					float right = x - iconSize;
					float middle = x - iconSizeDiv;
					float lastVertexX = 0;
					float lastVertexY = 0;
					float lastTexX = 0;
					float lastTexY = 0;
					
					if (cooldownRatio < 0.125F) {
						vertexNum = 6;
						lastTexX = cooldownRatio / 0.25F;
						lastTexY = 0.0F;
						lastVertexX = middle - iconSize * lastTexX;
						lastVertexY = top;
						lastTexX+=0.5F;
					} else if (cooldownRatio < 0.375F) {
						vertexNum = 5;
						lastTexX = 1.0F;
						lastTexY = (cooldownRatio-0.125F) / 0.25F;
						lastVertexX = right;
						lastVertexY = top - iconSize * lastTexY;
					} else if (cooldownRatio < 0.625F) {
						vertexNum = 4;
						lastTexX = (cooldownRatio-0.375F) / 0.25F;
						lastTexY = 1.0F;
						lastVertexX = right + iconSize * lastTexX;
						lastVertexY = bottom;
						lastTexX = 1.0F - lastTexX;
					} else if (cooldownRatio < 0.875F) {
						vertexNum = 3;
						lastTexX = 0.0F;
						lastTexY = (cooldownRatio-0.625F) / 0.25F;
						lastVertexX = left;
						lastVertexY = bottom + iconSize * lastTexY;
						lastTexY = 1.0F - lastTexY;
					} else {
						vertexNum = 2;
						lastTexX = (cooldownRatio-0.875F) / 0.25F;
						lastTexY = 0.0F;
						lastVertexX = left - iconSize * lastTexX;
						lastVertexY = top;
					}
					
					Minecraft.getInstance().getTextureManager().bind(this.getSkillTexture(container.getContaining()));
					
					if(isCompatibleWeapon) {
						GlStateManager._color4f(0.0F, 0.5F, 0.5F, 0.6F);
					} else {
						GlStateManager._color4f(0.5F, 0.5F, 0.5F, 0.6F);
					}
					
					Tessellator tessellator = Tessellator.getInstance();
			        BufferBuilder bufferbuilder = tessellator.getBuilder();
			        bufferbuilder.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);
			        
					for (int j = 0; j < vertexNum; j++) {
			        	bufferbuilder.vertex(matrix, (width - (left-iconSize*vectorz[j].x)) * multiplyScale, (height - (top-iconSize*vectorz[j].y)) * multiplyScale, 0.0F)
			        		.uv(vectorz[j].x, vectorz[j].y).endVertex();
					}
			        bufferbuilder.vertex(matrix, (width - lastVertexX) * multiplyScale, (height - lastVertexY) * multiplyScale, 0.0F)
	        			.uv(lastTexX, lastTexY).endVertex();
			        
			        tessellator.end();
			        
			        if(isCompatibleWeapon) {
			        	GlStateManager._color4f(0.08F, 0.89F, 0.95F, 1.0F);
					} else {
						GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
					}
			        
			        GlStateManager._disableCull();
			        
			        bufferbuilder.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);

					for (int j = 0; j < 2; j++) {
			        	bufferbuilder.vertex(matrix, (width - (left-iconSize*vectorz[j].x)) * multiplyScale, (height - (top-iconSize*vectorz[j].y)) * multiplyScale, 0.0F)
			        		.uv(vectorz[j].x, vectorz[j].y).endVertex();
					}
					
					for (int j = vectorz.length - 1; j >= vertexNum; j--) {
			        	bufferbuilder.vertex(matrix, (width - (left-iconSize*vectorz[j].x)) * multiplyScale, (height - (top-iconSize*vectorz[j].y)) * multiplyScale, 0.0F)
			        		.uv(vectorz[j].x, vectorz[j].y).endVertex();
					}
			        
			        bufferbuilder.vertex(matrix, (width - lastVertexX) * multiplyScale, (height - lastVertexY) * multiplyScale, 0.0F)
        			.uv(lastTexX, lastTexY).endVertex();
			        
			        bufferbuilder.end();
			        WorldVertexBufferUploader.end(bufferbuilder);
			        
					if (!isReady) {
			        	matStack.scale(multiplyScale, multiplyScale, 1.0F);
						String s = String.valueOf((int)(cooldownRatio * 100.0F));
						int stringWidth = (this.font.width(s) - 6) / 3;
						this.font.drawShadow(matStack, s, ((float)width - x+13-stringWidth), ((float)height - y+13), 16777215);
						GL11.glEnable(GL11.GL_ALPHA_TEST);
						GlStateManager._enableBlend();
					}
				}
				matStack.popPose();
			}
		}
		
		if(!depthTestEnabled)
			GlStateManager._disableDepthTest();
		if(!alphaTestEnabled)
			GL11.glDisable(GL11.GL_ALPHA_TEST);
		if(!blendEnabled)
			GlStateManager._disableBlend();
	}
	
	private ResourceLocation getSkillTexture(Skill skill) {
		ResourceLocation name = skill.getRegistryName();
		return new ResourceLocation(name.getNamespace(), "textures/gui/skills/" + name.getPath() + ".png");
	}
	
	public void slideUp() {
		this.guiSlider = 28;
		this.guiSliderToggle = true;
	}

	public void slideDown() {
		this.guiSlider = 1;
		this.guiSliderToggle = false;
	}
}