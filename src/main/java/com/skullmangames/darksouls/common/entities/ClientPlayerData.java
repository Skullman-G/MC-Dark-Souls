package com.skullmangames.darksouls.common.entities;

import com.skullmangames.darksouls.animation.LivingMotion;
import com.skullmangames.darksouls.client.ClientEngine;
import com.skullmangames.darksouls.common.items.CapabilityItem;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSPlayAnimation;
import com.skullmangames.darksouls.util.math.MathUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientPlayerData extends RemoteClientPlayerData<ClientPlayerEntity>
{
	private LivingEntity rayTarget;
	
	@Override
	public void onEntityConstructed(ClientPlayerEntity entity)
	{
		super.onEntityConstructed(entity);
		ClientEngine.INSTANCE.setPlayerData(this);
		ClientEngine.INSTANCE.inputController.setGamePlayer(this);
	}
	
	@Override
	public void updateMotion()
	{
		super.updateMotion();

		if (!this.getClientAnimator().prevAiming())
		{
			if (this.currentMixMotion == LivingMotion.AIMING)
			{
				this.orgEntity.getUseItemRemainingTicks();
				ClientEngine.INSTANCE.renderEngine.zoomIn();
			}
		}
	}
	
	@Override
	public void updateOnClient()
	{
		super.updateOnClient();
		@SuppressWarnings("resource")
		RayTraceResult rayResult = Minecraft.getInstance().hitResult;

		if (rayResult.getType() == RayTraceResult.Type.ENTITY)
		{
			Entity hit = ((EntityRayTraceResult) rayResult).getEntity();
			if (hit instanceof LivingEntity)
				this.rayTarget = (LivingEntity) hit;
		}

		if (this.rayTarget != null)
		{
			if(!this.rayTarget.isAlive())
			{
				this.rayTarget = null;
			}
			else if(this.getOriginalEntity().distanceToSqr(this.rayTarget) > 64.0D)
			{
				this.rayTarget = null;
			}
			else if(MathUtils.getAngleBetween(this.getOriginalEntity(), this.rayTarget) > 1.5707963267948966D)
			{
				this.rayTarget = null;
			}
		}
	}
	
	@Override
	protected void playReboundAnimation()
	{
		this.getClientAnimator().playReboundAnimation();
		ClientEngine.INSTANCE.renderEngine.zoomOut(40);
	}
	
	@Override
	public void playAnimationSynchronize(int id, float modifyTime)
	{
		ModNetworkManager.sendToServer(new CTSPlayAnimation(id, modifyTime, false, true));
	}
	
	@Override
	public void onHeldItemChange(CapabilityItem mainHandCap, CapabilityItem offHandCap)
	{
		super.onHeldItemChange(mainHandCap, offHandCap);

		if (mainHandCap != null)
		{
			mainHandCap.onHeld(this);
		}
	}
	
	@Override
	public void aboutToDeath()
	{
		;
	}
	
	public void initFromOldOne(ClientPlayerData old)
	{
		this.setStunArmor(old.getStunArmor());
	}
	
	@Override
	public LivingEntity getTarget()
	{
		return this.rayTarget;
	}
	
	@SuppressWarnings("resource")
	@Override
	public boolean isFirstPerson()
	{
		return Minecraft.getInstance().options.getCameraType() == PointOfView.FIRST_PERSON;
	}
}