package com.skullmangames.darksouls.client.renderer;

import com.skullmangames.darksouls.core.util.math.vector.Vector2f;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;

public class Camera extends ActiveRenderInfo
{
	private Vector2f pivotRot = Vector2f.ZERO;
	private Vector2f pivotRotOld = Vector2f.ZERO;
	
	@Override
	public void setup(IBlockReader world, Entity entity, boolean detached, boolean mirror, float partialTick)
	{
		this.initialized = true;
	    this.level = world;
	    this.entity = entity;
	    this.detached = detached;
	    this.mirror = mirror;
	    
	    this.setRotation(entity.getViewYRot(partialTick), entity.getViewXRot(partialTick));
	    this.setPosition(MathHelper.lerp((double)partialTick, entity.xo, entity.getX()), MathHelper.lerp((double)partialTick, entity.yo, entity.getY()) + (double)MathHelper.lerp(partialTick, this.eyeHeightOld, this.eyeHeight), MathHelper.lerp((double)partialTick, entity.zo, entity.getZ()));
	    if (detached)
	    {
	    	this.setRotation(this.pivotRot.x, this.pivotRot.y);
	    	this.move(-this.getMaxZoom(4.0D), 0.0D, 0.0D);
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
	   return partialTicks == 1.0F ? this.pivotRot.x : MathHelper.lerp(partialTicks, this.pivotRotOld.x, this.pivotRot.x);
	}

	public float getPivotYRot(float partialTicks)
	{
	   return partialTicks == 1.0F ? this.pivotRot.y : MathHelper.lerp(partialTicks, this.pivotRotOld.y, this.pivotRot.y);
	}
	
	public void setPivotRot(float x, float y)
	{
		float xMod = x * 0.1F;
	    float yMod = y * 0.1F;
	    
	    this.pivotRot.x = this.pivotRot.x + xMod;
	    this.pivotRot.y = this.pivotRot.y + yMod;
	    this.pivotRot.y = MathHelper.clamp(this.pivotRot.y, -90.0F, 90.0F);
	    
	    this.pivotRotOld.x = this.pivotRotOld.x + xMod;
	    this.pivotRotOld.y = this.pivotRotOld.y + yMod;
	    this.pivotRotOld.y = MathHelper.clamp(this.pivotRotOld.y, -90.0F, 90.0F);
	}
}
