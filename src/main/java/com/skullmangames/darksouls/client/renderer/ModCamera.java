package com.skullmangames.darksouls.client.renderer;

import java.util.Random;

import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.core.util.math.vector.Vector2f;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;

public class ModCamera extends ActiveRenderInfo
{
	private Vector2f pivotRot = Vector2f.ZERO;
	private Vector2f pivotRotOld = Vector2f.ZERO;
	private double aimZ = 0.0D;
	private int shakeDuration;
	private float shakeMagnitude;
	private final Random random = new Random();
	
	@Override
	public void setup(IBlockReader world, Entity entity, boolean detached, boolean mirror, float partialTick)
	{
		this.initialized = true;
	    this.level = world;
	    this.entity = entity;
	    this.detached = detached;
	    
	    this.setRotation(entity.getViewYRot(partialTick), entity.getViewXRot(partialTick));
	    this.setPosition(MathHelper.lerp((double)partialTick, entity.xo, entity.getX()), MathHelper.lerp((double)partialTick, entity.yo, entity.getY()) + (double)MathHelper.lerp(partialTick, this.eyeHeightOld, this.eyeHeight), MathHelper.lerp((double)partialTick, entity.zo, entity.getZ()));
	    
	    if (detached)
	    {
	    	if (ClientManager.INSTANCE.getPlayerCap().getClientAnimator().isAiming())
	    	{
	    		this.move(-this.getMaxZoom(4.0D), 0.25D, this.aimZ);
	    		
	    		this.pivotRotOld.y = entity.xRotO;
	    		this.pivotRotOld.x = entity.yRotO;
	    		this.pivotRot.y = entity.xRot;
	    		this.pivotRot.x = entity.yRot;
	    		
	    		if (this.aimZ > -2D) this.aimZ = Math.max(this.aimZ - 0.1D * partialTick, -2D);
	    	}
	    	else
	    	{
	    		float xRot = this.getPivotXRot(partialTick);
	    		float yRot = this.getPivotYRot(partialTick);
	    		this.setRotation(this.pivotRot.x, this.pivotRot.y);
		    	this.move(-this.getMaxZoom(4.0D), 0.25D, this.aimZ);
		    	
		    	entity.xRotO = 0;
		    	entity.xRot = 0;
		    	this.pivotRotOld.x = xRot;
		    	this.pivotRotOld.y = yRot;
		    	
		    	if (this.aimZ < 0D) this.aimZ = Math.min(this.aimZ + 0.1D * partialTick, 0D);
	    	}
	    }
	    else if (entity instanceof LivingEntity && ((LivingEntity)entity).isSleeping())
	    {
	       Direction direction = ((LivingEntity)entity).getBedOrientation();
	       this.setRotation(direction != null ? direction.toYRot() - 180.0F : 0.0F, 0.0F);
	       this.move(0.0D, 0.3D, 0.0D);
	    }
	    
	    if (this.shakeDuration > 0)
	    {
	    	double y = this.random.nextInt(2) * 0.1F * this.shakeMagnitude;
	    	this.move(0, y, 0);
	    	this.shakeDuration--;
	    }
	}
	
	public void shake(int duration, float magnitude)
	{
		this.shakeDuration = duration;
		this.shakeMagnitude = magnitude;
	}
	
	public float getPivotXRot(float partialTicks)
	{
	   return partialTicks == 1.0F ? this.pivotRot.x : MathHelper.lerp(partialTicks, this.pivotRotOld.x, this.pivotRot.x);
	}

	public float getPivotYRot(float partialTicks)
	{
	   return partialTicks == 1.0F ? this.pivotRot.y : MathHelper.lerp(partialTicks, this.pivotRotOld.y, this.pivotRot.y);
	}
	
	public void addPivotRot(float x, float y)
	{
		float xMod = x * 0.1F;
	    float yMod = y * 0.1F;
	    
	    this.pivotRot.x = this.limitRotAmount(this.pivotRot.x + xMod, 360.0F);
	    this.pivotRot.y = this.limitRotAmount(this.pivotRot.y + yMod, 360.0F);
	    this.pivotRot.y = MathHelper.clamp(this.pivotRot.y, -90.0F, 90.0F);
	}
	
	public void rotatePivotTo(Entity target, float limit)
	{
		Vector3d pos = this.getPosition();
		double dx = target.getX() - pos.x;
		double dz = target.getZ() - pos.z;
		double dy = target.getY() + 0.6D * target.getBbHeight() - pos.y - this.entity.getEyeHeight();
		float xDegree = (float) (Math.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
		float xAmount = MathHelper.wrapDegrees(xDegree - this.getYRot());
		float yDegree = (float) (Math.atan2(Math.sqrt(dx * dx + dz * dz), dy) * (180D / Math.PI)) - 90.0F;
		float yAmount = MathHelper.wrapDegrees(yDegree - this.getXRot());
		
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
