package com.skullmangames.darksouls.core.util.timer;

import java.util.function.IntConsumer;

public class EventTimer extends Timer
{
	private final IntConsumer onFinish;
	
	public EventTimer(IntConsumer onFinish)
	{
		this.onFinish = onFinish;
	}
	
	@Override
	public void drain(int value)
	{
		this.timer -= value;
		if (this.timer > 0)
		{
			this.pastTime += value;
			this.ticking = true;
		}
		else
		{
			this.onFinish.accept(this.pastTime);
			this.stop();
		}
	}
}
