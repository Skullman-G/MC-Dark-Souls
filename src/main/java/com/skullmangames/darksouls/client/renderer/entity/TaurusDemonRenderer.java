package com.skullmangames.darksouls.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3d;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.client.renderer.layer.HeldItemLayer;
import com.skullmangames.darksouls.common.capability.entity.TaurusDemonCap;
import com.skullmangames.darksouls.common.entity.TaurusDemon;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TaurusDemonRenderer extends ArmatureRenderer<TaurusDemon, TaurusDemonCap>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(DarkSouls.MOD_ID, "textures/entities/taurus_demon.png");
	
	public TaurusDemonRenderer()
	{
		this.layers.add(new HeldItemLayer<>(TaurusDemonCap.getWeaponScale(), new Vector3d(0.0D, 0.35D, 0.0D)));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(TaurusDemon entity)
	{
		return TEXTURE;
	}
	
	@Override
	protected void applyRotations(PoseStack matStack, Armature armature, TaurusDemonCap entityCap, float partialTicks)
	{
		float scale = 2.0F;
		matStack.scale(scale, scale, scale);
		super.applyRotations(matStack, armature, entityCap, partialTicks);
		this.transformJoint(2, armature, entityCap.getHeadMatrix(partialTicks));
	}
}
