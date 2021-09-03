package com.skullmangames.darksouls.client.input;

import com.skullmangames.darksouls.client.ClientEngine;
import com.skullmangames.darksouls.client.renderer.Camera;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.util.NativeUtil;

public class MouseInputManager extends MouseHelper
{
	private final Minecraft minecraft;
	private final Camera camera;
	
	public MouseInputManager(Minecraft minecraft)
	{
		super(minecraft);
		this.minecraft = minecraft;
		this.camera = ClientEngine.INSTANCE.mainCamera;
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
	        int i = 1;
	        if (this.minecraft.options.invertYMouse)
	        {
	           i = -1;
	        }

	        this.minecraft.getTutorial().onMouse(d2, d3);
	        if (this.minecraft.player != null)
	        {
	        	if (this.minecraft.options.getCameraType() == PointOfView.FIRST_PERSON)
	        	{
	        		this.minecraft.player.turn(d2, d3 * (double)i);
	        	}
	        	else
	        	{
	        		this.camera.setPivotRot((float)d2, (float)d3 * i);
	        	}
	        }

	     }
	     else
	     {
	        this.accumulatedDX = 0.0D;
	        this.accumulatedDY = 0.0D;
	     }
	}
}