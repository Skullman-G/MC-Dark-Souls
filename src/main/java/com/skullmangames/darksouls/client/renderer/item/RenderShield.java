package com.skullmangames.darksouls.client.renderer.item;

import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderShield extends RenderItemMirror
{
	private PublicMatrix4f rightHorizontalTransform;
	private PublicMatrix4f leftHorizontalTransform;
	
	public RenderShield()
	{
		super();
		this.transform.rotate((float)Math.toRadians(5), Vector3f.XN);
		this.transform.translate(0, 0.055F, -0.075F);
		this.leftTransform = new PublicMatrix4f(this.transform);
		this.leftTransform.rotate((float)Math.toRadians(180), Vector3f.ZP);
		this.leftTransform.translate(0, 0.25F, 0);
	}
	
	@Override
	public PublicMatrix4f getTransform(ItemStack stack, LivingCap<?> itemHolder, InteractionHand hand)
	{
		return itemHolder.holdsShieldHorizontally() ? new PublicMatrix4f(hand == InteractionHand.MAIN_HAND ? this.rightHorizontalTransform
				: this.leftHorizontalTransform) :
				super.getTransform(stack, itemHolder, hand);
	}
}