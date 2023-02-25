package com.skullmangames.darksouls.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.skullmangames.darksouls.common.capability.item.IShield.ShieldType;
import com.skullmangames.darksouls.common.capability.item.ShieldCap.ShieldMat;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.Scaling;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.WeaponMovesets;
import com.electronwill.nightconfig.core.AbstractCommentedConfig;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ServerConfig
{
	public final List<WeaponConfig> weapons = new ArrayList<>();
	public final List<ShieldConfig> shields = new ArrayList<>();
	
	public ServerConfig(ForgeConfigSpec.Builder config, Map<String, Object> values)
	{
		// Default Config
		String weaponKey = "weapon_config";
		if(values.get(weaponKey) == null)
		{
			config.define(weaponKey+".sample.registry_name", "sample");
			
			String movesetComment = "Default Movesets:";
			for (ResourceLocation r : WeaponMovesets.WEAPON_MOVESETS.keySet())
			{
				movesetComment += ", " + r;
			}
			config.comment(movesetComment);
			
			config.define(weaponKey+".sample.moveset", "darksouls:straight_sword");
			
			String colliderComment = "Default Colliders:";
			for (ResourceLocation r : Colliders.COLLIDERS.keySet())
			{
				colliderComment += ", " + r;
			}
			config.comment(colliderComment);
			
			config.define(weaponKey+".sample.collider", "darksouls:shortsword");
			config.define(weaponKey+".sample.required_strength", 0);
			config.define(weaponKey+".sample.required_dexterity", 0);
			config.define(weaponKey+".sample.required_faith", 0);
			config.defineEnum(weaponKey+".sample.strength_scaling", Scaling.NONE);
			config.defineEnum(weaponKey+".sample.dexterity_scaling", Scaling.NONE);
			config.defineEnum(weaponKey+".sample.faith_scaling", Scaling.NONE);
		}
		
		String shieldKey = "shield_config";
		if(values.get(shieldKey) == null)
		{
			config.define(shieldKey+".sample_shield.registry_name", "sample_shield");
			
			String movesetComment = "Default Movesets:";
			for (ResourceLocation r : WeaponMovesets.WEAPON_MOVESETS.keySet())
			{
				movesetComment += ", " + r;
			}
			config.comment(movesetComment);
			
			config.define(shieldKey+".sample_shield.moveset", "darksouls:shield");
			
			String colliderComment = "Default Colliders:";
			for (ResourceLocation r : Colliders.COLLIDERS.keySet())
			{
				colliderComment += ", " + r;
			}
			config.comment(colliderComment);
			
			config.define(shieldKey+".sample_shield.collider", "darksouls:fist");
			config.define(shieldKey+".sample_shield.required_strength", 0);
			config.define(shieldKey+".sample_shield.required_dexterity", 0);
			config.define(shieldKey+".sample_shield.required_faith", 0);
			config.defineEnum(shieldKey+".sample_shield.strength_scaling", Scaling.NONE);
			config.defineEnum(shieldKey+".sample_shield.dexterity_scaling", Scaling.NONE);
			config.defineEnum(shieldKey+".sample_shield.faith_scaling", Scaling.NONE);
			config.defineEnum(shieldKey+".sample_shield.shield_type", ShieldType.NORMAL);
			config.defineEnum(shieldKey+".sample_shield.shield_material", ShieldMat.WOOD);
			config.defineInRange(shieldKey+".sample_shield.physical_defense", 0.0F, 0.0F, 1.0F);
			config.defineInRange(shieldKey+".sample_shield.fire_defense", 0.0F, 0.0F, 1.0F);
			config.defineInRange(shieldKey+".sample_shield.lightning_defense", 0.0F, 0.0F, 1.0F);
		}
		
		// Init
		if (values.get(weaponKey) != null)
		{
			List<Map.Entry<String, Object>> entries = new LinkedList<>(((AbstractCommentedConfig)values.get(weaponKey)).valueMap().entrySet());
		    Collections.sort(entries, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
			for (Map.Entry<String, Object> entry : entries)
			{
				ConfigValue<String> registryName = config.define(weaponKey+"."+entry.getKey()+".registry_name", "null");
				ConfigValue<String> moveset = config.define(weaponKey+"."+entry.getKey()+".moveset", "darksouls:fist");
				ConfigValue<String> collider = config.define(weaponKey+"."+entry.getKey()+".collider", "darksouls:fist");
				ConfigValue<Integer> reqStrength = config.define(weaponKey+"."+entry.getKey()+".required_strength", 0);
				ConfigValue<Integer> reqDex = config.define(weaponKey+"."+entry.getKey()+".required_dexterity", 0);
				ConfigValue<Integer> reqFaith = config.define(weaponKey+"."+entry.getKey()+".required_faith", 0);
				ConfigValue<Scaling> strengthScaling = config.defineEnum(weaponKey+"."+entry.getKey()+".strength_scaling", Scaling.NONE);
				ConfigValue<Scaling> dexScaling = config.defineEnum(weaponKey+"."+entry.getKey()+".dexterity_scaling", Scaling.NONE);
				ConfigValue<Scaling> faithScaling = config.defineEnum(weaponKey+"."+entry.getKey()+".faith_scaling", Scaling.NONE);
				if (!entry.getKey().equals("sample")) weapons.add(new WeaponConfig(registryName, moveset, collider, reqStrength, reqDex, reqFaith,
						strengthScaling, dexScaling, faithScaling));
			}
		}
		if (values.get(shieldKey) != null)
		{
			List<Map.Entry<String, Object>> entries = new LinkedList<>(((AbstractCommentedConfig)values.get(shieldKey)).valueMap().entrySet());
		    Collections.sort(entries, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
			for (Map.Entry<String, Object> entry : entries)
			{
				ConfigValue<String> registryName = config.define(shieldKey+"."+entry.getKey()+".registry_name", "null");
				ConfigValue<String> moveset = config.define(shieldKey+"."+entry.getKey()+".moveset", "darksouls:shield");
				ConfigValue<String> collider = config.define(shieldKey+"."+entry.getKey()+".collider", "darksouls:fist");
				ConfigValue<Integer> reqStrength = config.define(shieldKey+"."+entry.getKey()+".required_strength", 0);
				ConfigValue<Integer> reqDex = config.define(shieldKey+"."+entry.getKey()+".required_dexterity", 0);
				ConfigValue<Integer> reqFaith = config.define(shieldKey+"."+entry.getKey()+".required_faith", 0);
				ConfigValue<Scaling> strengthScaling = config.defineEnum(shieldKey+"."+entry.getKey()+".strength_scaling", Scaling.NONE);
				ConfigValue<Scaling> dexScaling = config.defineEnum(shieldKey+"."+entry.getKey()+".dexterity_scaling", Scaling.NONE);
				ConfigValue<Scaling> faithScaling = config.defineEnum(shieldKey+"."+entry.getKey()+".faith_scaling", Scaling.NONE);
				
				ConfigValue<ShieldType> shieldType = config.defineEnum(shieldKey+"."+entry.getKey()+".shield_type", ShieldType.NONE);
				ConfigValue<ShieldMat> shieldMat = config.defineEnum(shieldKey+"."+entry.getKey()+".shield_material", ShieldMat.WOOD);
				ConfigValue<Double> physicalDef = config.defineInRange(shieldKey+"."+entry.getKey()+".physical_defense", 0.0F, 0.0F, 1.0F);
				ConfigValue<Double> fireDef = config.defineInRange(shieldKey+"."+entry.getKey()+".fire_defense", 0.0F, 0.0F, 1.0F);
				ConfigValue<Double> lightningDef = config.defineInRange(shieldKey+"."+entry.getKey()+".lightning_defense", 0.0F, 0.0F, 1.0F);
				if (!entry.getKey().equals("sample_shield")) shields.add(new ShieldConfig(registryName, moveset, collider,
						reqStrength, reqDex, reqFaith, strengthScaling, dexScaling, faithScaling, shieldType, shieldMat, physicalDef, fireDef, lightningDef));
			}
		}
	}
	
	public static class WeaponConfig
	{
		public final ConfigValue<String> registryName;
		public final ConfigValue<String> moveset;
		public final ConfigValue<String> collider;
		public final ConfigValue<Integer> reqStrength;
		public final ConfigValue<Integer> reqDex;
		public final ConfigValue<Integer> reqFaith;
		public final ConfigValue<Scaling> strengthScaling;
		public final ConfigValue<Scaling> dexScaling;
		public final ConfigValue<Scaling> faithScaling;
		
		public WeaponConfig(ConfigValue<String> registryName, ConfigValue<String> moveset, ConfigValue<String> collider,
				ConfigValue<Integer> reqStrength, ConfigValue<Integer> reqDex, ConfigValue<Integer> reqFaith,
				ConfigValue<Scaling> strengthScaling, ConfigValue<Scaling> dexScaling, ConfigValue<Scaling> faithScaling)
		{
			this.registryName = registryName;
			this.moveset = moveset;
			this.collider = collider;
			this.reqStrength = reqStrength;
			this.reqDex = reqDex;
			this.reqFaith = reqFaith;
			this.strengthScaling = strengthScaling;
			this.dexScaling = dexScaling;
			this.faithScaling = faithScaling;
		}
	}
	
	public static class ShieldConfig extends WeaponConfig
	{
		public final ConfigValue<ShieldType> shieldType;
		public final ConfigValue<ShieldMat> shieldMat;
		public final ConfigValue<Double> physicalDef;
		public final ConfigValue<Double> fireDef;
		public final ConfigValue<Double> lightningDef;
		
		public ShieldConfig(ConfigValue<String> registryName, ConfigValue<String> moveset, ConfigValue<String> collider,
				ConfigValue<Integer> reqStrength, ConfigValue<Integer> reqDex, ConfigValue<Integer> reqFaith,
				ConfigValue<Scaling> strengthScaling, ConfigValue<Scaling> dexScaling, ConfigValue<Scaling> faithScaling,
				ConfigValue<ShieldType> shieldType, ConfigValue<ShieldMat> shieldMat, ConfigValue<Double> physicalDef, ConfigValue<Double> fireDef, ConfigValue<Double> lightningDef)
		{
			super(registryName, moveset, collider, reqStrength, reqDex, reqFaith, strengthScaling, dexScaling, faithScaling);
			this.shieldType = shieldType;
			this.shieldMat = shieldMat;
			this.physicalDef = physicalDef;
			this.fireDef = fireDef;
			this.lightningDef = lightningDef;
		}
	}
}
