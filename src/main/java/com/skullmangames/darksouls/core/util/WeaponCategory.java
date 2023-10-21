package com.skullmangames.darksouls.core.util;

public enum WeaponCategory
{
	NONE_WEAON(false, false, false), BOW(false, false, false), GREATBOW(false, false, false), CROSSBOW(false, false, false),
	STAFF(false, false, true), FLAME(false, false, false), TALISMAN(false, false, false), SACRED_CHIME(false, false, false),
	STRAIGHT_SWORD(true, false, false), GREATSWORD(true, true, false), ULTRA_GREATSWORD(true, true, false),
	HAMMER(true, false, false), GREAT_HAMMER(true, true, false),
	DAGGER(true, false, false),
	CURVED_SWORD(true, false, false), KATANA(true, false, false), CURVED_GREATSWORD(true, true, false),
	THRUSTING_SWORD(true, false, false),
	AXE(true, false, false), GREATAXE(true, true, false),
	FIST(true, false, false), CLAW(true, false, false),
	SPEAR(true, false, true), HALBERD(true, false, true), SCYTHE(true, false, true),
	WHIP(true, false, false),
	SMALL_SHIELD(true, false, false), MEDIUM_SHIELD(true, false, false), GREATSHIELD(true, false, false);
	
	private final boolean melee;
	private final boolean heavy;
	private final boolean isLong;
	
	private WeaponCategory(boolean melee, boolean heavy, boolean isLong)
	{
		this.melee = melee;
		this.heavy = heavy;
		this.isLong = isLong;
	}
	
	public boolean isMelee()
	{
		return this.melee;
	}
	
	public boolean isHeavy()
	{
		return this.heavy;
	}
	
	public boolean isLong()
	{
		return this.isLong;
	}
	
	@Override
	public String toString()
	{
		return super.toString().toLowerCase();
	}
	
	public static WeaponCategory fromString(String id)
	{
		for (WeaponCategory category : WeaponCategory.values())
		{
			if (category.toString().equals(id)) return category;
		}
		return null;
	}
}
