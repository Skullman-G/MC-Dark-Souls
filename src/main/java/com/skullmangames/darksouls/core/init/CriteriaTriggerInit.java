package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.advancements.criterions.BonfireLitTrigger;

import net.minecraft.advancements.CriteriaTriggers;

public class CriteriaTriggerInit
{
	public static BonfireLitTrigger BONFIRE_LIT;
	
	public static void register()
	{
		BONFIRE_LIT = CriteriaTriggers.register(new BonfireLitTrigger());
	}
}
