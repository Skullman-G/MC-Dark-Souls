package com.skullmangames.darksouls.client.renderer.item;

import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderBow extends RenderShootableWeapon
{
	public RenderBow()
	{
		transform = new ModMatrix4f();
		
		transform.rotate((float)Math.toRadians(-90), Vector3f.XP);
		transform.rotate((float)Math.toRadians(-10), Vector3f.ZP);
		transform.translate(0.06F, 0.1F, 0);
	}
}