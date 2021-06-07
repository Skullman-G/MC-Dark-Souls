package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.effects.UndeadCurse;

import net.minecraft.potion.Effect;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EffectInit
{
	public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, DarkSouls.MOD_ID);
	
	public static final RegistryObject<UndeadCurse> UNDEAD_CURSE = EFFECTS.register("undead_curse", () -> new UndeadCurse());
}
