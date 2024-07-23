package com.skullmangames.darksouls.client.renderer.item;

import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderShield extends RenderItemMirror
{
	private ModMatrix4f rightHorizontalTransform;
	private ModMatrix4f leftHorizontalTransform;
	
	private ModMatrix4f rightReverseTransform;
	private ModMatrix4f leftReverseTransform;
	
	public RenderShield()
	{
		super();
		
		this.transform = new ModMatrix4f();
		this.transform.rotateDeg(-85, Vector3f.XP);
		this.transform.translate(0, 0.155F, 0);
		this.leftTransform = new ModMatrix4f();
		this.leftTransform.rotateDeg(-85, Vector3f.XP);
		this.leftTransform.rotateDeg(180, Vector3f.ZP);
		this.leftTransform.translate(0, 0.09F, 0);
		
		this.rightHorizontalTransform = new ModMatrix4f();
		this.rightHorizontalTransform.rotateDeg(5, Vector3f.XP);
		this.rightHorizontalTransform.translate(0.05F, 0.15F, -0.2F);
		this.leftHorizontalTransform = new ModMatrix4f();
		this.leftHorizontalTransform.rotateDeg(5, Vector3f.XP);
		this.leftHorizontalTransform.rotateDeg(180, Vector3f.ZP);
		this.leftHorizontalTransform.translate(0.05F, 0.01F, -0.2F);
		
		this.rightReverseTransform = new ModMatrix4f();
		this.rightReverseTransform.rotateDeg(-85, Vector3f.XP);
		this.rightReverseTransform.rotateDeg(180, Vector3f.YP);
		this.rightReverseTransform.rotateDeg(180, Vector3f.ZP);
		this.rightReverseTransform.translate(0F, 0.11F, -0.2F);
		this.leftReverseTransform = new ModMatrix4f();
		this.leftReverseTransform.rotateDeg(-85, Vector3f.XP);
		this.leftReverseTransform.rotateDeg(180, Vector3f.YP);
		this.leftReverseTransform.translate(0F, 0.18F, -0.2F);
		
		this.backTransform = new ModMatrix4f();
		this.backTransform.rotateDeg(-90, Vector3f.YP);
		this.backTransform.rotateDeg(70, Vector3f.XP);
		this.backTransform.translate(0.15F, 0.1F, -0.3F);
	}
	
	@Override
	public ModMatrix4f getTransform(ItemStack stack, LivingCap<?> itemHolder, InteractionHand hand)
	{
		boolean mainHand = hand == InteractionHand.MAIN_HAND;
		switch (itemHolder.getShieldHoldType(hand))
		{
			default:
			case VERTICAL:
				return super.getTransform(stack, itemHolder, hand);
			case HORIZONTAL:
				return new ModMatrix4f(mainHand ? this.rightHorizontalTransform
						: this.leftHorizontalTransform);
			case VERTICAL_REVERSE:
				return new ModMatrix4f(mainHand ? this.rightReverseTransform
						: this.leftReverseTransform);
		}
	}
}