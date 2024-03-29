package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.item.DarkSoulsUseAction;
import com.skullmangames.darksouls.common.item.HasDarkSoulsUseAction;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AbstractClientPlayerCap<T extends AbstractClientPlayer> extends PlayerCap<T>
{
	protected float prevYaw;
	protected float bodyYaw;
	protected float prevBodyYaw;
	private Item prevHeldItem;
	private Item prevHeldItemOffHand;
	
	@Override
	public void onEntityJoinWorld(T entityIn)
	{
		super.onEntityJoinWorld(entityIn);
		this.prevHeldItem = Items.AIR;
		this.prevHeldItemOffHand = Items.AIR;
	}
	
	@Override
	public void updateMotion()
	{
		if (this.orgEntity.isFallFlying())
		{
			this.currentMotion = LivingMotion.FLYING;
		}
		else if (this.isMounted())
		{
			this.currentMotion = LivingMotion.MOUNTED;
		}
		else if (this.orgEntity.getPose() == Pose.SWIMMING && !this.orgEntity.isSecondaryUseActive())
		{
			this.currentMotion = LivingMotion.SWIMMING;
		}
		else
		{
			ClientAnimator animator = getClientAnimator();
			Vec3 mov = this.orgEntity.getDeltaMovement();

			if (this.orgEntity.isSwimming() && mov.y < -0.005)
			{
				this.currentMotion = LivingMotion.FLOATING;
			}
			else if(mov.y < -0.55F)
			{
				this.currentMotion = LivingMotion.FALL;
			}
			else if (mov.x != 0 || mov.z != 0)
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
					animator.baseLayer.animationPlayer.setReversed(false);
				}
				else if (this.orgEntity.moveDist < 0)
				{
					animator.baseLayer.animationPlayer.setReversed(true);
				}
			}
			else
			{
				animator.baseLayer.animationPlayer.setReversed(false);
				if (this.orgEntity.isCrouching())
				{
					this.currentMotion = LivingMotion.KNEELING;
				}
				else if (this.orgEntity.getUseItemRemainingTicks() > 0 && this.isBlocking())
				{
					this.currentMotion = LivingMotion.BLOCKING;
				}
				else
				{
					this.currentMotion = LivingMotion.IDLE;
				}
			}
		}
		
		if (this.orgEntity.getUseItemRemainingTicks() > 0)
		{
			InteractionHand hand = this.orgEntity.getUsedItemHand();
			LayerPart layerPart = hand == InteractionHand.MAIN_HAND ? LayerPart.RIGHT : LayerPart.LEFT;
			if (this.orgEntity.getItemInHand(this.orgEntity.getUsedItemHand()).getItem() instanceof HasDarkSoulsUseAction)
			{
				DarkSoulsUseAction useaction = ((HasDarkSoulsUseAction)this.orgEntity.getItemInHand(this.orgEntity.getUsedItemHand()).getItem()).getDarkSoulsUseAnimation();
				
				if (useaction == DarkSoulsUseAction.DARKSIGN || useaction == DarkSoulsUseAction.MIRACLE || useaction == DarkSoulsUseAction.SOUL_CONTAINER)
				{
					this.currentMixMotions.put(layerPart, LivingMotion.CONSUME_SOUL);
				}
			}
			else if (this.currentMotion != LivingMotion.BLOCKING && this.isBlocking()) this.currentMixMotions.put(layerPart, LivingMotion.BLOCKING);
			else
			{
				UseAnim useAction = this.orgEntity.getItemInHand(this.orgEntity.getUsedItemHand()).getUseAnimation();
				switch (useAction)
				{
					case BOW:
						this.currentMixMotions.put(layerPart, LivingMotion.AIMING);
						break;
						
					case CROSSBOW:
						this.currentMixMotions.put(layerPart, LivingMotion.RELOADING);
						break;
						
					case SPEAR:
						this.currentMixMotions.put(layerPart, LivingMotion.AIMING);
						break;
						
					case DRINK:
						this.currentMixMotions.put(layerPart, LivingMotion.DRINKING);
						break;
						
					case EAT:
						this.currentMixMotions.put(layerPart, LivingMotion.EATING);
						break;
						
					default:
						this.currentMixMotions.put(layerPart, LivingMotion.NONE);
						break;
				}
			}
		}
		else
		{
			boolean changedLeft = false;
			boolean changedRight = false;
			if (this.orgEntity.swinging)
			{
				this.currentMixMotions.put(LayerPart.RIGHT, LivingMotion.DIGGING);
				changedRight = true;
			}
			if (CrossbowItem.isCharged(this.orgEntity.getMainHandItem()))
			{
				this.currentMixMotions.put(LayerPart.UP, LivingMotion.AIMING);
				changedLeft = true;
				changedRight = true;
			}
			else if (this.getClientAnimator().isAiming()) this.playReboundAnimation();
			else 
			{
				if (this.isHoldingWeaponWithHoldingAnimation(InteractionHand.MAIN_HAND))
				{
					this.currentMixMotions.put(LayerPart.RIGHT, LivingMotion.HOLDING_WEAPON);
					changedRight = true;
				}
				if (this.isHoldingWeaponWithHoldingAnimation(InteractionHand.OFF_HAND))
				{
					this.currentMixMotions.put(LayerPart.LEFT, LivingMotion.HOLDING_WEAPON);
					changedLeft = true;
				}
			}
			if (!changedLeft) this.currentMixMotions.put(LayerPart.LEFT, LivingMotion.NONE);
			if (!changedRight) this.currentMixMotions.put(LayerPart.RIGHT, LivingMotion.NONE);
			if (!changedLeft && !changedRight) this.currentMixMotions.put(LayerPart.UP, LivingMotion.NONE);
		}
	}
	
	
	
	@Override
	protected void updateOnClient()
	{
		this.prevYaw = this.yaw;
		this.prevBodyYaw = this.bodyYaw;
		this.bodyYaw = this.isInaction() ? this.orgEntity.yRot : this.orgEntity.yBodyRotO;
		
		Item mainHandItem = this.orgEntity.getItemInHand(InteractionHand.MAIN_HAND).getItem();
		Item offHandItem = this.orgEntity.getItemInHand(InteractionHand.OFF_HAND).getItem();
		boolean isMainHandChanged = this.prevHeldItem != mainHandItem;
		boolean isOffHandChanged = this.prevHeldItemOffHand != offHandItem;
		
		if(isMainHandChanged || isOffHandChanged)
		{
			this.onHeldItemChange(this.getHeldItemCapability(InteractionHand.MAIN_HAND), this.getHeldItemCapability(InteractionHand.OFF_HAND));
			this.prevHeldItem = mainHandItem;
			this.prevHeldItemOffHand = offHandItem;
		}
		super.updateOnClient();
	}

	public void onHeldItemChange(ItemCapability mainHandCap, ItemCapability offHandCap)
	{
		this.cancelUsingItem();
	}

	protected void playReboundAnimation()
	{
		this.getClientAnimator().playReboundAnimation();
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
        
		if (this.isInaction() || entity.getControllingPassenger() != null)
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
			mat.rotate((float)-Math.toRadians(yawDegree), Vector3f.YP);
			mat.rotate((float)-Math.toRadians(pitchDegree), Vector3f.XP);
			mat.rotate((float)Math.toRadians((orgEntity.tickCount + partialTick) * -55.0), Vector3f.YP);
            
            return mat;
		}
		else if (orgEntity.isFallFlying())
		{
			PublicMatrix4f mat = PublicMatrix4f.getModelMatrixIntegrated((float)entity.xOld, (float)entity.getX(), (float)entity.yOld, (float)entity.getY(),
					(float)entity.zOld, (float)entity.getZ(), 0, 0, 0, 0, partialTick, 1, 1, 1);
			
			mat.rotate((float)-Math.toRadians(entity.yBodyRot), Vector3f.YP);
			
            float f = (float)orgEntity.getFallFlyingTicks() + Minecraft.getInstance().getFrameTime();
            float f1 = MathUtils.clamp(f * f / 100.0F, 0.0F, 1.0F);
            mat.rotate((float)Math.toRadians(f1 * (-90F - orgEntity.xRot)), Vector3f.XP);
            
            Vec3 vec3d = orgEntity.getEyePosition(Minecraft.getInstance().getFrameTime());
            Vec3 vec3d1 = orgEntity.getDeltaMovement();
            
            double d0 = vec3d1.x * vec3d1.x + vec3d1.z * vec3d1.z;
            double d1 = vec3d.x * vec3d.x + vec3d.z * vec3d.z;
            
			if (d0 > 0.0D && d1 > 0.0D)
			{
                double d2 = (vec3d1.x * vec3d.x + vec3d1.z * vec3d.z) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = vec3d1.x * vec3d.z - vec3d1.z * vec3d.x;
                
                mat.rotate((float)Math.toRadians((float)(Math.signum(d3) * Math.acos(d2)) * 180.0F / (float)Math.PI), Vector3f.YP);
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
				yaw = this.isInaction() ? MathUtils.interpolateRotation(this.prevYaw, this.yaw, partialTick) : 0;
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