package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCPlayAnimation
{
	protected int animationId;
	protected int entityId;
	protected float convertTimeModifier;

	public STCPlayAnimation()
	{
		this.animationId = 0;
		this.entityId = 0;
		this.convertTimeModifier = 0;
	}

	public STCPlayAnimation(StaticAnimation animation, float convertTimeModifier, LivingCap<?> entityCap)
	{
		this(animation.getId(), entityCap.getOriginalEntity().getId(),
				convertTimeModifier);
	}

	public STCPlayAnimation(StaticAnimation animation, int entityId, float convertTimeModifier)
	{
		this(animation.getId(), entityId, convertTimeModifier);
	}

	public STCPlayAnimation(int animation, int entityId, float convertTimeModifier)
	{
		this.animationId = animation;
		this.entityId = entityId;
		this.convertTimeModifier = convertTimeModifier;
	}

	public void onArrive()
	{
		Minecraft mc = Minecraft.getInstance();
		Entity entity = mc.player.level.getEntity(this.entityId);
		if (entity == null) return;
		LivingCap<?> entityCap = (LivingCap<?>)entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);

		if (entityCap != null)
		{
			entityCap.getAnimator().playAnimation(this.animationId, this.convertTimeModifier);
		}
	}

	public static STCPlayAnimation fromBytes(FriendlyByteBuf buf)
	{
		return new STCPlayAnimation(buf.readInt(), buf.readInt(), buf.readFloat());
	}

	public static void toBytes(STCPlayAnimation msg, ByteBuf buf)
	{
		buf.writeInt(msg.animationId);
		buf.writeInt(msg.entityId);
		buf.writeFloat(msg.convertTimeModifier);
	}

	public static void handle(STCPlayAnimation msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			msg.onArrive();
		});

		ctx.get().setPacketHandled(true);
	}
}