package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.common.advancements.criterions.BonfireLitTrigger;
import com.skullmangames.darksouls.common.advancements.criterions.LevelUpTrigger;
import com.skullmangames.darksouls.common.advancements.criterions.MaxEstusHealLevelTrigger;
import com.skullmangames.darksouls.common.advancements.criterions.ObtainBiggestEstusFlaskTrigger;

import net.minecraft.advancements.CriteriaTriggers;

public class ModCriteriaTriggers
{
	public static BonfireLitTrigger BONFIRE_LIT;
	public static ObtainBiggestEstusFlaskTrigger OBTAIN_BIGGEST_ESTUS_FLASK;
	public static MaxEstusHealLevelTrigger MAX_ESTUS_HEAL_LEVEL_TRIGGER;
	public static LevelUpTrigger LEVEL_UP;
	
	public static void register()
	{
		BONFIRE_LIT = CriteriaTriggers.register(new BonfireLitTrigger());
		OBTAIN_BIGGEST_ESTUS_FLASK = CriteriaTriggers.register(new ObtainBiggestEstusFlaskTrigger());
		MAX_ESTUS_HEAL_LEVEL_TRIGGER = CriteriaTriggers.register(new MaxEstusHealLevelTrigger());
		LEVEL_UP = CriteriaTriggers.register(new LevelUpTrigger());
	}
}
