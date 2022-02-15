package com.skullmangames.darksouls.core.init;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.skullmangames.darksouls.common.capability.item.AxeCap;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.item.LongswordCap;
import com.skullmangames.darksouls.common.capability.item.ShieldCap;
import com.skullmangames.darksouls.common.capability.item.SpearCap;
import com.skullmangames.darksouls.common.capability.item.GreatHammerCap;
import com.skullmangames.darksouls.common.capability.item.HammerCap;
import com.skullmangames.darksouls.common.capability.item.SwordCap;
import com.skullmangames.darksouls.common.capability.item.UltraGreatswordCap;
import com.skullmangames.darksouls.common.capability.item.VanillaArmorCap;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.Scaling;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.registries.ForgeRegistries;

public class ProviderItem implements ICapabilityProvider, NonNullSupplier<ItemCapability>
{
	public static final Map<Item, ItemCapability> CAPABILITIES = new HashMap<Item, ItemCapability>();
	private static final Map<Class<? extends Item>, Function<Item, ItemCapability>> CAPABILITY_BY_CLASS = new HashMap<Class<? extends Item>, Function<Item, ItemCapability>>();

	private ItemCapability capability;
	private LazyOptional<ItemCapability> optional = LazyOptional.of(this);

	public ProviderItem(ItemStack itemstack)
	{
		this.capability = CAPABILITIES.get(itemstack.getItem());
	}

	public static void initCapabilityMap()
	{
		// WEAPONS
		//putCap(new DaggerCap(ModItems.DAGGER.get(), 5, 8, Scaling.E, Scaling.B));
		
		putCap(new GreatHammerCap(ModItems.DEMON_GREAT_HAMMER.get(), 46, 0, Scaling.B, Scaling.NONE));
		
		putCap(new AxeCap(Items.WOODEN_AXE, 8, 8, Scaling.C, Scaling.D));
		putCap(new AxeCap(Items.STONE_AXE, 8, 8, Scaling.C, Scaling.D));
		putCap(new AxeCap(Items.IRON_AXE, 10, 10, Scaling.C, Scaling.D));
		putCap(new AxeCap(Items.GOLDEN_AXE, 8, 10, Scaling.C, Scaling.D));
		putCap(new AxeCap(Items.DIAMOND_AXE, 15, 15, Scaling.C, Scaling.D));
		putCap(new AxeCap(Items.NETHERITE_AXE, 15, 15, Scaling.C, Scaling.D));
		putCap(new AxeCap(ModItems.BATTLE_AXE.get(), 12, 8, Scaling.C, Scaling.D));
		
		putCap(new HammerCap(Items.WOODEN_PICKAXE, 8, 8, Scaling.C, Scaling.D));
		putCap(new HammerCap(Items.STONE_PICKAXE, 8, 8, Scaling.C, Scaling.D));
		putCap(new HammerCap(Items.IRON_PICKAXE, 10, 10, Scaling.C, Scaling.D));
		putCap(new HammerCap(Items.GOLDEN_PICKAXE, 8, 10, Scaling.C, Scaling.D));
		putCap(new HammerCap(Items.DIAMOND_PICKAXE, 15, 15, Scaling.C, Scaling.D));
		putCap(new HammerCap(Items.NETHERITE_PICKAXE, 15, 15, Scaling.C, Scaling.D));
		
		putCap(new SwordCap(Items.WOODEN_SWORD, 8, 10, Scaling.C, Scaling.C));
		putCap(new SwordCap(Items.STONE_SWORD, 10, 10, Scaling.C, Scaling.C));
		putCap(new SwordCap(Items.IRON_SWORD, 9, 10, Scaling.C, Scaling.C));
		putCap(new SwordCap(Items.GOLDEN_SWORD, 8, 10, Scaling.C, Scaling.C));
		putCap(new SwordCap(Items.DIAMOND_SWORD, 10, 15, Scaling.C, Scaling.C));
		putCap(new SwordCap(Items.NETHERITE_SWORD, 12, 15, Scaling.C, Scaling.C));
		putCap(new SwordCap(ModItems.BROKEN_STRAIGHT_SWORD.get(), 8, 8, Scaling.D, Scaling.D));
		putCap(new SwordCap(ModItems.STRAIGHT_SWORD_HILT.get(), 6, 6, Scaling.E, Scaling.E));
		putCap(new LongswordCap(ModItems.LONGSWORD.get(), 10, 10, Scaling.C, Scaling.C));
		
		putCap(new SpearCap(ModItems.SPEAR.get(), 11, 10, Scaling.D, Scaling.C));
		putCap(new SpearCap(ModItems.WINGED_SPEAR.get(), 13, 15, Scaling.E, Scaling.C));
		
		putCap(new ShieldCap(Items.SHIELD, 8, 0, Scaling.D, Scaling.NONE, 0.5F));
		putCap(new ShieldCap(ModItems.HEATER_SHIELD.get(), 8, 0, Scaling.D, Scaling.NONE, 1F));
		
		putCap(new UltraGreatswordCap(ModItems.ZWEIHANDER.get(), 24, 10, Scaling.C, Scaling.D));
		
		// CLASS
		CAPABILITY_BY_CLASS.put(Item.class, ItemCapability::new);
		CAPABILITY_BY_CLASS.put(ArmorItem.class, VanillaArmorCap::new);
		/*CAPABILITY_BY_CLASS.put(BowItem.class, BowCapability::new);
		CAPABILITY_BY_CLASS.put(CrossbowItem.class, CrossbowCapability::new);
		CAPABILITY_BY_CLASS.put(TridentItem.class, TridentCapability::new);*/
	}
	
	private static void putCap(ItemCapability cap)
	{
		CAPABILITIES.put(cap.getOriginalItem(), cap);
	}

	public static void registerCapabilityItems()
	{
		for (Item item : ForgeRegistries.ITEMS.getValues())
		{
			if (!CAPABILITIES.containsKey(item))
			{
				Class<?> clazz = item.getClass();
				ItemCapability capability = null;

				for (; clazz != null && capability == null; clazz = clazz.getSuperclass())
				{
					capability = CAPABILITY_BY_CLASS.getOrDefault(clazz, (argIn) -> null).apply(item);
				}

				if (capability != null) CAPABILITIES.put(item, capability);
			}
		}
	}

	public boolean hasCapability()
	{
		return this.capability != null;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
		return cap == ModCapabilities.CAPABILITY_ITEM ? optional.cast() : LazyOptional.empty();
	}

	@Override
	public ItemCapability get()
	{
		return this.capability;
	}
}