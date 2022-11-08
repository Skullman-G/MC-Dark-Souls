package com.skullmangames.darksouls.client.renderer;

import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.core.util.math.vector.Vector2f;

import net.minecraft.client.Camera;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

public class ModCamera extends Camera
{
	private Vector2f pivotRot = Vector2f.ZERO;
	private Vector2f pivotRotOld = Vector2f.ZERO;
	
	@Override
	public void setup(BlockGetter world, Entity entity, boolean detached, boolean mirror, float partialTick)
	{
		this.initialized = true;
	    this.level = world;
	    this.entity = entity;
	    this.detached = detached;
	    
	    this.setRotation(entity.getViewYRot(partialTick), entity.getViewXRot(partialTick));
	    this.setPosition(Mth.lerp((double)partialTick, entity.xo, entity.getX()), Mth.lerp((double)partialTick, entity.yo, entity.getY()) + (double)Mth.lerp(partialTick, this.eyeHeightOld, this.eyeHeight), Mth.lerp((double)partialTick, entity.zo, entity.getZ()));
	    
	    if (detached)
	    {
	    	if (ClientManager.INSTANCE.getPlayerCap().getClientAnimator().isAiming())
	    	{
	    		this.move(-this.getMaxZoom(4.0D), 0.25D, -1.25D);
	    		
	    		this.pivotRotOld.y = entity.xRotO;
	    		this.pivotRotOld.x = entity.yRotO;
	    		this.pivotRot.y = entity.xRot;
	    		this.pivotRot.x = entity.yRot;
	    	}
	    	else
	    	{
	    		float xRot = this.getPivotXRot(partialTick);
	    		float yRot = this.getPivotYRot(partialTick);
	    		this.setRotation(this.pivotRot.x, this.pivotRot.y);
		    	this.move(-this.getMaxZoom(4.0D), 0.25D, 0.0D);
		    	
		    	entity.xRotO = 0;
		    	entity.xRot = 0;
		    	this.pivotRotOld.x = xRot;
		    	this.pivotRotOld.y = yRot;
	    	}
	    }
	    else if (entity instanceof LivingEntity && ((LivingEntity)entity).isSleeping())
	    {
	       Direction direction = ((LivingEntity)entity).getBedOrientation();
	       this.setRotation(direction != null ? direction.toYRot() - 180.0F : 0.0F, 0.0F);
	       this.move(0.0D, 0.3D, 0.0D);
	    }
	}
	
	public float getPivotXRot(float partialTicks)
	{
	   return partialTicks == 1.0F ? this.pivotRot.x : Mth.lerp(partialTicks, this.pivotRotOld.x, this.pivotRot.x);
	}

	public float getPivotYRot(float partialTicks)
	{
	   return partialTicks == 1.0F ? this.pivotRot.y : Mth.lerp(partialTicks, this.pivotRotOld.y, this.pivotRot.y);
	}
	
	public void addPivotRot(float x, float y)
	{
		float xMod = x * 0.1F;
	    float yMod = y * 0.1F;
	    
	    this.pivotRot.x = this.limitRotAmount(this.pivotRot.x + xMod, 360.0F);
	    this.pivotRot.y = this.limitRotAmount(this.pivotRot.y + yMod, 360.0F);
	    this.pivotRot.y = Mth.clamp(this.pivotRot.y, -90.0F, 90.0F);
	}
	
	public void rotatePivotTo(Entity target, float limit)
	{
		Vec3 pos = this.getPosition();
		double dx = target.getX() - pos.x;
		double dz = target.getZ() - pos.z;
		double dy = target.getY() + (3D/5D) * target.getBbHeight() - pos.y - 5D;
		float xDegree = (float) (Math.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
		float xAmount = Mth.wrapDegrees(xDegree - this.getYRot());
		float yDegree = (float) (Math.atan2(Math.sqrt(dx * dx + dz * dz), dy) * (180D / Math.PI)) - 90.0F;
		float yAmount = Mth.wrapDegrees(yDegree - this.getXRot());
		
		xAmount = this.limitRotAmount(xAmount, limit);
		yAmount = this.limitRotAmount(yAmount, limit);
		
		this.addPivotRot(xAmount, yAmount);
	}
	
	private float limitRotAmount(float amount, float limit)
	{
		while (amount < -180.0F)
		{
			amount += 360.0F;
		}
		
		while (amount > 180.0F)
		{
			amount -= 360.0F;
		}
		
		if (amount > limit)
		{
			amount = limit;
		}
		if (amount < -limit)
		{
			amount = -limit;
		}
		
		return amount;
	}
}
