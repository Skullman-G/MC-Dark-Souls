package com.skullmangames.darksouls.core.init;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.skullmangames.darksouls.common.capability.item.ArmorCapability;
import com.skullmangames.darksouls.common.capability.item.CapabilityItem;
import com.skullmangames.darksouls.common.capability.item.DaggerCapability;
import com.skullmangames.darksouls.common.capability.item.GreatHammerCapability;
import com.skullmangames.darksouls.common.capability.item.StraightSwordCapability;
import com.skullmangames.darksouls.common.capability.item.VanillaArmorCapability;
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

public class ProviderItem implements ICapabilityProvider, NonNullSupplier<CapabilityItem>
{
	public static final Map<Item, CapabilityItem> CAPABILITIES = new HashMap<Item, CapabilityItem>();
	private static final Map<Class<? extends Item>, Function<Item, CapabilityItem>> CAPABILITY_BY_CLASS = new HashMap<Class<? extends Item>, Function<Item, CapabilityItem>>();

	private CapabilityItem capability;
	private LazyOptional<CapabilityItem> optional = LazyOptional.of(this);

	public ProviderItem(ItemStack itemstack)
	{
		this.capability = CAPABILITIES.get(itemstack.getItem());
	}

	public static void initCapabilityMap()
	{
		// ARMOR
		CAPABILITIES.computeIfAbsent(Items.LEATHER_BOOTS, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.LEATHER_CHESTPLATE, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.LEATHER_HELMET, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.LEATHER_LEGGINGS, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.GOLDEN_BOOTS, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.GOLDEN_CHESTPLATE, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.GOLDEN_HELMET, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.GOLDEN_LEGGINGS, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.CHAINMAIL_BOOTS, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.CHAINMAIL_CHESTPLATE, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.CHAINMAIL_HELMET, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.CHAINMAIL_LEGGINGS, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.IRON_BOOTS, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.IRON_CHESTPLATE, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.IRON_HELMET, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.IRON_LEGGINGS, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.DIAMOND_BOOTS, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.DIAMOND_CHESTPLATE, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.DIAMOND_HELMET, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.DIAMOND_LEGGINGS, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.NETHERITE_BOOTS, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.NETHERITE_CHESTPLATE, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.NETHERITE_HELMET, VanillaArmorCapability::new);
		CAPABILITIES.computeIfAbsent(Items.NETHERITE_LEGGINGS, VanillaArmorCapability::new);
		
		// WEAPONS
		putCap(new DaggerCapability(ModItems.DAGGER.get(), 5, 8));
		
		putCap(new GreatHammerCapability(ModItems.DEMON_GREAT_HAMMER.get(), 46, 0));
		
		putCap(new StraightSwordCapability(ModItems.WOODEN_SWORD.get(), 8, 10));
		putCap(new StraightSwordCapability(ModItems.STONE_SWORD.get(), 10, 10));
		putCap(new StraightSwordCapability(ModItems.IRON_SWORD.get(), 9, 10));
		putCap(new StraightSwordCapability(ModItems.GOLDEN_SWORD.get(), 8, 10));
		putCap(new StraightSwordCapability(ModItems.DIAMOND_SWORD.get(), 10, 15));
		putCap(new StraightSwordCapability(ModItems.NETHERITE_SWORD.get(), 12, 15));
		putCap(new StraightSwordCapability(ModItems.BROKEN_STRAIGHT_SWORD.get(), 8, 8));
		putCap(new StraightSwordCapability(ModItems.STRAIGHT_SWORD_HILT.get(), 6, 6));

		// CLASS
		CAPABILITY_BY_CLASS.put(Item.class, CapabilityItem::new);
		CAPABILITY_BY_CLASS.put(ArmorItem.class, ArmorCapability::new);
		/*CAPABILITY_BY_CLASS.put(PickaxeItem.class, PickaxeCapability::new);
		CAPABILITY_BY_CLASS.put(AxeItem.class, AxeCapability::new);
		CAPABILITY_BY_CLASS.put(ShovelItem.class, ShovelCapability::new);
		CAPABILITY_BY_CLASS.put(HoeItem.class, HoeCapability::new);
		CAPABILITY_BY_CLASS.put(BowItem.class, BowCapability::new);
		CAPABILITY_BY_CLASS.put(CrossbowItem.class, CrossbowCapability::new);
		CAPABILITY_BY_CLASS.put(ShieldItem.class, ShieldCapability::new);
		CAPABILITY_BY_CLASS.put(TridentItem.class, TridentCapability::new);*/
	}
	
	private static void putCap(CapabilityItem cap)
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
				CapabilityItem capability = null;

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
	public CapabilityItem get()
	{
		return this.capability;
	}
}