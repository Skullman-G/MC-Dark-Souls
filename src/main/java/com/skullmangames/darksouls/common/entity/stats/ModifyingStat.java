package com.skullmangames.darksouls.common.entity.stats;

import java.util.UUID;

public class ModifyingStat extends Stat
{
	private final UUID MODIFIER_UUID;
	
	public ModifyingStat(String name, String uuid)
	{
		super(name);
		this.MODIFIER_UUID = UUID.fromString(uuid);
	}
	
	public UUID getModifierUUID()
	{
		return this.MODIFIER_UUID;
	}
}
