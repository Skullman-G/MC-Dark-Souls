package com.skullmangames.darksouls.client.renderer;

import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderCrossbow extends RenderShootableWeapon
{
	public RenderCrossbow()
	{
		correctionMatrix = new PublicMatrix4f();
	}
}