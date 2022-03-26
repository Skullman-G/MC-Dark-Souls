package com.skullmangames.darksouls.core.event;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.capability.entity.EntityCapability;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.projectile.CapabilityProjectile;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ProviderEntity;
import com.skullmangames.darksouls.core.init.ProviderItem;
import com.skullmangames.darksouls.core.init.ProviderProjectile;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DarkSouls.MOD_ID)
public class CapabilityEvents
{
	@SubscribeEvent
	public void registerCaps(RegisterCapabilitiesEvent event)
	{
	    event.register(EntityCapability.class);
	    event.register(ItemCapability.class);
	    event.register(CapabilityProjectile.class);
	}
	
	@SubscribeEvent
	public static void attachItemCapability(AttachCapabilitiesEvent<ItemStack> event)
	{
		ProviderItem prov = new ProviderItem(event.getObject());
		if (prov.hasCapability())
		{
			event.addCapability(new ResourceLocation(DarkSouls.MOD_ID, "item_cap"), prov);
		}
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@SubscribeEvent
	public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> event)
	{
		if (event.getObject().getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null) == null)
		{
			ProviderEntity prov = new ProviderEntity(event.getObject());
			if (prov.hasCapability())
			{
				EntityCapability entityCap = prov.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
				entityCap.onEntityConstructed(event.getObject());
				event.addCapability(new ResourceLocation(DarkSouls.MOD_ID, "entity_cap"), prov);
			}
		}

		if (event.getObject() instanceof Projectile)
		{
			Projectile projectile = ((Projectile) event.getObject());
			if (event.getObject().getCapability(ModCapabilities.CAPABILITY_PROJECTILE).orElse(null) == null)
			{
				ProviderProjectile prov = new ProviderProjectile(projectile);
				if (prov.hasCapability())
				{
					event.addCapability(new ResourceLocation(DarkSouls.MOD_ID, "projectile_cap"), prov);
				}
			}
		}
	}
}