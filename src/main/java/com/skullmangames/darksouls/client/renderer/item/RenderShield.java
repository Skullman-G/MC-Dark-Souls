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
	
	private PublicMatrix4f rightReverseTransform;
	private PublicMatrix4f leftReverseTransform;
	
	public RenderShield()
	{
		super();
		
		this.transform.rotate((float)Math.toRadians(5), Vector3f.XN);
		this.transform.translate(0, 0.055F, -0.075F);
		this.leftTransform = new PublicMatrix4f(this.transform);
		this.leftTransform.rotate((float)Math.toRadians(180), Vector3f.ZP);
		this.leftTransform.translate(0, 0.25F, 0);
		
		this.rightHorizontalTransform = new PublicMatrix4f();
		this.rightHorizontalTransform.rotate((float)Math.toRadians(5), Vector3f.XP);
		this.rightHorizontalTransform.translate(0.05F, 0.15F, -0.2F);
		this.leftHorizontalTransform = new PublicMatrix4f();
		this.leftHorizontalTransform.rotate((float)Math.toRadians(5), Vector3f.XP);
		this.leftHorizontalTransform.rotate((float)Math.toRadians(180), Vector3f.ZP);
		this.leftHorizontalTransform.translate(0.05F, 0.01F, -0.2F);
		
		this.rightReverseTransform = new PublicMatrix4f();
		this.rightReverseTransform.translate(0, 0.3F, 0.11F);
		this.rightReverseTransform.rotate((float)Math.toRadians(95), Vector3f.XP);
		this.leftReverseTransform = new PublicMatrix4f();
		this.leftReverseTransform.translate(0, 0.3F, -0.11F);
		this.leftReverseTransform.rotate((float)Math.toRadians(90), Vector3f.XP);
		this.leftReverseTransform.rotate((float)Math.toRadians(180), Vector3f.ZP);
		this.leftReverseTransform.rotate((float)Math.toRadians(-5), Vector3f.XP);
<<<<<<< Updated upstream
=======
		
		this.backTransform = new PublicMatrix4f();
		this.backTransform.rotate((float)Math.toRadians(90), Vector3f.YN);
		this.backTransform.rotate((float)Math.toRadians(90), Vector3f.XP);
		this.backTransform.translate(0.15F, 0.1F, -0.2F);
>>>>>>> Stashed changes
	}
	
	@Override
	public PublicMatrix4f getTransform(ItemStack stack, LivingCap<?> itemHolder, InteractionHand hand)
	{
		boolean mainHand = hand == InteractionHand.MAIN_HAND;
		switch (itemHolder.getShieldHoldType())
		{
		default:
		case VERTICAL:
			return super.getTransform(stack, itemHolder, hand);
		case HORIZONTAL:
			return new PublicMatrix4f(mainHand ? this.rightHorizontalTransform
					: this.leftHorizontalTransform);
		case VERTICAL_REVERSE:
			return new PublicMatrix4f(mainHand ? this.rightReverseTransform
					: this.leftReverseTransform);
		}
<<<<<<< Updated upstream
=======
	}
	
	@Override
	protected PublicMatrix4f getBackTransform()
	{
		return this.backTransform;
>>>>>>> Stashed changes
	}
}