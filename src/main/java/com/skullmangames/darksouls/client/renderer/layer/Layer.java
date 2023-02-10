package com.skullmangames.darksouls.client.renderer.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Layer<E extends LivingEntity, T extends LivingCap<E>>
{
	public abstract void renderLayer(T entityCap, MatrixStack poseStack, IRenderTypeBuffer buffer, int packedLight, PublicMatrix4f[] poses, float partialTicks);
}