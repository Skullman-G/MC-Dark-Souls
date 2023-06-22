package com.skullmangames.darksouls.common.animation;

public enum LivingMotion
{
	IDLE(0), ANGRY(1), FLOATING(2), BENDING(3), WALKING(4), RUNNING(5), SWIMMING(6), FLYING(7), SNEAKING(8), KNEELING(9),
	FALL(10), MOUNTED(11), CHASING(13), SPELLCASTING(14), JUMPING(15), CELEBRATE(16), ADMIRE(17),
	NONE(18), AIMING(19), SHIELD_BLOCKING(20), RELOADING(21), SHOOTING(22), THROW_JAVELIN(23), EATING(24), DRINKING(25), CONSUME_SOUL(26), HOLDING_WEAPON(27, true),
	POSING(28), DIGGING(29), INACTION(30), WEAPON_BLOCKING(31);
	
	final int id;
	final boolean addon;
	
	LivingMotion(int id)
	{
		this(id, false);
	}
	
	LivingMotion(int id, boolean addon)
	{
		this.id = id;
		this.addon = addon;
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public boolean isAddon()
	{
		return this.addon;
	}
}