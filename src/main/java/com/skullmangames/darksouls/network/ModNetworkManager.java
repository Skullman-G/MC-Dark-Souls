package com.skullmangames.darksouls.network;

import java.util.function.Function;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.network.packets.NetworkPacket;
import com.skullmangames.darksouls.network.packets.client.CTSBonfireTask;
import com.skullmangames.darksouls.network.packets.client.CTSCastSpell;
import com.skullmangames.darksouls.network.packets.client.CTSCovenant;
import com.skullmangames.darksouls.network.packets.client.CTSFinishNPCChat;
import com.skullmangames.darksouls.network.packets.client.CTSLevelUp;
import com.skullmangames.darksouls.network.packets.client.CTSOpenAttunementScreen;
import com.skullmangames.darksouls.network.packets.client.CTSOpenBonfireTeleportScreen;
import com.skullmangames.darksouls.network.packets.client.CTSOpenFireKeeperContainer;
import com.skullmangames.darksouls.network.packets.client.CTSPerformDodge;
import com.skullmangames.darksouls.network.packets.client.CTSPlayAnimation;
import com.skullmangames.darksouls.network.packets.client.CTSReqSpawnInfo;
import com.skullmangames.darksouls.network.packets.client.CTSSelectTrade;
import com.skullmangames.darksouls.network.packets.client.CTSTeleportPlayer;
import com.skullmangames.darksouls.network.packets.client.CTSTwoHanding;
import com.skullmangames.darksouls.network.packets.server.STCAttunements;
import com.skullmangames.darksouls.network.packets.server.STCBonfireKindleEffect;
import com.skullmangames.darksouls.network.packets.server.STCChangeEquipment;
import com.skullmangames.darksouls.network.packets.server.STCCovenant;
import com.skullmangames.darksouls.network.packets.server.STCCovenantProgress;
import com.skullmangames.darksouls.network.packets.server.STCEntityBloodImpactParticles;
import com.skullmangames.darksouls.network.packets.server.STCEntityImpactParticles;
import com.skullmangames.darksouls.network.packets.server.STCFP;
import com.skullmangames.darksouls.network.packets.server.STCHuman;
import com.skullmangames.darksouls.network.packets.server.STCHumanity;
import com.skullmangames.darksouls.network.packets.server.STCLivingMotionChange;
import com.skullmangames.darksouls.network.packets.server.STCLoadPlayerData;
import com.skullmangames.darksouls.network.packets.server.STCMobInitialSetting;
import com.skullmangames.darksouls.network.packets.server.STCNPCChat;
import com.skullmangames.darksouls.network.packets.server.STCNotifyPlayerYawChanged;
import com.skullmangames.darksouls.network.packets.server.STCPlayAnimation;
import com.skullmangames.darksouls.network.packets.server.STCPlayAnimationAndSetTarget;
import com.skullmangames.darksouls.network.packets.server.STCPlayBonfireAmbientSound;
import com.skullmangames.darksouls.network.packets.server.STCPotion;
import com.skullmangames.darksouls.network.packets.server.STCSetMaxPlayerLevel;
import com.skullmangames.darksouls.network.packets.server.STCSetPos;
import com.skullmangames.darksouls.network.packets.server.STCSoulMerchantOffers;
import com.skullmangames.darksouls.network.packets.server.STCSouls;
import com.skullmangames.darksouls.network.packets.server.STCStamina;
import com.skullmangames.darksouls.network.packets.server.STCStat;
import com.skullmangames.darksouls.network.packets.server.gui.STCOpenBonfireNameScreen;
import com.skullmangames.darksouls.network.packets.server.gui.STCOpenBonfireScreen;
import com.skullmangames.darksouls.network.packets.server.gui.STCOpenBonfireTeleportScreen;
import com.skullmangames.darksouls.network.packets.server.gui.STCOpenCovenantScreen;
import com.skullmangames.darksouls.network.packets.server.gui.STCOpenFireKeeperScreen;
import com.skullmangames.darksouls.network.packets.server.gui.STCOpenJoinCovenantScreen;
import com.skullmangames.darksouls.network.play.ModPlayNetHandler;

import net.minecraft.network.FriendlyByteBuf;
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
	public static ModPlayNetHandler connection = new ModPlayNetHandler();

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
	
	public static <MSG extends NetworkPacket> void registerMessage(int index, Class<MSG> messageType,
			Function<FriendlyByteBuf, MSG> decoder)
	{
		INSTANCE.registerMessage(index, messageType,
				(msg, buf) -> msg.encode(buf), decoder, (msg, ctx) -> msg.handle(ctx));
	}
	
	public static void registerPackets()
	{
		int id = 0;
		registerMessage(id++, CTSPlayAnimation.class, CTSPlayAnimation::new);
		registerMessage(id++, CTSReqSpawnInfo.class, CTSReqSpawnInfo::new);
		registerMessage(id++, CTSBonfireTask.class, CTSBonfireTask::new);
		registerMessage(id++, CTSOpenFireKeeperContainer.class, CTSOpenFireKeeperContainer::new);
		registerMessage(id++, CTSPerformDodge.class, CTSPerformDodge::new);
		registerMessage(id++, CTSLevelUp.class, CTSLevelUp::new);
		registerMessage(id++, CTSFinishNPCChat.class, CTSFinishNPCChat::new);
		registerMessage(id++, CTSOpenAttunementScreen.class, CTSOpenAttunementScreen::new);
		registerMessage(id++, CTSCastSpell.class, CTSCastSpell::new);
		registerMessage(id++, CTSSelectTrade.class, CTSSelectTrade::new);
		registerMessage(id++, CTSCovenant.class, CTSCovenant::new);
		registerMessage(id++, CTSTeleportPlayer.class, CTSTeleportPlayer::new);
		registerMessage(id++, CTSOpenBonfireTeleportScreen.class, CTSOpenBonfireTeleportScreen::new);
		registerMessage(id++, CTSTwoHanding.class, CTSTwoHanding::new);
		
		registerMessage(id++, STCMobInitialSetting.class, STCMobInitialSetting::new);
		registerMessage(id++, STCLivingMotionChange.class, STCLivingMotionChange::new);
		registerMessage(id++, STCNotifyPlayerYawChanged.class, STCNotifyPlayerYawChanged::new);
		registerMessage(id++, STCPlayAnimation.class, STCPlayAnimation::new);
		registerMessage(id++, STCPlayAnimationAndSetTarget.class, STCPlayAnimationAndSetTarget::new);
		registerMessage(id++, STCPotion.class, STCPotion::new);
		registerMessage(id++, STCStamina.class, STCStamina::new);
		registerMessage(id++, STCHumanity.class, STCHumanity::new);
		registerMessage(id++, STCHuman.class, STCHuman::new);
		registerMessage(id++, STCSouls.class, STCSouls::new);
		registerMessage(id++, STCStat.class, STCStat::new);
		registerMessage(id++, STCOpenBonfireNameScreen.class, STCOpenBonfireNameScreen::new);
		registerMessage(id++, STCOpenBonfireScreen.class, STCOpenBonfireScreen::new);
		registerMessage(id++, STCLoadPlayerData.class, STCLoadPlayerData::new);
		registerMessage(id++, STCPlayBonfireAmbientSound.class, STCPlayBonfireAmbientSound::new);
		registerMessage(id++, STCSetPos.class, STCSetPos::new);
		registerMessage(id++, STCNPCChat.class, STCNPCChat::new);
		registerMessage(id++, STCOpenFireKeeperScreen.class, STCOpenFireKeeperScreen::new);
		registerMessage(id++, STCFP.class, STCFP::new);
		registerMessage(id++, STCAttunements.class, STCAttunements::new);
		registerMessage(id++, STCSoulMerchantOffers.class, STCSoulMerchantOffers::new);
		registerMessage(id++, STCCovenant.class, STCCovenant::new);
		registerMessage(id++, STCOpenJoinCovenantScreen.class, STCOpenJoinCovenantScreen::new);
		registerMessage(id++, STCOpenCovenantScreen.class, STCOpenCovenantScreen::new);
		registerMessage(id++, STCCovenantProgress.class, STCCovenantProgress::new);
		registerMessage(id++, STCOpenBonfireTeleportScreen.class, STCOpenBonfireTeleportScreen::new);
		registerMessage(id++, STCBonfireKindleEffect.class, STCBonfireKindleEffect::new);
		registerMessage(id++, STCEntityImpactParticles.class, STCEntityImpactParticles::new);
		registerMessage(id++, STCChangeEquipment.class, STCChangeEquipment::new);
		registerMessage(id++, STCSetMaxPlayerLevel.class, STCSetMaxPlayerLevel::new);
		registerMessage(id++, STCEntityBloodImpactParticles.class, STCEntityBloodImpactParticles::new);
	}
}