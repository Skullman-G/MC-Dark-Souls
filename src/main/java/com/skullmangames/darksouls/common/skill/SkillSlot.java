package com.skullmangames.darksouls.common.skill;

public enum SkillSlot
{
	DODGE(0), WEAPON_GIMMICK(1), WEAPON_HEAVY_ATTACK(2), WEAPON_LIGHT_ATTACK(3);
	
	int index;
	
	SkillSlot(int index)
	{
		this.index = index;
	}
	
	public int getIndex()
	{
		return this.index;
	}
}