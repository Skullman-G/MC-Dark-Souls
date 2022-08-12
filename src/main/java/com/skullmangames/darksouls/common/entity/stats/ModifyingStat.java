package com.skullmangames.darksouls.common.entity.stats;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;

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
	public void onChange(PlayerEntity player, int value)
	{
		this.modifyAttributes(player, value);
		super.onChange(player, value);
	}
	
	public abstract double getModifyValue(PlayerEntity player, int value);
	
	protected void modifyAttributes(PlayerEntity player, int value)
	{
		ModifiableAttributeInstance instance = player.getAttribute(this.attribute.get());
		instance.removeModifier(this.getModifierUUID());
		instance.addPermanentModifier(new AttributeModifier(this.getModifierUUID(), this.toString(), this.getModifyValue(player, value), Operation.ADDITION));
	}
	
	@Override
	public void init(PlayerEntity player, int value)
	{
		super.init(player, value);
		this.modifyAttributes(player, value);
	}
	
	public UUID getModifierUUID()
	{
		return this.modifierUUID;
	}
}
