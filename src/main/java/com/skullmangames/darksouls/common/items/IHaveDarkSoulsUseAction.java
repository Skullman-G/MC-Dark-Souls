package com.skullmangames.darksouls.common.items;

import net.minecraft.util.SoundEvent;

public interface IHaveDarkSoulsUseAction
{
	public DarkSoulsUseAction getDarkSoulsUseAnimation();
	
	public SoundEvent getUseSound();
}
