package com.skullmangames.darksouls.common.inventory;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class SoulMerchantOffer
{
	private final int cost;
	private final ItemStack result;
	
	public SoulMerchantOffer(int cost, ItemStack result)
	{
		this.cost = cost;
		this.result = result;
	}
	
	public SoulMerchantOffer(CompoundNBT nbt)
	{
		this.cost = nbt.getInt("buy");
		this.result = ItemStack.of(nbt.getCompound("sell"));
	}
	
	public ItemStack getResult()
	{
		return this.result;
	}
	
	public int getCost()
	{
		return this.cost;
	}
	
	public boolean canAccept(PlayerEntity player)
	{
		PlayerCap<?> playerCap = (PlayerCap<?>)player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		return playerCap != null && playerCap.getSouls() >= this.cost || player.isCreative();
	}
	
	public CompoundNBT createTag()
	{
		CompoundNBT compoundtag = new CompoundNBT();
		compoundtag.putInt("buy", this.cost);
		compoundtag.put("sell", this.result.save(new CompoundNBT()));
		return compoundtag;
	}
}
