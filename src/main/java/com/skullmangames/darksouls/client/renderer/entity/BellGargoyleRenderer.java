package com.skullmangames.darksouls.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3d;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.client.renderer.layer.HeldItemLayer;
import com.skullmangames.darksouls.common.capability.entity.BellGargoyleCap;
import com.skullmangames.darksouls.common.entity.BellGargoyle;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BellGargoyleRenderer extends ArmatureRenderer<BellGargoyle, BellGargoyleCap>
{
	private static final ResourceLocation TEXTURE = DarkSouls.rl("textures/entities/bell_gargoyle.png");
	
	public BellGargoyleRenderer()
	{
		this.layers.add(new HeldItemLayer<>(BellGargoyleCap.WEAPON_SCALE, new Vector3d(0.0D, 0.0D, 0.0D)));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(BellGargoyle entityIn)
	{
		return TEXTURE;
	}
	
	@Override
	protected void applyRotations(PoseStack matStack, Armature armature, BellGargoyleCap entityCap, float partialTicks)
	{
		super.applyRotations(matStack, armature, entityCap, partialTicks);
		this.transformJoint(3, armature, entityCap.getHeadMatrix(partialTicks));
	}
}
