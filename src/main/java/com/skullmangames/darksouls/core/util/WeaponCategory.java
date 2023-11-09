package com.skullmangames.darksouls.core.util;

import com.skullmangames.darksouls.common.capability.item.Shield.Deflection;

public enum WeaponCategory
{
	NONE_WEAON(false, false, false, false), BOW(false, false, false, false), GREATBOW(false, false, false, false), CROSSBOW(false, false, false, false),
	STAFF(false, false, true, false), FLAME(false, false, false, false), TALISMAN(false, false, false, false), SACRED_CHIME(false, false, false, false),
	STRAIGHT_SWORD(true, false, false, false), GREATSWORD(true, true, false, false), ULTRA_GREATSWORD(true, true, false, false),
	HAMMER(true, false, false, false), GREAT_HAMMER(true, true, false, false),
	DAGGER(true, false, false, false),
	CURVED_SWORD(true, false, false, false), KATANA(true, false, false, false), CURVED_GREATSWORD(true, true, false, false),
	THRUSTING_SWORD(true, false, false, false),
	AXE(true, false, false, false), GREATAXE(true, true, false, false),
	FIST(true, false, false, false), CLAW(true, false, false, false),
	SPEAR(true, false, true, false), HALBERD(true, false, true, false), SCYTHE(true, false, true, false),
	WHIP(true, false, false, false),
	SMALL_SHIELD(Deflection.LIGHT, true, false, false, true), MEDIUM_SHIELD(Deflection.MEDIUM, true, false, false, true), GREATSHIELD(Deflection.HEAVY, true, false, false, true);
	
	private final boolean melee;
	private final boolean heavy;
	private final boolean isLong;
	private final boolean isShield;
	
	private final Deflection deflection;
	
	private WeaponCategory(boolean melee, boolean heavy, boolean isLong, boolean isShield)
	{
		this(Deflection.NONE, melee, heavy, isLong, isShield);
	}
	
	private WeaponCategory(Deflection deflection, boolean melee, boolean heavy, boolean isLong, boolean isShield)
	{
		this.deflection = deflection;
		this.melee = melee;
		this.heavy = heavy;
		this.isLong = isLong;
		this.isShield = isShield;
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
	
	public boolean isShield()
	{
		return this.isShield;
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
	
	public Deflection getDeflection()
	{
		return this.deflection;
	}
}
