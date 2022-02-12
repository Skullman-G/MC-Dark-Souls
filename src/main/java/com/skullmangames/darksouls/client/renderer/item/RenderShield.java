package com.skullmangames.darksouls.client.renderer.item;

import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderShield extends RenderItemMirror
{
	public RenderShield()
	{
		super();
		PublicMatrix4f.translate(new Vector3f(0F,0.1F,0F), this.correctionMatrix, this.correctionMatrix);
		
		this.leftHandCorrectionMatrix = new PublicMatrix4f(this.correctionMatrix);
		PublicMatrix4f.translate(new Vector3f(0F, 0F, 0.4F), leftHandCorrectionMatrix, leftHandCorrectionMatrix);
		PublicMatrix4f.rotate((float)Math.toRadians(180D), new Vector3f(0F,1F,0F), this.leftHandCorrectionMatrix, this.leftHandCorrectionMatrix);
	}
}