package com.skullmangames.darksouls.network;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.network.client.CTSHuman;
import com.skullmangames.darksouls.network.client.CTSHumanity;
import com.skullmangames.darksouls.network.client.CTSPlayAnimation;
import com.skullmangames.darksouls.network.client.CTSReqPlayerInfo;
import com.skullmangames.darksouls.network.client.CTSReqSpawnInfo;
import com.skullmangames.darksouls.network.client.CTSRotatePlayerYaw;
import com.skullmangames.darksouls.network.client.CTSSouls;
import com.skullmangames.darksouls.network.client.CTSStamina;
import com.skullmangames.darksouls.network.client.CTSStat;
import com.skullmangames.darksouls.network.server.STCGameruleChange;
import com.skullmangames.darksouls.network.server.STCHuman;
import com.skullmangames.darksouls.network.server.STCHumanity;
import com.skullmangames.darksouls.network.server.STCLivingMotionChange;
import com.skullmangames.darksouls.network.server.STCMobInitialSetting;
import com.skullmangames.darksouls.network.server.STCNotifyPlayerYawChanged;
import com.skullmangames.darksouls.network.server.STCPlayAnimation;
import com.skullmangames.darksouls.network.server.STCPlayAnimationTP;
import com.skullmangames.darksouls.network.server.STCPlayAnimationTarget;
import com.skullmangames.darksouls.network.server.STCPotion;
import com.skullmangames.darksouls.network.server.STCSouls;
import com.skullmangames.darksouls.network.server.STCStamina;
import com.skullmangames.darksouls.network.server.STCStat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ModNetworkManager
{
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(DarkSouls.MOD_ID, "network_manager"),
			() -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	public static <MSG> void sendToServer(MSG message)
	{
		INSTANCE.sendToServer(message);
	}
	
	public static <MSG> void sendToAll(MSG message)
	{
		INSTANCE.send(PacketDistributor.ALL.noArg(), message);
	}

	public static <MSG> void sendToAllPlayerTrackingThisEntity(MSG message, Entity entity)
	{
		INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> {return entity;}), message);
	}
	
	public static <MSG> void sendToPlayer(MSG message, ServerPlayerEntity player)
	{
		INSTANCE.send(PacketDistributor.PLAYER.with(() -> {return player;}), message);
	}

	public static <MSG> void sendToAllPlayerTrackingThisEntityWithSelf(MSG message, ServerPlayerEntity entity)
	{
		sendToPlayer(message, entity);
		INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> {return entity;}), message);
	}
	
	public static void registerPackets()
	{
		int id = 0;
		INSTANCE.registerMessage(id++, CTSPlayAnimation.class, CTSPlayAnimation::toBytes, CTSPlayAnimation::fromBytes, CTSPlayAnimation::handle);
		INSTANCE.registerMessage(id++, CTSReqSpawnInfo.class, CTSReqSpawnInfo::toBytes, CTSReqSpawnInfo::fromBytes, CTSReqSpawnInfo::handle);
		INSTANCE.registerMessage(id++, CTSRotatePlayerYaw.class, CTSRotatePlayerYaw::toBytes, CTSRotatePlayerYaw::fromBytes, CTSRotatePlayerYaw::handle);
		INSTANCE.registerMessage(id++, CTSReqPlayerInfo.class, CTSReqPlayerInfo::toBytes, CTSReqPlayerInfo::fromBytes, CTSReqPlayerInfo::handle);
		INSTANCE.registerMessage(id++, CTSStamina.class, CTSStamina::toBytes, CTSStamina::fromBytes, CTSStamina::handle);
		INSTANCE.registerMessage(id++, CTSHumanity.class, CTSHumanity::toBytes, CTSHumanity::fromBytes, CTSHumanity::handle);
		INSTANCE.registerMessage(id++, CTSHuman.class, CTSHuman::toBytes, CTSHuman::fromBytes, CTSHuman::handle);
		INSTANCE.registerMessage(id++, CTSSouls.class, CTSSouls::toBytes, CTSSouls::fromBytes, CTSSouls::handle);
		INSTANCE.registerMessage(id++, CTSStat.class, CTSStat::toBytes, CTSStat::fromBytes, CTSStat::handle);
		
		INSTANCE.registerMessage(id++, STCMobInitialSetting.class, STCMobInitialSetting::toBytes, STCMobInitialSetting::fromBytes, STCMobInitialSetting::handle);
		INSTANCE.registerMessage(id++, STCLivingMotionChange.class, STCLivingMotionChange::toBytes, STCLivingMotionChange::fromBytes, STCLivingMotionChange::handle);
		INSTANCE.registerMessage(id++, STCNotifyPlayerYawChanged.class, STCNotifyPlayerYawChanged::toBytes, STCNotifyPlayerYawChanged::fromBytes, STCNotifyPlayerYawChanged::handle);
		INSTANCE.registerMessage(id++, STCPlayAnimation.class, STCPlayAnimation::toBytes, STCPlayAnimation::fromBytes, STCPlayAnimation::handle);
		INSTANCE.registerMessage(id++, STCPlayAnimationTarget.class, STCPlayAnimationTarget::toBytes, STCPlayAnimationTarget::fromBytes, STCPlayAnimationTarget::handle);
		INSTANCE.registerMessage(id++, STCPlayAnimationTP.class, STCPlayAnimationTP::toBytes, STCPlayAnimationTP::fromBytes, STCPlayAnimationTP::handle);
		INSTANCE.registerMessage(id++, STCPotion.class, STCPotion::toBytes, STCPotion::fromBytes, STCPotion::handle);
		INSTANCE.registerMessage(id++, STCGameruleChange.class, STCGameruleChange::toBytes, STCGameruleChange::fromBytes, STCGameruleChange::handle);
		INSTANCE.registerMessage(id++, STCStamina.class, STCStamina::toBytes, STCStamina::fromBytes, STCStamina::handle);
		INSTANCE.registerMessage(id++, STCHumanity.class, STCHumanity::toBytes, STCHumanity::fromBytes, STCHumanity::handle);
		INSTANCE.registerMessage(id++, STCHuman.class, STCHuman::toBytes, STCHuman::fromBytes, STCHuman::handle);
		INSTANCE.registerMessage(id++, STCSouls.class, STCSouls::toBytes, STCSouls::fromBytes, STCSouls::handle);
		INSTANCE.registerMessage(id++, STCStat.class, STCStat::toBytes, STCStat::fromBytes, STCStat::handle);
	}
}