package com.skullmangames.darksouls.core.init;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.skullmangames.darksouls.common.capability.item.ArmorCap;
import com.skullmangames.darksouls.common.capability.item.ArmorCap.ArmorPart;
import com.skullmangames.darksouls.common.capability.item.AxeCap;
import com.skullmangames.darksouls.common.capability.item.BowCap;
import com.skullmangames.darksouls.common.capability.item.CrossbowCap;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.item.LongswordCap;
import com.skullmangames.darksouls.common.capability.item.ShieldCap;
import com.skullmangames.darksouls.common.capability.item.ShieldCap.ShieldMat;
import com.skullmangames.darksouls.common.capability.item.SpearCap;
import com.skullmangames.darksouls.common.capability.item.GreatHammerCap;
import com.skullmangames.darksouls.common.capability.item.HammerCap;
import com.skullmangames.darksouls.common.capability.item.IShield.ShieldType;
import com.skullmangames.darksouls.common.capability.item.SwordCap;
import com.skullmangames.darksouls.common.capability.item.TridentCap;
import com.skullmangames.darksouls.common.capability.item.UltraGreatswordCap;
import com.skullmangames.darksouls.common.capability.item.VanillaArmorCap;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.Scaling;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.WeaponCategory;
import com.skullmangames.darksouls.common.capability.item.WingedSpearCap;
import com.skullmangames.darksouls.config.CapabilityConfig;
import com.skullmangames.darksouls.config.CapabilityConfig.ShieldConfig;
import com.skullmangames.darksouls.config.CapabilityConfig.WeaponConfig;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DiggerItem;
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
		
		// Great Hammers
		putCap(new GreatHammerCap(ModItems.DEMON_GREAT_HAMMER.get(), 46, 0, Scaling.B, Scaling.NONE));
		
		// Axes
		putCap(new AxeCap(Items.WOODEN_AXE, 8, 8, Scaling.C, Scaling.D));
		putCap(new AxeCap(Items.STONE_AXE, 8, 8, Scaling.C, Scaling.D));
		putCap(new AxeCap(Items.IRON_AXE, 10, 10, Scaling.C, Scaling.D));
		putCap(new AxeCap(Items.GOLDEN_AXE, 8, 10, Scaling.C, Scaling.D));
		putCap(new AxeCap(Items.DIAMOND_AXE, 15, 15, Scaling.C, Scaling.D));
		putCap(new AxeCap(Items.NETHERITE_AXE, 15, 15, Scaling.C, Scaling.D));
		putCap(new AxeCap(ModItems.BATTLE_AXE.get(), 12, 8, Scaling.C, Scaling.D));
		
		// Hammers
		putCap(new HammerCap(Items.WOODEN_PICKAXE, 8, 8, Scaling.C, Scaling.D));
		putCap(new HammerCap(Items.STONE_PICKAXE, 8, 8, Scaling.C, Scaling.D));
		putCap(new HammerCap(Items.IRON_PICKAXE, 10, 10, Scaling.C, Scaling.D));
		putCap(new HammerCap(Items.GOLDEN_PICKAXE, 8, 10, Scaling.C, Scaling.D));
		putCap(new HammerCap(Items.DIAMOND_PICKAXE, 15, 15, Scaling.C, Scaling.D));
		putCap(new HammerCap(Items.NETHERITE_PICKAXE, 15, 15, Scaling.C, Scaling.D));
		
		// Straight Swords
		putCap(new SwordCap(Items.WOODEN_SWORD, 8, 10, Scaling.C, Scaling.C));
		putCap(new SwordCap(Items.STONE_SWORD, 10, 10, Scaling.C, Scaling.C));
		putCap(new SwordCap(Items.IRON_SWORD, 9, 10, Scaling.C, Scaling.C));
		putCap(new SwordCap(Items.GOLDEN_SWORD, 8, 10, Scaling.C, Scaling.C));
		putCap(new SwordCap(Items.DIAMOND_SWORD, 10, 15, Scaling.C, Scaling.C));
		putCap(new SwordCap(Items.NETHERITE_SWORD, 12, 15, Scaling.C, Scaling.C));
		putCap(new SwordCap(ModItems.BROKEN_STRAIGHT_SWORD.get(), 8, 8, Scaling.D, Scaling.D));
		putCap(new SwordCap(ModItems.STRAIGHT_SWORD_HILT.get(), 6, 6, Scaling.E, Scaling.E));
		putCap(new LongswordCap(ModItems.LONGSWORD.get(), 10, 10, Scaling.C, Scaling.C));
		
		// Spears
		putCap(new SpearCap(ModItems.SPEAR.get(), 11, 10, Scaling.D, Scaling.C));
		putCap(new WingedSpearCap(ModItems.WINGED_SPEAR.get(), 13, 15, Scaling.E, Scaling.C));
		
		// Shields
		putCap(new ShieldCap(Items.SHIELD, ShieldType.NORMAL, ShieldMat.WOOD, 0.7F, 8, 0, Scaling.D, Scaling.NONE));
		putCap(new ShieldCap(ModItems.HEATER_SHIELD.get(), ShieldType.SMALL, ShieldMat.METAL, 1F, 8, 0, Scaling.D, Scaling.NONE));
		putCap(new ShieldCap(ModItems.CRACKED_ROUND_SHIELD.get(), ShieldType.CRACKED_ROUND_SHIELD, ShieldMat.WOOD, 0.65F, 6, 0, Scaling.D, Scaling.NONE));
		putCap(new ShieldCap(ModItems.LORDRAN_SOLDIER_SHIELD.get(), ShieldType.NORMAL, ShieldMat.METAL, 1F, 11, 0, Scaling.D, Scaling.NONE));
		
		// Ultra Greatswords
		putCap(new UltraGreatswordCap(ModItems.ZWEIHANDER.get(), 24, 10, Scaling.C, Scaling.D));
		
		// Bows
		putCap(new BowCap(Items.BOW, 3, 7, 12, Scaling.D, Scaling.A));
		
		// Crossbows
		putCap(new CrossbowCap(Items.CROSSBOW, 4, 10, 8, Scaling.NONE, Scaling.NONE));
		
		// Tridents
		putCap(new TridentCap(Items.TRIDENT, 5, 11, 15, Scaling.NONE, Scaling.NONE));
		
		//Armor
		putCap(new ArmorCap(ModItems.BLOOD_STAINED_SKIRT.get(), ArmorPart.SKIRT));
		putCap(new ArmorCap(ModItems.LORDRAN_SOLDIER_WAISTCLOTH.get(), ArmorPart.SKIRT));
		putCap(new ArmorCap(ModItems.LORDRAN_WARRIOR_WAISTCLOTH.get(), ArmorPart.SKIRT));
		putCap(new ArmorCap(ModItems.LORDRAN_WARRIOR_BOOTS.get(), ArmorPart.ONE_SHOE));
		
		for (WeaponConfig configWeapon : CapabilityConfig.WEAPON_CONFIGS)
		{
			ResourceLocation name = new ResourceLocation(configWeapon.registryName.get());
			if (!ForgeRegistries.ITEMS.containsKey(name)) continue;
			Item item = ForgeRegistries.ITEMS.getValue(name);
			if (!(item instanceof SwordItem || item instanceof DiggerItem || item instanceof ProjectileWeaponItem)) continue;
			switch (configWeapon.category.get())
			{
			default: break;
			case GREAT_HAMMER:
				putCap(new GreatHammerCap(item,
						configWeapon.requiredStrength.get(), configWeapon.requiredDex.get(),
						configWeapon.strengthScaling.get(), configWeapon.dexScaling.get()));
				break;
			case AXE:
				putCap(new AxeCap(item,
						configWeapon.requiredStrength.get(), configWeapon.requiredDex.get(),
						configWeapon.strengthScaling.get(), configWeapon.dexScaling.get()));
				break;
			case HAMMER:
				putCap(new HammerCap(item,
						configWeapon.requiredStrength.get(), configWeapon.requiredDex.get(),
						configWeapon.strengthScaling.get(), configWeapon.dexScaling.get()));
				break;
			case STRAIGHT_SWORD:
				putCap(new SwordCap(item,
						configWeapon.requiredStrength.get(), configWeapon.requiredDex.get(),
						configWeapon.strengthScaling.get(), configWeapon.dexScaling.get()));
				break;
			case SPEAR:
				putCap(new SpearCap(item,
						configWeapon.requiredStrength.get(), configWeapon.requiredDex.get(),
						configWeapon.strengthScaling.get(), configWeapon.dexScaling.get()));
				break;
			case ULTRA_GREATSWORD:
				putCap(new UltraGreatswordCap(item,
						configWeapon.requiredStrength.get(), configWeapon.requiredDex.get(),
						configWeapon.strengthScaling.get(), configWeapon.dexScaling.get()));
				break;
			
			case BOW:
				putCap(new BowCap(item, 3,
						configWeapon.requiredStrength.get(), configWeapon.requiredDex.get(),
						configWeapon.strengthScaling.get(), configWeapon.dexScaling.get()));
				break;
			case CROSSBOW:
				putCap(new CrossbowCap(item, 4,
						configWeapon.requiredStrength.get(), configWeapon.requiredDex.get(),
						configWeapon.strengthScaling.get(), configWeapon.dexScaling.get()));
				break;
			}
		}
		
		for (ShieldConfig configShield : CapabilityConfig.SHIELD_CONFIGS)
		{
			ResourceLocation name = new ResourceLocation(configShield.registryName.get());
			if (!ForgeRegistries.ITEMS.containsKey(name)
					|| configShield.category.get() != WeaponCategory.SHIELD
					|| configShield.shieldType.get() == ShieldType.NONE) continue;
			Item item = ForgeRegistries.ITEMS.getValue(name);
			putCap(new ShieldCap(item, configShield.shieldType.get(), configShield.shieldMat.get(), (float)((double)configShield.physicalDefense.get()),
					configShield.requiredStrength.get(), configShield.requiredDex.get(),
					configShield.strengthScaling.get(), configShield.dexScaling.get()));
		}
		
		// CLASS
		CAPABILITY_BY_CLASS.put(Item.class, ItemCapability::new);
		CAPABILITY_BY_CLASS.put(ArmorItem.class, VanillaArmorCap::new);
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