package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCPlayAnimation;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CTSPlayAnimation
{
	private int animationId;
	private float modifyTime;
	private boolean isClientSideAnimation;
	private boolean resendToSender;

	public CTSPlayAnimation()
	{
		this.animationId = 0;
		this.modifyTime = 0;
		this.resendToSender = false;
	}

	public CTSPlayAnimation(StaticAnimation animation, float modifyTime, boolean clientOnly, boolean resendToSender)
	{
		this(animation.getId(), modifyTime, clientOnly, resendToSender);
	}

	public CTSPlayAnimation(int animationId, float modifyTime, boolean clientOnly,
			boolean resendToSender)
	{
		this.animationId = animationId;
		this.modifyTime = modifyTime;
		this.isClientSideAnimation = clientOnly;
		this.resendToSender = resendToSender;
	}

	public static CTSPlayAnimation fromBytes(FriendlyByteBuf buf)
	{
		return new CTSPlayAnimation(buf.readInt(), buf.readFloat(), buf.readBoolean(),
				buf.readBoolean());
	}

	public static void toBytes(CTSPlayAnimation msg, FriendlyByteBuf buf)
	{
		buf.writeInt(msg.animationId);
		buf.writeFloat(msg.modifyTime);
		buf.writeBoolean(msg.isClientSideAnimation);
		buf.writeBoolean(msg.resendToSender);
	}

	public static void handle(CTSPlayAnimation msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			ServerPlayer serverPlayer = ctx.get().getSender();
			ServerPlayerCap playerpatch = (ServerPlayerCap) serverPlayer
					.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (!msg.isClientSideAnimation)
			{
				playerpatch.getAnimator().playAnimation(msg.animationId, msg.modifyTime);
			}

			ModNetworkManager.sendToAllPlayerTrackingThisEntity(
					new STCPlayAnimation(msg.animationId, serverPlayer.getId(), msg.modifyTime),
					serverPlayer);

			if (msg.resendToSender)
			{
				ModNetworkManager.sendToPlayer(
						new STCPlayAnimation(msg.animationId, serverPlayer.getId(), msg.modifyTime),
						serverPlayer);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}