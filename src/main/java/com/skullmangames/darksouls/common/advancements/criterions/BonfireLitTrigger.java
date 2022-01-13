package com.skullmangames.darksouls.common.advancements.criterions;

import com.google.gson.JsonObject;

import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class BonfireLitTrigger extends SimpleCriterionTrigger<BonfireLitTrigger.Instance>
{
	private static final ResourceLocation ID = new ResourceLocation("player_lit_bonfire");
	
	@Override
	public ResourceLocation getId()
	{
		return ID;
	}
	
	@Override
	protected BonfireLitTrigger.Instance createInstance(JsonObject arg0, EntityPredicate.Composite arg1, DeserializationContext arg2)
	{
		return new BonfireLitTrigger.Instance(ID, arg1, true);
	}
	
	public void trigger(ServerPlayer player, boolean islit)
	{
	    this.trigger(player, (p_226524_1_) ->
	    {
	       return p_226524_1_.matches(islit);
	    });
	}
	
	public static class Instance extends AbstractCriterionTriggerInstance
	{
		private final boolean lit;
		
		public Instance(ResourceLocation resourcelocation, EntityPredicate.Composite predicate, boolean islit)
		{
			super(resourcelocation, predicate);
			this.lit = islit;
		}
		
		public static BonfireLitTrigger.Instance createInstance(ResourceLocation resourcelocation, EntityPredicate.Composite predicate, boolean islit)
		{
			return new BonfireLitTrigger.Instance(resourcelocation, predicate, islit);
	    }
		
		public boolean matches(boolean islit)
		{
			return this.lit == islit;
	    }
		
		@Override
		public JsonObject serializeToJson(SerializationContext serializer)
		{
			JsonObject jsonobject = super.serializeToJson(serializer);
			return jsonobject;
		}
	}
}
