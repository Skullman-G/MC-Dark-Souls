package com.skullmangames.darksouls.core.init;

import java.util.HashMap;
import java.util.Map;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.core.util.collider.CapsuleCollider;
import com.skullmangames.darksouls.core.util.collider.Collider;
import com.skullmangames.darksouls.core.util.collider.CubeCollider;
import com.skullmangames.darksouls.core.util.collider.MultiCapsuleCollider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class Colliders
{
	public static final Map<ResourceLocation, Collider> COLLIDERS = new HashMap<>();
	public static final Map<Collider, ResourceLocation> IDS = new HashMap<>();
	
	public static final Collider FIST = register("fist", new CapsuleCollider(0.5D, 1.0D, new Vec3(0, 0, 0.5D)));
	public static final Collider SHORTSWORD = register("shortsword", new CapsuleCollider(0.5D, 1.5D, new Vec3(0, 0.05D, 0), -4.5F, 0));
	public static final Collider LONGSWORD = register("longsword", new CapsuleCollider(0.5D, 1.75D, new Vec3(0, 0.05D, 0), -4.5F, 0));
	public static final Collider BROKEN_SWORD = register("broken_sword", new CapsuleCollider(0.5D, 1.3D, new Vec3(0, 0.05D, 0), -4.5F, 0));
	public static final Collider GREAT_HAMMER = register("great_hammer", new MultiCapsuleCollider
			(
				new CapsuleCollider(0.75D, 1.75D, new Vec3(0, 0.05D, -0.75D), -4.5F, 0),
				new CapsuleCollider(0.25D, 1.5D, new Vec3(0, 0.05D, 0), -4.5F, 0)
			));
	public static final Collider DAGGER = register("dagger", new CubeCollider(-0.3F, -0.15F, -0.7F, 0.3F, 0.45F, -0.2F));
	public static final Collider SPEAR = register("spear", new MultiCapsuleCollider
				(
					new CapsuleCollider(0.3D, 0.9D, new Vec3(0, 0.05D, -1.1D), -4.5F, 0),
					new CapsuleCollider(0.2D, 1.5D, new Vec3(0, 0.05D, 0), -4.5F, 0)
				));
	public static final Collider WINGED_SPEAR = register("winged_spear", new MultiCapsuleCollider
				(
					new CapsuleCollider(0.35D, 1D, new Vec3(0, 0.05D, -1.5D), -4.5F, 0),
					new CapsuleCollider(0.2D, 1.7D, new Vec3(0, 0.05D, 0), -4.5F, 0)
				));
	public static final Collider ULTRA_GREATSWORD = register("ultra_greatsword", new CapsuleCollider(0.5D, 2.7D, new Vec3(0, 0.125D, 0), -5.5F, 0));
	public static final Collider GREATSWORD = register("greatsword", new CapsuleCollider(0.5D, 2.1D, new Vec3(0, 0.1D, 0), -5.5F, 0));
	public static final Collider GREATAXE = register("greataxe", new MultiCapsuleCollider
			(
				new CapsuleCollider(0.8D, 1.75D, new Vec3(0, -0.1D, -0.5D), -4.5F, 0),
				new CapsuleCollider(0.25D, 1.5D, new Vec3(0, 0.2D, 0), -4.5F, 0)
			));
	public static final Collider SHIELD = register("shield", new CapsuleCollider(0.6D, 1.5D, new Vec3(0.15D, 0, 0.6D), -90F, 0));
	public static final Collider AXE = register("axe", new CapsuleCollider(0.3D, 0.75D, new Vec3(-0.025D, 0D, -0.1D), 3, 0));
	public static final Collider BATTLE_AXE = register("battle_axe", new CapsuleCollider(0.5D, 1.1D, new Vec3(0, 0, -0.4D), -4.5F, 0));
	public static final Collider PICKAXE = register("pickaxe", new CapsuleCollider(0.3D, 0.75D, new Vec3(-0.025D, 0D, -0.1D), 3, 0));
	public static final Collider MACE = register("mace", new CapsuleCollider(0.4D, 0.8D, new Vec3(0, 0.05D, -0.4D), -4.5F, 0));
	
	//Stray Demon
	public static final Collider STRAY_DEMON_GREAT_HAMMER = new MultiCapsuleCollider
			(
					new CapsuleCollider(2D, 4.5D, new Vec3(0, 0.05D, -2D), -4.5F, 0),
					new CapsuleCollider(0.75D, 3.5D, new Vec3(0, 0.05D, 0), -4.5F, 0)
			);
	public static final Collider STRAY_DEMON_BODY = new CapsuleCollider(4D, 8D, new Vec3(0, 0, 0), -90F, 0);
	
	//Taurus Demon
	public static final Collider TAURUS_DEMON_GREATAXE = new MultiCapsuleCollider
			(
					new CapsuleCollider(2D, 4.5D, new Vec3(0, 0.05D, -2D), -4.5F, 0),
					new CapsuleCollider(0.75D, 3.5D, new Vec3(0, 0.05D, 0), -4.5F, 0)
			);
	
	//Berenike Knight
	public static final Collider BERENIKE_KNIGHT_ULTRA_GREATSWORD = new CapsuleCollider(0.5D, 3D, new Vec3(0, 0.125D, 0), -5.5F, 0);
	public static final Collider BERENIKE_KNIGHT_MACE = new MultiCapsuleCollider
			(
					new CapsuleCollider(0.5D, 1.0D, new Vec3(0, 0.1D, -0.4D), -4.5F, 0),
					new CapsuleCollider(0.1D, 1.0D, new Vec3(0, 0.1D, 0), -4.5F, 0)
			);
	
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