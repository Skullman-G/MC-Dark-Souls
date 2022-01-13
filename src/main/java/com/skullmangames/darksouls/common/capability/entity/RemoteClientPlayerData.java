package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.item.CapabilityItem;
import com.skullmangames.darksouls.common.item.DarkSoulsUseAction;
import com.skullmangames.darksouls.common.item.IHaveDarkSoulsUseAction;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.client.animation.MixLayer;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSReqPlayerInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RemoteClientPlayerData<T extends AbstractClientPlayer> extends PlayerData<T>
{
	protected float prevYaw;
	protected float bodyYaw;
	protected float prevBodyYaw;
	private ItemStack prevHeldItem;
	private ItemStack prevHeldItemOffHand;
	
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
		else if (this.orgEntity.isUsingItem())
		{
			ItemStack useitem = this.orgEntity.getItemInHand(this.orgEntity.getUsedItemHand());
			if (useitem.getItem() instanceof IHaveDarkSoulsUseAction)
			{
				DarkSoulsUseAction useaction = ((IHaveDarkSoulsUseAction)useitem.getItem()).getDarkSoulsUseAnimation();
				
				switch (useaction)
				{
					case DARKSIGN:
						this.currentMotion = LivingMotion.CONSUME_SOUL;
						break;
						
					case MIRACLE:
						this.currentMotion = LivingMotion.CONSUME_SOUL;
						break;
						
					case SOUL_CONTAINER:
						this.currentMotion = LivingMotion.CONSUME_SOUL;
						break;
						
					default:
						break;
				}
			}
			else
			{
				UseAnim useaction = useitem.getUseAnimation();
				
				switch (useaction)
				{
					case DRINK:
						this.currentMotion = LivingMotion.DRINKING;
						break;
						
					case EAT:
						this.currentMotion = LivingMotion.EATING;
						break;
						
					default:
						break;
				}
			}
		}
		else if (this.orgEntity.isFallFlying())
		{
			this.currentMotion = LivingMotion.FLYING;
		}
		else if (this.orgEntity.getControllingPassenger() != null)
		{
			this.currentMotion = LivingMotion.MOUNT;
		}
		else if (this.orgEntity.getPose() == Pose.SWIMMING && !this.orgEntity.isSecondaryUseActive())
		{
			this.currentMotion = LivingMotion.SWIMMING;
		}
		else
		{
			AnimatorClient animator = getClientAnimator();

			if (this.orgEntity.isSwimming() && this.orgEntity.getDeltaMovement().y < -0.005)
			{
				this.currentMotion = LivingMotion.FLOATING;
			}
			else if(this.orgEntity.getDeltaMovement().y < -0.55F)
			{
				this.currentMotion = LivingMotion.FALL;
			}
			else if (this.orgEntity.animationSpeed > 0.01F)
			{
				if(this.orgEntity.isCrouching())
				{
					this.currentMotion = LivingMotion.SNEAKING;
				}
				else if (this.orgEntity.isSprinting())
				{
					this.currentMotion = LivingMotion.RUNNING;
				}
				else
				{
					this.currentMotion = LivingMotion.WALKING;
				}

				if (this.orgEntity.moveDist > 0)
				{
					animator.setReverse(false, this.currentMotion);
				}
				else if (this.orgEntity.moveDist < 0)
				{
					animator.setReverse(true, this.currentMotion);
				}
			}
			else
			{
				animator.setReverse(false, this.currentMotion);
				if (this.orgEntity.isCrouching())
				{
					this.currentMotion = LivingMotion.KNEELING;
				}
				else
				{
					this.currentMotion = LivingMotion.IDLE;
				}
			}
		}

		if (this.orgEntity.getUseItemRemainingTicks() > 0)
		{
			UseAnim useAction = this.orgEntity.getItemInHand(this.orgEntity.getUsedItemHand()).getUseAnimation();
			
			switch (useAction)
			{
				case BLOCK:
					this.currentMixMotion = LivingMotion.BLOCKING;
					break;
					
				case BOW:
					this.currentMixMotion = LivingMotion.AIMING;
					break;
					
				case CROSSBOW:
					this.currentMixMotion = LivingMotion.RELOADING;
					break;
					
				case SPEAR:
					this.currentMixMotion = LivingMotion.AIMING;
					break;
					
				default:
					this.currentMixMotion = LivingMotion.NONE;
					break;
			}
		}
		else if (this.orgEntity.swinging)
		{
			this.currentMixMotion = LivingMotion.DIGGING;
		}
		else
		{
			if (CrossbowItem.isCharged(this.orgEntity.getMainHandItem())) this.currentMixMotion = LivingMotion.AIMING;
			else if (this.getClientAnimator().prevAiming())	this.playReboundAnimation();
			else if (this.isHoldingWeaponWithHoldingAnimation(InteractionHand.MAIN_HAND) || this.isHoldingWeaponWithHoldingAnimation(InteractionHand.OFF_HAND)) this.currentMixMotion = LivingMotion.HOLDING_WEAPON;
			else this.currentMixMotion = LivingMotion.NONE;
		}
	}
	
	private void updateClientAnimator()
	{
		AnimatorClient animator = getClientAnimator();
		
		if(this.inaction)
		{
			this.currentMotion = LivingMotion.IDLE;
		}
		else
		{
			this.updateMotion();
			if(!animator.compareMotion(currentMotion))
			{
				animator.playLoopMotion();
			}
			if(!animator.compareMixMotion(currentMixMotion) || this.currentMixMotion == LivingMotion.HOLDING_WEAPON)
			{
				animator.playMixLoopMotion();
			}
		}
	}
	
	
	
	@Override
	protected void updateOnClient()
	{
		this.prevYaw = this.yaw;
		this.prevBodyYaw = this.bodyYaw;
		this.bodyYaw = this.inaction ? this.orgEntity.yRot : this.orgEntity.yBodyRotO;
		
		boolean isMainHandChanged = prevHeldItem != this.orgEntity.getItemInHand(InteractionHand.MAIN_HAND);
		boolean isOffHandChanged = prevHeldItemOffHand != this.orgEntity.getItemInHand(InteractionHand.OFF_HAND);
		
		if(isMainHandChanged || isOffHandChanged)
		{
			this.getClientAnimator().resetMixMotion();
			if(isMainHandChanged)
			{
				prevHeldItem = this.orgEntity.getItemInHand(InteractionHand.MAIN_HAND);
				MixLayer right = this.getClientAnimator().mixLayerRight;
				if (right.isActive() && !this.isHoldingWeaponWithHoldingAnimation(InteractionHand.MAIN_HAND)) this.getClientAnimator().offMixLayer(right, false);
			}
			if(isOffHandChanged)
			{
				prevHeldItemOffHand = this.orgEntity.getItemInHand(InteractionHand.OFF_HAND);
				MixLayer left = this.getClientAnimator().mixLayerLeft;
				if (left.isActive() && !this.isHoldingWeaponWithHoldingAnimation(InteractionHand.OFF_HAND)) this.getClientAnimator().offMixLayer(left, false);
			}
			this.onHeldItemChange(this.getHeldItemCapability(InteractionHand.MAIN_HAND), this.getHeldItemCapability(InteractionHand.OFF_HAND));
			this.updateClientAnimator();
		}
		else super.updateOnClient();
		
		if(this.orgEntity.deathTime == 1)
		{
			this.getClientAnimator().playDeathAnimation();
		}
	}

	public void onHeldItemChange(CapabilityItem mainHandCap, CapabilityItem offHandCap)
	{
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
	public <M extends Model> M getEntityModel(Models<M> modelDB)
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
            float f1 = MathUtils.clamp(f * f / 100.0F, 0.0F, 1.0F);
            PublicMatrix4f.rotate((float)Math.toRadians(f1 * (-90F - orgEntity.xRot)), new Vector3f(1F, 0F, 0F), mat, mat);
            
            Vec3 vec3d = orgEntity.getEyePosition(Minecraft.getInstance().getFrameTime());
            Vec3 vec3d1 = orgEntity.getDeltaMovement();
            
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
		        float f4 = Mth.lerp(f, 0.0F, f3);
		        prevPitch = f4;
		        pitch = f4;
			}
			
			return PublicMatrix4f.getModelMatrixIntegrated((float)entity.xOld, (float)entity.getX(), (float)entity.yOld, (float)entity.getY(),
					(float)entity.zOld, (float)entity.getZ(), prevPitch, pitch, prevRotYaw, rotyaw, partialTick, 1, 1, 1);
		}
	}
}