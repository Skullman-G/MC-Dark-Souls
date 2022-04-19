package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.common.capability.entity.StrayDemonCap;
import com.skullmangames.darksouls.core.util.physics.Collider;

public class Colliders
{
	public static final Collider BODY = new Collider(0.5F, 0.7F, 0.7F, 0F, 1.0F, -0.6F);
	public static final Collider BODY_SHOCKWAVE = new Collider(2.0F, 0.7F, 2.8F, 0F, 1.0F, -0.6F);
	public static final Collider FIST = new Collider(0.4F, 0.4F, 0.4F, 0F, 0F, 0F);
	public static final Collider SHORTSWORD = new Collider(0.75F, 0.55F, 0.85F, 0F, 0F, -0.25F);
	public static final Collider LONGSWORD = new Collider(0.75F, 0.65F, 0.85F, 0F, 0F, -0.25F);
	public static final Collider BROKEN_SWORD = new Collider(0.45F, 0.25F, 0.45F, 0.0F, 0.0F, -0.25F);
	public static final Collider TOOL = new Collider(0.4F, 0.4F, 0.55F, 0F, 0.0F, 0F);
	public static final Collider GREAT_HAMMER = new Collider(0.6F, 0.6F, 0.7F, 0.0F, 0.3F, -1.9F);
	public static final Collider DAGGER = new Collider(0.45F, 0.25F, 0.45F, 0.0F, 0.0F, -0.25F);
	public static final Collider SPEAR = new Collider(0.4F, 0.4F, 0.9F, 0F, 0F, -1.35F);
	public static final Collider ULTRA_GREATSWORD = new Collider(0.4F, 0.4F, 0.9F, 0F, 0F, -1.35F);
	
	
	//Asylum Demon
	public static final Collider STRAY_DEMON_GREAT_HAMMER = GREAT_HAMMER.getScaledCollider(StrayDemonCap.getWeaponScale());
	public static final Collider ASYLUM_DEMON_BODY = new Collider(3F, 1F, 3F, 0F, 0F, 0F);
}