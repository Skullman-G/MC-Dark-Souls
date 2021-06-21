package com.skullmangames.darksouls.core.event;

import java.awt.Color;
import java.io.File;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.FirstPersonRendererOverride;
import com.skullmangames.darksouls.common.effects.UndeadCurse;
import com.skullmangames.darksouls.common.entities.DarkSoulsEntityData;
import com.skullmangames.darksouls.common.items.DarksignItem;
import com.skullmangames.darksouls.common.items.IHaveDarkSoulsUseAction;
import com.skullmangames.darksouls.core.init.EffectInit;
import com.skullmangames.darksouls.core.util.CursedFoodStats;
import com.skullmangames.darksouls.server.DedicatedPlayerListOverride;
import com.skullmangames.darksouls.server.IntegratedPlayerListOverride;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.potion.EffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Util;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.living.PotionEvent.PotionAddedEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionExpiryEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionRemoveEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.LoadFromFile;
import net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile;
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
		if (event.getEntityItem().getItem().getItem() instanceof DarksignItem)
		{
			event.getPlayer().addItem(event.getEntityItem().getItem());
		}
    }
	
	@SubscribeEvent
	public static void onEntityJoinWorld(final EntityJoinWorldEvent event)
    {
		if (event.getEntity() instanceof PlayerEntity)
		{
			if (!((PlayerEntity)event.getEntity()).hasEffect(EffectInit.UNDEAD_CURSE.get()))
			{
				EffectInstance effectinstance = new EffectInstance(EffectInit.UNDEAD_CURSE.get(), 1000000000);
				((LivingEntity)event.getEntity()).addEffect(effectinstance);
			}
			
			((PlayerEntity)event.getEntity()).foodData = new CursedFoodStats();
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
	public static void onRenderGameOverlay(final RenderGameOverlayEvent event)
	{
		Minecraft minecraft = Minecraft.getInstance();
		
		if (event.getType() == ElementType.ALL && !minecraft.player.isCreative() && !minecraft.player.isSpectator())
		{
			DarkSoulsEntityData humanity = DarkSoulsEntityData.get(minecraft.player);
			int x = event.getWindow().getGuiScaledWidth() / 2;
			int y = event.getWindow().getGuiScaledHeight() - 45;
			int color = humanity.isHuman() ? Color.WHITE.getRGB() : Color.LIGHT_GRAY.getRGB();
			
			ForgeIngameGui.drawCenteredString(event.getMatrixStack(), minecraft.font, humanity.getStringHumanity(), x, y, color);
		}
	}
	
	@SubscribeEvent
	public static void onSavePlayerData(final SaveToFile event)
	{
		try
		{
			PlayerEntity player = event.getPlayer();
			CompoundNBT compoundnbt = DarkSoulsEntityData.get(player).save(new CompoundNBT());
	        File file1 = File.createTempFile(player.getStringUUID() + "_DS-", ".dat", event.getPlayerDirectory());
	        CompressedStreamTools.writeCompressed(compoundnbt, file1);
	        File file2 = new File(event.getPlayerDirectory(), player.getStringUUID() + "_DS.dat");
	        File file3 = new File(event.getPlayerDirectory(), player.getStringUUID() + "_DS.dat_old");
	        Util.safeReplaceFile(file2, file1, file3);
		}
		catch (Exception exception)
		{
	         DarkSouls.LOGGER.warn("Failed to save player data for {}", (Object)event.getPlayer().getName().getString());
	    }
	}
	
	@SubscribeEvent
	public static void onLoadPlayerData(final LoadFromFile event)
	{
		CompoundNBT compoundnbt = null;
		PlayerEntity player = event.getPlayer();
		
		try
		{
			File file1 = new File(event.getPlayerDirectory(), player.getStringUUID() + "_DS.dat");
	        if (file1.exists() && file1.isFile())
	        {
	           compoundnbt = CompressedStreamTools.readCompressed(file1);
	        }
	    }
		catch (Exception exception)
		{
	        DarkSouls.LOGGER.warn("Failed to load player data for {}", (Object)player.getName().getString());
	    }

	    if (compoundnbt != null)
	    {
	    	DarkSoulsEntityData.get(player).load(compoundnbt);
	    }
	}
	
	@SubscribeEvent
	public static void onLivingDeath(final LivingDeathEvent event)
	{
		DarkSoulsEntityData data = DarkSoulsEntityData.get(event.getEntityLiving());
		data.setHumanity(0);
		
		if (event.getEntityLiving().hasEffect(EffectInit.UNDEAD_CURSE.get()))
		{
			data.setHuman(false);
		}
	}
	
	@SubscribeEvent
	public static void onCheckSpawn(final CheckSpawn event)
	{
		if (event.getEntityLiving() instanceof ZombieEntity)
		{
			event.setResult(Result.DENY);
		}
	}
}
