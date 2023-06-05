package com.skullmangames.darksouls.common.entity.stats;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import com.google.common.base.Function;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.entity.stats.Stat.AttributeList;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class Stats
{
	public static final int STANDARD_LEVEL = 10;
	public static final int MAX_LEVEL = 99;
	public static final Map<String, Stat> STATS = new LinkedHashMap<>();
	
	public static final Stat VIGOR = register(new Stat("vigor", "35031b47-45fa-401b-92dc-12b6d258e553", AttributeList.of(() -> Attributes.MAX_HEALTH))
	{
		@Override
		public void onChange(Player player, int value)
		{
			super.onChange(player, value);
			if (player.getHealth() > player.getMaxHealth()) player.setHealth(player.getMaxHealth());
		}

		@Override
		public double getModifyValue(Player player, Attribute attribute, int value)
		{
			return (int)(1500 * Math.pow(-1.09D, -value) + 1500);
		}
	});
	public static final Stat ENDURANCE = register(new Stat("endurance", "8bbd5d2d-0188-41be-a673-cfca6cd8da8c", AttributeList.of(ModAttributes.MAX_STAMINA))
	{
		@Override
		public void onChange(Player player, int value)
		{
			super.onChange(player, value);
			PlayerCap<?> cap = (PlayerCap<?>)player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (cap != null && cap.getStamina() > cap.getMaxStamina()) cap.setStamina(cap.getMaxStamina());
		}

		@Override
		public double getModifyValue(Player player, Attribute attribute, int value)
		{
			return Math.min(value * (80D / 40D), 80D);
		}
	});
	public static final Stat VITALITY = register(new Stat("vitality", "1858d77f-b8fd-46a7-a9e1-373e5a2dac0a", AttributeList.of(ModAttributes.MAX_EQUIP_LOAD))
	{
		@Override
		public double getModifyValue(Player player, Attribute attribute, int value)
		{
			return -0.019D * (value - STANDARD_LEVEL) * (value - MAX_LEVEL * 2 + STANDARD_LEVEL);
		}
	});
	public static final Stat ATTUNEMENT = register(new Stat("attunement", "25c989d7-9585-4f9d-be78-fc1e3aba4fc6", AttributeList.of(
			ModAttributes.MAX_FOCUS_POINTS, ModAttributes.ATTUNEMENT_SLOTS))
	{
		@Override
		public void onChange(Player player, int value)
		{
			super.onChange(player, value);
			PlayerCap<?> playerCap = (PlayerCap<?>) player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (playerCap != null)
			{
				playerCap.getAttunements().updateSize();
				if (playerCap.getFP() > playerCap.getMaxFP()) playerCap.setFP(playerCap.getMaxFP());
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
	
	public static final Stat STRENGTH = register(new ScalingStat("strength", "c16888c7-e522-4260-8492-0a2da90482b8", AttributeList.of(() -> Attributes.ATTACK_DAMAGE,
			ModAttributes.FIRE_DAMAGE).addAll(ModAttributes.protectionAttributes()))
	{
		@Override
		public double getModifyValue(Player player, Attribute attribute, int value)
		{
			if (attribute == Attributes.ATTACK_DAMAGE)
			{
				return this.scalingPercentage(player, value, 0.75D, 0.85D, 1.00D);
			}
			if (attribute == ModAttributes.FIRE_DAMAGE.get())
			{
				return this.scalingPercentage(player, value, 0.55D, 0.65D, 0.75D);
			}
			if (attribute == ModAttributes.STANDARD_PROTECTION.get() || attribute == ModAttributes.STRIKE_PROTECTION.get()
					|| attribute == ModAttributes.SLASH_PROTECTION.get() || attribute == ModAttributes.THRUST_PROTECTION.get())
			{
				return 0.73D * value;
			}
			if (attribute == ModAttributes.FIRE_PROTECTION.get())
			{
				return 1.1D * value;
			}
			if (ModAttributes.isProtectionAttribute(attribute))
			{
				return 0.4D * value;
			}
			return super.getModifyValue(player, attribute, value);
		}
	});
	public static final Stat DEXTERITY = register(new ScalingStat("dexterity", "2e316050-52aa-446f-8b05-0abefbbb6cb2", AttributeList.of(() -> Attributes.ATTACK_DAMAGE,
			ModAttributes.FIRE_DAMAGE, ModAttributes.LIGHTNING_DAMAGE))
	{
		@Override
		public double getModifyValue(Player player, Attribute attribute, int value)
		{
			if (attribute == Attributes.ATTACK_DAMAGE)
			{
				return this.scalingPercentage(player, value, 0.75D, 0.85D, 1.00D);
			}
			if (attribute == ModAttributes.FIRE_DAMAGE.get())
			{
				return this.scalingPercentage(player, value, 0.05D, 0.15D, 0.25D);
			}
			if (attribute == ModAttributes.LIGHTNING_DAMAGE.get())
			{
				return this.scalingPercentage(player, value, 0.25D, 0.35D, 0.45D);
			}
			return super.getModifyValue(player, attribute, value);
		}
	});
	public static final Stat INTELLIGENCE = register(new ScalingStat("intelligence", "39d35885-78e0-4be3-b72f-7c3b4876ad8d", AttributeList.of(
			ModAttributes.MAGIC_DAMAGE).addAll(ModAttributes.protectionAttributes()))
	{
		@Override
		public double getModifyValue(Player player, Attribute attribute, int value)
		{
			if (attribute == ModAttributes.MAGIC_DAMAGE.get())
			{
				return this.scalingPercentage(player, value, 0.75D, 0.85D, 1.00D);
			}
			if (attribute == ModAttributes.MAGIC_PROTECTION.get())
			{
				return value * 1.1D;
			}
			if (ModAttributes.isProtectionAttribute(attribute))
			{
				return 0.4D * value;
			}
			return super.getModifyValue(player, attribute, value);
		}
	});
	public static final Stat FAITH = register(new ScalingStat("faith", "2939c660-37cc-4e0e-9cca-2b08d011f472", AttributeList.of(
			ModAttributes.HOLY_DAMAGE, ModAttributes.LIGHTNING_DAMAGE).addAll(ModAttributes.protectionAttributes()))
	{
		@Override
		public double getModifyValue(Player player, Attribute attribute, int value)
		{
			if (attribute == ModAttributes.HOLY_DAMAGE.get())
			{
				return this.scalingPercentage(player, value, 0.75D, 0.85D, 1.00D);
			}
			if (attribute == ModAttributes.LIGHTNING_DAMAGE.get())
			{
				return this.scalingPercentage(player, value, 0.25D, 0.35D, 0.45D);
			}
			if (attribute == ModAttributes.HOLY_PROTECTION.get())
			{
				return value * 1.1D;
			}
			if (ModAttributes.isProtectionAttribute(attribute))
			{
				return 0.4D * value;
			}
			return super.getModifyValue(player, attribute, value);
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
			mul += stat.getModifyValue(player, attribute, value.apply(stat));
		}
		return mul;
	}
	
	public static Stat[] getForAttribute(Attribute attribute)
	{
		List<Stat> list = new ArrayList<>();
		for (Stat stat : STATS.values())
		{
			if (stat.getAttributes().contains(attribute)) list.add(stat);
		}
		Stat[] array = new Stat[list.size()];
		return list.toArray(array);
	}
	
	public static void changeWeaponScalingAttributes(Player player)
	{
		PlayerCap<?> playerCap = (PlayerCap<?>) player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		if (playerCap != null)
		{
			List<Supplier<Attribute>> dmgAttributes = ModAttributes.damageAttributes();
			for (int i = 0; i < dmgAttributes.size(); i++)
			{
				Attribute attribute = dmgAttributes.get(i).get();
				UUID uuid = ModAttributes.WEAPON_SCALING_MODIFIER_UUIDS[i];
				double mul = Stats.getDamageMultiplier(playerCap.getOriginalEntity(), attribute, (stat) -> playerCap.getStatValue(stat));
				AttributeInstance instance = playerCap.getOriginalEntity().getAttribute(attribute);
				if (instance.getModifier(uuid) != null) instance.removeModifier(uuid);
				instance.addTransientModifier(new AttributeModifier(uuid, "weapon scaling", mul, Operation.MULTIPLY_TOTAL));
			}
		}
	}
	
	public static int getCost(int level)
	{
		return (int)(0.02F * Math.pow(level, 3) + 3.06F * Math.pow(level, 2) + 105.6F * level);
	}
}
