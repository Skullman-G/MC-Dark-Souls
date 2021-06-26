package com.skullmangames.darksouls.core.init;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.containers.ReinforceEstusFlaskContainer;
import com.skullmangames.darksouls.common.containers.SmithingTableContainerOverride;

public class ContainerTypeInit
{
	public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, DarkSouls.MOD_ID);
	
	public static final RegistryObject<ContainerType<ReinforceEstusFlaskContainer>> REINFORCE_ESTUS_FLASK = CONTAINERS.register("reinforce_estus_flask", () -> new ContainerType<>(ReinforceEstusFlaskContainer::new));
			
			
	// Vanilla Overrides
	public static final DeferredRegister<ContainerType<?>> VANILLA_CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, "minecraft");
	
	public static final RegistryObject<ContainerType<SmithingTableContainerOverride>> SMITHING = VANILLA_CONTAINERS.register("smithing", () -> new ContainerType<>(SmithingTableContainerOverride::new));
}
