package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.gui.screens.ReinforceEstusFlaskScreen;
import com.skullmangames.darksouls.common.inventory.container.ReinforceEstusFlaskContainer;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers
{
	public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, DarkSouls.MOD_ID);
	
	public static final RegistryObject<ContainerType<ReinforceEstusFlaskContainer>> REINFORCE_ESTUS_FLASK = CONTAINERS.register("reinforce_estus_flask", () -> new ContainerType<>(ReinforceEstusFlaskContainer::new));
	
	public static void registerScreens()
	{
		ScreenManager.register(REINFORCE_ESTUS_FLASK.get(), ReinforceEstusFlaskScreen::new);
	}
}
