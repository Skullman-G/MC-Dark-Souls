package com.skullmangames.darksouls.network;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.network.client.CTSExecuteSkill;
import com.skullmangames.darksouls.network.client.CTSPlayAnimation;
import com.skullmangames.darksouls.network.client.CTSReqPlayerInfo;
import com.skullmangames.darksouls.network.client.CTSReqSpawnInfo;
import com.skullmangames.darksouls.network.client.CTSRotatePlayerYaw;
import com.skullmangames.darksouls.network.play.server.STCExecuteSkill;
import com.skullmangames.darksouls.network.play.server.STCGameruleChange;
import com.skullmangames.darksouls.network.play.server.STCLivingMotionChange;
import com.skullmangames.darksouls.network.play.server.STCMobInitialSetting;
import com.skullmangames.darksouls.network.play.server.STCModifySkillVariable;
import com.skullmangames.darksouls.network.play.server.STCNotifyPlayerYawChanged;
import com.skullmangames.darksouls.network.play.server.STCPlayAnimation;
import com.skullmangames.darksouls.network.play.server.STCPlayAnimationTP;
import com.skullmangames.darksouls.network.play.server.STCPlayAnimationTarget;
import com.skullmangames.darksouls.network.play.server.STCPotion;
import com.skullmangames.darksouls.network.play.server.STCResetBasicAttackCool;
import com.skullmangames.darksouls.network.play.server.STCSetSkillValue;

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
		INSTANCE.registerMessage(id++, CTSExecuteSkill.class, CTSExecuteSkill::toBytes, CTSExecuteSkill::fromBytes, CTSExecuteSkill::handle);
		INSTANCE.registerMessage(id++, CTSPlayAnimation.class, CTSPlayAnimation::toBytes, CTSPlayAnimation::fromBytes, CTSPlayAnimation::handle);
		INSTANCE.registerMessage(id++, CTSReqSpawnInfo.class, CTSReqSpawnInfo::toBytes, CTSReqSpawnInfo::fromBytes, CTSReqSpawnInfo::handle);
		INSTANCE.registerMessage(id++, CTSRotatePlayerYaw.class, CTSRotatePlayerYaw::toBytes, CTSRotatePlayerYaw::fromBytes, CTSRotatePlayerYaw::handle);
		INSTANCE.registerMessage(id++, CTSReqPlayerInfo.class, CTSReqPlayerInfo::toBytes, CTSReqPlayerInfo::fromBytes, CTSReqPlayerInfo::handle);
		
		INSTANCE.registerMessage(id++, STCExecuteSkill.class, STCExecuteSkill::toBytes, STCExecuteSkill::fromBytes, STCExecuteSkill::handle);
		INSTANCE.registerMessage(id++, STCMobInitialSetting.class, STCMobInitialSetting::toBytes, STCMobInitialSetting::fromBytes, STCMobInitialSetting::handle);
		INSTANCE.registerMessage(id++, STCLivingMotionChange.class, STCLivingMotionChange::toBytes, STCLivingMotionChange::fromBytes, STCLivingMotionChange::handle);
		INSTANCE.registerMessage(id++, STCSetSkillValue.class, STCSetSkillValue::toBytes, STCSetSkillValue::fromBytes, STCSetSkillValue::handle);
		INSTANCE.registerMessage(id++, STCNotifyPlayerYawChanged.class, STCNotifyPlayerYawChanged::toBytes, STCNotifyPlayerYawChanged::fromBytes, STCNotifyPlayerYawChanged::handle);
		INSTANCE.registerMessage(id++, STCPlayAnimation.class, STCPlayAnimation::toBytes, STCPlayAnimation::fromBytes, STCPlayAnimation::handle);
		INSTANCE.registerMessage(id++, STCPlayAnimationTarget.class, STCPlayAnimationTarget::toBytes, STCPlayAnimationTarget::fromBytes, STCPlayAnimationTarget::handle);
		INSTANCE.registerMessage(id++, STCPlayAnimationTP.class, STCPlayAnimationTP::toBytes, STCPlayAnimationTP::fromBytes, STCPlayAnimationTP::handle);
		INSTANCE.registerMessage(id++, STCPotion.class, STCPotion::toBytes, STCPotion::fromBytes, STCPotion::handle);
		INSTANCE.registerMessage(id++, STCResetBasicAttackCool.class, STCResetBasicAttackCool::toBytes, STCResetBasicAttackCool::fromBytes, STCResetBasicAttackCool::handle);
		INSTANCE.registerMessage(id++, STCModifySkillVariable.class, STCModifySkillVariable::toBytes, STCModifySkillVariable::fromBytes, STCModifySkillVariable::handle);
		INSTANCE.registerMessage(id++, STCGameruleChange.class, STCGameruleChange::toBytes, STCGameruleChange::fromBytes, STCGameruleChange::handle);
	}
}