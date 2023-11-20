package com.skullmangames.darksouls.core.init;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.skullmangames.darksouls.common.capability.item.ArmorCap;
import com.skullmangames.darksouls.common.capability.item.BowCap;
import com.skullmangames.darksouls.common.capability.item.CrossbowCap;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.item.SpellcasterWeaponCap;
import com.skullmangames.darksouls.common.capability.item.ThrowableCap;
import com.skullmangames.darksouls.common.capability.item.TridentCap;
import com.skullmangames.darksouls.common.capability.item.VanillaArmorCap;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.Scaling;
import com.skullmangames.darksouls.common.entity.projectile.Firebomb;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.core.util.WeaponCategory;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.projectile.Snowball;
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
		
		//Throwables
		putCap(new ThrowableCap(Items.SNOWBALL, Snowball::new, () -> SoundEvents.SNOWBALL_THROW));
		putCap(new ThrowableCap(ModItems.FIREBOMB.get(), Firebomb::new, () -> SoundEvents.SNOWBALL_THROW));
		
		//ARMOR
		putCap(new ArmorCap(ModItems.DINGY_HOOD.get(), 0.8F, 0F));
		putCap(new ArmorCap(ModItems.DINGY_ROBE.get(), 3F, 0F));
		putCap(new ArmorCap(ModItems.BLOOD_STAINED_SKIRT.get(), 1.4F, 0F));
		putCap(new ArmorCap(ModItems.LORDRAN_SOLDIER_HELM.get(), 3F, 5F));
		putCap(new ArmorCap(ModItems.LORDRAN_SOLDIER_ARMOR.get(), 7.8F, 12F));
		putCap(new ArmorCap(ModItems.LORDRAN_SOLDIER_WAISTCLOTH.get(), 1.5F, 7F));
		putCap(new ArmorCap(ModItems.LORDRAN_SOLDIER_BOOTS.get(), 1F, 2F));
		putCap(new ArmorCap(ModItems.LORDRAN_WARRIOR_HELM.get(), 2.6F, 3F));
		putCap(new ArmorCap(ModItems.LORDRAN_WARRIOR_ARMOR.get(), 6.6F, 8F));
		putCap(new ArmorCap(ModItems.LORDRAN_WARRIOR_WAISTCLOTH.get(), 1.4F, 5F));
		putCap(new ArmorCap(ModItems.LORDRAN_WARRIOR_BOOTS.get(), 1F, 0F));
		putCap(new ArmorCap(ModItems.ELITE_CLERIC_HELM.get(), 4.8F, 8F));
		putCap(new ArmorCap(ModItems.ELITE_CLERIC_ARMOR.get(), 12.5F, 20F));
		putCap(new ArmorCap(ModItems.ELITE_CLERIC_LEGGINGS.get(), 7.4F, 12F));
		putCap(new ArmorCap(ModItems.FALCONER_HELM.get(), 4.5F, 6F));
		putCap(new ArmorCap(ModItems.FALCONER_ARMOR.get(), 7.7F, 12F));
		putCap(new ArmorCap(ModItems.FALCONER_LEGGINGS.get(), 5.8F, 8F));
		putCap(new ArmorCap(ModItems.FALCONER_BOOTS.get(), 3.2F, 4F));
		putCap(new ArmorCap(ModItems.BLACK_KNIGHT_HELM.get(), 5F, 8F));
		putCap(new ArmorCap(ModItems.BLACK_KNIGHT_ARMOR.get(), 13F, 21F));
		putCap(new ArmorCap(ModItems.BLACK_KNIGHT_LEGGINGS.get(), 7F, 17F));
		putCap(new ArmorCap(ModItems.BALDER_HELM.get(), 4.2F, 6F));
		putCap(new ArmorCap(ModItems.BALDER_ARMOR.get(), 10.9F, 16F));
		putCap(new ArmorCap(ModItems.BALDER_LEGGINGS.get(), 6.4F, 9F));
		putCap(new ArmorCap(ModItems.BALDER_BOOTS.get(), 3.5F, 5F));
		putCap(new ArmorCap(ModItems.BURNT_SHIRT.get(), 0.8F, 0F));
		putCap(new ArmorCap(ModItems.BURNT_TROUSERS.get(), 0.8F, 0F));
		putCap(new ArmorCap(ModItems.FANG_BOAR_HELM.get(), 8F, 12F));
		
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