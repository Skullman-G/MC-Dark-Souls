package com.skullmangames.darksouls.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.common.capability.entity.BlackKnightCap;
import com.skullmangames.darksouls.common.entity.BlackKnight;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlackKnightRenderer extends BipedRenderer<BlackKnight, BlackKnightCap>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(DarkSouls.MOD_ID, "textures/entities/hollow/hollow.png");
	
	@Override
	protected ResourceLocation getEntityTexture(BlackKnight entityIn)
	{
		return TEXTURE;
	}
	
	@Override
	protected void applyRotations(PoseStack matStack, Armature armature, BlackKnightCap entityCap, float partialTicks)
	{
		float scale = 1.2F;
		matStack.scale(scale, scale, scale);
		super.applyRotations(matStack, armature, entityCap, partialTicks);
		this.transformJoint(2, armature, entityCap.getHeadMatrix(partialTicks));
	}
}