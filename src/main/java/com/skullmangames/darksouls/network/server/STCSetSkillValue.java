package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.ClientPlayerData;
import com.skullmangames.darksouls.common.skill.SkillExecutionHelper;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCSetSkillValue
{
	private float floatSet;
	private boolean boolset;
	private int target;

	public STCSetSkillValue()
	{
		this.floatSet = 0;
	}

	public STCSetSkillValue(Target target, float amount, boolean boolset)
	{
		this.target = target.id;
		this.floatSet = amount;
		this.boolset = boolset;
	}

	public STCSetSkillValue(int target, float amount, boolean boolset)
	{
		this.target = target;
		this.floatSet = amount;
		this.boolset = boolset;
	}
	
	public static STCSetSkillValue fromBytes(PacketBuffer buf)
	{
		return new STCSetSkillValue(buf.readInt(), buf.readFloat(), buf.readBoolean());
	}

	public static void toBytes(STCSetSkillValue msg, PacketBuffer buf)
	{
		buf.writeInt(msg.target);
		buf.writeFloat(msg.floatSet);
		buf.writeBoolean(msg.boolset);
	}
	
	public static void handle(STCSetSkillValue msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			ClientPlayerData playerdata = (ClientPlayerData) minecraft.player.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			
			if (playerdata != null)
			{
				if (msg.target == Target.DURATION.id)
				{
					SkillExecutionHelper.setDuration((int) msg.floatSet);
				}
				if (msg.target == Target.DURATION_CONSUME.id)
				{
					SkillExecutionHelper.setDurationConsume(msg.boolset);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
	
	public static enum Target
	{
		DURATION(0), DURATION_CONSUME(1);

		public final int id;

		Target(int id)
		{
			this.id = id;
		}
	}
}