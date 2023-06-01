package com.skullmangames.darksouls.common.entity.stats;

import java.util.UUID;
import java.util.function.Supplier;

import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public abstract class Stat
{
	private final String name;
	private final UUID modifierUUID;
	private final Supplier<Attribute>[] attributes;
	
	@SafeVarargs
	public Stat(String name, String uuid, Supplier<Attribute>... attributes)
	{
		this.name = "stat."+DarkSouls.MOD_ID+"."+name;
		this.modifierUUID = UUID.fromString(uuid);
		this.attributes = attributes;
	}
	
	public Supplier<Attribute>[] getAttributes()
	{
		return this.attributes;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	@Override
	public String toString()
	{
		return this.name;
	}
	
	public void onChange(Player player, int value)
	{
		this.modifyAttributes(player, value);
	}
	
	public abstract double getModifyValue(Player player, Attribute attribute, int value);
	
	public Operation getOperation(Attribute attribute)
	{
		return Operation.ADDITION;
	}
	
	protected void modifyAttributes(Player player, int value)
	{
		for (Supplier<Attribute> attribute : this.attributes)
		{
			AttributeInstance instance = player.getAttribute(attribute.get());
			instance.removeModifier(this.getModifierUUID());
			instance.addPermanentModifier(new AttributeModifier(this.getModifierUUID(), this.toString(), this.getModifyValue(player, attribute.get(), value), Operation.ADDITION));
		}
	}
	
	public void init(Player player, int value)
	{
		this.modifyAttributes(player, value);
	}
	
	public UUID getModifierUUID()
	{
		return this.modifierUUID;
	}
}
