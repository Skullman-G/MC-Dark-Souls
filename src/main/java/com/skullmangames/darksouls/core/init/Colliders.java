package com.skullmangames.darksouls.core.init;

import java.util.HashMap;
import java.util.Map;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.capability.entity.BerenikeKnightCap;
import com.skullmangames.darksouls.common.capability.entity.StrayDemonCap;
import com.skullmangames.darksouls.common.capability.entity.TaurusDemonCap;
import com.skullmangames.darksouls.core.util.physics.Collider;
import com.skullmangames.darksouls.core.util.physics.CubeCollider;

import net.minecraft.resources.ResourceLocation;

public class Colliders
{
	public static final Map<ResourceLocation, Collider> COLLIDERS = new HashMap<>();
	public static final Map<Collider, ResourceLocation> IDS = new HashMap<>();
	
	public static final Collider FIST = register("fist", new CubeCollider(-0.2F, -0.2F, -0.2F, 0.15F, 0.1F, 0.15F));
	public static final Collider SHORTSWORD = register("shortsword", new CubeCollider(-0.2F, -0.15F, -1.1F, 0.2F, 0.25F, -0.15F));
	public static final Collider LONGSWORD = register("longsword", new CubeCollider(-0.2F, -0.15F, -1.25F, 0.2F, 0.25F, -0.15F));
	public static final Collider BROKEN_SWORD = register("broken_sword", new CubeCollider(-0.2F, -0.15F, -0.9F, 0.2F, 0.25F, -0.15F));
	public static final Collider GREAT_HAMMER = register("great_hammer", new CubeCollider(-0.5F, -0.45F, -2.3F, 0.5F, 0.45F, -1.1F));
	public static final Collider DAGGER = register("dagger", new CubeCollider(-0.3F, -0.15F, -0.7F, 0.3F, 0.45F, -0.2F));
	public static final Collider SPEAR = register("spear", new CubeCollider(-0.2F, -0.15F, -1.8F, 0.15F, 0.2F, -1.3F));
	public static final Collider WINGED_SPEAR = register("winged_spear", new CubeCollider(-0.2F, 0.1F, -2.2F, 0.2F, 0.7F, -1.7F));
	public static final Collider ULTRA_GREATSWORD = register("ultra_greatsword", new CubeCollider(-0.3F, -0.15F, -2.3F, 0.3F, 0.45F, -0.2F));
	public static final Collider GREATSWORD = register("greatsword", new CubeCollider(-0.25F, -0.1F, -1.5F, 0.2F, 0.35F, -0.2F));
	public static final Collider GREATAXE = register("greataxe", new CubeCollider(-0.4F, -0.5F, -2.3F, 0.4F, 0.5F, -1.1F));
	public static final Collider SHIELD = register("shield", new CubeCollider(-0.2F, -0.3F, -0.5F, 0.2F, 0.7F, 0.6F));
	public static final Collider AXE = register("axe", new CubeCollider(-0.2F, -0.25F, -0.7F, 0.2F, 0.25F, -0.3F));
	public static final Collider BATTLE_AXE = register("battle_axe", new CubeCollider(-0.2F, -0.45F, -1.15F, 0.2F, 0.45F, -0.65F));
	public static final Collider PICKAXE = register("pickaxe", new CubeCollider(-0.2F, -0.25F, -0.7F, 0.2F, 0.25F, -0.3F));
	public static final Collider MACE = register("mace", new CubeCollider(-0.2F, -0.1F, -1.1F, 0.2F, 0.35F, -0.45F));
	
	//Stray Demon
	public static final Collider STRAY_DEMON_GREAT_HAMMER = GREAT_HAMMER.getScaledCollider(StrayDemonCap.WEAPON_SCALE);
	public static final Collider STRAY_DEMON_BODY = new CubeCollider(-2F, -2F, -2F, 2F, 2F, 2F);
	
	//Taurus Demon
	public static final Collider TAURUS_DEMON_GREATAXE = GREATAXE.getScaledCollider(TaurusDemonCap.WEAPON_SCALE);
	
	//Berenike Knight
	public static final Collider BERENIKE_KNIGHT_ULTRA_GREATSWORD = ULTRA_GREATSWORD.getScaledCollider(BerenikeKnightCap.WEAPON_SCALE);
	public static final Collider BERENIKE_KNIGHT_MACE = MACE.getScaledCollider(BerenikeKnightCap.WEAPON_SCALE);
	
	private static Collider register(String name, Collider collider)
	{
		ResourceLocation id = DarkSouls.rl(name);
		COLLIDERS.put(id, collider);
		IDS.put(collider, id);
		return collider;
	}
	
	public static Collider getCollider(ResourceLocation id)
	{
		return COLLIDERS.get(id);
	}
	
	public static ResourceLocation getId(Collider collider)
	{
		return IDS.get(collider);
	}
}