package com.skullmangames.darksouls.client.renderer.item;

import net.minecraft.util.math.vector.Vector3f;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderShield extends RenderItemMirror
{
	public RenderShield()
	{
		super();
		this.correctionMatrix.translate(new Vector3f(0F,0.1F,0F));
		
		this.leftHandCorrectionMatrix = new PublicMatrix4f(this.correctionMatrix);
		leftHandCorrectionMatrix.translate(0F, 0F, 0.4F);
		this.leftHandCorrectionMatrix.rotate((float)Math.toRadians(180D), Vector3f.YP);
	}
}