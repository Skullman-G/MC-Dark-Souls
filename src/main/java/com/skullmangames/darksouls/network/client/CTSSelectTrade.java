package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.inventory.SoulMerchantMenu;

import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class CTSSelectTrade
{
	private int shopItem;
	
	public CTSSelectTrade(int shopItem)
	{
		this.shopItem = shopItem;
	}
	
	public static CTSSelectTrade fromBytes(PacketBuffer buf)
	{
		return new CTSSelectTrade(buf.readInt());
	}

	public static void toBytes(CTSSelectTrade msg, PacketBuffer buf)
	{
		buf.writeInt(msg.shopItem);
	}
	
	public static void handle(CTSSelectTrade msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Container abstractcontainermenu = ctx.get().getSender().containerMenu;
			if (abstractcontainermenu instanceof SoulMerchantMenu)
			{
				SoulMerchantMenu merchantmenu = (SoulMerchantMenu) abstractcontainermenu;
				merchantmenu.setSelectionHint(msg.shopItem);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
