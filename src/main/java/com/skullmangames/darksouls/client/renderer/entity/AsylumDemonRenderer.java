package com.skullmangames.darksouls.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.common.capability.entity.AsylumDemonData;
import com.skullmangames.darksouls.common.entity.AsylumDemonEntity;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AsylumDemonRenderer extends ArmatureRenderer<AsylumDemonEntity, AsylumDemonData>
{
	private final ResourceLocation textureLocation = new ResourceLocation(DarkSouls.MOD_ID, "textures/entities/asylum_demon/asylum_demon.png");
	
	@Override
	protected ResourceLocation getEntityTexture(AsylumDemonEntity entityIn)
	{
		return this.textureLocation;
	}
	
	@Override
	protected void applyRotations(MatrixStack matStack, Armature armature, AsylumDemonEntity entityIn, AsylumDemonData entitydata, float partialTicks)
	{
		super.applyRotations(matStack, armature, entityIn, entitydata, partialTicks);
		matStack.translate(0.0D, 2.0D, 0.0D);
	}
}
