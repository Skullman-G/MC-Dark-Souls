package com.skullmangames.darksouls.common.entities;

import com.skullmangames.darksouls.animation.LivingMotion;
import com.skullmangames.darksouls.animation.types.StaticAnimation;
import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.items.CapabilityItem;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModelInit;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSReqPlayerInfo;
import com.skullmangames.darksouls.util.IExtendedDamageSource.StunType;
import com.skullmangames.darksouls.util.math.MathUtils;
import com.skullmangames.darksouls.util.math.vector.PublicMatrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RemoteClientPlayerData<T extends AbstractClientPlayerEntity> extends PlayerData<T>
{
	protected float prevYaw;
	protected float bodyYaw;
	protected float prevBodyYaw;
	private ItemStack prevHeldItem;
	private ItemStack prevHeldItemOffHand;
	private boolean swingArm;
	
	@Override
	public void onEntityJoinWorld(T entityIn)
	{
		super.onEntityJoinWorld(entityIn);
		this.prevHeldItem = ItemStack.EMPTY;
		this.prevHeldItemOffHand = ItemStack.EMPTY;
		if(!(this instanceof ClientPlayerData))
		{
			ModNetworkManager.sendToServer(new CTSReqPlayerInfo(this.orgEntity.getId()));
		}
	}
	
	@Override
	public void updateMotion()
	{
		if (this.orgEntity.getHealth() <= 0.0F)
		{
			currentMotion = LivingMotion.DEATH;
		}
		else if (orgEntity.isFallFlying())
		{
			currentMotion = LivingMotion.FLYING;
		}
		else if (orgEntity.getControllingPassenger() != null)
		{
			currentMotion = LivingMotion.MOUNT;
		}
		else if (orgEntity.getPose() == Pose.SWIMMING && !this.orgEntity.isSecondaryUseActive())
		{
			currentMotion = LivingMotion.SWIMMING;
		}
		else
		{
			AnimatorClient animator = getClientAnimator();

			if (orgEntity.isSwimming() && orgEntity.getDeltaMovement().y < -0.005)
			{
				currentMotion = LivingMotion.FLOATING;
			}
			else if(orgEntity.getDeltaMovement().y < -0.55F)
			{
				currentMotion = LivingMotion.FALL;
			}
			else if (orgEntity.animationSpeed > 0.01F)
			{
				if(orgEntity.isCrouching())
				{
					currentMotion = LivingMotion.SNEAKING;
				}
				else if (orgEntity.isSprinting())
				{
					currentMotion = LivingMotion.RUNNING;
				}
				else
				{
					currentMotion = LivingMotion.WALKING;
				}

				if (orgEntity.moveDist > 0)
				{
					animator.setReverse(false, this.currentMotion);
				}
				else if (orgEntity.moveDist < 0)
				{
					animator.setReverse(true, this.currentMotion);
				}
			}
			else
			{
				animator.setReverse(false, this.currentMotion);
				if (orgEntity.isCrouching())
				{
					currentMotion = LivingMotion.KNEELING;
				}
				else
				{
					currentMotion = LivingMotion.IDLE;
				}
			}
		}

		if (this.orgEntity.isSecondaryUseActive() && orgEntity.getUseItemRemainingTicks() > 0)
		{
			UseAction useAction = this.orgEntity.getItemInHand(this.orgEntity.getUsedItemHand()).getUseAnimation();

			if (useAction == UseAction.BLOCK)
				currentMixMotion = LivingMotion.BLOCKING;
			else if (useAction == UseAction.BOW)
				currentMixMotion = LivingMotion.AIMING;
			else if (useAction == UseAction.CROSSBOW)
				currentMixMotion = LivingMotion.RELOADING;
			else if (useAction == UseAction.SPEAR)
				currentMixMotion = LivingMotion.AIMING;
			else
				currentMixMotion = LivingMotion.NONE;
		}
		else
		{
			if (CrossbowItem.isCharged(this.orgEntity.getMainHandItem()))
				currentMixMotion = LivingMotion.AIMING;
			else if (this.getClientAnimator().prevAiming())
				this.playReboundAnimation();
			else
				currentMixMotion = LivingMotion.NONE;
		}
	}
	
	@Override
	protected void updateOnClient()
	{
		this.prevYaw = this.yaw;
		this.prevBodyYaw = this.bodyYaw;
		this.bodyYaw = this.inaction ? this.orgEntity.yRot : this.orgEntity.yBodyRotO;
		
		boolean isMainHandChanged = prevHeldItem.getItem() != this.orgEntity.inventory.getCarried().getItem();
		boolean isOffHandChanged = prevHeldItemOffHand.getItem() != this.orgEntity.inventory.offhand.get(0).getItem();
		
		if(isMainHandChanged || isOffHandChanged)
		{
			onHeldItemChange(this.getHeldItemCapability(Hand.MAIN_HAND), this.getHeldItemCapability(Hand.OFF_HAND));
			if(isMainHandChanged)
				prevHeldItem = this.orgEntity.inventory.getCarried();
			if(isOffHandChanged)
				prevHeldItemOffHand = this.orgEntity.inventory.offhand.get(0);
		}
		
		super.updateOnClient();
		
		if(this.orgEntity.deathTime == 1)
		{
			this.getClientAnimator().playDeathAnimation();
		}
		
		if (this.swingArm != orgEntity.swinging)
		{
			if(!this.swingArm)
			{
				this.getClientAnimator().playMixLayerAnimation(Animations.BIPED_DIG);
			}
			else
			{
				this.getClientAnimator().offMixLayer(false);
			}
			
			this.swingArm = orgEntity.swinging;
		}
	}

	public void onHeldItemChange(CapabilityItem mainHandCap, CapabilityItem offHandCap)
	{
		this.getClientAnimator().resetMixMotion();
		this.getClientAnimator().offMixLayer(false);
		this.cancelUsingItem();
	}

	protected void playReboundAnimation()
	{
		this.getClientAnimator().playReboundAnimation();
	}

	@Override
	public void playAnimationSynchronize(int id, float modifyTime)
	{
		
	}

	@Override
	public <M extends Model> M getEntityModel(ModelInit<M> modelDB)
	{
		return this.orgEntity.getModelName().equals("slim") ? modelDB.ENTITY_BIPED_SLIM_ARM : modelDB.ENTITY_BIPED;
	}

	@Override
	public PublicMatrix4f getHeadMatrix(float partialTick)
	{
		T entity = getOriginalEntity();
		
        float yaw;
        float pitch = 0;
        float prvePitch = 0;
        
		if (inaction || entity.getControllingPassenger() != null)
		{
	        yaw = 0;
		}
		else
		{
			float f = MathUtils.interpolateRotation(this.prevBodyYaw, this.bodyYaw, partialTick);
			float f1 = MathUtils.interpolateRotation(entity.yHeadRotO, entity.yHeadRot, partialTick);
	        yaw = f1 - f;
		}
        
		if (!orgEntity.isFallFlying())
		{
			prvePitch = entity.xRotO;
			pitch = entity.xRot;
		}
        
		return PublicMatrix4f.getModelMatrixIntegrated(0, 0, 0, 0, 0, 0, prvePitch, pitch, yaw, yaw, partialTick, 1, 1, 1);
	}
	
	@Override
	public PublicMatrix4f getModelMatrix(float partialTick)
	{
		LivingEntity entity = getOriginalEntity();

		if (orgEntity.isAutoSpinAttack())
		{
			PublicMatrix4f mat = PublicMatrix4f.getModelMatrixIntegrated((float)entity.xOld, (float)entity.getX(), (float)entity.yOld, (float)entity.getY(),
					(float)entity.zOld, (float)entity.getZ(), 0, 0, 0, 0, partialTick, 1, 1, 1);
			float yawDegree = MathUtils.interpolateRotation(orgEntity.yRotO, orgEntity.yRot, partialTick);
			float pitchDegree = MathUtils.interpolateRotation(orgEntity.xRotO, orgEntity.xRot, partialTick) + 90.0F;
			PublicMatrix4f.rotate((float)-Math.toRadians(yawDegree), new Vector3f(0F, 1F, 0F), mat, mat);
			PublicMatrix4f.rotate((float)-Math.toRadians(pitchDegree), new Vector3f(1F, 0F, 0F), mat, mat);
			PublicMatrix4f.rotate((float)Math.toRadians((orgEntity.tickCount + partialTick) * -55.0), new Vector3f(0F, 1F, 0F), mat, mat);
            
            return mat;
		}
		else if (orgEntity.isFallFlying())
		{
			PublicMatrix4f mat = PublicMatrix4f.getModelMatrixIntegrated((float)entity.xOld, (float)entity.getX(), (float)entity.yOld, (float)entity.getY(),
					(float)entity.zOld, (float)entity.getZ(), 0, 0, 0, 0, partialTick, 1, 1, 1);
			
			PublicMatrix4f.rotate((float)-Math.toRadians(entity.yBodyRot), new Vector3f(0F, 1F, 0F), mat, mat);
			
            float f = (float)orgEntity.getFallFlyingTicks() + Minecraft.getInstance().getFrameTime();
            float f1 = MathHelper.clamp(f * f / 100.0F, 0.0F, 1.0F);
            PublicMatrix4f.rotate((float)Math.toRadians(f1 * (-90F - orgEntity.xRot)), new Vector3f(1F, 0F, 0F), mat, mat);
            
            Vector3d vec3d = orgEntity.getEyePosition(Minecraft.getInstance().getFrameTime());
            Vector3d vec3d1 = orgEntity.getDeltaMovement();
            
            double d0 = vec3d1.x * vec3d1.x + vec3d1.z * vec3d1.z;
            double d1 = vec3d.x * vec3d.x + vec3d.z * vec3d.z;
            
			if (d0 > 0.0D && d1 > 0.0D)
			{
                double d2 = (vec3d1.x * vec3d.x + vec3d1.z * vec3d.z) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = vec3d1.x * vec3d.z - vec3d1.z * vec3d.x;
                
                PublicMatrix4f.rotate((float)Math.toRadians((float)(Math.signum(d3) * Math.acos(d2)) * 180.0F / (float)Math.PI), new Vector3f(0F, 1F, 0F), mat, mat);
            }
			
            return mat;
		}
		else
		{
			float yaw;
			float prevRotYaw;
			float rotyaw;
			float prevPitch = 0;
			float pitch = 0;
			
			if (orgEntity.getControllingPassenger() instanceof LivingEntity)
			{
				LivingEntity ridingEntity = (LivingEntity)orgEntity.getControllingPassenger();
				prevRotYaw = ridingEntity.yBodyRotO;
				rotyaw = ridingEntity.yBodyRot;
			}
			else
			{
				yaw = inaction ? MathUtils.interpolateRotation(this.prevYaw, this.yaw, partialTick) : 0;
				prevRotYaw = this.prevBodyYaw + yaw;
				rotyaw = this.bodyYaw + yaw;
			}
			
			if (!this.isInaction() && orgEntity.getPose() == Pose.SWIMMING)
			{
				float f = this.orgEntity.getSwimAmount(partialTick);
				float f3 = this.orgEntity.isInWater() ? this.orgEntity.xRot : 0;
		        float f4 = MathHelper.lerp(f, 0.0F, f3);
		        prevPitch = f4;
		        pitch = f4;
			}
			
			return PublicMatrix4f.getModelMatrixIntegrated((float)entity.xOld, (float)entity.getX(), (float)entity.yOld, (float)entity.getY(),
					(float)entity.zOld, (float)entity.getZ(), prevPitch, pitch, prevRotYaw, rotyaw, partialTick, 1, 1, 1);
		}
	}

	@Override
	public StaticAnimation getHitAnimation(StunType stunType)
	{
		return null;
	}
}