package com.skullmangames.darksouls.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.client.renderer.layer.HeldItemLayer;
import com.skullmangames.darksouls.client.renderer.layer.WearableItemLayer;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class BipedRenderer<E extends LivingEntity, T extends LivingCap<E>> extends ArmatureRenderer<E, T>
{
	public BipedRenderer()
	{
		this.layers.add(new HeldItemLayer<>());
		this.layers.add(new WearableItemLayer<>(EquipmentSlot.HEAD));
		this.layers.add(new WearableItemLayer<>(EquipmentSlot.CHEST));
		this.layers.add(new WearableItemLayer<>(EquipmentSlot.LEGS));
		this.layers.add(new WearableItemLayer<>(EquipmentSlot.FEET));
	}
	
	@Override
	protected void applyRotations(PoseStack matStack, Armature armature, E entityIn, T entitydata, float partialTicks)
	{
		super.applyRotations(matStack, armature, entityIn, entitydata, partialTicks);
		if (entityIn.isCrouching())
		{
			matStack.translate(0.0D, 0.15D, 0.0D);
		}
		if (entityIn.isBaby())
		{
			this.transformJoint(9, armature, new PublicMatrix4f().scale(new Vector3f(1.25F, 1.25F, 1.25F)));
		}
		
		this.transformJoint(9, armature, entitydata.getHeadMatrix(partialTicks));
	}
}