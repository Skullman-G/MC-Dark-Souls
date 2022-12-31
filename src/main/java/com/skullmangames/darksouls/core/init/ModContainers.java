package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.gui.screens.AttunementScreen;
import com.skullmangames.darksouls.client.gui.screens.ReinforceEstusFlaskScreen;
import com.skullmangames.darksouls.client.gui.screens.SoulMerchantScreen;
import com.skullmangames.darksouls.common.inventory.AttunementsMenu;
import com.skullmangames.darksouls.common.inventory.ReinforceEstusFlaskMenu;
import com.skullmangames.darksouls.common.inventory.SoulMerchantMenu;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContainers
{
	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, DarkSouls.MOD_ID);
	
	public static final RegistryObject<MenuType<ReinforceEstusFlaskMenu>> REINFORCE_ESTUS_FLASK = CONTAINERS.register("reinforce_estus_flask", () -> new MenuType<>(ReinforceEstusFlaskMenu::new));
	public static final RegistryObject<MenuType<AttunementsMenu>> ATTUNEMENTS = CONTAINERS.register("attunements", () -> new MenuType<>(AttunementsMenu::new));
	public static final RegistryObject<MenuType<SoulMerchantMenu>> SOUL_MERCHANT = CONTAINERS.register("soul_merchant", () -> new MenuType<>(SoulMerchantMenu::new));
	
	public static void registerScreens()
	{
		MenuScreens.register(REINFORCE_ESTUS_FLASK.get(), ReinforceEstusFlaskScreen::new);
		MenuScreens.register(ATTUNEMENTS.get(), AttunementScreen::new);
		MenuScreens.register(SOUL_MERCHANT.get(), SoulMerchantScreen::new);
	}
}
