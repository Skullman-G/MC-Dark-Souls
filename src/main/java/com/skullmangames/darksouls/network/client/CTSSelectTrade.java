package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.inventory.SoulMerchantMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

public class CTSSelectTrade
{
	private int shopItem;
	
	public CTSSelectTrade(int shopItem)
	{
		this.shopItem = shopItem;
	}
	
	public static CTSSelectTrade fromBytes(FriendlyByteBuf buf)
	{
		return new CTSSelectTrade(buf.readInt());
	}

	public static void toBytes(CTSSelectTrade msg, FriendlyByteBuf buf)
	{
		buf.writeInt(msg.shopItem);
	}
	
	public static void handle(CTSSelectTrade msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			AbstractContainerMenu abstractcontainermenu = ctx.get().getSender().containerMenu;
			if (abstractcontainermenu instanceof SoulMerchantMenu)
			{
				SoulMerchantMenu merchantmenu = (SoulMerchantMenu) abstractcontainermenu;
				merchantmenu.setSelectionHint(msg.shopItem);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
