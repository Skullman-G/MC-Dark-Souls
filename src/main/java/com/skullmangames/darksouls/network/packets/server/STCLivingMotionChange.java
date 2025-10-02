package com.skullmangames.darksouls.network.packets.server;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.data.AnimationManager;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCLivingMotionChange implements NetworkPacket
{
	private int entityId;
	private boolean setChangesAsDefault;
	private Map<LivingMotion, StaticAnimation> changes = new HashMap<>();

	public STCLivingMotionChange(int entityId, boolean setChangesAsDefault)
	{
		this.entityId = entityId;
		this.setChangesAsDefault = setChangesAsDefault;
	}

	public STCLivingMotionChange put(LivingMotion motion, StaticAnimation animation)
	{
		this.changes.put(motion, animation);
		return this;
	}

	public STCLivingMotionChange putEntries(Map<LivingMotion, StaticAnimation> motions)
	{
		this.changes.putAll(motions);
		return this;
	}

	public STCLivingMotionChange(FriendlyByteBuf buf)
	{
		this.entityId = buf.readInt();
		this.setChangesAsDefault = buf.readBoolean();
		this.changes = buf.readMap((b) -> b.readEnum(LivingMotion.class), (b) ->
		{
			return AnimationManager.getAnimation(b.readResourceLocation());
		});
	}

	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(this.entityId);
		buf.writeBoolean(this.setChangesAsDefault);
		
		buf.writeMap(this.changes, (b, motion) -> b.writeEnum(motion), (b, anim) -> b.writeResourceLocation(anim.getId()));
	}

	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			Minecraft mc = Minecraft.getInstance();
			Entity entity = mc.player.level.getEntity(this.entityId);

			if (entity != null)
			{
				LivingCap<?> entityCap = (LivingCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
				ClientAnimator animator = entityCap.getClientAnimator();
				animator.resetLivingAnimations();
				animator.resetMixMotions();
				
				this.changes.forEach((motion, animation) ->
				{
					if (animation.getLayerPart() != LayerPart.FULL)
					{
						animator.putAnimOverride(animation.getLayerPart(), motion, animation);
						if (entityCap.baseMotion == motion) animator.playAnimation(animation, 0.0F);
					}
					else
					{
						animator.putLivingAnimation(motion, animation);
						if (entityCap.baseMotion == motion) animator.playAnimation(animation, 0.0F);
					}
				});

				if (this.setChangesAsDefault)
				{
					animator.setCurrentMotionsToDefault();
				}
			}
		});

		ctx.get().setPacketHandled(true);
	}
}