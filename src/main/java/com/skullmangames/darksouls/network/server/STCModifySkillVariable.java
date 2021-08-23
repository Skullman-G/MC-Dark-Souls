package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.ClientPlayerData;
import com.skullmangames.darksouls.common.skill.SkillExecutionHelper;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCModifySkillVariable
{
	private String nbtName;
	private Object value;
	private int type;
	
	public STCModifySkillVariable()
	{
		this.nbtName = "";
		this.value = null;
	}
	
	public STCModifySkillVariable(VariableType type, String nbtName, Object value)
	{
		this(type.id, nbtName, value);
	}
	
	public STCModifySkillVariable(int type, String nbtName, Object value)
	{
		this.type = type;
		this.nbtName = nbtName;
		this.value = value;
	}
	
	public static STCModifySkillVariable fromBytes(PacketBuffer buf)
	{
		Object value = null;
		int type = buf.readInt();
		
		switch(type)
		{
			case 0:
				value = buf.readBoolean();
				break;
				
			case 1:
				value = buf.readInt();
				break;
				
			case 2:
				value = buf.readFloat();
				break;
				
			default:
				break;
		}
		
		return new STCModifySkillVariable(type, buf.readUtf(), value);
	}
	
	public static void toBytes(STCModifySkillVariable msg, PacketBuffer buf)
	{
		buf.writeInt(msg.type);
		
		switch(msg.type)
		{
			case 0:
				buf.writeBoolean((boolean)msg.value);
				break;
				
			case 1:
				buf.writeInt((int)msg.value);
				break;
				
			case 2:
				buf.writeFloat((float)msg.value);
				break;
				
			default:
				break;
		}
		
		buf.writeUtf(msg.nbtName);
	}
	
	public static void handle(STCModifySkillVariable msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			Minecraft minecraft = Minecraft.getInstance();
			ClientPlayerData playerdata = (ClientPlayerData) minecraft.player.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			
			if(playerdata != null)
			{
				CompoundNBT nbt = SkillExecutionHelper.getVariableNBT();
				
				switch(msg.type)
				{
					case 0:
						nbt.putBoolean(msg.nbtName, (boolean)msg.value);
						break;
						
					case 1:
						nbt.putInt(msg.nbtName, (int)msg.value);
						break;
						
					case 2:
						nbt.putFloat(msg.nbtName, (float)msg.value);
						break;
						
					default:
						break;
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
	
	public static enum VariableType
	{
		BOOLEAN(0), INTEGER(1), FLOAT(2);
		
		final int id;
		
		VariableType(int id)
		{
			this.id = id;
		}
	}
}