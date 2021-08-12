package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCPlayAnimation
{
	protected int animationId;
	protected int entityId;
	protected float modifyTime;
	protected boolean mixLayer;

	public STCPlayAnimation()
	{
		this.animationId = 0;
		this.entityId = 0;
		this.modifyTime = 0;
		this.mixLayer = false;
	}
	
	public STCPlayAnimation(int animation, int entityId, float modifyTime)
	{
		this(animation, entityId, modifyTime, false);
	}
	
	public STCPlayAnimation(int animation, int entityId, float modifyTime, boolean mixLayer)
	{
		this.animationId = animation;
		this.entityId = entityId;
		this.modifyTime = modifyTime;
		this.mixLayer = mixLayer;
	}
	
	public <T extends STCPlayAnimation> void onArrive()
	{
		@SuppressWarnings("resource")
		Entity entity = Minecraft.getInstance().player.level.getEntity(this.entityId);
		if(entity == null)
		{
			return;
		}
		
		LivingData<?> entitydata = (LivingData<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		
		if (this.animationId < 0)
		{
			entitydata.getClientAnimator().offMixLayer(false);
		}
		else
		{
			if (this.mixLayer)
			{
				entitydata.getClientAnimator().playMixLayerAnimation(this.animationId);
			}
			else
			{
				entitydata.getAnimator().playAnimation(this.animationId, this.modifyTime);
			}
		}
	}
	
	public static STCPlayAnimation fromBytes(PacketBuffer buf)
	{
		return new STCPlayAnimation(buf.readInt(), buf.readInt(), buf.readFloat(), buf.readBoolean());
	}

	public static void toBytes(STCPlayAnimation msg, ByteBuf buf)
	{
		buf.writeInt(msg.animationId);
		buf.writeInt(msg.entityId);
		buf.writeFloat(msg.modifyTime);
		buf.writeBoolean(msg.mixLayer);
	}

	public static void handle(STCPlayAnimation msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			msg.onArrive();
		});
		ctx.get().setPacketHandled(true);
	}
}