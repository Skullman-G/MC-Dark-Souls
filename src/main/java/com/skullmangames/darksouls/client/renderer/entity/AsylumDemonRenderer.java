package com.skullmangames.darksouls.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3d;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.client.renderer.layer.HeldItemLayer;
import com.skullmangames.darksouls.common.capability.entity.AsylumDemonData;
import com.skullmangames.darksouls.common.entity.AsylumDemon;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AsylumDemonRenderer extends ArmatureRenderer<AsylumDemon, AsylumDemonData>
{
	private final ResourceLocation textureLocation = new ResourceLocation(DarkSouls.MOD_ID, "textures/entities/asylum_demon/asylum_demon.png");
	
	public AsylumDemonRenderer()
	{
		this.layers.add(new HeldItemLayer<>(AsylumDemonData.getWeaponScale(), new Vector3d(0.0D, 0.0D, 0.1D)));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(AsylumDemon entityIn)
	{
		return this.textureLocation;
	}
	
	@Override
	protected void applyRotations(PoseStack matStack, Armature armature, AsylumDemon entityIn, AsylumDemonData entitydata, float partialTicks)
	{
		float scale = 1.4F;
		matStack.scale(scale, scale, scale);
		super.applyRotations(matStack, armature, entityIn, entitydata, partialTicks);
		this.transformJoint(2, armature, entitydata.getHeadMatrix(partialTicks));
	}
}
