package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.advancements.criterions.BonfireLitTrigger;
import com.skullmangames.darksouls.advancements.criterions.MaxEstusHealLevelTrigger;
import com.skullmangames.darksouls.advancements.criterions.ObtainBiggestEstusFlaskTrigger;

import net.minecraft.advancements.CriteriaTriggers;

public class CriteriaTriggerInit
{
	public static BonfireLitTrigger BONFIRE_LIT;
	public static ObtainBiggestEstusFlaskTrigger OBTAIN_BIGGEST_ESTUS_FLASK;
	public static MaxEstusHealLevelTrigger MAX_ESTUS_HEAL_LEVEL_TRIGGER;
	
	public static void register()
	{
		BONFIRE_LIT = CriteriaTriggers.register(new BonfireLitTrigger());
		OBTAIN_BIGGEST_ESTUS_FLASK = CriteriaTriggers.register(new ObtainBiggestEstusFlaskTrigger());
		MAX_ESTUS_HEAL_LEVEL_TRIGGER = CriteriaTriggers.register(new MaxEstusHealLevelTrigger());
	}
}
