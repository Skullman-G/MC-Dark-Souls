package com.skullmangames.darksouls.client.gui;

import java.util.Collection;
import java.util.Iterator;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.ModRenderTypes;
import com.skullmangames.darksouls.common.capability.entity.EntityData;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.common.potion.effect.ModEffect;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HealthBarIndicator extends EntityIndicator
{
	@Override
	public boolean shouldDraw(LivingEntity entityIn)
	{
		Minecraft minecraft = Minecraft.getInstance();
		if (!DarkSouls.CLIENT_INGAME_CONFIG.showHealthIndicator.getValue()) return false;
		else if (entityIn.isInvisible() || entityIn == minecraft.player.getControllingPassenger()) return false;
		else if (entityIn.distanceToSqr(Minecraft.getInstance().getCameraEntity()) >= 400) return false;
		else if (entityIn instanceof PlayerEntity)
		{
			PlayerEntity playerIn = (PlayerEntity) entityIn;
			if (playerIn == minecraft.player) return false;
			else if (playerIn.isCreative() || playerIn.isSpectator()) return false;
		}
		
		if(entityIn.getActiveEffects().isEmpty() && entityIn.getHealth() >= entityIn.getMaxHealth() || entityIn.deathTime >= 19) return false;
		return true;
	}
	
	@Override
	public void drawIndicator(LivingEntity entityIn, MatrixStack matStackIn, IRenderTypeBuffer bufferIn, float partialTicks)
	{
		Matrix4f mvMatrix = super.getMVMatrix(matStackIn, entityIn, 0.0F, entityIn.getBbHeight() + 0.25F, 0.0F, true, false, partialTicks);
		
		if (!entityIn.getActiveEffects().isEmpty())
		{
			Collection<EffectInstance> activeEffects = entityIn.getActiveEffects();
			Iterator<EffectInstance> iter = activeEffects.iterator();
			int acives = activeEffects.size();
			int row = acives > 1 ? 1 : 0;
			int column = ((acives-1) / 2);
			float startX = -0.8F + -0.3F * row;
			float startY = -0.15F + 0.15F * column;
			
			for (int i = 0; i <= column; i++)
			{
				for (int j = 0; j <= row; j++)
				{
					Effect effect = iter.next().getEffect();
					ResourceLocation rl;
					
					if(effect instanceof ModEffect) rl = ((ModEffect)effect).getIcon();
					else rl = new ResourceLocation("textures/mob_effect/" + effect.getRegistryName().getPath() + ".png");
					
					Minecraft.getInstance().getTextureManager().bind(rl);
					float x = startX + 0.3F * j;
					float y = startY + -0.3F * i;
					
					IVertexBuilder vertexBuilder1 = bufferIn.getBuffer(ModRenderTypes.getEntityIndicator(rl));
					
					this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder1, x, y, x + 0.3F, y + 0.3F, 0, 0, 256, 256);
					if(!iter.hasNext()) break;
				}
			}
		}
		
		IVertexBuilder vertexBuilder = bufferIn.getBuffer(ModRenderTypes.getEntityIndicator(BATTLE_ICON));
		float ratio = entityIn.getHealth() / entityIn.getMaxHealth();
		float healthRatio = -0.5F + ratio;
		int textureRatio = (int) (62 * ratio);
		this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder, -0.5F, -0.05F, healthRatio, 0.05F, 1, 15, textureRatio, 20);
		this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder, healthRatio, -0.05F, 0.5F, 0.05F, textureRatio, 10, 62, 15);
		float absorption = entityIn.getAbsorptionAmount();
		
		if(absorption > 0.0D)
		{
			float absorptionRatio = absorption / entityIn.getMaxHealth();
			int absTexRatio = (int) (62 * absorptionRatio);
			this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder, -0.5F, -0.05F, absorptionRatio - 0.5F, 0.05F, 1, 20, absTexRatio, 25);
		}
		
		EntityData<?> entitycap = entityIn.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		
		if(entitycap != null && entitycap instanceof LivingData) renderStunArmor((LivingData<?>)entitycap, mvMatrix, vertexBuilder);
	}
	
	public void renderStunArmor(LivingData<?> entitydataFighter, Matrix4f mvMatrix, IVertexBuilder vertexBuilder)
	{
		if(entitydataFighter.getStunArmor() == 0) return;
		
		float ratio = entitydataFighter.getStunArmor() / entitydataFighter.getMaxStunArmor();
		float barRatio = -0.5F + ratio;
		int textureRatio = (int) (62 * ratio);
		
		this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder, -0.5F, -0.1F, barRatio, -0.05F, 1, 5, textureRatio, 10);
		this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder, barRatio, -0.1F, 0.5F, -0.05F, textureRatio, 0, 63, 5);
	}
}