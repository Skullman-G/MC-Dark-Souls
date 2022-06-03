package com.skullmangames.darksouls.common.capability.entity;

public interface EquipLoaded
{
	float getEncumbrance();
	
	EquipLoadLevel getEquipLoadLevel();
	
	public enum EquipLoadLevel
	{
		NONE, LIGHT, MEDIUM, HEAVY, OVERENCUMBERED
	}
}
