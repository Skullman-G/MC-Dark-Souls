package com.skullmangames.darksouls.common.entity.stats;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.common.capability.entity.EntityData;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.core.init.AttributeInit;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;

public class Stats
{
	private static List<Stat> stats = new ArrayList<Stat>();
	
	public static final Stat VIGOR = register(new ModifyingStat("vigor", "35031b47-45fa-401b-92dc-12b6d258e553")
			{
				@Override		
				public void onChange(LivingEntity livingentity, boolean isinit, int value)
				{
					ModifiableAttributeInstance maxhealth = livingentity.getAttribute(Attributes.MAX_HEALTH);
					if (maxhealth.getModifier(this.getModifierUUID()) != null) maxhealth.removeModifier(this.getModifierUUID());
					AttributeModifier modifier = new AttributeModifier(this.getModifierUUID(), "change_with_vigor", value - 1, Operation.ADDITION);
					maxhealth.addPermanentModifier(modifier);
					if (!isinit) livingentity.setHealth(livingentity.getMaxHealth());
				}
			});
	
	public static final Stat ENDURANCE = register(new ModifyingStat("endurance", "8bbd5d2d-0188-41be-a673-cfca6cd8da8c")
			{
				@Override		
				public void onChange(LivingEntity livingentity, boolean isinit, int value)
				{
					ModifiableAttributeInstance maxStamina = livingentity.getAttribute(AttributeInit.MAX_STAMINA.get());
					if (maxStamina.getModifier(this.getModifierUUID()) != null) maxStamina.removeModifier(this.getModifierUUID());
					AttributeModifier modifier = new AttributeModifier(this.getModifierUUID(), "change_with_endurance", value - 1, Operation.ADDITION);
					maxStamina.addPermanentModifier(modifier);
					if (!isinit)
					{
						EntityData<?> cap = livingentity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
						if (cap instanceof PlayerData<?>)
						{
							PlayerData<?> playerCap = (PlayerData<?>)cap;
							playerCap.setStamina(playerCap.getMaxStamina());
						}
					}
				}
			});
	
	public static final Stat STRENGTH = register(new Stat("strength"));
	
	private static Stat register(Stat stat)
	{
		stats.add(stat);
		return stat;
	}
	
	public static List<Stat> getStats()
	{
		return stats;
	}
	
	public static int getLevel(LivingEntity livingentity)
	{
		int level = 1;
		for (Stat stat : getStats())
		{
			level += stat.getValue(livingentity) - 1;
		}
		
		return level;
	}
}
