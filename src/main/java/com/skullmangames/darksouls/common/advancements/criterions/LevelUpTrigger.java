package com.skullmangames.darksouls.common.advancements.criterions;

import com.google.gson.JsonObject;

import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate.AndPredicate;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.ServerPlayerEntity;

public class LevelUpTrigger extends AbstractCriterionTrigger<LevelUpTrigger.Instance>
{
	private static final ResourceLocation ID = new ResourceLocation("player_level_up");
	
	@Override
	public ResourceLocation getId()
	{
		return ID;
	}
	
	@Override
	protected LevelUpTrigger.Instance createInstance(JsonObject arg0, AndPredicate arg1, ConditionArrayParser arg2)
	{
		return new LevelUpTrigger.Instance(ID, arg1, true);
	}
	
	public void trigger(ServerPlayerEntity player, boolean bool)
	{
	    this.trigger(player, (instance) ->
	    {
	       return instance.matches(bool);
	    });
	}
	
	public static class Instance extends CriterionInstance
	{
		private final boolean bool;
		
		public Instance(ResourceLocation resourcelocation, AndPredicate predicate, boolean bool)
		{
			super(resourcelocation, predicate);
			this.bool = bool;
		}
		
		public static LevelUpTrigger.Instance createInstance(ResourceLocation resourcelocation, AndPredicate predicate, boolean bool)
		{
			return new LevelUpTrigger.Instance(resourcelocation, predicate, bool);
	    }
		
		public boolean matches(boolean bool)
		{
			return this.bool == bool;
	    }
		
		@Override
		public JsonObject serializeToJson(ConditionArraySerializer serializer)
		{
			JsonObject jsonobject = super.serializeToJson(serializer);
			return jsonobject;
		}
	}
}
