package com.skullmangames.darksouls.client.renderer.item;

import net.minecraft.util.math.vector.Vector3f;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderBow extends RenderShootableWeapon
{
	public RenderBow()
	{
		correctionMatrix = new PublicMatrix4f();
		
		correctionMatrix.rotate((float)Math.toRadians(-90), Vector3f.XP);
		correctionMatrix.rotate((float)Math.toRadians(-10), Vector3f.ZP);
		correctionMatrix.translate(0.06F, 0.1F, 0);
	}
}