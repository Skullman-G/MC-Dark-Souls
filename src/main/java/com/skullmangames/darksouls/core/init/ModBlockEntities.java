package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import com.skullmangames.darksouls.common.blockentity.LightSourceBlockEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlockEntities 
{
	public static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, DarkSouls.MOD_ID);
	
	
	public static final RegistryObject<TileEntityType<BonfireBlockEntity>> BONFIRE = BLOCK_ENTITIES.register("bonfire", () -> TileEntityType.Builder.of(BonfireBlockEntity::new, ModBlocks.BONFIRE.get()).build(null));
	public static final RegistryObject<TileEntityType<LightSourceBlockEntity>> LIGHT_SOURCE = BLOCK_ENTITIES.register("light_source", () -> TileEntityType.Builder.of(LightSourceBlockEntity::new, ModBlocks.LIGHT_SOURCE.get()).build(null));
}
