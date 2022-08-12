package com.skullmangames.darksouls.core.util.timer;

public class Timer
{
	protected int timer;
	protected int pastTime;
	protected boolean ticking;
	
	public void start(int value)
	{
		this.timer = value;
		this.pastTime = 0;
		this.ticking = true;
	}
	
	public void drain(int value)
	{
		if (!this.isTicking()) return;
		
		this.timer -= value;
		if (this.timer > 0)
		{
			this.pastTime += value;
		}
		else
		{
			this.stop();
		}
	}
	
	public void stop()
	{
		this.timer = 0;
		this.pastTime = 0;
		this.ticking = false;
	}
	
	public int getLeftTime()
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
