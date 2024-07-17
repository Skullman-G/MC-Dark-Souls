package com.skullmangames.darksouls.client.renderer.entity;

import com.skullmangames.darksouls.common.entity.AbstractSoulEntity;
import com.skullmangames.darksouls.core.util.math.ModMath;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractSoulRenderer extends EntityRenderer<AbstractSoulEntity>
{
	public AbstractSoulRenderer(EntityRendererProvider.Context context)
	{
		super(context);
		this.shadowRadius = 0.15F;
		this.shadowStrength = 0.75F;
	}

	@Override
	protected int getBlockLightLevel(AbstractSoulEntity entity, BlockPos blockPos)
	{
		return ModMath.clamp(super.getBlockLightLevel(entity, blockPos) + 7, 0, 15);
	}
}
