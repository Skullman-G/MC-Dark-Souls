package com.skullmangames.darksouls.common.entity.stats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public abstract class Stat
{
	private final String name;
	private final UUID modifierUUID;
	private final AttributeList attributes;
	
	public Stat(String name, String uuid, AttributeList attributes)
	{
		this.name = "stat."+DarkSouls.MOD_ID+"."+name;
		this.modifierUUID = UUID.fromString(uuid);
		this.attributes = attributes;
		this.attributes.close();
	}
	
	public AttributeList getAttributes()
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
	
	public void onChange(PlayerCap<?> playerCap, int value)
	{
		this.modifyAttributes(playerCap, value);
	}
	
	public void init(PlayerCap<?> playerCap, int value) {}
	
	public double getModifyValue(PlayerCap<?> playerCap, Attribute attribute, int value)
	{
		return 0D;
	}
	
	public Operation getOperation(Attribute attribute)
	{
		return Operation.ADDITION;
	}
	
	protected void modifyAttributes(PlayerCap<?> playerCap, int value)
	{
		for (Attribute attribute : this.attributes)
		{
			this.modifyAttribute(playerCap, attribute, value);
		}
	}
	
	protected void modifyAttribute(PlayerCap<?> playerCap, Attribute attribute, int value)
	{
		AttributeInstance instance = playerCap.getOriginalEntity().getAttribute(attribute);
		instance.removeModifier(this.getModifierUUID());
		instance.addPermanentModifier(
				new AttributeModifier(this.getModifierUUID(), this.toString(),
						this.getModifyValue(playerCap, attribute, value), this.getOperation(attribute)));
	}
	
	public UUID getModifierUUID()
	{
		return this.modifierUUID;
	}
	
	public static class AttributeList implements Iterable<Attribute>
	{
		private final List<Supplier<Attribute>> list = new ArrayList<>();
		/**
		 * Use getAttributeList instead
		 */
		private List<Attribute> attributeList;
		private boolean closed;
		
		@SafeVarargs
		private AttributeList(Supplier<Attribute>... attributes)
		{
			for (Supplier<Attribute> supplier : attributes)
			{
				this.list.add(supplier);
			}
		}
		
		@SafeVarargs
		public static AttributeList of(Supplier<Attribute>... attributes)
		{
			return new AttributeList(attributes);
		}
		
		public AttributeList addAll(Collection<Supplier<Attribute>> attributes)
		{
			if (!this.closed) this.list.addAll(attributes);
			return this;
		}
		
		private void close()
		{
			this.closed = true;
		}
		
		public boolean contains(Attribute attribute)
		{
			return this.getAttributeList().contains(attribute);
		}
		
		private List<Attribute> getAttributeList()
		{
			if (this.attributeList == null)
			{
				this.attributeList = new ArrayList<>();
				for (Supplier<Attribute> supplier : this.list)
				{
					this.attributeList.add(supplier.get());
				}
			}
			return this.attributeList;
		}
		
		@Override
		public Iterator<Attribute> iterator()
		{
			return this.getAttributeList().iterator();
		}
	}
}
