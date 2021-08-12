package com.skullmangames.darksouls.core.event;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.gui.GameOverlayManager;
import com.skullmangames.darksouls.client.renderer.FirstPersonRendererOverride;
import com.skullmangames.darksouls.common.entity.ModEntityDataManager;
import com.skullmangames.darksouls.common.entity.SoulEntity;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.common.item.IHaveDarkSoulsUseAction;
import com.skullmangames.darksouls.common.potion.effect.UndeadCurse;
import com.skullmangames.darksouls.core.init.CommandInit;
import com.skullmangames.darksouls.core.init.EffectInit;
import com.skullmangames.darksouls.core.init.ItemInit;
import com.skullmangames.darksouls.core.util.CursedFoodStats;
import com.skullmangames.darksouls.server.DedicatedPlayerListOverride;
import com.skullmangames.darksouls.server.IntegratedPlayerListOverride;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.living.PotionEvent.PotionAddedEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionExpiryEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionRemoveEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
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
	public static void onRegisterCommands(final RegisterCommandsEvent event)
    {
		CommandInit.register(event.getDispatcher());
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
			PlayerEntity player = (PlayerEntity)event.getEntity();
			
			player.foodData = new CursedFoodStats();
			
			if (!player.hasEffect(EffectInit.UNDEAD_CURSE.get()))
			{
				EffectInstance effectinstance = new EffectInstance(EffectInit.UNDEAD_CURSE.get(), 1000000000);
				player.addEffect(effectinstance);
			}
		}
		
		if (event.getEntity() instanceof LivingEntity)
		{
			LivingEntity livingentity = (LivingEntity)event.getEntity();
			for (Stat stat : Stats.getStats())
			{
				stat.init(livingentity);
			}
			//StaminaDataManager.initMaxStamina(livingentity);
		}
    }
	
	@SubscribeEvent
	public static void onPlayerClone(final Clone event)
    {
		// Copy Stats
		for (Stat stat : Stats.getStats())
		{
			stat.setValue(event.getPlayer(), stat.getValue(event.getOriginal()));
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
		if (event.getEntityLiving() instanceof PlayerEntity && event.getEntityLiving().getUUID() == minecraft.player.getUUID() && !event.getEntityLiving().isSpectator())
		{
			GameOverlayManager.isHealing = true;
		}
	}
	
	@SubscribeEvent
	public static void onLivingExperienceDrop(final LivingExperienceDropEvent event)
	{
		LivingEntity entity = event.getEntityLiving();
		entity.level.addFreshEntity(new SoulEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), event.getDroppedExperience()));
	}
	
	@SubscribeEvent
	public static void onFOVUpdate(final FOVUpdateEvent event)
	{
		 PlayerEntity player = event.getEntity();
		float f = 1.0F;
	      if (player.abilities.flying) {
	         f *= 1.1F;
	      }

	      f = (float)((double)f * ((player.getAttributeValue(Attributes.MOVEMENT_SPEED) / (double)player.abilities.getWalkingSpeed() + 1.0D) / 2.0D));
	      if (player.getAttributeValue(Attributes.MOVEMENT_SPEED) == 0.0D || player.abilities.getWalkingSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
	         f = 1.0F;
	      }

	      if (player.isUsingItem() && player.getUseItem().getItem() == Items.BOW) {
	         int i = player.getTicksUsingItem();
	         float f1 = (float)i / 20.0F;
	         if (f1 > 1.0F) {
	            f1 = 1.0F;
	         } else {
	            f1 = f1 * f1;
	         }

	         f *= 1.0F - f1 * 0.15F;
	      }
	      
	     event.setNewfov(f);
	}
}
