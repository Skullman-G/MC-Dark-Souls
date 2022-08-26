package com.skullmangames.darksouls.common.inventory;

import java.util.ArrayList;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class SoulMerchantOffers extends ArrayList<SoulMerchantOffer>
{
	private static final long serialVersionUID = 5147832285095369191L;
	
	public SoulMerchantOffers()
	{
	}

	public SoulMerchantOffers(CompoundTag nbt)
	{
		ListTag listtag = nbt.getList("Recipes", 10);

		for (int i = 0; i < listtag.size(); ++i)
		{
			this.add(new SoulMerchantOffer(listtag.getCompound(i)));
		}

	}

	public void writeToStream(FriendlyByteBuf buf)
	{
		buf.writeByte((byte) (this.size() & 255));

		for (int i = 0; i < this.size(); ++i)
		{
			SoulMerchantOffer merchantoffer = this.get(i);
			buf.writeInt(merchantoffer.getCost());
			buf.writeItem(merchantoffer.getResult());
		}
	}

	public static SoulMerchantOffers createFromStream(FriendlyByteBuf buf)
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

	public CompoundTag createTag()
	{
		CompoundTag compoundtag = new CompoundTag();
		ListTag listtag = new ListTag();

		for (int i = 0; i < this.size(); ++i)
		{
			SoulMerchantOffer merchantoffer = this.get(i);
			listtag.add(merchantoffer.createTag());
		}

		compoundtag.put("Recipes", listtag);
		return compoundtag;
	}
}
