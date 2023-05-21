package com.skullmangames.darksouls.common.capability.item;

import com.google.common.collect.ImmutableMap;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.item.SpellItem;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSCastSpell;
import net.minecraft.world.item.Item;

public class SpellcasterWeaponCap extends WeaponCap
{
	public SpellcasterWeaponCap(Item item, WeaponCategory category, ImmutableMap<Stat, Integer> statRequirements, ImmutableMap<Stat, Scaling> statScaling)
	{
		super(item, category, statRequirements, statScaling);
	}
	
	@Override
	public void performAttack(AttackType type, LocalPlayerCap playerCap)
	{
		Item item = playerCap.getAttunements().getSelected().getItem();
		if (item instanceof SpellItem)
		{
			ModNetworkManager.sendToServer(new CTSCastSpell((SpellItem)item));
		}
	}

	@Override
	public int getStaminaDamage()
	{
		return 0;
	}

	@Override
	public float getDamage()
	{
		return 0;
	}

	@Override
	public int getStaminaUsage(AttackType type, boolean twohanded)
	{
		return 0;
	}
}
