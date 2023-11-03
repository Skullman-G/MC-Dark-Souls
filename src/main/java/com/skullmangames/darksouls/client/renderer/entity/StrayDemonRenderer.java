package com.skullmangames.darksouls.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3d;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.client.renderer.layer.HeldItemLayer;
import com.skullmangames.darksouls.common.capability.entity.StrayDemonCap;
import com.skullmangames.darksouls.common.entity.StrayDemon;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StrayDemonRenderer extends ArmatureRenderer<StrayDemon, StrayDemonCap>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(DarkSouls.MOD_ID, "textures/entities/stray_demon.png");
	
	public StrayDemonRenderer()
	{
		this.layers.add(new HeldItemLayer<>(StrayDemonCap.getWeaponScale(), new Vector3d(0.0D, 0.0D, 0.0D)));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(StrayDemon entityIn)
	{
		return TEXTURE;
	}
	
	@Override
	protected void applyRotations(PoseStack matStack, Armature armature, StrayDemonCap entityCap, float partialTicks)
	{
		super.applyRotations(matStack, armature, entityCap, partialTicks);
		this.transformJoint(3, armature, entityCap.getHeadMatrix(partialTicks));
	}
}
