package com.skullmangames.darksouls.client.renderer.item;

import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;

public class RenderTrident extends RenderItemBase
{
	private PublicMatrix4f correctionMatrixReverse = new PublicMatrix4f();
	
	public RenderTrident()
	{
		correctionMatrix = new PublicMatrix4f();
		PublicMatrix4f.rotate((float)Math.toRadians(-80), new Vector3f(1,0,0), correctionMatrix, correctionMatrix);
		PublicMatrix4f.translate(new Vector3f(0.0F,0.1F,0.0F), correctionMatrix, correctionMatrix);
		
		PublicMatrix4f.rotate((float)Math.toRadians(-80), new Vector3f(1,0,0), correctionMatrixReverse, correctionMatrixReverse);
		PublicMatrix4f.translate(new Vector3f(0.0F,0.1F,0.0F), correctionMatrixReverse, correctionMatrixReverse);
	}
	
	@Override
	public PublicMatrix4f getCorrectionMatrix(ItemStack stack, LivingData<?> itemHolder, InteractionHand hand)
	{
		if(itemHolder.getOriginalEntity().getUseItemRemainingTicks() > 0)
		{
			return new PublicMatrix4f(this.correctionMatrixReverse);
		}
		else
		{
			PublicMatrix4f mat = new PublicMatrix4f(correctionMatrix);
			PublicMatrix4f.translate(new Vector3f(0.0F, 0.4F, 0.0F), mat, mat);
			return mat;
		}
	}
}