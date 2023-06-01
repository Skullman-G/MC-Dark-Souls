package com.skullmangames.darksouls.common.entity.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.base.Function;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.capability.item.WeaponCap;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;

public class Stats
{
	public static final int STANDARD_LEVEL = 10;
	public static final int MAX_LEVEL = 99;
	public static final Map<String, Stat> STATS = new LinkedHashMap<>();
	
	public static final Stat VIGOR = register(new Stat("vigor", "35031b47-45fa-401b-92dc-12b6d258e553", () -> Attributes.MAX_HEALTH)
	{
		@Override
		public void onChange(Player player, int value)
		{
			super.onChange(player, value);
			player.setHealth(player.getMaxHealth());
		}

		@Override
		public double getModifyValue(Player player, Attribute attribute, int value)
		{
			return (int)(1500 * Math.pow(-1.09D, -value) + 1500);
		}
	});
	public static final Stat ENDURANCE = register(new Stat("endurance", "8bbd5d2d-0188-41be-a673-cfca6cd8da8c", ModAttributes.MAX_STAMINA)
	{
		@Override
		public void onChange(Player player, int value)
		{
			super.onChange(player, value);
			PlayerCap<?> cap = (PlayerCap<?>)player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (cap != null) cap.setStamina(cap.getMaxStamina());
		}

		@Override
		public double getModifyValue(Player player, Attribute attribute, int value)
		{
			return (int)Math.min(2 * value, 80);
		}
	});
	public static final Stat VITALITY = register(new Stat("vitality", "1858d77f-b8fd-46a7-a9e1-373e5a2dac0a", ModAttributes.MAX_EQUIP_LOAD)
	{
		@Override
		public double getModifyValue(Player player, Attribute attribute, int value)
		{
			return -0.019D * (value - STANDARD_LEVEL) * (value - MAX_LEVEL * 2 + STANDARD_LEVEL);
		}
	});
	public static final Stat ATTUNEMENT = register(new Stat("attunement", "25c989d7-9585-4f9d-be78-fc1e3aba4fc6", ModAttributes.MAX_FOCUS_POINTS, ModAttributes.ATTUNEMENT_SLOTS)
	{
		@Override
		public void onChange(Player player, int value)
		{
			super.onChange(player, value);
			PlayerCap<?> playerCap = (PlayerCap<?>) player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (playerCap != null)
			{
				playerCap.getAttunements().updateSize();
				playerCap.setFP(playerCap.getMaxFP());
			}
		}

		@Override
		public void init(Player player, int value)
		{
			super.init(player, value);
			PlayerCap<?> playerCap = (PlayerCap<?>) player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (playerCap != null)
			{
				playerCap.getAttunements().updateSize();
				playerCap.setFP(playerCap.getMaxFP());
			}
		}

		@Override
		public double getModifyValue(Player player, Attribute attribute, int value)
		{
			if (attribute == ModAttributes.ATTUNEMENT_SLOTS.get())
			{
				return (int) (-0.001262468 * (value - STANDARD_LEVEL) * (value - MAX_LEVEL * 2 + STANDARD_LEVEL));
			}
			else return -0.0076 * (value - STANDARD_LEVEL) * (value - MAX_LEVEL * 2 + STANDARD_LEVEL);
		}
	});
	public static final Stat STRENGTH = register(new ScalingStat("strength", "c16888c7-e522-4260-8492-0a2da90482b8", () -> Attributes.ATTACK_DAMAGE,
			ModAttributes.STANDARD_PROTECTION, ModAttributes.STRIKE_PROTECTION, ModAttributes.SLASH_PROTECTION, ModAttributes.THRUST_PROTECTION,
			ModAttributes.MAGIC_PROTECTION, ModAttributes.FIRE_PROTECTION, ModAttributes.LIGHTNING_PROTECTION, ModAttributes.DARK_PROTECTION,
			ModAttributes.HOLY_PROTECTION, ModAttributes.FIRE_DAMAGE)
	{
		@Override
		public double getModifyValue(Player player, Attribute attribute, int value)
		{
			if (attribute == Attributes.ATTACK_DAMAGE)
			{
				WeaponCap weapon = ModCapabilities.getWeaponCap(player.getMainHandItem());
				double percentage = 0D;
				if (value <= 40) percentage = value * (75D / 40D);
				else if (value <= 60) percentage = value * (85D / 60D);
				else if (value <= 99) percentage = value * (100D / 99D);
				return weapon.getScaling(this).getPercentage() * percentage;
			}
			if (attribute == ModAttributes.FIRE_DAMAGE.get())
			{
				WeaponCap weapon = ModCapabilities.getWeaponCap(player.getMainHandItem());
				double percentage = 0D;
				if (value <= 40) percentage = value * (55D / 40D);
				else if (value <= 60) percentage = value * (65D / 60D);
				else if (value <= 99) percentage = value * (75D / 99D);
				return weapon.getScaling(this).getPercentage() * percentage;
			}
			if (attribute == ModAttributes.STANDARD_PROTECTION.get() || attribute == ModAttributes.STRIKE_PROTECTION.get()
					|| attribute == ModAttributes.SLASH_PROTECTION.get() || attribute == ModAttributes.THRUST_PROTECTION.get())
			{
				return 0.73D * value;
			}
			if (attribute == ModAttributes.MAGIC_PROTECTION.get() || attribute == ModAttributes.DARK_PROTECTION.get()
					|| attribute == ModAttributes.LIGHTNING_PROTECTION.get()
					|| attribute == ModAttributes.HOLY_PROTECTION.get())
			{
				return 0.4D * value;
			}
			if (attribute == ModAttributes.FIRE_PROTECTION.get())
			{
				return 1.1D * value;
			}
			return 0D;
		}
	});
	public static final Stat DEXTERITY = register(new ScalingStat("dexterity", "2e316050-52aa-446f-8b05-0abefbbb6cb2", () -> Attributes.ATTACK_DAMAGE,
			ModAttributes.FIRE_DAMAGE, ModAttributes.LIGHTNING_DAMAGE)
	{
		@Override
		public double getModifyValue(Player player, Attribute attribute, int value)
		{
			if (attribute == Attributes.ATTACK_DAMAGE)
			{
				WeaponCap weapon = ModCapabilities.getWeaponCap(player.getMainHandItem());
				double percentage = 0D;
				if (value <= 40) percentage = value * (75D / 40D);
				else if (value <= 60) percentage = value * (85D / 60D);
				else if (value <= 99) percentage = value * (100D / 99D);
				return weapon.getScaling(this).getPercentage() * percentage;
			}
			if (attribute == ModAttributes.FIRE_DAMAGE.get())
			{
				WeaponCap weapon = ModCapabilities.getWeaponCap(player.getMainHandItem());
				double percentage = 0D;
				if (value <= 40) percentage = value * (5D / 40D);
				else if (value <= 60) percentage = value * (15D / 60D);
				else if (value <= 99) percentage = value * (25D / 99D);
				return weapon.getScaling(this).getPercentage() * percentage;
			}
			if (attribute == ModAttributes.LIGHTNING_DAMAGE.get())
			{
				WeaponCap weapon = ModCapabilities.getWeaponCap(player.getMainHandItem());
				double percentage = 0D;
				if (value <= 40) percentage = value * (25D / 40D);
				else if (value <= 60) percentage = value * (35D / 60D);
				else if (value <= 99) percentage = value * (45D / 99D);
				return weapon.getScaling(this).getPercentage() * percentage;
			}
			return 0D;
		}
	});
	public static final Stat INTELLIGENCE = register(new ScalingStat("intelligence", "39d35885-78e0-4be3-b72f-7c3b4876ad8d", ModAttributes.MAGIC_DAMAGE,
			ModAttributes.STANDARD_PROTECTION, ModAttributes.STRIKE_PROTECTION, ModAttributes.SLASH_PROTECTION, ModAttributes.THRUST_PROTECTION,
			ModAttributes.FIRE_PROTECTION, ModAttributes.DARK_PROTECTION, ModAttributes.LIGHTNING_PROTECTION, ModAttributes.HOLY_PROTECTION,
			ModAttributes.MAGIC_PROTECTION)
	{
		@Override
		public double getModifyValue(Player player, Attribute attribute, int value)
		{
			if (attribute == ModAttributes.MAGIC_DAMAGE.get())
			{
				WeaponCap weapon = ModCapabilities.getWeaponCap(player.getMainHandItem());
				double percentage = 0D;
				if (value <= 40) percentage = value * (75D / 40D);
				else if (value <= 60) percentage = value * (85D / 60D);
				else if (value <= 99) percentage = value * (100D / 99D);
				return weapon.getScaling(this).getPercentage() * percentage;
			}
			if (attribute == ModAttributes.MAGIC_PROTECTION.get())
			{
				return value * 1.1D;
			}
			if (attribute == ModAttributes.STANDARD_PROTECTION.get() || attribute == ModAttributes.STRIKE_PROTECTION.get()
					|| attribute == ModAttributes.SLASH_PROTECTION.get() || attribute == ModAttributes.THRUST_PROTECTION.get()
					|| attribute == ModAttributes.FIRE_PROTECTION.get() || attribute == ModAttributes.DARK_PROTECTION.get()
					|| attribute == ModAttributes.LIGHTNING_PROTECTION.get() || attribute == ModAttributes.HOLY_PROTECTION.get())
			{
				return 0.4D * value;
			}
			return 0D;
		}
	});
	public static final Stat FAITH = register(new ScalingStat("faith", "2939c660-37cc-4e0e-9cca-2b08d011f472", ModAttributes.HOLY_DAMAGE, ModAttributes.LIGHTNING_DAMAGE,
			ModAttributes.STANDARD_PROTECTION, ModAttributes.STRIKE_PROTECTION, ModAttributes.SLASH_PROTECTION, ModAttributes.THRUST_PROTECTION,
			ModAttributes.FIRE_PROTECTION, ModAttributes.DARK_PROTECTION, ModAttributes.LIGHTNING_PROTECTION, ModAttributes.HOLY_PROTECTION,
			ModAttributes.MAGIC_PROTECTION)
	{
		@Override
		public double getModifyValue(Player player, Attribute attribute, int value)
		{
			if (attribute == ModAttributes.HOLY_DAMAGE.get())
			{
				WeaponCap weapon = ModCapabilities.getWeaponCap(player.getMainHandItem());
				double percentage = 0D;
				if (value <= 40) percentage = value * (75D / 40D);
				else if (value <= 60) percentage = value * (85D / 60D);
				else if (value <= 99) percentage = value * (100D / 99D);
				return weapon.getScaling(this).getPercentage() * percentage;
			}
			if (attribute == ModAttributes.LIGHTNING_DAMAGE.get())
			{
				WeaponCap weapon = ModCapabilities.getWeaponCap(player.getMainHandItem());
				double percentage = 0D;
				if (value <= 40) percentage = value * (25D / 40D);
				else if (value <= 60) percentage = value * (35D / 60D);
				else if (value <= 99) percentage = value * (45D / 99D);
				return weapon.getScaling(this).getPercentage() * percentage;
			}
			if (attribute == ModAttributes.HOLY_PROTECTION.get())
			{
				return value * 1.1D;
			}
			if (attribute == ModAttributes.STANDARD_PROTECTION.get() || attribute == ModAttributes.STRIKE_PROTECTION.get()
					|| attribute == ModAttributes.SLASH_PROTECTION.get() || attribute == ModAttributes.THRUST_PROTECTION.get()
					|| attribute == ModAttributes.FIRE_PROTECTION.get() || attribute == ModAttributes.DARK_PROTECTION.get()
					|| attribute == ModAttributes.LIGHTNING_PROTECTION.get() || attribute == ModAttributes.MAGIC_PROTECTION.get())
			{
				return 0.4D * value;
			}
			return 0D;
		}
	});
	
	private static <T extends Stat> T register(T stat)
	{
		STATS.put(stat.toString(), stat);
		return stat;
	}
	
	public static final Stat[] SCALING_STATS = new Stat[]
	{
			STRENGTH, DEXTERITY, INTELLIGENCE, FAITH
	};
	
	public static double getDamageMultiplier(Player player, Attribute attribute, Function<Stat, Integer> value)
	{
		double mul = 0D;
		for (Stat stat : getForAttribute(attribute))
		{
			mul *= stat.getModifyValue(player, attribute, value.apply(stat));
		}
		return mul;
	}
	
	public static Stat[] getForAttribute(Attribute attribute)
	{
		List<Stat> list = new ArrayList<>();
		for (Stat stat : STATS.values())
		{
			for (Supplier<Attribute> a : stat.getAttributes())
			{
				if (attribute == a.get()) list.add(stat);
			}
		}
		Stat[] array = new Stat[list.size()];
		return list.toArray(array);
	}
	
	public static int getCost(int level)
	{
		return (int)(0.02F * Math.pow(level, 3) + 3.06F * Math.pow(level, 2) + 105.6F * level);
	}
	
	private final Map<String, Integer> statValues = new HashMap<>();
	
	public int getStatValue(Stat stat)
	{
		return this.getStatValue(stat.getName());
	}
	
	public int getStatValue(String name)
	{
		return this.statValues.get(name);
	}
	
	public void setStatValue(Player player, Stat stat, int value)
	{
		this.setStatValue(player, stat.getName(), value);
	}
	
	public void setStatValue(Player player, String name, int value)
	{
		this.statValues.put(name, value);
		STATS.get(name).onChange(player, value);
	}
	
	public void loadStats(Player player, CompoundTag nbt)
	{
		for (String name : STATS.keySet())
		{
			int value = Math.max(STANDARD_LEVEL, Math.min(nbt.getInt(name), 99));
			this.initStatValue(player, name, value);
		}
	}
	
	public void initStatValue(Player player, String name, int value)
	{
		this.statValues.put(name, value);
		STATS.get(name).init(player, value);
	}
	
	public void saveStats(CompoundTag nbt)
	{
		STATS.forEach((name, stat) ->
		{
			nbt.putInt(stat.getName(), this.statValues.getOrDefault(name, STANDARD_LEVEL));
		});
	}
	
	public int getLevel()
	{
		int level = 1;
		for (Stat stat : STATS.values())
		{
			level += this.getStatValue(stat) - STANDARD_LEVEL;
		}
		
		return level;
	}
}
