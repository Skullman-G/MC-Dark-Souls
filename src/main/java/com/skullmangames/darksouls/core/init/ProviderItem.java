package com.skullmangames.darksouls.core.init;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.skullmangames.darksouls.common.capability.item.ArmorCap;
import com.skullmangames.darksouls.common.capability.item.AxeCap;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.item.DaggerCap;
import com.skullmangames.darksouls.common.capability.item.GreatHammerCap;
import com.skullmangames.darksouls.common.capability.item.SwordCap;
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
		// ARMOR
		CAPABILITIES.computeIfAbsent(Items.LEATHER_BOOTS, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.LEATHER_CHESTPLATE, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.LEATHER_HELMET, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.LEATHER_LEGGINGS, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.GOLDEN_BOOTS, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.GOLDEN_CHESTPLATE, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.GOLDEN_HELMET, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.GOLDEN_LEGGINGS, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.CHAINMAIL_BOOTS, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.CHAINMAIL_CHESTPLATE, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.CHAINMAIL_HELMET, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.CHAINMAIL_LEGGINGS, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.IRON_BOOTS, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.IRON_CHESTPLATE, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.IRON_HELMET, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.IRON_LEGGINGS, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.DIAMOND_BOOTS, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.DIAMOND_CHESTPLATE, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.DIAMOND_HELMET, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.DIAMOND_LEGGINGS, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.NETHERITE_BOOTS, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.NETHERITE_CHESTPLATE, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.NETHERITE_HELMET, VanillaArmorCap::new);
		CAPABILITIES.computeIfAbsent(Items.NETHERITE_LEGGINGS, VanillaArmorCap::new);
		
		// WEAPONS
		putCap(new DaggerCap(ModItems.DAGGER.get(), 5, 8, Scaling.E, Scaling.B));
		
		putCap(new GreatHammerCap(ModItems.DEMON_GREAT_HAMMER.get(), 46, 0, Scaling.B, Scaling.NONE));
		
		putCap(new AxeCap(Items.WOODEN_AXE, 8, 8, Scaling.C, Scaling.D));
		putCap(new AxeCap(Items.STONE_AXE, 8, 8, Scaling.C, Scaling.D));
		putCap(new AxeCap(Items.IRON_AXE, 10, 10, Scaling.C, Scaling.D));
		putCap(new AxeCap(Items.GOLDEN_AXE, 8, 10, Scaling.C, Scaling.D));
		putCap(new AxeCap(Items.DIAMOND_AXE, 15, 15, Scaling.C, Scaling.D));
		putCap(new AxeCap(Items.NETHERITE_AXE, 15, 15, Scaling.C, Scaling.D));
		
		putCap(new SwordCap(Items.WOODEN_SWORD, 8, 10, Scaling.C, Scaling.C));
		putCap(new SwordCap(Items.STONE_SWORD, 10, 10, Scaling.C, Scaling.C));
		putCap(new SwordCap(Items.IRON_SWORD, 9, 10, Scaling.C, Scaling.C));
		putCap(new SwordCap(Items.GOLDEN_SWORD, 8, 10, Scaling.C, Scaling.C));
		putCap(new SwordCap(Items.DIAMOND_SWORD, 10, 15, Scaling.C, Scaling.C));
		putCap(new SwordCap(Items.NETHERITE_SWORD, 12, 15, Scaling.C, Scaling.C));
		putCap(new SwordCap(ModItems.BROKEN_STRAIGHT_SWORD.get(), 8, 8, Scaling.D, Scaling.D));
		putCap(new SwordCap(ModItems.STRAIGHT_SWORD_HILT.get(), 6, 6, Scaling.E, Scaling.E));
		
		// CLASS
		CAPABILITY_BY_CLASS.put(Item.class, ItemCapability::new);
		CAPABILITY_BY_CLASS.put(ArmorItem.class, ArmorCap::new);
		/*CAPABILITY_BY_CLASS.put(PickaxeItem.class, PickaxeCapability::new);
		CAPABILITY_BY_CLASS.put(ShovelItem.class, ShovelCapability::new);
		CAPABILITY_BY_CLASS.put(HoeItem.class, HoeCapability::new);
		CAPABILITY_BY_CLASS.put(BowItem.class, BowCapability::new);
		CAPABILITY_BY_CLASS.put(CrossbowItem.class, CrossbowCapability::new);
		CAPABILITY_BY_CLASS.put(ShieldItem.class, ShieldCapability::new);
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