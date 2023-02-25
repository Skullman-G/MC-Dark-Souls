package com.skullmangames.darksouls.core.init;

import java.util.HashMap;
import java.util.Map;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.capability.entity.StrayDemonCap;
import com.skullmangames.darksouls.core.util.physics.Collider;
import com.skullmangames.darksouls.core.util.physics.CubeCollider;

import net.minecraft.resources.ResourceLocation;

public class Colliders
{
	public static final Map<ResourceLocation, Collider> COLLIDERS = new HashMap<>();
	
	public static final Collider FIST = register("fist", new CubeCollider(0.4F, 0.4F, 0.4F, 0F, 0F, 0F));
	public static final Collider SHORTSWORD = register("shortsword", new CubeCollider(0.4F, 0.3F, 0.7F, 0F, 0F, -0.5F));
	public static final Collider LONGSWORD = register("longsword", new CubeCollider(0.4F, 0.3F, 0.9F, 0F, 0F, -0.5F));
	public static final Collider BROKEN_SWORD = register("broken_sword", new CubeCollider(0.45F, 0.25F, 0.45F, 0.0F, 0.0F, -0.25F));
	public static final Collider TOOL = register("tool", new CubeCollider(0.3F, 0.3F, 0.4F, 0F, 0.25F, -0.7F));
	public static final Collider GREAT_HAMMER = register("great_hammer", new CubeCollider(0.7F, 0.7F, 0.8F, 0.0F, 0.3F, -1.9F));
	public static final Collider DAGGER = register("dagger", new CubeCollider(0.45F, 0.25F, 0.45F, 0.0F, 0.0F, -0.25F));
	public static final Collider SPEAR = register("spear", new CubeCollider(0.4F, 0.4F, 0.4F, 0F, 0F, -1.35F));
	public static final Collider WINGED_SPEAR = register("winged_spear", new CubeCollider(0.4F, 0.4F, 0.75F, 0F, 0F, -1.35F));
	public static final Collider ULTRA_GREATSWORD = register("ultra_greatsword", new CubeCollider(0.4F, 0.4F, 0.95F, 0F, 0F, -1.35F));
	
	//Stray Demon
	public static final Collider STRAY_DEMON_GREAT_HAMMER = GREAT_HAMMER.getScaledCollider(StrayDemonCap.getWeaponScale());
	public static final Collider STRAY_DEMON_BODY = new CubeCollider(3F, 1F, 3F, 0F, 0F, 0F);
	
	private static Collider register(String name, Collider collider)
	{
		COLLIDERS.put(new ResourceLocation(DarkSouls.MOD_ID, name), collider);
		return collider;
	}
}