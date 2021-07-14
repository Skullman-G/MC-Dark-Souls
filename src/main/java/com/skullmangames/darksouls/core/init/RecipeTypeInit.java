package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.items.crafting.ReinforceEstusFlaskRecipe;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class RecipeTypeInit
{
	public static final IRecipeType<ReinforceEstusFlaskRecipe> REINFORCE_ESTUS_FLASK = registerType(new ResourceLocation(DarkSouls.MOD_ID, "reinforce_estus_flask"));
	
	private static class RecipeType<T extends IRecipe<?>> implements IRecipeType<T>
	{
		@Override
		public String toString()
		{
			return Registry.RECIPE_TYPE.getKey(this).toString();
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends IRecipeType<?>> T registerType(ResourceLocation recipeTypeId)
	{
		return (T) Registry.register(Registry.RECIPE_TYPE, recipeTypeId, new RecipeType<>());
	}
}
