package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities 
{
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, DarkSouls.MOD_ID);
	
	
	public static final RegistryObject<BlockEntityType<BonfireBlockEntity>> BONFIRE = BLOCK_ENTITIES.register("bonfire", () -> BlockEntityType.Builder.of(BonfireBlockEntity::new, ModBlocks.BONFIRE.get()).build(null));
}
