package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.ServerPlayerData;
import com.skullmangames.darksouls.common.skill.SkillExecutionHelper;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class CTSExecuteSkill
{
	private boolean active;
	private PacketBuffer buffer;
	
	public CTSExecuteSkill()
	{
		this(true);
	}
	
	public CTSExecuteSkill(boolean active)
	{
		this.active = active;
		this.buffer = new PacketBuffer(Unpooled.buffer());
	}
	
	public CTSExecuteSkill(boolean active, PacketBuffer pb)
	{
		this.active = active;
		this.buffer = new PacketBuffer(Unpooled.buffer());
		if(pb != null) this.buffer.writeBytes(pb);
	}
	
	public PacketBuffer getBuffer()
	{
		return buffer;
	}
	
	public static CTSExecuteSkill fromBytes(PacketBuffer buf)
	{
		CTSExecuteSkill msg = new CTSExecuteSkill(buf.readBoolean());
		
		while(buf.isReadable())
		{
			msg.buffer.writeByte(buf.readByte());
		}
		
		return msg;
	}
	
	public static void toBytes(CTSExecuteSkill msg, PacketBuffer buf)
	{
		buf.writeBoolean(msg.active);
		
		while(msg.buffer.isReadable())
		{
			buf.writeByte(msg.buffer.readByte());
		}
	}
	
	public static void handle(CTSExecuteSkill msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			ServerPlayerEntity serverPlayer = ctx.get().getSender();
			ServerPlayerData playerdata = (ServerPlayerData)serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			
			if(msg.active)
			{
				SkillExecutionHelper.requestExecute(playerdata, msg.getBuffer());
			}
			else
			{
				SkillExecutionHelper.getActiveSkill().cancelOnServer(playerdata, msg.getBuffer());
			}
		});
		ctx.get().setPacketHandled(true);
	}
}