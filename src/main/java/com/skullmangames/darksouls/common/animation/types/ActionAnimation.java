package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.common.animation.JointTransform;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.core.event.EntityEventListener.EventType;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.block.BlockState;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;

public class ActionAnimation extends ImmovableAnimation
{
	protected final boolean breakMovement;
	protected final boolean affectYCoord;
	protected float delayTime;
	
	public ActionAnimation(int id, float convertTime, boolean breakMove, boolean affectY, String path)
	{
		this(id, convertTime, -1.0F, breakMove, affectY, path);
	}

	public ActionAnimation(int id, float convertTime, float postDelay, boolean breakMove, boolean affectY, String path)
	{
		super(id, convertTime, path);
		this.breakMovement = breakMove;
		this.affectYCoord = affectY;
		this.delayTime = postDelay;
	}
	
	@Override
	public void onActivate(LivingData<?> entity) {
		super.onActivate(entity);
		Entity orgEntity = entity.getOriginalEntity();
		
		float yaw = orgEntity.yRot;
		
		orgEntity.setYHeadRot(yaw);
		orgEntity.setYBodyRot(yaw);
		
		if(breakMovement)
		{
			entity.getOriginalEntity().setDeltaMovement(0.0D, orgEntity.getDeltaMovement().y, 0.0D);
		}
		
		if(entity instanceof PlayerData)
		{
			((PlayerData<?>)entity).getEventListener().activateEvents(EventType.ON_ACTION_EVENT);
		}
	}
	
	@Override
	public void onUpdate(LivingData<?> entity) {
		super.onUpdate(entity);

		LivingEntity livingentity = entity.getOriginalEntity();

		if (entity.isRemote()) {
			if (!(livingentity instanceof ClientPlayerEntity)) {
				return;
			}
		} else {
			if ((livingentity instanceof ServerPlayerEntity)) {
				return;
			}
		}
		
		if (entity.isInaction()) {
			Vector3f vec3 = this.getCoordVector(entity);
			BlockPos blockpos = new BlockPos(livingentity.getX(), livingentity.getBoundingBox().minY - 1.0D, livingentity.getZ());
			BlockState blockState = livingentity.level.getBlockState(blockpos);
			ModifiableAttributeInstance attribute = livingentity.getAttribute(Attributes.MOVEMENT_SPEED);
			boolean soulboost = blockState.is(BlockTags.SOUL_SPEED_BLOCKS) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SOUL_SPEED, livingentity) > 0;
			double speedFactor = soulboost ? 1.0D : livingentity.level.getBlockState(blockpos).getBlock().getSpeedFactor();
			double moveMultiplier = attribute.getValue() / attribute.getBaseValue() * speedFactor;
			livingentity.move(MoverType.SELF, new Vector3d(vec3.x() * moveMultiplier, vec3.y(), vec3.z() * moveMultiplier));
		}
	}
	
	@Override
	public LivingData.EntityState getState(float time)
	{
		if(time < this.delayTime)
		{
			return LivingData.EntityState.PRE_DELAY;
		}
		else
		{
			return LivingData.EntityState.FREE;
		}
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
	public StaticAnimation bindFull(Armature armature)
	{
		super.bindFull(armature);
		
		if(this.delayTime < 0.0F)
		{
			this.delayTime = this.totalTime;
		}
		
		return this;
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
			Vector3d motion = elb.getDeltaMovement();
			elb.setDeltaMovement(motion.x, motion.y + 0.08D, motion.z);
		}
		
		return new Vector3f(dx, dy, dz);
	}
}