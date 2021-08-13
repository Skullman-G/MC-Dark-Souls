package com.skullmangames.darksouls.network.server;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

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
	
	public static STCLivingMotionChange fromBytes(PacketBuffer buf)
	{
		STCLivingMotionChange msg = new STCLivingMotionChange(buf.readInt(), buf.readInt());
		LivingMotion[] motionarr = new LivingMotion[msg.count];
		StaticAnimation[] idarr = new StaticAnimation[msg.count];
		
		for(int i = 0; i < msg.count; i++) {
			motionarr[i] = LivingMotion.values()[buf.readInt()];
		}
			
		for(int i = 0; i < msg.count; i++) {
			idarr[i] = Animations.findAnimationDataById(buf.readInt());
		}
		
		msg.motion = motionarr;
		msg.animation = idarr;
		
		return msg;
	}

	public static void toBytes(STCLivingMotionChange msg, PacketBuffer buf)
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
		ctx.get().enqueueWork(()->{
			@SuppressWarnings("resource")
			Entity entity = Minecraft.getInstance().player.level.getEntity(msg.entityId);
			if(entity != null)
			{
				LivingData<?> entitydata = (LivingData<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				AnimatorClient animator = entitydata.getClientAnimator();
				animator.resetMixMotion();
				animator.offMixLayer(false);
				animator.resetModifiedLivingMotions();
				
				for(int i = 0; i < msg.count; i++)
					entitydata.getClientAnimator().addModifiedLivingMotion(msg.motion[i], msg.animation[i]);
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
}