package com.skullmangames.darksouls.client.input.detector;

import net.minecraft.client.KeyMapping;

public class AdvancedKeyActionDetector extends KeyActionDetector
{
	private final int pressThreshold;
	
	private int holdTicks;
	private boolean longPressed;
	
	public AdvancedKeyActionDetector(KeyMapping keyMapping, int pressThreshold, Action action)
	{
		super(keyMapping, action);
		
		this.pressThreshold = pressThreshold;
		this.holdTicks = 0;
		this.longPressed = false;
	}
	
	@Override
	public void release()
	{
		if (this.down)
		{
			this.down = false;
			
			if (!this.isLongPress())
			{
				this.act(ActionType.SHORT_PRESS);
			}
			else
			{
				this.act(ActionType.RELEASE);
			}
			
			this.holdTicks = 0;
			this.longPressed = false;
		}
	}
	
	public void tick()
	{
		if (this.down)
		{
			this.holdTicks = Math.min(this.holdTicks + 1, this.pressThreshold);
			if (this.isLongPress())
			{
				if (!this.longPressed)
				{
					this.act(ActionType.LONG_PRESS);
					this.longPressed = true;
				}
				else
				{
					this.act(ActionType.LONG_HOLD);
				}
			}
		}
	}
	
	public boolean isLongPress()
	{
		return this.holdTicks >= this.pressThreshold;
	}
}
