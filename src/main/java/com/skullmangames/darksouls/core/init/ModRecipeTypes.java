package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.item.crafting.ReinforceEstusFlaskRecipe;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipeTypes
{
	public static final RecipeType<ReinforceEstusFlaskRecipe> REINFORCE_ESTUS_FLASK = register("reinforce_estus_flask");
	
	private static <T extends Recipe<?>> RecipeType<T> register(String name)
	{
		return Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(DarkSouls.MOD_ID, name), new RecipeType<T>()
		{
			public String toString() { return name; }
		});
	}
	
	public static void call() {}
}
