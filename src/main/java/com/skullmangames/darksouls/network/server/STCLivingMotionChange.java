package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class STCLivingMotionChange
{
	private int entityId;
	private int count;
	
	private LivingMotion[] motion;
	private StaticAnimation[] animation;
	
	public STCLivingMotionChange()
	{
		this.entityId = 0;
		this.count = 0;
	}
	
	public STCLivingMotionChange(int entityId, int count)
	{
		this.entityId = entityId;
		this.count = count;
		this.motion = new LivingMotion[0];
		this.animation = new StaticAnimation[0];
	}
	
	public void setAnimations(StaticAnimation... animation)
	{
		this.animation = animation;
	}
	
	public void setMotions(LivingMotion... motion)
	{
		this.motion = motion;
	}
	
	public LivingMotion[] getMotions()
	{
		return motion;
	}
	
	public StaticAnimation[] getAnimations()
	{
		return animation;
	}
	
	public static STCLivingMotionChange fromBytes(FriendlyByteBuf buf)
	{
		STCLivingMotionChange msg = new STCLivingMotionChange(buf.readInt(), buf.readInt());
		LivingMotion[] motionarr = new LivingMotion[msg.count];
		StaticAnimation[] idarr = new StaticAnimation[msg.count];
		
		for(int i = 0; i < msg.count; i++) {
			motionarr[i] = LivingMotion.values()[buf.readInt()];
		}
			
		for(int i = 0; i < msg.count; i++) {
			idarr[i] = Animations.getById(buf.readInt());
		}
		
		msg.motion = motionarr;
		msg.animation = idarr;
		
		return msg;
	}

	public static void toBytes(STCLivingMotionChange msg, FriendlyByteBuf buf)
	{
		buf.writeInt(msg.entityId);
		buf.writeInt(msg.count);
		
		for(LivingMotion motion : msg.motion) {
			buf.writeInt(motion.getId());
		}
		
		for(StaticAnimation anim : msg.animation) {
			buf.writeInt(anim.getId());
		}
	}
	
	public static void handle(STCLivingMotionChange msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.player.level.getEntity(msg.entityId);
			if(entity != null)
			{
				LivingCap<?> entitydata = (LivingCap<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				AnimatorClient animator = entitydata.getClientAnimator();
				if (entitydata.currentMixMotion != LivingMotion.HOLDING_WEAPON)
				{
					animator.resetMixMotion();
					animator.offMixLayer(animator.mixLayerLeft, false);
					animator.offMixLayer(animator.mixLayerRight, false);
				}
				animator.resetModifiedLivingMotions();
				
				for(int i = 0; i < msg.count; i++)
					entitydata.getClientAnimator().addModifiedLivingMotion(msg.motion[i], msg.animation[i]);
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
}