package com.skullmangames.darksouls.common.animation.types;

import java.util.Map;
import java.util.function.Function;

import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.JointTransform;
import com.skullmangames.darksouls.common.animation.Keyframe;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.animation.Property;
import com.skullmangames.darksouls.common.animation.TransformSheet;
import com.skullmangames.darksouls.common.animation.Property.ActionAnimationProperty;
import com.skullmangames.darksouls.common.animation.Property.MovementAnimationSet;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCSetPos;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ActionAnimation extends ImmovableAnimation
{
	protected float delayTime;

	public ActionAnimation(float convertTime, String path, Function<Models<?>, Model> model)
	{
		this(convertTime, Float.MAX_VALUE, path, model);
	}

	public ActionAnimation(float convertTime, float postDelay, String path, Function<Models<?>, Model> model)
	{
		super(convertTime, path, model);
		this.delayTime = postDelay;
	}

	public <V> ActionAnimation addProperty(Property<V> propertyType, V value)
	{
		this.properties.put(propertyType, value);
		return this;
	}

	@Override
	public void onStart(LivingCap<?> entityCap)
	{
		super.onStart(entityCap);
		if (!(this instanceof BlockAnimation)) entityCap.cancelUsingItem();

		if (this.getProperty(ActionAnimationProperty.INTERRUPT_PREVIOUS_DELTA_MOVEMENT).orElse(true))
		{
			entityCap.getOriginalEntity().setDeltaMovement(0.0D, entityCap.getOriginalEntity().getDeltaMovement().y, 0.0D);
		}

		MovementAnimationSet movementAnimationSetter = this.getProperty(ActionAnimationProperty.MOVEMENT_ANIMATION_SETTER).orElse((self, entityCap$2, transformSheet) ->
				{
					transformSheet.readFrom(self.jointTransforms.get("Root"));
				});

		entityCap.getAnimator().getPlayerFor(this).setMovementAnimation(this, entityCap, movementAnimationSetter);
	}

	@Override
	public void onUpdate(LivingCap<?> entityCap)
	{
		super.onUpdate(entityCap);
		this.move(entityCap, this);
	}

	@Override
	public void onUpdateLink(LivingCap<?> entityCap, LinkAnimation linkAnimation)
	{
		this.move(entityCap, linkAnimation);
	};

	private void move(LivingCap<?> entityCap, DynamicAnimation animation)
	{
		LivingEntity livingentity = entityCap.getOriginalEntity();
		
		if (entityCap.isClientSide())
		{
			if (!(livingentity instanceof LocalPlayer)) return;
		} else
		{
			if ((livingentity instanceof ServerPlayer)) return;
		}

		if (!this.validateMovement(entityCap, animation)) return;

		if (entityCap.isInaction())
		{
			Vector3f vec3 = this.getCoordVector(entityCap, animation);
			BlockPos blockpos = new BlockPos(livingentity.getX(), livingentity.getBoundingBox().minY - 1.0D,
					livingentity.getZ());
			BlockState blockState = livingentity.level.getBlockState(blockpos);
			AttributeInstance movementSpeed = livingentity.getAttribute(Attributes.MOVEMENT_SPEED);
			boolean soulboost = blockState.is(BlockTags.SOUL_SPEED_BLOCKS)
					&& EnchantmentHelper.getEnchantmentLevel(Enchantments.SOUL_SPEED, livingentity) > 0;
			double speedFactor = soulboost ? 1.0D
					: livingentity.level.getBlockState(blockpos).getBlock().getSpeedFactor();
			double moveMultiplier = this.getProperty(ActionAnimationProperty.AFFECT_SPEED).orElse(false)
					? (movementSpeed.getValue() / movementSpeed.getBaseValue())
					: 1.0F;
			livingentity.move(MoverType.SELF, new Vec3(vec3.x() * moveMultiplier * speedFactor, vec3.y(), vec3.z() * moveMultiplier * speedFactor));
			
			if (animation instanceof LinkAnimation && this.getProperty(ActionAnimationProperty.ROTATE_TO_TARGET).orElse(true) && livingentity instanceof Mob)
			{
				Mob mob = (Mob)livingentity;
				LivingEntity target = mob.getTarget();
				if (target != null)
				{
					entityCap.rotateTo(target, 90F, true);
				}
			}
			
			if (!entityCap.isClientSide()) ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCSetPos(livingentity.position(), livingentity.getYRot(), livingentity.getXRot(), livingentity.getId()), livingentity);
		}
	}

	private boolean validateMovement(LivingCap<?> entityCap, DynamicAnimation animation)
	{
		if (animation instanceof LinkAnimation)
		{
			if (!this.getProperty(ActionAnimationProperty.MOVE_ON_LINK).orElse(true))
			{
				return false;
			}
			else
			{
				return this.checkMovementTime(0.0F);
			}
		} else
		{
			return this.checkMovementTime(entityCap.getAnimator().getPlayerFor(animation).getElapsedTime());
		}
	}

	private boolean checkMovementTime(float currentTime)
	{
		if (this.properties.containsKey(ActionAnimationProperty.ACTION_TIME))
		{
			ActionTime[] actionTimes = this.getProperty(ActionAnimationProperty.ACTION_TIME).get();
			for (ActionTime actionTime : actionTimes)
			{
				if (currentTime <= actionTime.end && actionTime.onStart <= currentTime) return true;
			}
			return false;
		}
		else return true;
	}

	@Override
	public EntityState getState(float time)
	{
		if (time <= this.delayTime)
		{
			return EntityState.PRE_CONTACT;
		} else
		{
			return EntityState.FREE;
		}
	}

	@Override
	protected void modifyPose(Pose pose, LivingCap<?> entityCap, float time)
	{
		JointTransform rootTransform = pose.getTransformByName("Root");
		Vector3f rootPosition = rootTransform.translation();
		PublicMatrix4f toRootTransformApplied = entityCap.getEntityModel(Models.SERVER).getArmature()
				.searchJointByName("Root").getLocalTransform().removeTranslation();
		PublicMatrix4f toOrigin = PublicMatrix4f.invert(toRootTransformApplied, null);
		Vector3f worldPosition = PublicMatrix4f.transform3v(toRootTransformApplied, rootPosition, null);
		worldPosition.setX(0.0F);
		worldPosition.setY(
				(this.getProperty(ActionAnimationProperty.MOVE_VERTICAL).orElse(false) && worldPosition.y() > 0.0F)
						? 0.0F
						: worldPosition.y());
		worldPosition.setZ(0.0F);
		PublicMatrix4f.transform3v(toOrigin, worldPosition, worldPosition);
		rootPosition.set(worldPosition.x(), worldPosition.y(), worldPosition.z());
	}

	@Override
	public void setLinkAnimation(Pose lastPose, float convertTimeModifier, LivingCap<?> entityCap, LinkAnimation dest)
	{
		float totalTime = convertTimeModifier > 0.0F ? convertTimeModifier : this.convertTime;
		float nextStart = 0.0F;
		
		if (convertTimeModifier < 0.0F)
		{
			nextStart -= convertTimeModifier;
		}
		dest.startsAt = nextStart;
		
		dest.getTransfroms().clear();
		dest.setTotalTime(totalTime);
		dest.setNextAnimation(this);
		
		Pose pose = this.getPoseByTime(entityCap, nextStart, 1.0F);
		Map<String, JointTransform> lastTransforms = lastPose.getJointTransformData();
		Map<String, JointTransform> nextTransforms = pose.getJointTransformData();
		JointTransform rootTransform = pose.getTransformByName("Root");
		Vector3f withPosition = entityCap.getAnimator().getPlayerFor(this).getMovementAnimation().getInterpolatedTranslation(nextStart);
		
		rootTransform.translation().set(withPosition.x(), rootTransform.translation().y(), withPosition.z());

		for (String jointName : lastTransforms.keySet())
		{
			if (lastTransforms.containsKey(jointName) && nextTransforms.containsKey(jointName))
			{
				Keyframe[] keyframes = new Keyframe[2];
				keyframes[0] = new Keyframe(0, lastTransforms.get(jointName));
				keyframes[1] = new Keyframe(totalTime, nextTransforms.get(jointName));
				TransformSheet sheet = new TransformSheet(keyframes);
				dest.addSheet(jointName, sheet);
			}
		}
	}

	protected Vector3f getCoordVector(LivingCap<?> entityCap, DynamicAnimation animation)
	{
		MovementAnimationSet coordFunction = this.getProperty(ActionAnimationProperty.MOVEMENT_ANIMATION_SETTER)
				.orElse(null);
		TransformSheet rootTransforms = (coordFunction == null || animation instanceof LinkAnimation)
				? animation.jointTransforms.get("Root")
				: entityCap.getAnimator().getPlayerFor(this).getMovementAnimation();

		if (rootTransforms != null)
		{
			LivingEntity livingentity = entityCap.getOriginalEntity();
			AnimationPlayer player = entityCap.getAnimator().getPlayerFor(animation);
			
			JointTransform jt = rootTransforms.getInterpolatedTransform(player.getElapsedTime());
			JointTransform prevJt = rootTransforms.getInterpolatedTransform(player.getPrevElapsedTime());
			
			Vector4f currentpos = new Vector4f(jt.translation().x(), jt.translation().y(), jt.translation().z(), 1.0F);
			Vector4f prevpos = new Vector4f(prevJt.translation().x(), prevJt.translation().y(), prevJt.translation().z(), 1.0F);
			PublicMatrix4f rotationTransform = entityCap.getModelMatrix(1.0F).removeTranslation();
			PublicMatrix4f localTransform = entityCap.getEntityModel(Models.SERVER).getArmature().searchJointByName("Root").getLocalTransform().removeTranslation();
			rotationTransform.mulBack(localTransform);
			currentpos = rotationTransform.transform(currentpos);
			prevpos = rotationTransform.transform(prevpos);
			boolean hasNoGravity = entityCap.getOriginalEntity().isNoGravity();
			boolean moveVertical = this.getProperty(ActionAnimationProperty.MOVE_VERTICAL).orElse(false);
			float dx = prevpos.x() - currentpos.x();
			float dy = (moveVertical || hasNoGravity) ? currentpos.y() - prevpos.y() : 0.0F;
			float dz = prevpos.z() - currentpos.z();
			dx = Math.abs(dx) > 0.0000001F ? dx : 0.0F;
			dz = Math.abs(dz) > 0.0000001F ? dz : 0.0F;

			if (moveVertical && currentpos.y() > 0.0F && !hasNoGravity)
			{
				Vec3 motion = livingentity.getDeltaMovement();
				livingentity.setDeltaMovement(motion.x, motion.y <= 0 ? (motion.y + 0.08D) : motion.y, motion.z);
			}

			return new Vector3f(dx, dy, dz);
		} else
		{
			return new Vector3f(0, 0, 0);
		}
	}

	public static class ActionTime
	{
		private float onStart;
		private float end;

		private ActionTime(float onStart, float end)
		{
			this.onStart = onStart;
			this.end = end;
		}

		public static ActionTime crate(float onStart, float end)
		{
			return new ActionTime(onStart, end);
		}
	}
}