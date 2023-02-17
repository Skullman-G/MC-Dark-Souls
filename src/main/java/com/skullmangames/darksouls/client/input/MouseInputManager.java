package com.skullmangames.darksouls.client.input;

import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.renderer.ModCamera;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.util.NativeUtil;
import net.minecraft.entity.LivingEntity;

public class MouseInputManager extends MouseHelper
{
	private final Minecraft minecraft;
	private final ModCamera camera;
	
	public MouseInputManager(Minecraft minecraft)
	{
		super(minecraft);
		this.minecraft = minecraft;
		this.camera = ClientManager.INSTANCE.mainCamera;
	}
	
	@Override
	public void turnPlayer()
	{
		double d0 = NativeUtil.getTime();
		double d1 = d0 - this.lastMouseEventTime;
	    this.lastMouseEventTime = d0;
	    if (this.isMouseGrabbed() && this.minecraft.isWindowActive())
	    {
	    	double d4 = this.minecraft.options.sensitivity * (double)0.6F + (double)0.2F;
	        double d5 = d4 * d4 * d4 * 8.0D;
	        double d2;
	        double d3;
	        if (this.minecraft.options.smoothCamera)
	        {
	           double d6 = this.smoothTurnX.getNewDeltaValue(this.accumulatedDX * d5, d1 * d5);
	           double d7 = this.smoothTurnY.getNewDeltaValue(this.accumulatedDY * d5, d1 * d5);
	           d2 = d6;
	           d3 = d7;
	        }
	        else
	        {
	           this.smoothTurnX.reset();
	           this.smoothTurnY.reset();
	           d2 = this.accumulatedDX * d5;
	           d3 = this.accumulatedDY * d5;
	        }

	        this.accumulatedDX = 0.0D;
	        this.accumulatedDY = 0.0D;
	        int i = this.minecraft.options.invertYMouse ? -1 : 1;

	        this.minecraft.getTutorial().onMouse(d2, d3);
	        if (this.minecraft.player != null)
	        {
	        	LivingEntity target = ClientManager.INSTANCE.getPlayerCap().getTarget();
	        	if (target != null)
	        	{
	        		this.camera.rotatePivotTo(target, 60.0F);
	        	}
	        	else if (this.minecraft.options.getCameraType() == PointOfView.FIRST_PERSON)
	        	{
	        		this.minecraft.player.turn(d2, d3 * (double)i);
	        	}
	        	else if (ClientManager.INSTANCE.getPlayerCap().shouldShoulderSurf())
	        	{
	        		this.minecraft.player.turn(d2, d3 * (double)i);
	        		this.minecraft.player.yBodyRot = (float)((double)this.minecraft.player.yRot + d1);
	        		this.minecraft.player.yBodyRotO = (float)((double)this.minecraft.player.yRotO + d1);
	        	}
	        	else this.camera.addPivotRot((float)d2, (float)d3 * i);
	        }

	     }
	     else
	     {
	        this.accumulatedDX = 0.0D;
	        this.accumulatedDY = 0.0D;
	     }
	}
}
