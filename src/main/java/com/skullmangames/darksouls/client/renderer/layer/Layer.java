package com.skullmangames.darksouls.client.renderer.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.common.entities.LivingData;
import com.skullmangames.darksouls.util.math.vector.PublicMatrix4f;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Layer<E extends LivingEntity, T extends LivingData<E>>
{
	public abstract void renderLayer(T entitydata, E entityliving, MatrixStack matrixStackIn, IRenderTypeBuffer buffer,
			int packedLightIn, PublicMatrix4f[] poses, float partialTicks);
}