package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.item.crafting.ReinforceEstusFlaskRecipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes
{
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, DarkSouls.MOD_ID);
	
	public static final RegistryObject<RecipeSerializer<ReinforceEstusFlaskRecipe>> REINFORCE_ESTUS_FLASK = RECIPE_SERIALIZERS.register("reinforce_estus_flask", () -> new ReinforceEstusFlaskRecipe.Serializer());
}
