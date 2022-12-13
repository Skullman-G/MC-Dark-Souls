package com.skullmangames.darksouls.network;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.network.client.CTSBonfireTask;
import com.skullmangames.darksouls.network.client.CTSCastSpell;
import com.skullmangames.darksouls.network.client.CTSCovenant;
import com.skullmangames.darksouls.network.client.CTSFinishNPCChat;
import com.skullmangames.darksouls.network.client.CTSLevelUp;
import com.skullmangames.darksouls.network.client.CTSOpenAttunementScreen;
import com.skullmangames.darksouls.network.client.CTSOpenBonfireTeleportScreen;
import com.skullmangames.darksouls.network.client.CTSOpenFireKeeperContainer;
import com.skullmangames.darksouls.network.client.CTSPerformDodge;
import com.skullmangames.darksouls.network.client.CTSPlayAnimation;
import com.skullmangames.darksouls.network.client.CTSReqSpawnInfo;
import com.skullmangames.darksouls.network.client.CTSSelectTrade;
import com.skullmangames.darksouls.network.client.CTSTeleportPlayer;
import com.skullmangames.darksouls.network.play.ModPlayNetHandler;
import com.skullmangames.darksouls.network.server.STCAttunements;
import com.skullmangames.darksouls.network.server.STCBonfireKindleEffect;
import com.skullmangames.darksouls.network.server.STCCovenant;
import com.skullmangames.darksouls.network.server.STCCovenantProgress;
import com.skullmangames.darksouls.network.server.STCEntityImpactParticles;
import com.skullmangames.darksouls.network.server.STCFP;
import com.skullmangames.darksouls.network.server.STCHuman;
import com.skullmangames.darksouls.network.server.STCHumanity;
import com.skullmangames.darksouls.network.server.STCLivingMotionChange;
import com.skullmangames.darksouls.network.server.STCLoadPlayerData;
import com.skullmangames.darksouls.network.server.STCMobInitialSetting;
import com.skullmangames.darksouls.network.server.STCNPCChat;
import com.skullmangames.darksouls.network.server.STCNotifyPlayerYawChanged;
import com.skullmangames.darksouls.network.server.STCPlayAnimation;
import com.skullmangames.darksouls.network.server.STCPlayAnimationAndSetTarget;
import com.skullmangames.darksouls.network.server.STCPlayBonfireAmbientSound;
import com.skullmangames.darksouls.network.server.STCPotion;
import com.skullmangames.darksouls.network.server.STCSetPos;
import com.skullmangames.darksouls.network.server.STCSoulMerchantOffers;
import com.skullmangames.darksouls.network.server.STCSouls;
import com.skullmangames.darksouls.network.server.STCStamina;
import com.skullmangames.darksouls.network.server.STCStat;
import com.skullmangames.darksouls.network.server.gui.STCOpenBonfireNameScreen;
import com.skullmangames.darksouls.network.server.gui.STCOpenBonfireScreen;
import com.skullmangames.darksouls.network.server.gui.STCOpenBonfireTeleportScreen;
import com.skullmangames.darksouls.network.server.gui.STCOpenCovenantScreen;
import com.skullmangames.darksouls.network.server.gui.STCOpenFireKeeperScreen;
import com.skullmangames.darksouls.network.server.gui.STCOpenJoinCovenantScreen;

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
	public static ModPlayNetHandler connection;

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
		INSTANCE.registerMessage(id++, CTSBonfireTask.class, CTSBonfireTask::toBytes, CTSBonfireTask::fromBytes, CTSBonfireTask::handle);
		INSTANCE.registerMessage(id++, CTSOpenFireKeeperContainer.class, CTSOpenFireKeeperContainer::toBytes, CTSOpenFireKeeperContainer::fromBytes, CTSOpenFireKeeperContainer::handle);
		INSTANCE.registerMessage(id++, CTSPerformDodge.class, CTSPerformDodge::toBytes, CTSPerformDodge::fromBytes, CTSPerformDodge::handle);
		INSTANCE.registerMessage(id++, CTSLevelUp.class, CTSLevelUp::toBytes, CTSLevelUp::fromBytes, CTSLevelUp::handle);
		INSTANCE.registerMessage(id++, CTSFinishNPCChat.class, CTSFinishNPCChat::toBytes, CTSFinishNPCChat::fromBytes, CTSFinishNPCChat::handle);
		INSTANCE.registerMessage(id++, CTSOpenAttunementScreen.class, CTSOpenAttunementScreen::toBytes, CTSOpenAttunementScreen::fromBytes, CTSOpenAttunementScreen::handle);
		INSTANCE.registerMessage(id++, CTSCastSpell.class, CTSCastSpell::toBytes, CTSCastSpell::fromBytes, CTSCastSpell::handle);
		INSTANCE.registerMessage(id++, CTSSelectTrade.class, CTSSelectTrade::toBytes, CTSSelectTrade::fromBytes, CTSSelectTrade::handle);
		INSTANCE.registerMessage(id++, CTSCovenant.class, CTSCovenant::toBytes, CTSCovenant::fromBytes, CTSCovenant::handle);
		INSTANCE.registerMessage(id++, CTSTeleportPlayer.class, CTSTeleportPlayer::toBytes, CTSTeleportPlayer::fromBytes, CTSTeleportPlayer::handle);
		INSTANCE.registerMessage(id++, CTSOpenBonfireTeleportScreen.class, CTSOpenBonfireTeleportScreen::toBytes, CTSOpenBonfireTeleportScreen::fromBytes, CTSOpenBonfireTeleportScreen::handle);
		
		INSTANCE.registerMessage(id++, STCMobInitialSetting.class, STCMobInitialSetting::toBytes, STCMobInitialSetting::fromBytes, STCMobInitialSetting::handle);
		INSTANCE.registerMessage(id++, STCLivingMotionChange.class, STCLivingMotionChange::toBytes, STCLivingMotionChange::fromBytes, STCLivingMotionChange::handle);
		INSTANCE.registerMessage(id++, STCNotifyPlayerYawChanged.class, STCNotifyPlayerYawChanged::toBytes, STCNotifyPlayerYawChanged::fromBytes, STCNotifyPlayerYawChanged::handle);
		INSTANCE.registerMessage(id++, STCPlayAnimation.class, STCPlayAnimation::toBytes, STCPlayAnimation::fromBytes, STCPlayAnimation::handle);
		INSTANCE.registerMessage(id++, STCPlayAnimationAndSetTarget.class, STCPlayAnimationAndSetTarget::toBytes, STCPlayAnimationAndSetTarget::fromBytes, STCPlayAnimationAndSetTarget::handle);
		INSTANCE.registerMessage(id++, STCPotion.class, STCPotion::toBytes, STCPotion::fromBytes, STCPotion::handle);
		INSTANCE.registerMessage(id++, STCStamina.class, STCStamina::toBytes, STCStamina::fromBytes, STCStamina::handle);
		INSTANCE.registerMessage(id++, STCHumanity.class, STCHumanity::toBytes, STCHumanity::fromBytes, STCHumanity::handle);
		INSTANCE.registerMessage(id++, STCHuman.class, STCHuman::toBytes, STCHuman::fromBytes, STCHuman::handle);
		INSTANCE.registerMessage(id++, STCSouls.class, STCSouls::toBytes, STCSouls::fromBytes, STCSouls::handle);
		INSTANCE.registerMessage(id++, STCStat.class, STCStat::toBytes, STCStat::fromBytes, STCStat::handle);
		INSTANCE.registerMessage(id++, STCOpenBonfireNameScreen.class, STCOpenBonfireNameScreen::toBytes, STCOpenBonfireNameScreen::fromBytes, STCOpenBonfireNameScreen::handle);
		INSTANCE.registerMessage(id++, STCOpenBonfireScreen.class, STCOpenBonfireScreen::toBytes, STCOpenBonfireScreen::fromBytes, STCOpenBonfireScreen::handle);
		INSTANCE.registerMessage(id++, STCLoadPlayerData.class, STCLoadPlayerData::toBytes, STCLoadPlayerData::fromBytes, STCLoadPlayerData::handle);
		INSTANCE.registerMessage(id++, STCPlayBonfireAmbientSound.class, STCPlayBonfireAmbientSound::toBytes, STCPlayBonfireAmbientSound::fromBytes, STCPlayBonfireAmbientSound::handle);
		INSTANCE.registerMessage(id++, STCSetPos.class, STCSetPos::toBytes, STCSetPos::fromBytes, STCSetPos::handle);
		INSTANCE.registerMessage(id++, STCNPCChat.class, STCNPCChat::toBytes, STCNPCChat::fromBytes, STCNPCChat::handle);
		INSTANCE.registerMessage(id++, STCOpenFireKeeperScreen.class, STCOpenFireKeeperScreen::toBytes, STCOpenFireKeeperScreen::fromBytes, STCOpenFireKeeperScreen::handle);
		INSTANCE.registerMessage(id++, STCFP.class, STCFP::toBytes, STCFP::fromBytes, STCFP::handle);
		INSTANCE.registerMessage(id++, STCAttunements.class, STCAttunements::toBytes, STCAttunements::fromBytes, STCAttunements::handle);
		INSTANCE.registerMessage(id++, STCSoulMerchantOffers.class, STCSoulMerchantOffers::toBytes, STCSoulMerchantOffers::fromBytes, STCSoulMerchantOffers::handle);
		INSTANCE.registerMessage(id++, STCCovenant.class, STCCovenant::toBytes, STCCovenant::fromBytes, STCCovenant::handle);
		INSTANCE.registerMessage(id++, STCOpenJoinCovenantScreen.class, STCOpenJoinCovenantScreen::toBytes, STCOpenJoinCovenantScreen::fromBytes, STCOpenJoinCovenantScreen::handle);
		INSTANCE.registerMessage(id++, STCOpenCovenantScreen.class, STCOpenCovenantScreen::toBytes, STCOpenCovenantScreen::fromBytes, STCOpenCovenantScreen::handle);
		INSTANCE.registerMessage(id++, STCCovenantProgress.class, STCCovenantProgress::toBytes, STCCovenantProgress::fromBytes, STCCovenantProgress::handle);
		INSTANCE.registerMessage(id++, STCOpenBonfireTeleportScreen.class, STCOpenBonfireTeleportScreen::toBytes, STCOpenBonfireTeleportScreen::fromBytes, STCOpenBonfireTeleportScreen::handle);
		INSTANCE.registerMessage(id++, STCBonfireKindleEffect.class, STCBonfireKindleEffect::toBytes, STCBonfireKindleEffect::fromBytes, STCBonfireKindleEffect::handle);
		INSTANCE.registerMessage(id++, STCEntityImpactParticles.class, STCEntityImpactParticles::toBytes, STCEntityImpactParticles::fromBytes, STCEntityImpactParticles::handle);
	}
}