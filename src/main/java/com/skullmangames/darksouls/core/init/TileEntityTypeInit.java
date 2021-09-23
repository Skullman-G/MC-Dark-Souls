package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.tileentity.BonfireTileEntity;
import com.skullmangames.darksouls.common.tileentity.LockableDoorTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityTypeInit 
{
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, DarkSouls.MOD_ID);
	
	
	public static final RegistryObject<TileEntityType<BonfireTileEntity>> BONFIRE = TILE_ENTITIES.register("bonfire", () -> TileEntityType.Builder.of(BonfireTileEntity::new, BlockInit.BONFIRE.get()).build(null));
	
	public static final RegistryObject<TileEntityType<LockableDoorTileEntity>> LOCKABLE_BLOCK = TILE_ENTITIES.register("lockable_door", () -> TileEntityType.Builder.of(LockableDoorTileEntity::new, 
			BlockInit.BIG_ACACIA_DOOR.get(),
			BlockInit.BIG_BIRCH_DOOR.get(),
			BlockInit.BIG_CRIMSON_DOOR.get(),
			BlockInit.BIG_DARK_OAK_DOOR.get(),
			BlockInit.BIG_JUNGLE_DOOR.get(),
			BlockInit.BIG_OAK_DOOR.get(),
			BlockInit.BIG_SPRUCE_DOOR.get(),
			BlockInit.BIG_WARPED_DOOR.get(),
			
			BlockInit.ACACIA_DOOR.get(),
			BlockInit.BIRCH_DOOR.get(),
			BlockInit.CRIMSON_DOOR.get(),
			BlockInit.DARK_OAK_DOOR.get(),
			BlockInit.JUNGLE_DOOR.get(),
			BlockInit.OAK_DOOR.get(),
			BlockInit.SPRUCE_DOOR.get(),
			BlockInit.WARPED_DOOR.get()).build(null));
}
