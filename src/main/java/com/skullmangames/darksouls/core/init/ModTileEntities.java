package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.tileentity.BonfireTileEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModTileEntities 
{
	public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, DarkSouls.MOD_ID);
	
	
	public static final RegistryObject<BlockEntityType<BonfireTileEntity>> BONFIRE = TILE_ENTITIES.register("bonfire", () -> BlockEntityType.Builder.of(BonfireTileEntity::new, ModBlocks.BONFIRE.get()).build(null));
}
