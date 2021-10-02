package com.skullmangames.darksouls.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.client.renderer.layer.HeldItemLayer;
import com.skullmangames.darksouls.common.capability.entity.AsylumDemonData;
import com.skullmangames.darksouls.common.entity.AsylumDemonEntity;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AsylumDemonRenderer extends ArmatureRenderer<AsylumDemonEntity, AsylumDemonData>
{
	private final ResourceLocation textureLocation = new ResourceLocation(DarkSouls.MOD_ID, "textures/entities/asylum_demon/asylum_demon.png");
	
	public AsylumDemonRenderer()
	{
		this.layers.add(new HeldItemLayer<>(1.5F, new Vector3d(0.0D, 0.0D, 0.1D)));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(AsylumDemonEntity entityIn)
	{
		return this.textureLocation;
	}
	
	@Override
	protected void applyRotations(MatrixStack matStack, Armature armature, AsylumDemonEntity entityIn, AsylumDemonData entitydata, float partialTicks)
	{
		super.applyRotations(matStack, armature, entityIn, entitydata, partialTicks);
		this.transformJoint(2, armature, entitydata.getHeadMatrix(partialTicks));
	}
}
