package com.skullmangames.darksouls.client.renderer.item;

import net.minecraft.util.math.vector.Vector3f;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class RenderTrident extends RenderItemBase
{
	private PublicMatrix4f correctionMatrixReverse = new PublicMatrix4f();
	
	public RenderTrident()
	{
		correctionMatrix = new PublicMatrix4f();
		correctionMatrix.rotate((float)Math.toRadians(-80), Vector3f.XP);
		correctionMatrix.translate(0.0F, 0.1F, 0.0F);
		
		correctionMatrixReverse.rotate((float)Math.toRadians(-80), Vector3f.XP);
		correctionMatrixReverse.translate(0.0F, 0.1F, 0.0F);
	}
	
	@Override
	public PublicMatrix4f getCorrectionMatrix(ItemStack stack, LivingCap<?> itemHolder, Hand hand)
	{
		if(itemHolder.getOriginalEntity().getUseItemRemainingTicks() > 0)
		{
			return new PublicMatrix4f(this.correctionMatrixReverse);
		}
		else
		{
			PublicMatrix4f mat = new PublicMatrix4f(correctionMatrix);
			mat.translate(0.0F, 0.4F, 0.0F);
			return mat;
		}
	}
}