package com.skullmangames.darksouls.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.skullmangames.darksouls.common.capability.item.IShield.ShieldType;
import com.skullmangames.darksouls.common.capability.item.ShieldCap.ShieldMat;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.Scaling;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.WeaponCategory;

import com.electronwill.nightconfig.core.AbstractCommentedConfig;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class CapabilityConfig
{
	public static final List<WeaponConfig> WEAPON_CONFIGS = new ArrayList<>();
	public static final List<ShieldConfig> SHIELD_CONFIGS = new ArrayList<>();
	
	public static void init(ForgeConfigSpec.Builder config, Map<String, Object> values)
	{
		String weaponKey = "weapon_config";
		if (values.get(weaponKey) != null)
		{
			List<Map.Entry<String, Object>> entries = new LinkedList<>(((AbstractCommentedConfig)values.get(weaponKey)).valueMap().entrySet());
		    Collections.sort(entries, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
			for (Map.Entry<String, Object> entry : entries)
			{
				ConfigValue<String> registryName = config.define(weaponKey+"."+entry.getKey()+".registry_name", "null");
				ConfigValue<WeaponCategory> category = config.defineEnum(weaponKey+"."+entry.getKey()+".category", WeaponCategory.NONE_WEAON);
				ConfigValue<Integer> requiredStrength = config.define(weaponKey+"."+entry.getKey()+".requiredStrength", 0);
				ConfigValue<Integer> requiredDex = config.define(weaponKey+"."+entry.getKey()+".requiredDex", 0);;
				ConfigValue<Scaling> strengthScaling = config.defineEnum(weaponKey+"."+entry.getKey()+".strengthScaling", Scaling.NONE);
				ConfigValue<Scaling> dexScaling = config.defineEnum(weaponKey+"."+entry.getKey()+".dexScaling", Scaling.NONE);
				if (!entry.getKey().equals("sample")) WEAPON_CONFIGS.add(new WeaponConfig(registryName, category, requiredStrength, requiredDex, strengthScaling, dexScaling));
			}
		}
		String shieldKey = "shield_config";
		if (values.get(shieldKey) != null)
		{
			List<Map.Entry<String, Object>> entries = new LinkedList<>(((AbstractCommentedConfig)values.get(shieldKey)).valueMap().entrySet());
		    Collections.sort(entries, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
			for (Map.Entry<String, Object> entry : entries)
			{
				ConfigValue<String> registryName = config.define(shieldKey+"."+entry.getKey()+".registry_name", "null");
				ConfigValue<WeaponCategory> category = config.defineEnum(shieldKey+"."+entry.getKey()+".category", WeaponCategory.NONE_WEAON);
				ConfigValue<Integer> requiredStrength = config.define(shieldKey+"."+entry.getKey()+".requiredStrength", 0);
				ConfigValue<Integer> requiredDex = config.define(shieldKey+"."+entry.getKey()+".requiredDex", 0);;
				ConfigValue<Scaling> strengthScaling = config.defineEnum(shieldKey+"."+entry.getKey()+".strengthScaling", Scaling.NONE);
				ConfigValue<Scaling> dexScaling = config.defineEnum(shieldKey+"."+entry.getKey()+".dexScaling", Scaling.NONE);
				
				ConfigValue<ShieldType> shieldType = config.defineEnum(shieldKey+"."+entry.getKey()+".shield_type", ShieldType.NONE);
				ConfigValue<ShieldMat> shieldMat = config.defineEnum(shieldKey+"."+entry.getKey()+"shield_material", ShieldMat.WOOD);
				ConfigValue<Double> physicalDefense = config.defineInRange(shieldKey+"."+entry.getKey()+".physicalDefense", 0.0F, 0.0F, 1.0F);
				if (!entry.getKey().equals("sample_shield")) SHIELD_CONFIGS.add(new ShieldConfig(registryName, category,
						requiredStrength, requiredDex, strengthScaling, dexScaling, shieldType, shieldMat, physicalDefense));
			}
		}
	}
	
	public static class WeaponConfig
	{
		public final ConfigValue<String> registryName;
		public final ConfigValue<WeaponCategory> category;
		public final ConfigValue<Integer> requiredStrength;
		public final ConfigValue<Integer> requiredDex;
		public final ConfigValue<Scaling> strengthScaling;
		public final ConfigValue<Scaling> dexScaling;
		
		public WeaponConfig(ConfigValue<String> registryName, ConfigValue<WeaponCategory> category,
				ConfigValue<Integer> requiredStrength, ConfigValue<Integer> requiredDex,
				ConfigValue<Scaling> strengthScaling, ConfigValue<Scaling> dexScaling)
		{
			this.registryName = registryName;
			this.category = category;
			this.requiredStrength = requiredStrength;
			this.requiredDex = requiredDex;
			this.strengthScaling = strengthScaling;
			this.dexScaling = dexScaling;
		}
	}
	
	public static class ShieldConfig extends WeaponConfig
	{
		public final ConfigValue<ShieldType> shieldType;
		public final ConfigValue<ShieldMat> shieldMat;
		public final ConfigValue<Double> physicalDefense;
		
		public ShieldConfig(ConfigValue<String> registryName, ConfigValue<WeaponCategory> category,
				ConfigValue<Integer> requiredStrength, ConfigValue<Integer> requiredDex,
				ConfigValue<Scaling> strengthScaling, ConfigValue<Scaling> dexScaling,
				ConfigValue<ShieldType> shieldType, ConfigValue<ShieldMat> shieldMat, ConfigValue<Double> physicalDefense)
		{
			super(registryName, category, requiredStrength, requiredDex, strengthScaling, dexScaling);
			this.shieldType = shieldType;
			this.shieldMat = shieldMat;
			this.physicalDefense = physicalDefense;
		}
	}
}
