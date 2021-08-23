package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.client.ClientEngine;
import com.skullmangames.darksouls.common.capability.entity.ClientPlayerData;
import com.skullmangames.darksouls.common.skill.SkillExecutionHelper;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCExecuteSkill
{
	private int skillSlot;
	private boolean active;
	private PacketBuffer buffer;

	public STCExecuteSkill()
	{
		this(0);
	}

	public STCExecuteSkill(int slotIndex)
	{
		this(slotIndex, true);
	}

	public STCExecuteSkill(int slotIndex, boolean active)
	{
		this.skillSlot = slotIndex;
		this.active = active;
		this.buffer = new PacketBuffer(Unpooled.buffer());
	}

	public PacketBuffer getBuffer()
	{
		return buffer;
	}
	
	public static STCExecuteSkill fromBytes(PacketBuffer buf)
	{
		STCExecuteSkill msg = new STCExecuteSkill(buf.readInt(), buf.readBoolean());

		while (buf.isReadable())
		{
			msg.buffer.writeByte(buf.readByte());
		}
		
		return msg;
	}

	public static void toBytes(STCExecuteSkill msg, PacketBuffer buf)
	{
		buf.writeInt(msg.skillSlot);
		buf.writeBoolean(msg.active);

		while (msg.buffer.isReadable())
		{
			buf.writeByte(msg.buffer.readByte());
		}
	}
	
	public static void handle(STCExecuteSkill msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			ClientPlayerData playerdata = ClientEngine.INSTANCE.getPlayerData();
			
			if(msg.active)
			{
				SkillExecutionHelper.getActiveSkill().executeOnClient(playerdata, msg.getBuffer());
			}
			else
			{
				SkillExecutionHelper.getActiveSkill().cancelOnClient(playerdata, msg.getBuffer());
			}
		});
		ctx.get().setPacketHandled(true);
	}
}