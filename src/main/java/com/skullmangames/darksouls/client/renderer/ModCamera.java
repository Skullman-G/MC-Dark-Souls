package com.skullmangames.darksouls.client.renderer;

import java.util.Random;

import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.core.util.math.MathUtils;
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
	private double xo;
	private double yo;
	private double zo;
	private float anim;
	private int shakeDuration;
	private float shakeMagnitude;
	private final Random random = new Random();
	private boolean forceShoulderSurf = false;
	private boolean shoulderSurfO = false;
	
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
	    	if (this.forceShoulderSurf)
	    	{
	    		float xRot = this.getPivotXRot(partialTick);
	    		float yRot = this.getPivotYRot(partialTick);
	    		this.setRotation(xRot, yRot);
	    		this.move(-this.getMaxZoom(this.xo), this.yo, this.zo);
	    		
	    		if (this.anim >= 1 && this.zo != 2.5D) this.anim = 0;
	    		this.updatePosO(3.0D, 0.25D, 2.5D, partialTick);
	    	}
	    	else if (ClientManager.INSTANCE.getPlayerCap().shouldShoulderSurf())
	    	{
	    		this.move(-this.getMaxZoom(this.xo), this.yo, this.zo);
	    		
	    		this.pivotRotOld.y = entity.xRotO;
	    		this.pivotRotOld.x = entity.yRotO;
	    		this.pivotRot.y = entity.xRot;
	    		this.pivotRot.x = entity.yRot;
	    		
	    		if (!this.shoulderSurfO)
	    		{
	    			this.anim = 0;
	    			this.shoulderSurfO = true;
	    		}
	    		this.updatePosO(3.0D, 0.25D, -1D, partialTick);
	    	}
	    	else
	    	{
	    		float xRot = this.getPivotXRot(partialTick);
	    		float yRot = this.getPivotYRot(partialTick);
	    		this.setRotation(xRot, yRot);
		    	this.move(-this.getMaxZoom(this.xo), this.yo, this.zo);
		    	
		    	entity.xRotO = 0;
		    	entity.xRot = 0;
		    	this.pivotRotOld.x = xRot;
		    	this.pivotRotOld.y = yRot;
		    	
		    	if (this.shoulderSurfO)
	    		{
	    			this.anim = 0;
	    			this.shoulderSurfO = false;
	    		}
		    	this.updatePosO(4D, 0.25D, 0D, partialTick);
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
	    	double y = this.random.nextInt(-1, 2) * 0.1F * this.shakeMagnitude;
	    	this.move(0, y, 0);
	    	this.shakeDuration--;
	    }
	}
	
	public void forceShoulderSurf(boolean value)
	{
		this.forceShoulderSurf = value;
		this.anim = 0;
	}
	
	private void updatePosO(double x, double y, double z, float partialTick)
	{
		if (this.anim < 1)
		{
			this.anim = MathUtils.clamp(this.anim + 0.01F * partialTick, 0, 1);
			this.xo = Mth.lerp(this.anim, this.xo, x);
			this.yo = Mth.lerp(this.anim, this.yo, y);
			this.zo = Mth.lerp(this.anim, this.zo, z);
		}
		else
		{
			this.xo = x;
			this.yo = y;
			this.zo = z;
		}
	}
	
	public void shake(int duration, float magnitude)
	{
		this.shakeDuration = duration;
		this.shakeMagnitude = magnitude;
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
		double dy = target.getY() + 0.6D * target.getBbHeight() - pos.y - this.entity.getEyeHeight();
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
