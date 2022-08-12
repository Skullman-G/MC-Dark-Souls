package com.skullmangames.darksouls.common.item.crafting;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.item.EstusFlaskItem;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModRecipes;
import com.skullmangames.darksouls.core.init.ModRecipeTypes;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;

public class ReinforceEstusFlaskRecipe implements IRecipe<Inventory>
{
	private final ResourceLocation id;
	
	public ReinforceEstusFlaskRecipe(ResourceLocation id)
	{
		this.id = id;
	}
	
	@Override
	public boolean matches(Inventory inventory, World level)
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
	public IRecipeSerializer<?> getSerializer()
	{
		return ModRecipes.REINFORCE_ESTUS_FLASK.get();
	}

	@Override
	public IRecipeType<?> getType()
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
	
	public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ReinforceEstusFlaskRecipe>
	{
	    @Override
		public ReinforceEstusFlaskRecipe fromJson(ResourceLocation id, JsonObject p_199425_2_)
	    {
	    	return new ReinforceEstusFlaskRecipe(id);
	    }

	    @Override
	    public ReinforceEstusFlaskRecipe fromNetwork(ResourceLocation id, PacketBuffer p_199426_2_)
	    {
	    	return new ReinforceEstusFlaskRecipe(id);
	    }

	    @Override
	    public void toNetwork(PacketBuffer p_199427_1_, ReinforceEstusFlaskRecipe p_199427_2_)
	    {
	    	return;
	    }
	}
}
