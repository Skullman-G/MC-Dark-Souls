package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlockEntities 
{
	public static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, DarkSouls.MOD_ID);
	
	
	public static final RegistryObject<TileEntityType<BonfireBlockEntity>> BONFIRE = BLOCK_ENTITIES.register("bonfire", () -> TileEntityType.Builder.of(BonfireBlockEntity::new, ModBlocks.BONFIRE.get()).build(null));
}
