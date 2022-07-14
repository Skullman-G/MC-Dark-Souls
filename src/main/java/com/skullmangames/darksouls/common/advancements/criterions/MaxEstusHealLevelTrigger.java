package com.skullmangames.darksouls.common.advancements.criterions;

import com.google.gson.JsonObject;

import net.minecraft.util.ResourceLocation;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate.AndPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;

public class MaxEstusHealLevelTrigger extends AbstractCriterionTrigger<MaxEstusHealLevelTrigger.Instance>
{
	private static final ResourceLocation ID = new ResourceLocation("get_max_estus_heal_level");
	
	@Override
	public ResourceLocation getId()
	{
		return ID;
	}
	
	@Override
	protected MaxEstusHealLevelTrigger.Instance createInstance(JsonObject arg0, AndPredicate arg1, ConditionArrayParser arg2)
	{
		return new MaxEstusHealLevelTrigger.Instance(ID, arg1);
	}
	
	public void trigger(ServerPlayerEntity player)
	{
	    this.trigger(player, (p_226524_1_) ->
	    {
	       return p_226524_1_.matches();
	    });
	}
	
	public static class Instance extends CriterionInstance
	{
		public Instance(ResourceLocation resourcelocation, AndPredicate predicate)
		{
			super(resourcelocation, predicate);
		}
		
		public static MaxEstusHealLevelTrigger.Instance createInstance(ResourceLocation resourcelocation, AndPredicate predicate)
		{
			return new MaxEstusHealLevelTrigger.Instance(resourcelocation, predicate);
	    }
		
		public boolean matches()
		{
			return true;
	    }
		
		@Override
		public JsonObject serializeToJson(ConditionArraySerializer serializer)
		{
			JsonObject jsonobject = super.serializeToJson(serializer);
			return jsonobject;
		}
	}
}
