package com.skullmangames.darksouls.network.packets.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCPotion implements NetworkPacket
{
	private MobEffect effect;
	private Action action;
	private int entityId;
	
	public STCPotion()
	{
		this.effect = null;
		this.entityId = 0;
		this.action = Action.REMOVE;
	}
	
	public STCPotion(MobEffect effect, Action action, int entityId)
	{
		this.effect = effect;
		this.entityId = entityId;
		this.action = action;
	}
	
	public STCPotion(FriendlyByteBuf buf)
	{
		this.effect = MobEffect.byId(buf.readInt());
		this.entityId = buf.readInt();
		this.action = Action.getAction(buf.readInt());
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(MobEffect.getId(this.effect));
		buf.writeInt(this.entityId);
		buf.writeInt(this.action.getSymb());
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.level.getEntity(this.entityId);
			
			if(entity != null && entity instanceof LivingEntity)
			{
				LivingEntity livEntity = ((LivingEntity)entity);
				
				switch(this.action)
				{
					case ACTIVE:
						livEntity.addEffect(new MobEffectInstance(this.effect, 0));
						break;
					case REMOVE:
						livEntity.removeEffect(this.effect);
						break;
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
	
	public static enum Action
	{
		ACTIVE(0), REMOVE(1);
		
		int action;
		
		Action(int action)
		{
			this.action = action;
		}
		
		public int getSymb()
		{
			return action;
		}
		
		private static Action getAction(int symb)
		{
			if(symb == 0) return ACTIVE;
			else if(symb == 1) return REMOVE;
			else return null;
		}
	}
}
