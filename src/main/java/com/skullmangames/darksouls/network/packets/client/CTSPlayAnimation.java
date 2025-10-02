package com.skullmangames.darksouls.network.packets.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.packets.NetworkPacket;
import com.skullmangames.darksouls.network.packets.server.STCPlayAnimation;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CTSPlayAnimation implements NetworkPacket
{
	private ResourceLocation animationId;
	private float modifyTime;
	private boolean isClientSideAnimation;
	private boolean resendToSender;

	public CTSPlayAnimation()
	{
		this.animationId = null;
		this.modifyTime = 0;
		this.resendToSender = false;
	}

	public CTSPlayAnimation(StaticAnimation animation, float modifyTime, boolean clientOnly, boolean resendToSender)
	{
		this(animation.getId(), modifyTime, clientOnly, resendToSender);
	}

	public CTSPlayAnimation(ResourceLocation animationId, float modifyTime, boolean clientOnly,
			boolean resendToSender)
	{
		this.animationId = animationId;
		this.modifyTime = modifyTime;
		this.isClientSideAnimation = clientOnly;
		this.resendToSender = resendToSender;
	}
	
	public CTSPlayAnimation(FriendlyByteBuf buf)
	{
		this.animationId = buf.readResourceLocation();
		this.modifyTime = buf.readFloat();
		this.isClientSideAnimation = buf.readBoolean();
		this.resendToSender = buf.readBoolean();
	}

	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeResourceLocation(this.animationId);
		buf.writeFloat(this.modifyTime);
		buf.writeBoolean(this.isClientSideAnimation);
		buf.writeBoolean(this.resendToSender);
	}

	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			ServerPlayer serverPlayer = ctx.get().getSender();
			ServerPlayerCap playerCap = (ServerPlayerCap) serverPlayer
					.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (!this.isClientSideAnimation)
			{
				playerCap.getAnimator().playAnimation(this.animationId, this.modifyTime);
			}

			ModNetworkManager.sendToAllPlayerTrackingThisEntity(
					new STCPlayAnimation(this.animationId, serverPlayer.getId(), this.modifyTime),
					serverPlayer);

			if (this.resendToSender)
			{
				ModNetworkManager.sendToPlayer(
						new STCPlayAnimation(this.animationId, serverPlayer.getId(), this.modifyTime),
						serverPlayer);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}