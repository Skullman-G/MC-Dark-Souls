package com.skullmangames.darksouls.network;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.network.client.CTSHuman;
import com.skullmangames.darksouls.network.client.CTSHumanity;
import com.skullmangames.darksouls.network.client.CTSLevelUp;
import com.skullmangames.darksouls.network.client.CTSOpenFireKeeperContainer;
import com.skullmangames.darksouls.network.client.CTSPerformDodge;
import com.skullmangames.darksouls.network.client.CTSPlayAnimation;
import com.skullmangames.darksouls.network.client.CTSReqPlayerInfo;
import com.skullmangames.darksouls.network.client.CTSReqSpawnInfo;
import com.skullmangames.darksouls.network.client.CTSRotatePlayerYaw;
import com.skullmangames.darksouls.network.client.CTSUpdateBonfireBlock;
import com.skullmangames.darksouls.network.play.IModClientPlayNetHandler;
import com.skullmangames.darksouls.network.server.STCHuman;
import com.skullmangames.darksouls.network.server.STCHumanity;
import com.skullmangames.darksouls.network.server.STCLivingMotionChange;
import com.skullmangames.darksouls.network.server.STCLoadPlayerData;
import com.skullmangames.darksouls.network.server.STCMobInitialSetting;
import com.skullmangames.darksouls.network.server.STCNotifyPlayerYawChanged;
import com.skullmangames.darksouls.network.server.STCPlayAnimation;
import com.skullmangames.darksouls.network.server.STCPlayAnimationTP;
import com.skullmangames.darksouls.network.server.STCPlayAnimationTarget;
import com.skullmangames.darksouls.network.server.STCPotion;
import com.skullmangames.darksouls.network.server.STCSouls;
import com.skullmangames.darksouls.network.server.STCStamina;
import com.skullmangames.darksouls.network.server.STCStat;
import com.skullmangames.darksouls.network.server.gui.STCOpenBonfireNameScreen;
import com.skullmangames.darksouls.network.server.gui.STCOpenBonfireScreen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworkManager
{
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(DarkSouls.MOD_ID, "network_manager"),
			() -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	public static IModClientPlayNetHandler connection;

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
	
	public static <MSG> void sendToPlayer(MSG message, ServerPlayer player)
	{
		INSTANCE.send(PacketDistributor.PLAYER.with(() -> {return player;}), message);
	}

	public static <MSG> void sendToAllPlayerTrackingThisEntityWithSelf(MSG message, ServerPlayer entity)
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
		INSTANCE.registerMessage(id++, CTSHumanity.class, CTSHumanity::toBytes, CTSHumanity::fromBytes, CTSHumanity::handle);
		INSTANCE.registerMessage(id++, CTSHuman.class, CTSHuman::toBytes, CTSHuman::fromBytes, CTSHuman::handle);
		INSTANCE.registerMessage(id++, CTSUpdateBonfireBlock.class, CTSUpdateBonfireBlock::toBytes, CTSUpdateBonfireBlock::fromBytes, CTSUpdateBonfireBlock::handle);
		INSTANCE.registerMessage(id++, CTSOpenFireKeeperContainer.class, CTSOpenFireKeeperContainer::toBytes, CTSOpenFireKeeperContainer::fromBytes, CTSOpenFireKeeperContainer::handle);
		INSTANCE.registerMessage(id++, CTSPerformDodge.class, CTSPerformDodge::toBytes, CTSPerformDodge::fromBytes, CTSPerformDodge::handle);
		INSTANCE.registerMessage(id++, CTSLevelUp.class, CTSLevelUp::toBytes, CTSLevelUp::fromBytes, CTSLevelUp::handle);
		
		INSTANCE.registerMessage(id++, STCMobInitialSetting.class, STCMobInitialSetting::toBytes, STCMobInitialSetting::fromBytes, STCMobInitialSetting::handle);
		INSTANCE.registerMessage(id++, STCLivingMotionChange.class, STCLivingMotionChange::toBytes, STCLivingMotionChange::fromBytes, STCLivingMotionChange::handle);
		INSTANCE.registerMessage(id++, STCNotifyPlayerYawChanged.class, STCNotifyPlayerYawChanged::toBytes, STCNotifyPlayerYawChanged::fromBytes, STCNotifyPlayerYawChanged::handle);
		INSTANCE.registerMessage(id++, STCPlayAnimation.class, STCPlayAnimation::toBytes, STCPlayAnimation::fromBytes, STCPlayAnimation::handle);
		INSTANCE.registerMessage(id++, STCPlayAnimationTarget.class, STCPlayAnimationTarget::toBytes, STCPlayAnimationTarget::fromBytes, STCPlayAnimationTarget::handle);
		INSTANCE.registerMessage(id++, STCPlayAnimationTP.class, STCPlayAnimationTP::toBytes, STCPlayAnimationTP::fromBytes, STCPlayAnimationTP::handle);
		INSTANCE.registerMessage(id++, STCPotion.class, STCPotion::toBytes, STCPotion::fromBytes, STCPotion::handle);
		INSTANCE.registerMessage(id++, STCStamina.class, STCStamina::toBytes, STCStamina::fromBytes, STCStamina::handle);
		INSTANCE.registerMessage(id++, STCHumanity.class, STCHumanity::toBytes, STCHumanity::fromBytes, STCHumanity::handle);
		INSTANCE.registerMessage(id++, STCHuman.class, STCHuman::toBytes, STCHuman::fromBytes, STCHuman::handle);
		INSTANCE.registerMessage(id++, STCSouls.class, STCSouls::toBytes, STCSouls::fromBytes, STCSouls::handle);
		INSTANCE.registerMessage(id++, STCStat.class, STCStat::toBytes, STCStat::fromBytes, STCStat::handle);
		INSTANCE.registerMessage(id++, STCOpenBonfireNameScreen.class, STCOpenBonfireNameScreen::toBytes, STCOpenBonfireNameScreen::fromBytes, STCOpenBonfireNameScreen::handle);
		INSTANCE.registerMessage(id++, STCOpenBonfireScreen.class, STCOpenBonfireScreen::toBytes, STCOpenBonfireScreen::fromBytes, STCOpenBonfireScreen::handle);
		INSTANCE.registerMessage(id++, STCLoadPlayerData.class, STCLoadPlayerData::toBytes, STCLoadPlayerData::fromBytes, STCLoadPlayerData::handle);
	}
}