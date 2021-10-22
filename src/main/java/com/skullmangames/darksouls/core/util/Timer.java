package com.skullmangames.darksouls.core.util;

public class Timer
{
	private float timer;
	private float pastTime;
	private boolean ticking;
	
	public Timer(int timer)
	{
		this.start(timer);
	}
	
	public void start(int value)
	{
		this.timer = value;
		this.pastTime = 0;
		this.ticking = true;
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
		return Math.round(this.pastTime);
	}
	
	public boolean isTicking()
	{
		return this.ticking;
	}
}
