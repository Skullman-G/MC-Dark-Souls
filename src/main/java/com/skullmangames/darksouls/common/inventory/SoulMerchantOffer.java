package com.skullmangames.darksouls.common.inventory;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SoulMerchantOffer
{
	private final int cost;
	private final ItemStack result;
	
	public SoulMerchantOffer(int cost, ItemStack result)
	{
		this.cost = cost;
		this.result = result;
	}
	
	public SoulMerchantOffer(CompoundTag nbt)
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
	
	public boolean canAccept(Player player)
	{
		PlayerCap<?> playerCap = (PlayerCap<?>)player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		return playerCap != null && playerCap.getSouls() >= this.cost || player.isCreative();
	}
	
	public CompoundTag createTag()
	{
		CompoundTag compoundtag = new CompoundTag();
		compoundtag.putInt("buy", this.cost);
		compoundtag.put("sell", this.result.save(new CompoundTag()));
		return compoundtag;
	}
}
