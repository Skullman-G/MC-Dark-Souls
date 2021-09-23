package com.skullmangames.darksouls.common.item;

import java.util.UUID;

import com.skullmangames.darksouls.client.gui.screens.KeyNameScreen;
import com.skullmangames.darksouls.common.tileentity.LockableDoorTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.StringTextComponent;

public class KeyItem extends Item
{
	public KeyItem(Properties properties)
	{
		super(properties.stacksTo(1));
	}
	
	private static CompoundNBT getOrCreateNBT(ItemStack itemstack)
	{
		CompoundNBT nbt = itemstack.getTag();
		if (nbt == null)
		{
			nbt = itemstack.getOrCreateTag();
			nbt.putString("key_name", "");
			nbt.putUUID("uuid", UUID.randomUUID());
		}
		
		return nbt;
	}
	
	public static String getKeyName(ItemStack itemstack)
	{
		CompoundNBT nbt = getOrCreateNBT(itemstack);
		return nbt.getString("key_name");
	}
	
	public static UUID getKeyUUID(ItemStack itemstack)
	{
		CompoundNBT nbt = getOrCreateNBT(itemstack);
		return nbt.getUUID("uuid");
	}
	
	public static void setKeyName(ItemStack itemstack, String value)
	{
		CompoundNBT nbt = getOrCreateNBT(itemstack);
		nbt.putString("key_name", value);
		itemstack.setHoverName(new StringTextComponent(value));
	}
	
	@Override
	public ActionResultType useOn(ItemUseContext context)
	{
		TileEntity tileentity = context.getLevel().getBlockEntity(context.getClickedPos());
		if (tileentity == null || !(tileentity instanceof LockableDoorTileEntity)) return ActionResultType.PASS;
		
		if (getKeyName(context.getItemInHand()).isEmpty())
		{
			Minecraft.getInstance().setScreen(new KeyNameScreen(context.getItemInHand()));
			return ActionResultType.PASS;
		}
		
		LockableDoorTileEntity lockable = (LockableDoorTileEntity)tileentity;
		lockable.tryInteract(context.getPlayer(), getKeyUUID(context.getItemInHand()), getKeyName(context.getItemInHand()));
		return ActionResultType.SUCCESS;
	}
}
