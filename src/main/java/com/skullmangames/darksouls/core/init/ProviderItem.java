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
import com.skullmangames.darksouls.common.capability.item.TalismanCap;
import com.skullmangames.darksouls.common.capability.item.TridentCap;
import com.skullmangames.darksouls.common.capability.item.UltraGreatswordCap;
import com.skullmangames.darksouls.common.capability.item.VanillaArmorCap;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.Scaling;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.WeaponCategory;
import com.skullmangames.darksouls.common.capability.item.WingedSpearCap;
import com.skullmangames.darksouls.config.ConfigManager;
import com.skullmangames.darksouls.config.ServerConfig.ShieldConfig;
import com.skullmangames.darksouls.config.ServerConfig.WeaponConfig;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ToolItem;
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
		//WEAPONS
		//putCap(new DaggerCap(ModItems.DAGGER.get(), 5, 8, Scaling.E, Scaling.B));
		
		//Great Hammers
		putCap(new GreatHammerCap(ModItems.DEMON_GREAT_HAMMER.get(), 46, 0, 0, Scaling.B, Scaling.NONE, Scaling.NONE));
		
		//Axes
		putCap(new AxeCap(Items.WOODEN_AXE, 8, 8, 0, Scaling.C, Scaling.D, Scaling.NONE));
		putCap(new AxeCap(Items.STONE_AXE, 8, 8, 0, Scaling.C, Scaling.D, Scaling.NONE));
		putCap(new AxeCap(Items.IRON_AXE, 10, 10, 0, Scaling.C, Scaling.D, Scaling.NONE));
		putCap(new AxeCap(Items.GOLDEN_AXE, 8, 10, 0, Scaling.C, Scaling.D, Scaling.NONE));
		putCap(new AxeCap(Items.DIAMOND_AXE, 15, 15, 0, Scaling.C, Scaling.D, Scaling.NONE));
		putCap(new AxeCap(Items.NETHERITE_AXE, 15, 15, 0, Scaling.C, Scaling.D, Scaling.NONE));
		putCap(new AxeCap(ModItems.BATTLE_AXE.get(), 12, 8, 0, Scaling.C, Scaling.D, Scaling.NONE));
		
		//Hammers
		putCap(new HammerCap(Items.WOODEN_PICKAXE, 8, 8, 0, Scaling.C, Scaling.NONE, Scaling.NONE));
		putCap(new HammerCap(Items.STONE_PICKAXE, 8, 8, 0, Scaling.C, Scaling.NONE, Scaling.NONE));
		putCap(new HammerCap(Items.IRON_PICKAXE, 10, 10, 0, Scaling.C, Scaling.NONE, Scaling.NONE));
		putCap(new HammerCap(Items.GOLDEN_PICKAXE, 8, 10, 0, Scaling.C, Scaling.NONE, Scaling.NONE));
		putCap(new HammerCap(Items.DIAMOND_PICKAXE, 15, 15, 0,  Scaling.C, Scaling.NONE, Scaling.NONE));
		putCap(new HammerCap(Items.NETHERITE_PICKAXE, 15, 15, 0, Scaling.C, Scaling.NONE, Scaling.NONE));
		putCap(new HammerCap(ModItems.MACE.get(), 12, 0, 0, Scaling.B, Scaling.NONE, Scaling.NONE));
		
		//Straight Swords
		putCap(new SwordCap(Items.WOODEN_SWORD, 8, 10, 0, Scaling.C, Scaling.C, Scaling.NONE));
		putCap(new SwordCap(Items.STONE_SWORD, 10, 10, 0, Scaling.C, Scaling.C, Scaling.NONE));
		putCap(new SwordCap(Items.IRON_SWORD, 9, 10, 0, Scaling.C, Scaling.C, Scaling.NONE));
		putCap(new SwordCap(Items.GOLDEN_SWORD, 8, 10, 0, Scaling.C, Scaling.C, Scaling.NONE));
		putCap(new SwordCap(Items.DIAMOND_SWORD, 10, 15, 0, Scaling.C, Scaling.C, Scaling.NONE));
		putCap(new SwordCap(Items.NETHERITE_SWORD, 12, 15, 0, Scaling.C, Scaling.C, Scaling.NONE));
		putCap(new SwordCap(ModItems.BROKEN_STRAIGHT_SWORD.get(), 8, 8, 0, Scaling.D, Scaling.D, Scaling.NONE));
		putCap(new SwordCap(ModItems.STRAIGHT_SWORD_HILT.get(), 6, 6, 0, Scaling.E, Scaling.E, Scaling.NONE));
		putCap(new LongswordCap(ModItems.LONGSWORD.get(), 10, 10, 0, Scaling.C, Scaling.C, Scaling.NONE));
		
		//Spears
		putCap(new SpearCap(ModItems.SPEAR.get(), 11, 10, 0, Scaling.D, Scaling.C, Scaling.NONE));
		putCap(new WingedSpearCap(ModItems.WINGED_SPEAR.get(), 13, 15, 0, Scaling.E, Scaling.C, Scaling.NONE));
		
		//Shields
		putCap(new ShieldCap(Items.SHIELD, ShieldType.NORMAL, ShieldMat.WOOD, 0.7F, 0.3F, 0.65F, 8, 0, 0, Scaling.D, Scaling.NONE, Scaling.NONE));
		putCap(new ShieldCap(ModItems.HEATER_SHIELD.get(), ShieldType.SMALL, ShieldMat.METAL, 1F, 0.7F, 0.5F, 8, 0, 0, Scaling.D, Scaling.NONE, Scaling.NONE));
		putCap(new ShieldCap(ModItems.CRACKED_ROUND_SHIELD.get(), ShieldType.CRACKED_ROUND_SHIELD, ShieldMat.WOOD, 0.65F, 0.1F, 0.45F, 6, 0, 0, Scaling.D, Scaling.NONE, Scaling.NONE));
		putCap(new ShieldCap(ModItems.LORDRAN_SOLDIER_SHIELD.get(), ShieldType.NORMAL, ShieldMat.METAL, 1F, 0.65F, 0.5F, 11, 0, 0, Scaling.D, Scaling.NONE, Scaling.NONE));
		putCap(new ShieldCap(ModItems.KNIGHT_SHIELD.get(), ShieldType.NORMAL, ShieldMat.METAL, 1F, 0.6F, 0.4F, 10, 0, 0, Scaling.D, Scaling.NONE, Scaling.NONE));
		putCap(new ShieldCap(ModItems.GOLDEN_FALCON_SHIELD.get(), ShieldType.SMALL, ShieldMat.GOLD, 0.8F, 0.65F, 0.65F, 7, 10, 0, Scaling.D, Scaling.D, Scaling.NONE));
		
		//Ultra Greatswords
		putCap(new UltraGreatswordCap(ModItems.ZWEIHANDER.get(), 24, 10, 0, Scaling.C, Scaling.D, Scaling.NONE));
		
		//Bows
		putCap(new BowCap(Items.BOW, 3, 7, 12, 0, Scaling.D, Scaling.A, Scaling.NONE));
		
		//Crossbows
		putCap(new CrossbowCap(Items.CROSSBOW, 4, 10, 8, 0, Scaling.NONE, Scaling.NONE, Scaling.NONE));
		
		//Tridents
		putCap(new TridentCap(Items.TRIDENT, 5, 11, 15, 0, Scaling.NONE, Scaling.NONE, Scaling.NONE));
		
		//Talismans
		putCap(new TalismanCap(ModItems.TALISMAN.get(), 4, 0, 10, Scaling.E, Scaling.NONE, Scaling.B));
		putCap(new TalismanCap(ModItems.THOROLUND_TALISMAN.get(), 4, 0, 10, Scaling.C, Scaling.NONE, Scaling.D));
		
		//ARMOR
		putCap(new ArmorCap(ModItems.BLOOD_STAINED_SKIRT.get(), ArmorPart.SKIRT));
		putCap(new ArmorCap(ModItems.LORDRAN_SOLDIER_WAISTCLOTH.get(), ArmorPart.SKIRT));
		putCap(new ArmorCap(ModItems.LORDRAN_WARRIOR_WAISTCLOTH.get(), ArmorPart.SKIRT));
		putCap(new ArmorCap(ModItems.LORDRAN_WARRIOR_BOOTS.get(), ArmorPart.ONE_SHOE));
		putCap(new ArmorCap(ModItems.ELITE_CLERIC_LEGGINGS.get(), ArmorPart.SKIRT));
		putCap(new ArmorCap(ModItems.FALCONER_HELM.get(), ArmorPart.FALCONER_HELM));
		putCap(new ArmorCap(ModItems.FALCONER_ARMOR.get(), ArmorPart.FALCONER_ARMOR));
		
		//CONFIG
		for (WeaponConfig configWeapon : ConfigManager.SERVER_CONFIG.weapons)
		{
			ResourceLocation name = new ResourceLocation(configWeapon.registryName.get());
			if (!ForgeRegistries.ITEMS.containsKey(name)) continue;
			Item item = ForgeRegistries.ITEMS.getValue(name);
			if (!(item instanceof SwordItem || item instanceof ToolItem || item instanceof ShootableItem)) continue;
			switch (configWeapon.category.get())
			{
			default: break;
			case GREAT_HAMMER:
				putCap(new GreatHammerCap(item,
						configWeapon.reqStrength.get(), configWeapon.reqDex.get(), configWeapon.reqFaith.get(),
						configWeapon.strengthScaling.get(), configWeapon.dexScaling.get(), configWeapon.faithScaling.get()));
				break;
			case AXE:
				putCap(new AxeCap(item,
						configWeapon.reqStrength.get(), configWeapon.reqDex.get(), configWeapon.reqFaith.get(),
						configWeapon.strengthScaling.get(), configWeapon.dexScaling.get(), configWeapon.faithScaling.get()));
				break;
			case HAMMER:
				putCap(new HammerCap(item,
						configWeapon.reqStrength.get(), configWeapon.reqDex.get(), configWeapon.reqFaith.get(),
						configWeapon.strengthScaling.get(), configWeapon.dexScaling.get(), configWeapon.faithScaling.get()));
				break;
			case STRAIGHT_SWORD:
				putCap(new SwordCap(item,
						configWeapon.reqStrength.get(), configWeapon.reqDex.get(), configWeapon.reqFaith.get(),
						configWeapon.strengthScaling.get(), configWeapon.dexScaling.get(), configWeapon.faithScaling.get()));
				break;
			case SPEAR:
				putCap(new SpearCap(item,
						configWeapon.reqStrength.get(), configWeapon.reqDex.get(), configWeapon.reqFaith.get(),
						configWeapon.strengthScaling.get(), configWeapon.dexScaling.get(), configWeapon.faithScaling.get()));
				break;
			case ULTRA_GREATSWORD:
				putCap(new UltraGreatswordCap(item,
						configWeapon.reqStrength.get(), configWeapon.reqDex.get(), configWeapon.reqFaith.get(),
						configWeapon.strengthScaling.get(), configWeapon.dexScaling.get(), configWeapon.faithScaling.get()));
				break;
			
			case BOW:
				putCap(new BowCap(item, 3,
						configWeapon.reqStrength.get(), configWeapon.reqDex.get(), configWeapon.reqFaith.get(),
						configWeapon.strengthScaling.get(), configWeapon.dexScaling.get(), configWeapon.faithScaling.get()));
				break;
			case CROSSBOW:
				putCap(new CrossbowCap(item, 4,
						configWeapon.reqStrength.get(), configWeapon.reqDex.get(), configWeapon.reqFaith.get(),
						configWeapon.strengthScaling.get(), configWeapon.dexScaling.get(), configWeapon.faithScaling.get()));
				break;
			}
		}
		
		for (ShieldConfig configShield : ConfigManager.SERVER_CONFIG.shields)
		{
			ResourceLocation name = new ResourceLocation(configShield.registryName.get());
			if (!ForgeRegistries.ITEMS.containsKey(name)
					|| configShield.category.get() != WeaponCategory.SHIELD
					|| configShield.shieldType.get() == ShieldType.NONE) continue;
			Item item = ForgeRegistries.ITEMS.getValue(name);
			putCap(new ShieldCap(item, configShield.shieldType.get(), configShield.shieldMat.get(), (float)((double)configShield.physicalDef.get()),
					(float)((double)configShield.fireDef.get()),
					(float)((double)configShield.lightningDef.get()),
					configShield.reqStrength.get(), configShield.reqDex.get(), configShield.reqFaith.get(),
					configShield.strengthScaling.get(), configShield.dexScaling.get(), configShield.faithScaling.get()));
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