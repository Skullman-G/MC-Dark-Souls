package com.skullmangames.darksouls.core.init;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.skullmangames.darksouls.common.capability.item.ArmorCap;
import com.skullmangames.darksouls.common.capability.item.BowCap;
import com.skullmangames.darksouls.common.capability.item.CrossbowCap;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.item.SpellcasterWeaponCap;
import com.skullmangames.darksouls.common.capability.item.TridentCap;
import com.skullmangames.darksouls.common.capability.item.VanillaArmorCap;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.Scaling;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.core.util.WeaponCategory;

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
	public static final Map<Item, ItemCapability> DEFAULT_CAPABILITIES = new HashMap<Item, ItemCapability>();
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
		//Bows
		putCap(BowCap.builder(Items.BOW, 1.00F, 2.0F)
				.putDamage(CoreDamageType.PHYSICAL, 77)
				.putStatInfo(Stats.STRENGTH, 7, Scaling.E)
				.putStatInfo(Stats.DEXTERITY, 12, Scaling.D)
				.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
				.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
				.build());
		
		//Crossbows
		putCap(CrossbowCap.builder(Items.CROSSBOW, 1.00F, 3.0F)
				.putDamage(CoreDamageType.PHYSICAL, 64)
				.putStatInfo(Stats.STRENGTH, 10, Scaling.NONE)
				.putStatInfo(Stats.DEXTERITY, 8, Scaling.NONE)
				.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
				.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
				.build());
		
		//Tridents
		putCap(TridentCap.builder(Items.TRIDENT, 1.00F, 3.0F)
				.putDamage(CoreDamageType.PHYSICAL, 64)
				.putStatInfo(Stats.STRENGTH, 15, Scaling.NONE)
				.putStatInfo(Stats.DEXTERITY, 9, Scaling.NONE)
				.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
				.putStatInfo(Stats.FAITH, 0, Scaling.NONE)
				.build());
		
		//Talismans
		putCap(SpellcasterWeaponCap.builder(ModItems.TALISMAN.get(), WeaponCategory.TALISMAN, 1.00F, 1.00F, 0.5F)
				.putDamage(CoreDamageType.PHYSICAL, 52)
				.putStatInfo(Stats.STRENGTH, 4, Scaling.E)
				.putStatInfo(Stats.DEXTERITY, 0, Scaling.NONE)
				.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
				.putStatInfo(Stats.FAITH, 10, Scaling.B)
				.build());
		putCap(SpellcasterWeaponCap.builder(ModItems.THOROLUND_TALISMAN.get(), WeaponCategory.TALISMAN, 1.00F, 1.15F, 0.3F)
				.putDamage(CoreDamageType.PHYSICAL, 52)
				.putStatInfo(Stats.STRENGTH, 4, Scaling.E)
				.putStatInfo(Stats.DEXTERITY, 0, Scaling.NONE)
				.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
				.putStatInfo(Stats.FAITH, 10, Scaling.B)
				.build());
		
		//ARMOR
		putCap(new ArmorCap(ModItems.BLOOD_STAINED_SKIRT.get(), (models) -> models.ITEM_SKIRT));
		putCap(new ArmorCap(ModItems.LORDRAN_SOLDIER_WAISTCLOTH.get(), (models) -> models.ITEM_SKIRT));
		putCap(new ArmorCap(ModItems.LORDRAN_WARRIOR_WAISTCLOTH.get(), (models) -> models.ITEM_SKIRT));
		putCap(new ArmorCap(ModItems.LORDRAN_WARRIOR_BOOTS.get(), (models) -> models.ITEM_ONE_SHOE));
		putCap(new ArmorCap(ModItems.ELITE_CLERIC_LEGGINGS.get(), (models) -> models.ITEM_SKIRT));
		putCap(new ArmorCap(ModItems.FALCONER_HELM.get(), (models) -> models.ITEM_FALCONER_HELM));
		putCap(new ArmorCap(ModItems.FALCONER_ARMOR.get(), (models) -> models.ITEM_FALCONER_ARMOR));
		putCap(new ArmorCap(ModItems.BLACK_KNIGHT_HELM.get(), (models) -> models.BLACK_KNIGHT_HELM, 5F, 8F));
		putCap(new ArmorCap(ModItems.BLACK_KNIGHT_ARMOR.get(), (models) -> models.BLACK_KNIGHT_ARMOR, 13F, 21F));
		putCap(new ArmorCap(ModItems.BLACK_KNIGHT_LEGGINGS.get(), (models) -> models.BLACK_KNIGHT_LEGGINGS, 7F, 17F));
		putCap(new ArmorCap(ModItems.BALDER_HELM.get(), (models) -> models.BALDER_HELM));
		putCap(new ArmorCap(ModItems.BALDER_ARMOR.get(), (models) -> models.BALDER_ARMOR));
		putCap(new ArmorCap(ModItems.BALDER_BOOTS.get(), (models) -> models.BALDER_BOOTS));
		
		// CLASS
		CAPABILITY_BY_CLASS.put(Item.class, ItemCapability::new);
		CAPABILITY_BY_CLASS.put(ArmorItem.class, VanillaArmorCap::new);
		
		CAPABILITIES.putAll(DEFAULT_CAPABILITIES);
	}
	
	private static void putCap(ItemCapability cap)
	{
		DEFAULT_CAPABILITIES.put(cap.getOriginalItem(), cap);
	}

	public static void registerCapabilityItems()
	{
		for (Item item : ForgeRegistries.ITEMS.getValues())
		{
			if (!DEFAULT_CAPABILITIES.containsKey(item))
			{
				Class<?> clazz = item.getClass();
				ItemCapability capability = null;

				for (; clazz != null && capability == null; clazz = clazz.getSuperclass())
				{
					capability = CAPABILITY_BY_CLASS.getOrDefault(clazz, (argIn) -> null).apply(item);
				}

				if (capability != null) DEFAULT_CAPABILITIES.put(item, capability);
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
		return cap == ModCapabilities.CAPABILITY_ITEM ? this.optional.cast() : LazyOptional.empty();
	}

	@Override
	public ItemCapability get()
	{
		return this.capability;
	}
}