package com.skullmangames.darksouls.common.item;

import net.minecraft.sounds.SoundEvent;

public interface IHaveDarkSoulsUseAction
{
	public DarkSoulsUseAction getDarkSoulsUseAnimation();
	
	public SoundEvent getUseSound();
}
