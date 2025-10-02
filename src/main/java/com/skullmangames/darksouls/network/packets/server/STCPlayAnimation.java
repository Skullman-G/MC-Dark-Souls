package com.skullmangames.darksouls.network.packets.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

public class STCPlayAnimation implements NetworkPacket
{
	protected ResourceLocation animationId;
	protected int entityId;
	protected float convertTimeModifier;

	public STCPlayAnimation()
	{
		this.animationId = null;
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

	public STCPlayAnimation(ResourceLocation animation, int entityId, float convertTimeModifier)
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

	public STCPlayAnimation(FriendlyByteBuf buf)
	{
		this.animationId = buf.readResourceLocation();
		this.entityId = buf.readInt();
		this.convertTimeModifier = buf.readFloat();
	}

	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeResourceLocation(this.animationId);
		buf.writeInt(this.entityId);
		buf.writeFloat(this.convertTimeModifier);
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