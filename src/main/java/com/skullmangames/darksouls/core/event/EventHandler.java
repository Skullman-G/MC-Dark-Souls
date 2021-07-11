package com.skullmangames.darksouls.core.event;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.gui.GameOverlayManager;
import com.skullmangames.darksouls.client.renderer.FirstPersonRendererOverride;
import com.skullmangames.darksouls.common.effects.UndeadCurse;
import com.skullmangames.darksouls.common.entities.HealthDataManager;
import com.skullmangames.darksouls.common.entities.ModEntityDataManager;
import com.skullmangames.darksouls.common.entities.SoulEntity;
import com.skullmangames.darksouls.common.entities.StaminaDataManager;
import com.skullmangames.darksouls.common.items.IHaveDarkSoulsUseAction;
import com.skullmangames.darksouls.core.init.EffectInit;
import com.skullmangames.darksouls.core.init.ItemInit;
import com.skullmangames.darksouls.core.util.CursedFoodStats;
import com.skullmangames.darksouls.server.DedicatedPlayerListOverride;
import com.skullmangames.darksouls.server.IntegratedPlayerListOverride;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.living.PotionEvent.PotionAddedEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionExpiryEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionRemoveEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

@EventBusSubscriber(modid = DarkSouls.MOD_ID, bus = Bus.FORGE)
public class EventHandler
{
	@SubscribeEvent
	public static void onItemToss(final ItemTossEvent event)
    {
		if (event.getEntityItem().getItem().getItem() == ItemInit.DARKSIGN.get())
		{
			event.getPlayer().addItem(event.getEntityItem().getItem());
		}
    }
	
	@SubscribeEvent
	public static void onEntityJoinWorld(final EntityJoinWorldEvent event)
    {
		if (event.getEntity() instanceof ItemEntity && ((ItemEntity)event.getEntity()).getItem().getItem() == ItemInit.DARKSIGN.get())
		{
			event.setCanceled(true);
			return;
		}
		
		if (event.getEntity() instanceof PlayerEntity)
		{
			if (!((PlayerEntity)event.getEntity()).hasEffect(EffectInit.UNDEAD_CURSE.get()))
			{
				EffectInstance effectinstance = new EffectInstance(EffectInit.UNDEAD_CURSE.get(), 1000000000);
				((LivingEntity)event.getEntity()).addEffect(effectinstance);
			}
			
			((PlayerEntity)event.getEntity()).foodData = new CursedFoodStats();
		}
		
		if (event.getEntity() instanceof LivingEntity)
		{
			HealthDataManager.initMaxHealth((LivingEntity)event.getEntity());
			StaminaDataManager.initMaxStamina((LivingEntity)event.getEntity());
		}
    }
	
	@SubscribeEvent
	public static void onServerAboutToStart(final FMLServerAboutToStartEvent event)
    {
		MinecraftServer server = event.getServer();
		
		if (server instanceof DedicatedServer)
		{
			server.setPlayerList(new DedicatedPlayerListOverride((DedicatedServer)server, server.registryHolder, server.playerDataStorage));
		}
		else if (server instanceof IntegratedServer)
		{
			server.setPlayerList(new IntegratedPlayerListOverride((IntegratedServer)server, server.registryHolder, server.playerDataStorage));
		}
    }
	
	@SubscribeEvent
	public static void onPotionAdd(final PotionAddedEvent event)
    {
		if (event.getPotionEffect().getEffect() instanceof UndeadCurse && /*event.getPotionEffect().getEffect() != event.getOldPotionEffect().getEffect() &&*/ event.getEntityLiving() instanceof PlayerEntity)
		{
			EffectInit.UNDEAD_CURSE.get().onPotionAdd(((PlayerEntity)event.getEntityLiving()));
		}
    }
	
	@SubscribeEvent
	public static void onPotionRemove(final PotionRemoveEvent event)
    {
		if (event.getPotion() instanceof UndeadCurse && event.getEntityLiving() instanceof PlayerEntity)
		{
			EffectInit.UNDEAD_CURSE.get().onPotionRemove(((PlayerEntity)event.getEntityLiving()));
		}
    }
	
	@SubscribeEvent
	public static void onPotionExpire(final PotionExpiryEvent event)
    {
		if (event.getPotionEffect().getEffect() instanceof UndeadCurse && event.getEntityLiving() instanceof PlayerEntity)
		{
			EffectInit.UNDEAD_CURSE.get().onPotionRemove(((PlayerEntity)event.getEntityLiving()));
		}
    }
	
	@SubscribeEvent
	public static void onRenderHand(final RenderHandEvent event)
	{
		if (event.getItemStack().getItem() instanceof IHaveDarkSoulsUseAction)
		{
			event.setCanceled(true);
			Minecraft minecraft = Minecraft.getInstance();
			IHaveDarkSoulsUseAction item = (IHaveDarkSoulsUseAction)event.getItemStack().getItem();
			FirstPersonRendererOverride.renderArmWithItem(item, event.getSwingProgress(), event.getPartialTicks(), event.getEquipProgress(), event.getHand(), event.getItemStack(), event.getMatrixStack(), event.getBuffers(), minecraft.getEntityRenderDispatcher().getPackedLightCoords(minecraft.player, event.getPartialTicks()));
		}
	}
	
	@SubscribeEvent
	public static void onLivingDeath(final LivingDeathEvent event)
	{
		ModEntityDataManager.setHumanity(event.getEntityLiving(), 0);
		
		if (event.getEntityLiving().hasEffect(EffectInit.UNDEAD_CURSE.get()))
		{
			ModEntityDataManager.setHuman(event.getEntityLiving(), false);
		}
		
		System.out.print(event.getEntityLiving().getName());
	}
	
	@SubscribeEvent
	public static void onCheckSpawn(final CheckSpawn event)
	{
		if (event.getEntityLiving() instanceof ZombieEntity)
		{
			event.setResult(Result.DENY);
		}
	}
	
	@SubscribeEvent
	public static void onLivingHeal(final LivingHealEvent event)
	{
		Minecraft minecraft = Minecraft.getInstance();
		if (event.getEntityLiving().getUUID() == minecraft.player.getUUID() && !event.getEntityLiving().isSpectator())
		{
			GameOverlayManager.isHealing = true;
		}
	}
	
	@SubscribeEvent
	public static void onLivingExperienceDrop(final LivingExperienceDropEvent event)
	{
		LivingEntity entity = event.getEntityLiving();
		entity.level.addFreshEntity(new SoulEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), 1));
		event.setCanceled(true);
	}
}
