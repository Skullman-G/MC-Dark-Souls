package com.skullmangames.darksouls.client.renderer;

import com.skullmangames.darksouls.util.math.vector.PublicMatrix4f;

import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderBow extends RenderShootableWeapon
{
	public RenderBow()
	{
		correctionMatrix = new PublicMatrix4f();
		
		PublicMatrix4f.rotate((float)Math.toRadians(-90), new Vector3f(1,0,0), correctionMatrix, correctionMatrix);
		PublicMatrix4f.rotate((float)Math.toRadians(-10), new Vector3f(0,0,1), correctionMatrix, correctionMatrix);
		PublicMatrix4f.translate(new Vector3f(0.06F,0.1F,0), correctionMatrix, correctionMatrix);
	}
}