package com.skullmangames.darksouls.network.packets.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.inventory.SoulMerchantMenu;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

public class CTSSelectTrade implements NetworkPacket
{
	private int shopItem;
	
	public CTSSelectTrade(int shopItem)
	{
		this.shopItem = shopItem;
	}
	
	public CTSSelectTrade(FriendlyByteBuf buf)
	{
		this.shopItem = buf.readInt();
	}

	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(this.shopItem);
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			AbstractContainerMenu abstractcontainermenu = ctx.get().getSender().containerMenu;
			if (abstractcontainermenu instanceof SoulMerchantMenu)
			{
				SoulMerchantMenu merchantmenu = (SoulMerchantMenu) abstractcontainermenu;
				merchantmenu.setSelectionHint(this.shopItem);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
