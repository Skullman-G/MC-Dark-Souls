package com.skullmangames.darksouls.network.packets.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.item.AttributeItemCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class STCChangeEquipment implements NetworkPacket
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
	
	public STCChangeEquipment(FriendlyByteBuf buf)
	{
		this.entityId = buf.readInt();
		this.from = buf.readItem();
		this.to = buf.readItem();
		this.slot = buf.readEnum(EquipmentSlot.class);
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(this.entityId);
		buf.writeItem(this.from);
		buf.writeItem(this.to);
		buf.writeEnum(this.slot);
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.player.level.getEntity(this.entityId);
			if (entity instanceof LivingEntity livingEntity)
			{
				LivingCap<?> cap = (LivingCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				if (cap == null) return;
				
				AttributeItemCap fromCap = ModCapabilities.getAttributeItemCap(this.from);
				AttributeItemCap toCap = ModCapabilities.getAttributeItemCap(this.to);
				
				if(fromCap != null)
				{
					livingEntity.getAttributes().removeAttributeModifiers(fromCap.getAttributeModifiers(this.slot));
				}
				
				if(toCap != null)
				{
					livingEntity.getAttributes().addTransientAttributeModifiers(toCap.getAttributeModifiers(this.slot));
				}
				
				if (this.slot.getType() == EquipmentSlot.Type.ARMOR)
				{
					cap.onArmorSlotChanged(fromCap, toCap, this.slot);
				}
				else
				{
					cap.onHeldItemChange(toCap, this.to, this.slot == EquipmentSlot.MAINHAND ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
				}
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
}
