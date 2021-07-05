package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.advancements.criterions.BonfireLitTrigger;
import com.skullmangames.darksouls.advancements.criterions.ObtainBiggestEstusFlaskTrigger;

import net.minecraft.advancements.CriteriaTriggers;

public class CriteriaTriggerInit
{
	public static final BonfireLitTrigger BONFIRE_LIT = CriteriaTriggers.register(new BonfireLitTrigger());
	public static final ObtainBiggestEstusFlaskTrigger OBTAIN_BIGGEST_ESTUS_FLASK = CriteriaTriggers.register(new ObtainBiggestEstusFlaskTrigger());
}
