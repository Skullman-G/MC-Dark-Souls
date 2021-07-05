package com.skullmangames.darksouls.common.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.util.text.TranslationTextComponent;

public class ModEntityDataManager
{
	public static void setHumanity(LivingEntity livingentity, int value)
	{
		livingentity.getPersistentData().putInt("Humanity", value);
	}
	
	public static void raiseHumanity(LivingEntity livingentity, int raise)
	{
		int currentvalue = getHumanity(livingentity);
		setHumanity(livingentity, currentvalue + raise);
	}
	
	public static void shrinkHumanity(LivingEntity livingentity, int shrink)
	{
		int currentvalue = getHumanity(livingentity);
		setHumanity(livingentity, currentvalue - shrink);
	}
	
	public static int getHumanity(LivingEntity livingentity)
	{
		if (livingentity instanceof ClientPlayerEntity) livingentity = getServerPlayer((ClientPlayerEntity)livingentity);
		return livingentity.getPersistentData().getInt("Humanity");
	}
	
	public static boolean isHuman(LivingEntity livingentity)
	{
		if (livingentity instanceof ClientPlayerEntity) livingentity = getServerPlayer((ClientPlayerEntity)livingentity);
		return livingentity.getPersistentData().getBoolean("IsHuman");
	}
	
	public static void setHuman(LivingEntity livingentity, boolean value)
	{
		boolean currentvalue = isHuman(livingentity);
		
		if (currentvalue != value)
		{
			livingentity.getPersistentData().putBoolean("IsHuman", value);
			
			if (value && livingentity instanceof ServerPlayerEntity)
			{
				((ServerPlayerEntity)livingentity).connection.send(new STitlePacket(STitlePacket.Type.TITLE, new TranslationTextComponent("gui.darksouls.humanity_restored_message")));
			}
		}
	}
	
	public static String getStringHumanity(LivingEntity livingentity)
	{
		return Integer.toString(getHumanity(livingentity));
	}
	
	private static ServerPlayerEntity getServerPlayer(ClientPlayerEntity clientplayer)
	{
		return Minecraft.getInstance().getSingleplayerServer().getPlayerList().getPlayer(clientplayer.getUUID());
	}
}
