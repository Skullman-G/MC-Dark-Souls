package com.skullmangames.darksouls.common.capability.entity;

public interface IEquipLoaded
{
	float getEncumbrance();
	
	EquipLoadLevel getEquipLoadLevel();
	
	public enum EquipLoadLevel
	{
		NONE, LIGHT, MEDIUM, HEAVY, OVERENCUMBERED
	}
}
