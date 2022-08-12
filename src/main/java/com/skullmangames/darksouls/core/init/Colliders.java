package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.common.capability.entity.StrayDemonCap;
import com.skullmangames.darksouls.core.util.physics.Collider;
import com.skullmangames.darksouls.core.util.physics.CubeCollider;

public class Colliders
{
	public static Collider BODY = new CubeCollider(0.5F, 0.7F, 0.7F, 0F, 1.0F, -0.6F);
	public static Collider BODY_SHOCKWAVE = new CubeCollider(2.0F, 0.7F, 2.8F, 0F, 1.0F, -0.6F);
	public static Collider FIST = new CubeCollider(0.4F, 0.4F, 0.4F, 0F, 0F, 0F);
	public static Collider SHORTSWORD = new CubeCollider(0.4F, 0.3F, 0.7F, 0F, 0F, -0.5F);
	public static Collider LONGSWORD = new CubeCollider(0.4F, 0.3F, 0.9F, 0F, 0F, -0.5F);
	public static Collider BROKEN_SWORD = new CubeCollider(0.45F, 0.25F, 0.45F, 0.0F, 0.0F, -0.25F);
	public static Collider TOOL = new CubeCollider(0.3F, 0.3F, 0.4F, 0F, 0.25F, -0.7F);
	public static Collider GREAT_HAMMER = new CubeCollider(0.6F, 0.6F, 0.7F, 0.0F, 0.3F, -1.9F);
	public static Collider DAGGER = new CubeCollider(0.45F, 0.25F, 0.45F, 0.0F, 0.0F, -0.25F);
	public static Collider SPEAR = new CubeCollider(0.4F, 0.4F, 0.4F, 0F, 0F, -1.35F);
	public static Collider WINGED_SPEAR = new CubeCollider(0.4F, 0.4F, 0.75F, 0F, 0F, -1.35F);
	public static Collider ULTRA_GREATSWORD = new CubeCollider(0.4F, 0.4F, 0.95F, 0F, 0F, -1.35F);
	
	//Stray Demon
	public static Collider STRAY_DEMON_GREAT_HAMMER = GREAT_HAMMER.getScaledCollider(StrayDemonCap.getWeaponScale());
	public static Collider STRAY_DEMON_BODY = new CubeCollider(3F, 1F, 3F, 0F, 0F, 0F);
}