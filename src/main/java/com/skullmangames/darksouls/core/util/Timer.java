package com.skullmangames.darksouls.core.util;

public class Timer
{
	private float timer;
	private int pastTime;
	private boolean ticking;
	
	public Timer(int timer)
	{
		this.setTimer(timer);
	}
	
	public void setTimer(int value)
	{
		this.timer = value;
		this.pastTime = 0;
		this.ticking = false;
	}
	
	public void drain(float value)
	{
		this.timer -= value;
		if (this.timer > 0)
		{
			this.pastTime += value;
			this.ticking = true;
		}
		else
		{
			this.pastTime = 0;
			this.ticking = false;
		}
	}
	
	public void stop()
	{
		this.timer = 0;
		this.pastTime = 0;
		this.ticking = false;
	}
	
	public float getLeftTime()
	{
		return this.timer;
	}
	
	public int getPastTime()
	{
		return this.pastTime;
	}
	
	public boolean isTicking()
	{
		return this.ticking;
	}
}
