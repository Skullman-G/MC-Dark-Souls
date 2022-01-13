package com.skullmangames.darksouls.common.entity.stats;

import java.util.UUID;

import net.minecraft.world.entity.player.Player;

public class ModifyingStat extends Stat
{
	private final UUID MODIFIER_UUID;
	
	public ModifyingStat(String name, String uuid)
	{
		super(name);
		this.MODIFIER_UUID = UUID.fromString(uuid);
	}
	
	@Override
	public void onChange(Player player, int value)
	{
		this.modifyAttributes(player, value);
		super.onChange(player, value);
	}
	
	public void modifyAttributes(Player player, int value) {}
	
	@Override
	public void init(Player player, int value)
	{
		super.init(player, value);
		this.modifyAttributes(player, value);
	}
	
	public UUID getModifierUUID()
	{
		return this.MODIFIER_UUID;
	}
}
