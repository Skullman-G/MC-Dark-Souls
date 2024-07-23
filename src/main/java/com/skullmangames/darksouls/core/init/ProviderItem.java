package com.skullmangames.darksouls.core.init;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.skullmangames.darksouls.common.capability.item.BowCap;
import com.skullmangames.darksouls.common.capability.item.CrossbowCap;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.item.ThrowableCap;
import com.skullmangames.darksouls.common.capability.item.TridentCap;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.Scaling;
import com.skullmangames.darksouls.common.entity.projectile.Firebomb;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.projectile.Snowball;
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
		
		//Throwables
		putCap(new ThrowableCap(Items.SNOWBALL, Snowball::new, () -> SoundEvents.SNOWBALL_THROW));
		putCap(new ThrowableCap(ModItems.FIREBOMB.get(), Firebomb::firebomb, () -> SoundEvents.SNOWBALL_THROW));
		putCap(new ThrowableCap(ModItems.BLACK_FIREBOMB.get(), Firebomb::blackFirebomb, () -> SoundEvents.SNOWBALL_THROW));
		
		// CLASS
		CAPABILITY_BY_CLASS.put(Item.class, ItemCapability::new);
		
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