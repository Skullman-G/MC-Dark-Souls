package com.skullmangames.darksouls.common.advancements.criterions;

import com.google.gson.JsonObject;

import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate.AndPredicate;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;

public class ObtainBiggestEstusFlaskTrigger extends AbstractCriterionTrigger<ObtainBiggestEstusFlaskTrigger.Instance>
{
	private static final ResourceLocation ID = new ResourceLocation("obtain_biggest_estus_flask");
	
	@Override
	public ResourceLocation getId()
	{
		return ID;
	}
	
	@Override
	protected ObtainBiggestEstusFlaskTrigger.Instance createInstance(JsonObject arg0, AndPredicate arg1, ConditionArrayParser arg2)
	{
		return new ObtainBiggestEstusFlaskTrigger.Instance(ID, arg1);
	}
	
	public void trigger(ServerPlayerEntity player, int totaluses)
	{
	    this.trigger(player, (p_226524_1_) ->
	    {
	       return p_226524_1_.matches(totaluses);
	    });
	}
	
	public static class Instance extends CriterionInstance
	{
		private final int totalUses = 20;
		
		public Instance(ResourceLocation resourcelocation, AndPredicate predicate)
		{
			super(resourcelocation, predicate);
		}
		
		public static ObtainBiggestEstusFlaskTrigger.Instance createInstance(ResourceLocation resourcelocation, AndPredicate predicate)
		{
			return new ObtainBiggestEstusFlaskTrigger.Instance(resourcelocation, predicate);
	    }
		
		public boolean matches(int totaluses)
		{
			return this.totalUses == totaluses;
	    }
		
		@Override
		public JsonObject serializeToJson(ConditionArraySerializer serializer)
		{
			JsonObject jsonobject = super.serializeToJson(serializer);
			return jsonobject;
		}
	}
}
