package com.skullmangames.darksouls.common.entity;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.common.entity.Covenant.Reward;
import com.skullmangames.darksouls.core.init.ModItems;

public class Covenants
{
	public static final List<Covenant> COVENANTS = new ArrayList<Covenant>();
	
	public static final Covenant NONE = register(new Covenant("none"));
	public static final Covenant WARRIORS_OF_SUNLIGHT = register(new Covenant("warriors_of_sunlight",
			new Reward(0, ModItems.SUNLIGHT_MEDAL.get(), ModItems.MIRACLE_LIGHTNING_SPEAR.get().getDefaultInstance()),
			new Reward(30, ModItems.SUNLIGHT_MEDAL.get(), ModItems.MIRACLE_GREAT_LIGHTNING_SPEAR.get().getDefaultInstance())));
	
	private static Covenant register(Covenant covenant)
	{
		COVENANTS.add(covenant);
		return covenant;
	}
}
