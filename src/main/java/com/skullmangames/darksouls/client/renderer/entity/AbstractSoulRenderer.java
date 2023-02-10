package com.skullmangames.darksouls.client.renderer.entity;

import com.skullmangames.darksouls.common.entity.AbstractSoulEntity;
import com.skullmangames.darksouls.core.util.math.MathUtils;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractSoulRenderer extends EntityRenderer<AbstractSoulEntity>
{
	public AbstractSoulRenderer(EntityRendererManager context)
	{
		super(context);
		this.shadowRadius = 0.15F;
		this.shadowStrength = 0.75F;
	}

	@Override
	protected int getBlockLightLevel(AbstractSoulEntity entity, BlockPos blockPos)
	{
		return MathUtils.clamp(super.getBlockLightLevel(entity, blockPos) + 7, 0, 15);
	}
}
