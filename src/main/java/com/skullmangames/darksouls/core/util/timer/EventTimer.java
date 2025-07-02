package com.skullmangames.darksouls.core.util.timer;

public class EventTimer extends Timer
{
	private final TimerEvent onUpdate;
	private final TimerEvent onFinish;
	
	public EventTimer(TimerEvent onUpdate, TimerEvent onFinish)
	{
		this.onUpdate = onUpdate;
		this.onFinish = onFinish;
	}
	
	@Override
	public void drain(int value)
	{
		this.leftTime -= value;
		if (this.leftTime > 0)
		{
			this.pastTime += value;
			this.ticking = true;
			this.onUpdate.trigger(this);
		}
		else
		{
			this.onFinish.trigger(this);
			this.stop();
		}
	}
	
	@FunctionalInterface
	public interface TimerEvent
	{
		void trigger(EventTimer timer);
	}
}
