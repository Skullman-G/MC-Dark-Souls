package com.skullmangames.darksouls.common.entity.stats;

import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;

public class ModifyingStat extends Stat
{
	private final UUID MODIFIER_UUID;
	
	public ModifyingStat(String name, String uuid)
	{
		super(name);
		this.MODIFIER_UUID = UUID.fromString(uuid);
	}
	
	@Override
	public void onChange(PlayerEntity player, int value)
	{
		this.modifyAttributes(player, value);
		super.onChange(player, value);
	}
	
	public void modifyAttributes(PlayerEntity player, int value) {}
	
	@Override
	public void init(PlayerEntity player, int value)
	{
		super.init(player, value);
		this.modifyAttributes(player, value);
	}
	
	public UUID getModifierUUID()
	{
		return this.MODIFIER_UUID;
	}
}
