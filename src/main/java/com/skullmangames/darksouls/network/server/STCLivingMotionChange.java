package com.skullmangames.darksouls.network.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCLivingMotionChange
{
	private int entityId;
	private int count;
	private boolean setChangesAsDefault;
	private List<LivingMotion> motionList = new ArrayList<>();
	private List<StaticAnimation> animationList = new ArrayList<>();

	public STCLivingMotionChange()
	{
		this(-1);
	}

	public STCLivingMotionChange(int entityId)
	{
		this(entityId, 0, false);
	}

	public STCLivingMotionChange(int entityId, boolean setChangesAsDefault)
	{
		this(entityId, 0, setChangesAsDefault);
	}

	private STCLivingMotionChange(int entityId, int count, boolean setChangesAsDefault)
	{
		this.entityId = entityId;
		this.count = count;
		this.setChangesAsDefault = setChangesAsDefault;
	}

	public STCLivingMotionChange put(LivingMotion motion, StaticAnimation animation)
	{
		this.motionList.add(motion);
		this.animationList.add(animation);
		this.count++;
		return this;
	}

	public void putEntries(Set<Map.Entry<LivingMotion, StaticAnimation>> motionSet)
	{
		this.count += motionSet.size();

		motionSet.forEach((entry) ->
		{
			this.motionList.add(entry.getKey());
			this.animationList.add(entry.getValue());
		});
	}

	public static STCLivingMotionChange fromBytes(PacketBuffer buf)
	{
		STCLivingMotionChange msg = new STCLivingMotionChange(buf.readInt(), buf.readInt(), buf.readBoolean());
		List<LivingMotion> motionList = new ArrayList<>();
		List<StaticAnimation> animationList = new ArrayList<>();

		for (int i = 0; i < msg.count; i++)
		{
			motionList.add(LivingMotion.values()[buf.readInt()]);
		}

		for (int i = 0; i < msg.count; i++)
		{
			animationList.add(DarkSouls.getInstance().animationManager.findAnimationById(buf.readInt()));
		}

		msg.motionList = motionList;
		msg.animationList = animationList;

		return msg;
	}

	public static void toBytes(STCLivingMotionChange msg, PacketBuffer buf)
	{
		buf.writeInt(msg.entityId);
		buf.writeInt(msg.count);
		buf.writeBoolean(msg.setChangesAsDefault);

		for (LivingMotion motion : msg.motionList)
		{
			buf.writeInt(motion.getId());
		}

		for (StaticAnimation anim : msg.animationList)
		{
			buf.writeInt(anim.getId());
		}
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
				animator.resetMotions();
				animator.resetCompositeMotion();

				for (int i = 0; i < msg.count; i++)
				{
					LivingMotion motion = msg.motionList.get(i);
					StaticAnimation animation = msg.animationList.get(i);
					animator.addLivingAnimation(motion, animation);
					if (animator.isMotionActive(motion)) animator.playAnimation(animation, 0.0F);
				}

				if (msg.setChangesAsDefault)
				{
					animator.setCurrentMotionsToDefault();
				}
			}
		});

		ctx.get().setPacketHandled(true);
	}
}