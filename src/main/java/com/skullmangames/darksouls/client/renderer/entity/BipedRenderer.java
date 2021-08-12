package com.skullmangames.darksouls.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.client.renderer.layer.HeldItemLayer;
import com.skullmangames.darksouls.client.renderer.layer.WearableItemLayer;
import com.skullmangames.darksouls.common.entities.LivingData;
import com.skullmangames.darksouls.util.math.vector.PublicMatrix4f;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class BipedRenderer<E extends LivingEntity, T extends LivingData<E>> extends ArmatureRenderer<E, T>
{
	public BipedRenderer()
	{
		this.layers.add(new HeldItemLayer<>());
		this.layers.add(new WearableItemLayer<>(EquipmentSlotType.HEAD));
		this.layers.add(new WearableItemLayer<>(EquipmentSlotType.CHEST));
		this.layers.add(new WearableItemLayer<>(EquipmentSlotType.LEGS));
		this.layers.add(new WearableItemLayer<>(EquipmentSlotType.FEET));
	}
	
	@Override
	protected void applyRotations(MatrixStack matStack, Armature armature, E entityIn, T entitydata, float partialTicks)
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