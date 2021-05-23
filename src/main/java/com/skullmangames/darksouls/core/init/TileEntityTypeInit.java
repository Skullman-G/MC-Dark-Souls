package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.tiles.BonfireTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityTypeInit 
{
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, DarkSouls.MOD_ID);
	
	public static final RegistryObject<TileEntityType<BonfireTileEntity>> BONFIRE_TILE_ENTITY = TILE_ENTITIES.register("bonfire", () -> TileEntityType.Builder.of(BonfireTileEntity::new, BlockInit.BONFIRE.get()).build(null));
}
