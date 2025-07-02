package com.skullmangames.darksouls.core.util.timer;

public class Timer
{
	protected int leftTime;
	protected int pastTime;
	protected boolean ticking;
	
	public void start(int value)
	{
		this.leftTime = value;
		this.pastTime = 0;
		this.ticking = true;
	}
	
	public void drain(int value)
	{
		if (!this.isTicking()) return;
		
		this.leftTime -= value;
		if (this.leftTime > 0)
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
		this.leftTime = 0;
		this.pastTime = 0;
		this.ticking = false;
	}
	
	public int getLeftTime()
	{
		return this.leftTime;
	}
	
	public int getPastTime()
	{
		return this.pastTime;
	}
	
	public boolean isTicking()
	{
		return this.ticking;
	}
	
	public float getTimePercentage()
	{
		return (float)this.pastTime / (float)(this.leftTime + this.pastTime);
	}
}
