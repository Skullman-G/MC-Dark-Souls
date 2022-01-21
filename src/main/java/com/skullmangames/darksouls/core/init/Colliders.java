package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.common.capability.entity.AsylumDemonData;
import com.skullmangames.darksouls.core.util.physics.Collider;

public class Colliders
{
	public static final Collider body = new Collider(0.5F, 0.7F, 0.7F, 0F, 1.0F, -0.6F);
	public static final Collider bodyShockwave = new Collider(2.0F, 0.7F, 2.8F, 0F, 1.0F, -0.6F);
	public static final Collider fist = new Collider(0.4F, 0.4F, 0.4F, 0F, 0F, 0F);
	public static final Collider sword = new Collider(0.45F, 0.25F, 0.45F, 0.0F, 0.0F, -0.25F);
	public static final Collider swordDash = new Collider(0.4F, 0.4F, 0.75F, 0F, 0F, -0.6F);
	public static final Collider brokenSword = new Collider(0.45F, 0.25F, 0.45F, 0.0F, 0.0F, -0.25F);
	public static final Collider tools = new Collider(0.4F, 0.4F, 0.55F, 0F, 0.0F, 0F);
	public static final Collider great_hammer = new Collider(0.6F, 0.6F, 0.7F, 0.0F, 0.3F, -1.9F);
	public static final Collider dagger = new Collider(0.45F, 0.25F, 0.45F, 0.0F, 0.0F, -0.25F);
	
	
	//Asylum Demon
	public static final Collider asylum_demon_great_hammer = great_hammer.getScaledCollider(AsylumDemonData.getWeaponScale());
	public static final Collider asylum_demon_body = new Collider(3F, 1F, 3F, 0F, 0F, 0F);
}