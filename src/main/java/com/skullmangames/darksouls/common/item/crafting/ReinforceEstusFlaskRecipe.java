package com.skullmangames.darksouls.common.item.crafting;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.item.EstusFlaskItem;
import com.skullmangames.darksouls.core.init.ItemInit;
import com.skullmangames.darksouls.core.init.RecipeSerializerInit;
import com.skullmangames.darksouls.core.init.RecipeTypeInit;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ReinforceEstusFlaskRecipe implements IRecipe<IInventory>
{
	private final ResourceLocation id;
	
	public ReinforceEstusFlaskRecipe(ResourceLocation id)
	{
		this.id = id;
	}
	
	@Override
	public boolean matches(IInventory inventory, World level)
	{
		return inventory.getItem(0).getItem() == ItemInit.ESTUS_FLASK.get() && EstusFlaskItem.getTotalUses(inventory.getItem(0)) < 20 && inventory.getItem(1).getItem() == ItemInit.ESTUS_SHARD.get();
	}

	@Override
	public ItemStack assemble(IInventory inventory)
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
		return new ItemStack(ItemInit.ESTUS_FLASK.get());
	}

	@Override
	public ResourceLocation getId()
	{
		return this.id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer()
	{
		return RecipeSerializerInit.REINFORCE_ESTUS_FLASK.get();
	}

	@Override
	public IRecipeType<?> getType()
	{
		return RecipeTypeInit.REINFORCE_ESTUS_FLASK;
	}

	@Override
	public ItemStack getToastSymbol()
	{
		return new ItemStack(ItemInit.ESTUS_FLASK.get());
	}
	
	public boolean isAdditionIngredient(ItemStack itemstack)
	{
	      return itemstack.getItem() == ItemInit.ESTUS_SHARD.get();
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
