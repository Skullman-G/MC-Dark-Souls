package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.inventory.SoulMerchantMenu;
import com.skullmangames.darksouls.common.inventory.SoulMerchantOffers;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCSoulMerchantOffers
{
	private int containerId;
	private SoulMerchantOffers offers;
	
	public STCSoulMerchantOffers(int containerId, SoulMerchantOffers offers)
	{
		this.containerId = containerId;
		this.offers = offers;
	}
	
	public static STCSoulMerchantOffers fromBytes(PacketBuffer buf)
	{
		return new STCSoulMerchantOffers(buf.readInt(), SoulMerchantOffers.createFromStream(buf));
	}
	
	public static void toBytes(STCSoulMerchantOffers msg, PacketBuffer buf)
	{
		buf.writeInt(msg.containerId);
		msg.offers.writeToStream(buf);
	}
	
	public static void handle(STCSoulMerchantOffers msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Container abstractcontainermenu = minecraft.player.containerMenu;
			if (msg.containerId == abstractcontainermenu.containerId
					&& abstractcontainermenu instanceof SoulMerchantMenu)
			{
				SoulMerchantMenu merchantmenu = (SoulMerchantMenu) abstractcontainermenu;
				merchantmenu.setOffers(new SoulMerchantOffers(msg.offers.createTag()));
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
}
