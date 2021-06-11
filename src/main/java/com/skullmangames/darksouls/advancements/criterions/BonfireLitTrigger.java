package com.skullmangames.darksouls.advancements.criterions;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.blocks.Bonfire;
import com.skullmangames.darksouls.core.init.BlockInit;

import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.block.BlockState;
import net.minecraft.advancements.criterion.EntityPredicate.AndPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

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
		BlockState blockstate = BlockInit.BONFIRE.get().defaultBlockState().setValue(Bonfire.LIT, Boolean.valueOf(true));
		return new BonfireLitTrigger.Instance(ID, arg1, blockstate);
	}
	
	public void trigger(ServerPlayerEntity player, BlockState blockstate)
	{
	    this.trigger(player, (p_226524_1_) ->
	    {
	       return p_226524_1_.matches(blockstate);
	    });
	}
	
	public static class Instance extends CriterionInstance
	{
		private final BlockState blockState;
		
		public Instance(ResourceLocation resourcelocation, AndPredicate predicate, BlockState blockstate)
		{
			super(resourcelocation, predicate);
			this.blockState = blockstate;
		}
		
		public static BonfireLitTrigger.Instance createInstance(ResourceLocation resourcelocation, AndPredicate predicate, BlockState blockstate)
		{
			return new BonfireLitTrigger.Instance(resourcelocation, predicate, blockstate);
	    }
		
		public boolean matches(BlockState blockstate)
		{
			return this.blockState == blockstate;
	    }
		
		@Override
		public JsonObject serializeToJson(ConditionArraySerializer serializer)
		{
			JsonObject jsonobject = super.serializeToJson(serializer);
			return jsonobject;
		}
	}
}
