package com.skullmangames.darksouls.common.advancements.criterions;

import com.google.gson.JsonObject;

import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class MaxEstusHealLevelTrigger extends SimpleCriterionTrigger<MaxEstusHealLevelTrigger.Instance>
{
	private static final ResourceLocation ID = new ResourceLocation("get_max_estus_heal_level");
	
	@Override
	public ResourceLocation getId()
	{
		return ID;
	}
	
	@Override
	protected MaxEstusHealLevelTrigger.Instance createInstance(JsonObject arg0, EntityPredicate.Composite arg1, DeserializationContext arg2)
	{
		return new MaxEstusHealLevelTrigger.Instance(ID, arg1);
	}
	
	public void trigger(ServerPlayer player)
	{
	    this.trigger(player, (p_226524_1_) ->
	    {
	       return p_226524_1_.matches();
	    });
	}
	
	public static class Instance extends AbstractCriterionTriggerInstance
	{
		public Instance(ResourceLocation resourcelocation, EntityPredicate.Composite predicate)
		{
			super(resourcelocation, predicate);
		}
		
		public static MaxEstusHealLevelTrigger.Instance createInstance(ResourceLocation resourcelocation, EntityPredicate.Composite predicate)
		{
			return new MaxEstusHealLevelTrigger.Instance(resourcelocation, predicate);
	    }
		
		public boolean matches()
		{
			return true;
	    }
		
		@Override
		public JsonObject serializeToJson(SerializationContext serializer)
		{
			JsonObject jsonobject = super.serializeToJson(serializer);
			return jsonobject;
		}
	}
}
