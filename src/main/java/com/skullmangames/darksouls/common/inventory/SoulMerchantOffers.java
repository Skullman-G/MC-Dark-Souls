package com.skullmangames.darksouls.common.inventory;

import java.util.ArrayList;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.item.ItemStack;

public class SoulMerchantOffers extends ArrayList<SoulMerchantOffer>
{
	private static final long serialVersionUID = 5147832285095369191L;
	
	public SoulMerchantOffers()
	{
	}

	public SoulMerchantOffers(CompoundNBT nbt)
	{
		ListNBT listtag = nbt.getList("Recipes", 10);

		for (int i = 0; i < listtag.size(); ++i)
		{
			this.add(new SoulMerchantOffer(listtag.getCompound(i)));
		}

	}

	public void writeToStream(PacketBuffer buf)
	{
		buf.writeByte((byte) (this.size() & 255));

		for (int i = 0; i < this.size(); ++i)
		{
			SoulMerchantOffer merchantoffer = this.get(i);
			buf.writeInt(merchantoffer.getCost());
			buf.writeItem(merchantoffer.getResult());
		}
	}

	public static SoulMerchantOffers createFromStream(PacketBuffer buf)
	{
		SoulMerchantOffers merchantoffers = new SoulMerchantOffers();
		int i = buf.readByte() & 255;

		for (int j = 0; j < i; ++j)
		{
			int cost = buf.readInt();
			ItemStack result = buf.readItem();
			SoulMerchantOffer merchantoffer = new SoulMerchantOffer(cost, result);
			merchantoffers.add(merchantoffer);
		}

		return merchantoffers;
	}

	public CompoundNBT createTag()
	{
		CompoundNBT compoundtag = new CompoundNBT();
		ListNBT listtag = new ListNBT();

		for (int i = 0; i < this.size(); ++i)
		{
			SoulMerchantOffer merchantoffer = this.get(i);
			listtag.add(merchantoffer.createTag());
		}

		compoundtag.put("Recipes", listtag);
		return compoundtag;
	}
}
