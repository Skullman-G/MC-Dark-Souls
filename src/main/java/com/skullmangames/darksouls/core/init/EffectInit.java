package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.InstantEffect;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EffectInit
{
	public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, DarkSouls.MOD_ID);
	
	public static final RegistryObject<Effect> INSTANT_HEAL = EFFECTS.register("instant_heal", () -> new InstantEffect(EffectType.BENEFICIAL, 16262179));
}
