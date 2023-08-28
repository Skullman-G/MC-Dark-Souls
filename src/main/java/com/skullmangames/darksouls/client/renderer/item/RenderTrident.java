package com.skullmangames.darksouls.client.renderer.item;

import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;

public class RenderTrident extends RenderItemBase
{
	private final ModMatrix4f correctionMatrixReverse = new ModMatrix4f();
	
	public RenderTrident()
	{
		this.transform = new ModMatrix4f();
		this.transform.rotate((float)Math.toRadians(-80), Vector3f.XP);
		this.transform.translate(0.0F, 0.5F, 0.0F);

		this.correctionMatrixReverse.rotate((float)Math.toRadians(-80), Vector3f.XP);
		this.correctionMatrixReverse.translate(0.0F, 0.1F, 0.0F);
	}
	
	@Override
	public ModMatrix4f getTransform(ItemStack stack, LivingCap<?> itemHolder, InteractionHand hand)
	{
		if(itemHolder.getOriginalEntity().getUseItemRemainingTicks() > 0)
		{
			return new ModMatrix4f(this.correctionMatrixReverse);
		}
		else return new ModMatrix4f(this.transform);
	}
}