package com.skullmangames.darksouls.network.packets.server;

import java.util.List;
import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class STCAttunements implements NetworkPacket
{
	private int entityId;
	private List<ItemStack> stacks;
	
	public STCAttunements(int entityid, List<ItemStack> stacks)
	{
		this.entityId = entityid;
		this.stacks = stacks;
	}
	
	public STCAttunements(FriendlyByteBuf buf)
	{
		this.entityId = buf.readInt();
		this.stacks = buf.readList((b) -> b.readItem());
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(this.entityId);
		buf.writeCollection(this.stacks, (b, stack) -> b.writeItem(stack));
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.player.level.getEntity(this.entityId);
			if (entity == null) return;
			
			PlayerCap<?> playerCap = (PlayerCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerCap == null) return;
			
			for (int i = 0; i < this.stacks.size(); i++)
			{
				playerCap.getAttunements().setItem(i, this.stacks.get(i));
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
}
