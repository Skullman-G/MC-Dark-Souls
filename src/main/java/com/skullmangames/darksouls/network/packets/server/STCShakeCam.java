package com.skullmangames.darksouls.network.packets.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class STCShakeCam implements NetworkPacket
{
	private int duration;
	private float magnitude;
	
	public STCShakeCam(int duration, float magnitude)
	{
		this.duration = duration;
		this.magnitude = magnitude;
	}
	
	public STCShakeCam(FriendlyByteBuf buf)
	{
		this.duration = buf.readInt();
		this.magnitude = buf.readFloat();
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(this.duration);
		buf.writeFloat(this.magnitude);
	}
	
	@Override
	public void handle(Supplier<Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			ClientManager.INSTANCE.mainCamera.shake(this.duration, this.magnitude);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
