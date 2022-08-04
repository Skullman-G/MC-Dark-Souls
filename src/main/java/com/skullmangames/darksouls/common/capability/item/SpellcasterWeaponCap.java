package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.common.item.SpellItem;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSCastSpell;
import net.minecraft.world.item.Item;

public class SpellcasterWeaponCap extends WeaponCap
{
	public SpellcasterWeaponCap(Item item, WeaponCategory category, int requiredStrength, int requiredDex,
			Scaling strengthScaling, Scaling dexScaling, float poiseDamage)
	{
		super(item, category, requiredStrength, requiredDex, strengthScaling, dexScaling, poiseDamage);
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
	public float getStaminaDamage()
	{
		return 0;
	}

	@Override
	public float getDamage()
	{
		return 0;
	}
}
