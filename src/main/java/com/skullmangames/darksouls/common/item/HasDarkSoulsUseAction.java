package com.skullmangames.darksouls.common.item;

import net.minecraft.sounds.SoundEvent;

public interface HasDarkSoulsUseAction
{
	public DarkSoulsUseAction getDarkSoulsUseAnimation();
	
	public SoundEvent getUseSound();
}
