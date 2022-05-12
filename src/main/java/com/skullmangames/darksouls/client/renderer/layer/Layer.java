package com.skullmangames.darksouls.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Layer<E extends LivingEntity, T extends LivingCap<E>>
{
	public abstract void renderLayer(T entityCap, E entityliving, PoseStack matrixStackIn, MultiBufferSource buffer,
			int packedLightIn, PublicMatrix4f[] poses, float partialTicks);
}