package com.skullmangames.darksouls.network.server;

import java.util.List;
import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.ModUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCAttunements
{
	private int entityId;
	private List<ItemStack> stacks;
	
	public STCAttunements(int entityid, List<ItemStack> stacks)
	{
		this.entityId = entityid;
		this.stacks = stacks;
	}
	
	public static STCAttunements fromBytes(PacketBuffer buf)
	{
		return new STCAttunements(buf.readInt(), ModUtil.readList(buf, (b) -> b.readItem()));
	}
	
	public static void toBytes(STCAttunements msg, PacketBuffer buf)
	{
		buf.writeInt(msg.entityId);
		ModUtil.writeList(buf, msg.stacks, (b, stack) -> b.writeItem(stack));
	}
	
	public static void handle(STCAttunements msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.player.level.getEntity(msg.entityId);
			if (entity == null) return;
			
			PlayerCap<?> playerCap = (PlayerCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerCap == null) return;
			
			for (int i = 0; i < msg.stacks.size(); i++)
			{
				playerCap.getAttunements().setItem(i, msg.stacks.get(i));
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
}
