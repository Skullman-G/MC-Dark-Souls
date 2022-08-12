package com.skullmangames.darksouls.common.advancements.criterions;

import com.google.gson.JsonObject;

import net.minecraft.util.ResourceLocation;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate.AndPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;

public class BonfireLitTrigger extends AbstractCriterionTrigger<BonfireLitTrigger.Instance>
{
	private static final ResourceLocation ID = new ResourceLocation("player_lit_bonfire");
	
	@Override
	public ResourceLocation getId()
	{
		return ID;
	}
	
	@Override
	protected BonfireLitTrigger.Instance createInstance(JsonObject arg0, AndPredicate arg1, ConditionArrayParser arg2)
	{
		return new BonfireLitTrigger.Instance(ID, arg1, true);
	}
	
	public void trigger(ServerPlayerEntity player, boolean islit)
	{
	    this.trigger(player, (p_226524_1_) ->
	    {
	       return p_226524_1_.matches(islit);
	    });
	}
	
	public static class Instance extends CriterionInstance
	{
		private final boolean lit;
		
		public Instance(ResourceLocation resourcelocation, AndPredicate predicate, boolean islit)
		{
			super(resourcelocation, predicate);
			this.lit = islit;
		}
		
		public static BonfireLitTrigger.Instance createInstance(ResourceLocation resourcelocation, AndPredicate predicate, boolean islit)
		{
			return new BonfireLitTrigger.Instance(resourcelocation, predicate, islit);
	    }
		
		public boolean matches(boolean islit)
		{
			return this.lit == islit;
	    }
		
		@Override
		public JsonObject serializeToJson(ConditionArraySerializer serializer)
		{
			JsonObject jsonobject = super.serializeToJson(serializer);
			return jsonobject;
		}
	}
}
