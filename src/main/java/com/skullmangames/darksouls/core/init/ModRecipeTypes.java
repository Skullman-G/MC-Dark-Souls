package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.item.crafting.ReinforceEstusFlaskRecipe;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class ModRecipeTypes
{
	public static final IRecipeType<ReinforceEstusFlaskRecipe> REINFORCE_ESTUS_FLASK = register("reinforce_estus_flask");
	
	private static <T extends IRecipe<?>> IRecipeType<T> register(String name)
	{
		return Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(DarkSouls.MOD_ID, name), new IRecipeType<T>()
		{
			public String toString() { return name; }
		});
	}
	
	public static void call() {}
}
