package com.skullmangames.darksouls.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3d;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.client.renderer.layer.HeldItemLayer;
import com.skullmangames.darksouls.common.capability.entity.BerenikeKnightCap;
import com.skullmangames.darksouls.common.entity.BerenikeKnight;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BerenikeKnightRenderer extends BipedRenderer<BerenikeKnight, BerenikeKnightCap>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(DarkSouls.MOD_ID, "textures/entities/hollow/lordran_hollow.png");
	
	public BerenikeKnightRenderer()
	{
		this.layers.remove(0);
		this.layers.add(new HeldItemLayer<>(BerenikeKnightCap.WEAPON_SCALE, new Vector3d(0.0D, 0.0D, 0.0D)));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(BerenikeKnight entityIn)
	{
		return TEXTURE;
	}
	
	@Override
	protected void applyRotations(PoseStack poseStack, Armature armature, BerenikeKnightCap entityCap, float partialTicks)
	{
		float scale = 1.5F;
		poseStack.scale(scale, scale, scale);
		super.applyRotations(poseStack, armature, entityCap, partialTicks);
	}
}
