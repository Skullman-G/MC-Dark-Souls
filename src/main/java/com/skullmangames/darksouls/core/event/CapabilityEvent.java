package com.skullmangames.darksouls.core.event;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.capability.entity.EntityData;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ProviderEntity;
import com.skullmangames.darksouls.core.init.ProviderItem;
import com.skullmangames.darksouls.core.init.ProviderProjectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DarkSouls.MOD_ID)
public class CapabilityEvent
{
	@SubscribeEvent
	public static void attachItemCapability(AttachCapabilitiesEvent<ItemStack> event)
	{
		if (event.getObject().getCapability(ModCapabilities.CAPABILITY_ITEM).orElse(null) == null)
		{
			ProviderItem prov = new ProviderItem(event.getObject());
			if (prov.hasCapability())
			{
				event.addCapability(new ResourceLocation(DarkSouls.MOD_ID, "item_cap"), prov);
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SubscribeEvent
	public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> event)
	{
		if(event.getObject().getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null) == null)
		{
			ProviderEntity prov = new ProviderEntity(event.getObject());
			if(prov.hasCapability())
			{
				EntityData entityCap = prov.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
				entityCap.onEntityConstructed(event.getObject());
				event.addCapability(new ResourceLocation(DarkSouls.MOD_ID, "entity_cap"), prov);
			}
		}
		
		if (event.getObject() instanceof ProjectileEntity)
		{
			ProjectileEntity projectile = ((ProjectileEntity)event.getObject());
			if(event.getObject().getCapability(ModCapabilities.CAPABILITY_PROJECTILE).orElse(null) == null)
			{
				ProviderProjectile prov = new ProviderProjectile(projectile);
				if(prov.hasCapability())
				{
					event.addCapability(new ResourceLocation(DarkSouls.MOD_ID, "projectile_cap"), prov);
				}
			}
		}
	}
}