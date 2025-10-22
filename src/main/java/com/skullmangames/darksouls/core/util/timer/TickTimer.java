package com.skullmangames.darksouls.core.util.timer;

public class TickTimer
{
	private int leftTime;
	private int pastTime;
	private boolean ticking;
	
	private final TimerEvent onUpdate;
	private final TimerEvent onFinish;
	
	private TickTimer(TimerEvent onUpdate, TimerEvent onFinish)
	{
		this.onUpdate = onUpdate;
		this.onFinish = onFinish;
	}
	
	public static TickTimer timer()
	{
		return new TickTimer(() -> {}, () -> {});
	}
	
	public TickTimer withOnUpdate(TimerEvent onUpdate)
	{
		return new TickTimer(onUpdate, this.onFinish);
	}
	
	public TickTimer withOnFinish(TimerEvent onFinish)
	{
		return new TickTimer(this.onUpdate, onFinish);
	}
	
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
			this.onUpdate.trigger();
		}
		else
		{
			this.stop();
		}
	}
	
	public void tick()
	{
		this.drain(1);
	}
	
	public void stop()
	{
		this.leftTime = 0;
		this.pastTime = 0;
		this.ticking = false;
		
		this.onFinish.trigger();
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
	
	@FunctionalInterface
	public interface TimerEvent
	{
		void trigger();
	}
}
