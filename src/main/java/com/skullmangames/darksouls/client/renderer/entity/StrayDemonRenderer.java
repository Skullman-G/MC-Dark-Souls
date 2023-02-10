package com.skullmangames.darksouls.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3d;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.client.renderer.layer.HeldItemLayer;
import com.skullmangames.darksouls.common.capability.entity.StrayDemonCap;
import com.skullmangames.darksouls.common.entity.StrayDemon;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StrayDemonRenderer extends ArmatureRenderer<StrayDemon, StrayDemonCap>
{
	private final ResourceLocation textureLocation = new ResourceLocation(DarkSouls.MOD_ID, "textures/entities/asylum_demon/asylum_demon.png");
	
	public StrayDemonRenderer()
	{
		this.layers.add(new HeldItemLayer<>(StrayDemonCap.getWeaponScale(), new Vector3d(0.0D, 0.0D, 0.1D)));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(StrayDemon entityIn)
	{
		return this.textureLocation;
	}
	
	@Override
	protected void applyRotations(MatrixStack matStack, Armature armature, StrayDemonCap entityCap, float partialTicks)
	{
		float scale = 1.4F;
		matStack.scale(scale, scale, scale);
		super.applyRotations(matStack, armature, entityCap, partialTicks);
		this.transformJoint(2, armature, entityCap.getHeadMatrix(partialTicks));
	}
}
