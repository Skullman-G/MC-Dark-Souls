package com.skullmangames.darksouls.common.item;

import net.minecraft.util.SoundEvent;

public interface IHaveDarkSoulsUseAction
{
	public DarkSoulsUseAction getDarkSoulsUseAnimation();
	
	public SoundEvent getUseSound();
}
