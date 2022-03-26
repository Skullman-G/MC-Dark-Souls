package com.skullmangames.darksouls.common.animation.types;

import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import com.skullmangames.darksouls.common.animation.JointTransform;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.core.event.EntityEventListener.EventType;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;

public class ActionAnimation extends ImmovableAnimation
{
	protected final boolean affectYCoord;
	protected float delayTime;
	
	public ActionAnimation(float convertTime, boolean affectY, String path, String armature)
	{
		this(convertTime, -1.0F, affectY, path, armature);
	}

	public ActionAnimation(float convertTime, float postDelay, boolean affectY, String path, String armature)
	{
		super(convertTime, path, armature, false);
		this.affectYCoord = affectY;
		this.delayTime = postDelay;
	}
	
	@Override
	public void onActivate(LivingData<?> entity)
	{
		super.onActivate(entity);
		Entity orgEntity = entity.getOriginalEntity();
		
		float yaw = orgEntity.yRot;
		
		orgEntity.setYHeadRot(yaw);
		orgEntity.setYBodyRot(yaw);
		
		if(entity instanceof PlayerData)
		{
			((PlayerData<?>)entity).getEventListener().activateEvents(EventType.ON_ACTION_EVENT);
		}
	}
	
	@Override
	public void onUpdate(LivingData<?> entity)
	{
		super.onUpdate(entity);

		LivingEntity livingentity = entity.getOriginalEntity();

		if (entity.isClientSide()) if (!(livingentity instanceof LocalPlayer)) return;
		else if ((livingentity instanceof ServerPlayer)) return;
		
		if (entity.isInaction())
		{
			Vector3f vec3 = this.getCoordVector(entity);
			BlockPos blockpos = new BlockPos(livingentity.getX(), livingentity.getBoundingBox().minY - 1.0D, livingentity.getZ());
			BlockState blockState = livingentity.level.getBlockState(blockpos);
			AttributeInstance attribute = livingentity.getAttribute(Attributes.MOVEMENT_SPEED);
			boolean soulboost = blockState.is(BlockTags.SOUL_SPEED_BLOCKS) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SOUL_SPEED, livingentity) > 0;
			double speedFactor = soulboost ? 1.0D : livingentity.level.getBlockState(blockpos).getBlock().getSpeedFactor();
			double moveMultiplier = attribute.getValue() / attribute.getBaseValue() * speedFactor;
			livingentity.move(MoverType.SELF, new Vec3(vec3.x() * moveMultiplier, vec3.y(), vec3.z() * moveMultiplier));
		}
	}
	
	@Override
	public LivingData.EntityState getState(float time)
	{
		if(time < this.delayTime) return LivingData.EntityState.PRE_DELAY;
		else return LivingData.EntityState.FREE;
	}
	
	@Override
	public Pose getPoseByTime(LivingData<?> entity, float time)
	{
		Pose pose = new Pose();

		for (String jointName : jointTransforms.keySet())
		{
			JointTransform jt = jointTransforms.get(jointName).getInterpolatedTransform(time);

			if (jointName.equals("Root"))
			{
				Vector3f vec = jt.getPosition();
				vec.setX(0.0F);
				vec.setY(this.affectYCoord && vec.y() > 0.0F ? 0.0F : vec.y());
				vec.setZ(0.0F);
			}
			
			pose.putJointData(jointName, jt);
		}
		
		return pose;
	}
	
	@Override
	public void bind(Dist dist)
	{
		super.bind(dist);
		if (this.clientOnly && dist != Dist.CLIENT) return;
		if(this.delayTime >= 0.0F) return;
		this.delayTime = 0.0F;
	}
	
	protected Vector3f getCoordVector(LivingData<?> entitydata)
	{
		LivingEntity elb = entitydata.getOriginalEntity();
		JointTransform jt = jointTransforms.get("Root").getInterpolatedTransform(entitydata.getAnimator().getPlayer().getElapsedTime());
		JointTransform prevJt = jointTransforms.get("Root").getInterpolatedTransform(entitydata.getAnimator().getPlayer().getPrevElapsedTime());	
		Vector4f currentPos = new Vector4f(jt.getPosition().x(), jt.getPosition().y(), jt.getPosition().z(), 1.0F);
		Vector4f prevPos = new Vector4f(prevJt.getPosition().x(), prevJt.getPosition().y(), prevJt.getPosition().z(), 1.0F);
		PublicMatrix4f mat = entitydata.getModelMatrix(1.0F);
		mat.m30 = 0;
		mat.m31 = 0;
		mat.m32 = 0;
		PublicMatrix4f.transform(mat, currentPos, currentPos);
		PublicMatrix4f.transform(mat, prevPos, prevPos);
		boolean hasNoGravity = entitydata.getOriginalEntity().isNoGravity();
		float dx = prevPos.x() - currentPos.x();
		float dy = (this.affectYCoord && currentPos.y() > 0.0F) || hasNoGravity ? currentPos.y() - prevPos.y() : 0.0F;
		float dz = prevPos.z() - currentPos.z();
		
		if (this.affectYCoord && currentPos.y() > 0.0F && !hasNoGravity) {
			Vec3 motion = elb.getDeltaMovement();
			elb.setDeltaMovement(motion.x, motion.y + 0.08D, motion.z);
		}
		
		Vector3f vec = new Vector3f(dx, dy, dz);
		vec.mul(2F);
		
		return vec;
	}
}