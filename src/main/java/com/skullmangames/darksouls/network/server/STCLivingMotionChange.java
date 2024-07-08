package com.skullmangames.darksouls.network.server;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.skullmangames.darksouls.common.animation.AnimationManager;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCLivingMotionChange
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

	public static STCLivingMotionChange fromBytes(FriendlyByteBuf buf)
	{
		return new STCLivingMotionChange(buf.readInt(), buf.readBoolean())
				.putEntries(buf.readMap((b) -> b.readEnum(LivingMotion.class), (b) ->
				{
					return AnimationManager.getAnimation(b.readResourceLocation());
				}));
	}

	public static void toBytes(STCLivingMotionChange msg, FriendlyByteBuf buf)
	{
		buf.writeInt(msg.entityId);
		buf.writeBoolean(msg.setChangesAsDefault);
		
		buf.writeMap(msg.changes, (b, motion) -> b.writeEnum(motion), (b, anim) -> b.writeResourceLocation(anim.getId()));
	}

	public static void handle(STCLivingMotionChange msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			Minecraft mc = Minecraft.getInstance();
			Entity entity = mc.player.level.getEntity(msg.entityId);

			if (entity != null)
			{
				LivingCap<?> entityCap = (LivingCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
				ClientAnimator animator = entityCap.getClientAnimator();
				animator.resetLivingAnimations();
				animator.resetMixMotions();
				
				msg.changes.forEach((motion, animation) ->
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

				if (msg.setChangesAsDefault)
				{
					animator.setCurrentMotionsToDefault();
				}
			}
		});

		ctx.get().setPacketHandled(true);
	}
}