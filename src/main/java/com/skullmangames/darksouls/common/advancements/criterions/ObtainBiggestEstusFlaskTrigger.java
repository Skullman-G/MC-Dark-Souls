package com.skullmangames.darksouls.common.advancements.criterions;

import com.google.gson.JsonObject;

import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ObtainBiggestEstusFlaskTrigger extends SimpleCriterionTrigger<ObtainBiggestEstusFlaskTrigger.Instance>
{
	private static final ResourceLocation ID = new ResourceLocation("obtain_biggest_estus_flask");
	
	@Override
	public ResourceLocation getId()
	{
		return ID;
	}
	
	@Override
	protected ObtainBiggestEstusFlaskTrigger.Instance createInstance(JsonObject arg0, EntityPredicate.Composite arg1, DeserializationContext arg2)
	{
		return new ObtainBiggestEstusFlaskTrigger.Instance(ID, arg1);
	}
	
	public void trigger(ServerPlayer player, int totaluses)
	{
	    this.trigger(player, (p_226524_1_) ->
	    {
	       return p_226524_1_.matches(totaluses);
	    });
	}
	
	public static class Instance extends AbstractCriterionTriggerInstance
	{
		private final int totalUses = 20;
		
		public Instance(ResourceLocation resourcelocation, EntityPredicate.Composite predicate)
		{
			super(resourcelocation, predicate);
		}
		
		public static ObtainBiggestEstusFlaskTrigger.Instance createInstance(ResourceLocation resourcelocation, EntityPredicate.Composite predicate)
		{
			return new ObtainBiggestEstusFlaskTrigger.Instance(resourcelocation, predicate);
	    }
		
		public boolean matches(int totaluses)
		{
			return this.totalUses == totaluses;
	    }
		
		@Override
		public JsonObject serializeToJson(SerializationContext serializer)
		{
			JsonObject jsonobject = super.serializeToJson(serializer);
			return jsonobject;
		}
	}
}
