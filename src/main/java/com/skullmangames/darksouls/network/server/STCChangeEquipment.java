package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.item.AttributeItemCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class STCChangeEquipment
{
	private int entityId;
	private ItemStack from;
	private ItemStack to;
	private EquipmentSlot slot;
	
	public STCChangeEquipment(int entityId, ItemStack from, ItemStack to, EquipmentSlot slot)
	{
		this.entityId = entityId;
		this.from = from;
		this.to = to;
		this.slot = slot;
	}
	
	public static STCChangeEquipment fromBytes(FriendlyByteBuf buf)
	{
		return new STCChangeEquipment(buf.readInt(), buf.readItem(), buf.readItem(), buf.readEnum(EquipmentSlot.class));
	}
	
	public static void toBytes(STCChangeEquipment msg, FriendlyByteBuf buf)
	{
		buf.writeInt(msg.entityId);
		buf.writeItem(msg.from);
		buf.writeItem(msg.to);
		buf.writeEnum(msg.slot);
	}
	
	public static void handle(STCChangeEquipment msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.player.level.getEntity(msg.entityId);
			if (entity instanceof LivingEntity livingEntity)
			{
				LivingCap<?> cap = (LivingCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				if (cap == null) return;
				
				AttributeItemCap fromCap = ModCapabilities.getAttributeItemCap(msg.from);
				AttributeItemCap toCap = ModCapabilities.getAttributeItemCap(msg.to);
				
				if(fromCap != null)
				{
					livingEntity.getAttributes().removeAttributeModifiers(fromCap.getAttributeModifiers(msg.slot));
				}
				
				if(toCap != null)
				{
					livingEntity.getAttributes().addTransientAttributeModifiers(toCap.getAttributeModifiers(msg.slot));
				}
				
				if (msg.slot.getType() == EquipmentSlot.Type.ARMOR)
				{
					cap.onArmorSlotChanged(fromCap, toCap, msg.slot);
				}
				else
				{
					cap.onHeldItemChange(toCap, msg.to, msg.slot == EquipmentSlot.MAINHAND ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
				}
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
}
