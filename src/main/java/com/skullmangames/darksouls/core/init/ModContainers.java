package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.gui.screens.AttunementScreen;
import com.skullmangames.darksouls.client.gui.screens.ReinforceEstusFlaskScreen;
import com.skullmangames.darksouls.client.gui.screens.SoulMerchantScreen;
import com.skullmangames.darksouls.common.inventory.AttunementsMenu;
import com.skullmangames.darksouls.common.inventory.ReinforceEstusFlaskMenu;
import com.skullmangames.darksouls.common.inventory.SoulMerchantMenu;

import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;

public class ModContainers
{
	public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, DarkSouls.MOD_ID);
	
	public static final RegistryObject<ContainerType<ReinforceEstusFlaskMenu>> REINFORCE_ESTUS_FLASK = CONTAINERS.register("reinforce_estus_flask", () -> new ContainerType<>(ReinforceEstusFlaskMenu::new));
	public static final RegistryObject<ContainerType<AttunementsMenu>> ATTUNEMENTS = CONTAINERS.register("attunements", () -> new ContainerType<>(AttunementsMenu::new));
	public static final RegistryObject<ContainerType<SoulMerchantMenu>> SOUL_MERCHANT = CONTAINERS.register("soul_merchant", () -> new ContainerType<>(SoulMerchantMenu::new));
	
	public static void registerScreens()
	{
		ScreenManager.register(REINFORCE_ESTUS_FLASK.get(), ReinforceEstusFlaskScreen::new);
		ScreenManager.register(ATTUNEMENTS.get(), AttunementScreen::new);
		ScreenManager.register(SOUL_MERCHANT.get(), SoulMerchantScreen::new);
	}
}
