package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.item.crafting.ReinforceEstusFlaskRecipe;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RecipeSerializerInit
{
	public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, DarkSouls.MOD_ID);
	
	public static final RegistryObject<IRecipeSerializer<ReinforceEstusFlaskRecipe>> REINFORCE_ESTUS_FLASK = RECIPE_SERIALIZERS.register("reinforce_estus_flask", () -> new ReinforceEstusFlaskRecipe.Serializer());
}
