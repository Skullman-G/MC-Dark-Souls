package com.skullmangames.darksouls.client.renderer.item;

import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderShield extends RenderItemMirror
{
	public RenderShield()
	{
		super();
		this.leftHandCorrectionMatrix = new PublicMatrix4f();
		PublicMatrix4f.translate(new Vector3f(0F,0.5F,-0.13F), this.leftHandCorrectionMatrix, this.leftHandCorrectionMatrix);
		PublicMatrix4f.rotate((float)Math.toRadians(180D), new Vector3f(0F,1F,0F), this.leftHandCorrectionMatrix, this.leftHandCorrectionMatrix);
		PublicMatrix4f.rotate((float)Math.toRadians(90D), new Vector3f(1F,0F,0F), this.leftHandCorrectionMatrix, this.leftHandCorrectionMatrix);
		PublicMatrix4f.translate(new Vector3f(0F,0.1F,0F), this.correctionMatrix, this.correctionMatrix);
	}
}