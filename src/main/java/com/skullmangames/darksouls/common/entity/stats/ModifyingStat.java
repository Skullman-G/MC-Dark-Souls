package com.skullmangames.darksouls.common.entity.stats;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public abstract class ModifyingStat extends Stat
{
	private final UUID modifierUUID;
	private final Supplier<Attribute> attribute;
	
	public ModifyingStat(String name, String uuid, Supplier<Attribute> attribute)
	{
		super(name);
		this.modifierUUID = UUID.fromString(uuid);
		this.attribute = attribute;
	}
	
	@Override
	public void onChange(Player player, int value)
	{
		this.modifyAttributes(player, value);
		super.onChange(player, value);
	}
	
	public abstract double getModifyValue(Player player, int value);
	
	protected void modifyAttributes(Player player, int value)
	{
		AttributeInstance instance = player.getAttribute(this.attribute.get());
		instance.removeModifier(this.getModifierUUID());
		instance.addPermanentModifier(new AttributeModifier(this.getModifierUUID(), this.toString(), this.getModifyValue(player, value), Operation.ADDITION));
	}
	
	@Override
	public void init(Player player, int value)
	{
		super.init(player, value);
		this.modifyAttributes(player, value);
	}
	
	public UUID getModifierUUID()
	{
		return this.modifierUUID;
	}
}
