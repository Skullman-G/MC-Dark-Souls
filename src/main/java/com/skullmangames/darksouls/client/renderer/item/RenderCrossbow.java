package com.skullmangames.darksouls.client.renderer.item;

import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderCrossbow extends RenderShootableWeapon
{
	public RenderCrossbow()
	{
		transform = new ModMatrix4f();
	}
}