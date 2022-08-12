package com.skullmangames.darksouls.common.advancements.criterions;

import com.google.gson.JsonObject;

import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class LevelUpTrigger extends SimpleCriterionTrigger<LevelUpTrigger.Instance>
{
	private static final ResourceLocation ID = new ResourceLocation("player_level_up");
	
	@Override
	public ResourceLocation getId()
	{
		return ID;
	}
	
	@Override
	protected LevelUpTrigger.Instance createInstance(JsonObject arg0, EntityPredicate.Composite arg1, DeserializationContext arg2)
	{
		return new LevelUpTrigger.Instance(ID, arg1, true);
	}
	
	public void trigger(ServerPlayer player, boolean bool)
	{
	    this.trigger(player, (instance) ->
	    {
	       return instance.matches(bool);
	    });
	}
	
	public static class Instance extends AbstractCriterionTriggerInstance
	{
		private final boolean bool;
		
		public Instance(ResourceLocation resourcelocation, EntityPredicate.Composite predicate, boolean bool)
		{
			super(resourcelocation, predicate);
			this.bool = bool;
		}
		
		public static LevelUpTrigger.Instance createInstance(ResourceLocation resourcelocation, EntityPredicate.Composite predicate, boolean bool)
		{
			return new LevelUpTrigger.Instance(resourcelocation, predicate, bool);
	    }
		
		public boolean matches(boolean bool)
		{
			return this.bool == bool;
	    }
		
		@Override
		public JsonObject serializeToJson(SerializationContext serializer)
		{
			JsonObject jsonobject = super.serializeToJson(serializer);
			return jsonobject;
		}
	}
}
