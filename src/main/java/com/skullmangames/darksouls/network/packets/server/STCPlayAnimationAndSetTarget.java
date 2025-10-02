package com.skullmangames.darksouls.network.packets.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

public class STCPlayAnimationAndSetTarget extends STCPlayAnimation
{
	protected int targetId;

	public STCPlayAnimationAndSetTarget()
	{
		super();
		this.targetId = 0;
	}

	public STCPlayAnimationAndSetTarget(ResourceLocation animationId, int entityId, float modifyTime, int targetId)
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

	public STCPlayAnimationAndSetTarget(FriendlyByteBuf buf)
	{
		super(buf.readResourceLocation(), buf.readInt(), buf.readFloat());
		this.targetId = buf.readInt();
	}

	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeResourceLocation(this.animationId);
		buf.writeInt(this.entityId);
		buf.writeFloat(this.convertTimeModifier);
		buf.writeInt(this.targetId);
	}

	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			this.onArrive();
		});
		ctx.get().setPacketHandled(true);
	}
}