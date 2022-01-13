package com.skullmangames.darksouls.common.item.crafting;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.item.EstusFlaskItem;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModRecipes;
import com.skullmangames.darksouls.core.init.ModRecipeTypes;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class ReinforceEstusFlaskRecipe implements Recipe<Inventory>
{
	private final ResourceLocation id;
	
	public ReinforceEstusFlaskRecipe(ResourceLocation id)
	{
		this.id = id;
	}
	
	@Override
	public boolean matches(Inventory inventory, Level level)
	{
		return inventory.getItem(0).getItem() == ModItems.ESTUS_FLASK.get() && EstusFlaskItem.getTotalUses(inventory.getItem(0)) < 20 && inventory.getItem(1).getItem() == ModItems.ESTUS_SHARD.get();
	}

	@Override
	public ItemStack assemble(Inventory inventory)
	{
		ItemStack itemstack = inventory.getItem(0).copy();
		EstusFlaskItem.setTotalUses(itemstack, EstusFlaskItem.getTotalUses(itemstack) + 1);
		EstusFlaskItem.setUses(itemstack, EstusFlaskItem.getTotalUses(itemstack));
	    return itemstack;
	}

	@Override
	public boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_)
	{
		return p_194133_1_ * p_194133_2_ >= 2;
	}

	@Override
	public ItemStack getResultItem()
	{
		return new ItemStack(ModItems.ESTUS_FLASK.get());
	}

	@Override
	public ResourceLocation getId()
	{
		return this.id;
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return ModRecipes.REINFORCE_ESTUS_FLASK.get();
	}

	@Override
	public RecipeType<?> getType()
	{
		return ModRecipeTypes.REINFORCE_ESTUS_FLASK;
	}

	@Override
	public ItemStack getToastSymbol()
	{
		return new ItemStack(ModItems.ESTUS_FLASK.get());
	}
	
	public boolean isAdditionIngredient(ItemStack itemstack)
	{
	      return itemstack.getItem() == ModItems.ESTUS_SHARD.get();
	}
	
	public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<ReinforceEstusFlaskRecipe>
	{
	    @Override
		public ReinforceEstusFlaskRecipe fromJson(ResourceLocation id, JsonObject p_199425_2_)
	    {
	    	return new ReinforceEstusFlaskRecipe(id);
	    }

	    @Override
	    public ReinforceEstusFlaskRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf p_199426_2_)
	    {
	    	return new ReinforceEstusFlaskRecipe(id);
	    }

	    @Override
	    public void toNetwork(FriendlyByteBuf p_199427_1_, ReinforceEstusFlaskRecipe p_199427_2_)
	    {
	    	return;
	    }
	}
}
