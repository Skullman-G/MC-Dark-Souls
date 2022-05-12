package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCPlayAnimationAndSetTarget extends STCPlayAnimation
{
	protected int targetId;

	public STCPlayAnimationAndSetTarget()
	{
		super();
		this.targetId = 0;
	}

	public STCPlayAnimationAndSetTarget(int animationId, int entityId, float modifyTime, int targetId)
	{
		super(animationId, entityId, modifyTime);
		this.targetId = targetId;
	}

	public STCPlayAnimationAndSetTarget(StaticAnimation animation, float modifyTime, LivingCap<?> entityCap)
	{
		super(animation, modifyTime, entityCap);
		this.targetId = entityCap.getTarget().getId();
	}

	@Override
	public void onArrive()
	{
		super.onArrive();
		Minecraft mc = Minecraft.getInstance();
		Entity entity = mc.player.level.getEntity(this.entityId);
		Entity target = mc.player.level.getEntity(this.targetId);

		if (entity instanceof Mob && target instanceof LivingEntity)
		{
			Mob entityliving = (Mob) entity;
			entityliving.setTarget((LivingEntity) target);
		}
	}

	public static STCPlayAnimationAndSetTarget fromBytes(FriendlyByteBuf buf)
	{
		return new STCPlayAnimationAndSetTarget(buf.readInt(), buf.readInt(), buf.readFloat(), buf.readInt());
	}

	public static void toBytes(STCPlayAnimationAndSetTarget msg, FriendlyByteBuf buf)
	{
		buf.writeInt(msg.animationId);
		buf.writeInt(msg.entityId);
		buf.writeFloat(msg.convertTimeModifier);
		buf.writeInt(msg.targetId);
	}

	public static void handle(STCPlayAnimationAndSetTarget msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			msg.onArrive();
		});
		ctx.get().setPacketHandled(true);
	}
}