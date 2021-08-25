package com.skullmangames.darksouls.common.capability.item;

import net.minecraft.item.Item;

public class ShieldCapability extends CapabilityItem
{
	private float physicalDefense;
	private final ShieldType shieldType;
	
	public ShieldCapability(Item item)
	{
		super(item, WeaponCategory.SHIELD);
		this.physicalDefense = 0.93F;
		this.shieldType = ShieldType.NORMAL;
	}
	
	public float getPhysicalDefense()
	{
		return this.physicalDefense;
	}
	
	public int getDeflectionLevel()
	{
		return this.shieldType.getDeflection().getLevel();
	}
	
	public enum Deflection
	{
		NONE(0), LIGHT(1), MEDIUM(2), HEAVY(3);
		
		private final int level;
		
		private Deflection(int level)
		{
			this.level = level;
		}
		
		public int getLevel()
		{
			return this.level;
		}
	}
	
	public enum ShieldType
	{
		SMALL(Deflection.LIGHT),
		NORMAL(Deflection.MEDIUM),
		GREAT(Deflection.HEAVY),
		UNIQUE(Deflection.LIGHT),
		CRACKED_ROUND_SHIELD(Deflection.NONE),
		IRON_ROUND_SHIELD(Deflection.HEAVY);
		
		
		private final Deflection deflection;
		
		private ShieldType(Deflection deflection)
		{
			this.deflection = deflection;
		}
		
		public Deflection getDeflection()
		{
			return this.deflection;
		}
	}
}
