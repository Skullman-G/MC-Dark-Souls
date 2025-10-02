package com.skullmangames.darksouls.network.packets.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.inventory.SoulMerchantMenu;
import com.skullmangames.darksouls.common.inventory.SoulMerchantOffers;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

public class STCSoulMerchantOffers implements NetworkPacket
{
	private int containerId;
	private SoulMerchantOffers offers;
	
	public STCSoulMerchantOffers(int containerId, SoulMerchantOffers offers)
	{
		this.containerId = containerId;
		this.offers = offers;
	}
	
	public STCSoulMerchantOffers(FriendlyByteBuf buf)
	{
		this.containerId = buf.readInt();
		this.offers = SoulMerchantOffers.createFromStream(buf);
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(this.containerId);
		this.offers.writeToStream(buf);
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			AbstractContainerMenu abstractcontainermenu = minecraft.player.containerMenu;
			if (this.containerId == abstractcontainermenu.containerId
					&& abstractcontainermenu instanceof SoulMerchantMenu)
			{
				SoulMerchantMenu merchantmenu = (SoulMerchantMenu) abstractcontainermenu;
				merchantmenu.setOffers(new SoulMerchantOffers(this.offers.createTag()));
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
}
