package com.skullmangames.darksouls.client.input.detector;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;

public class KeyActionDetector
{
	private final KeyMapping keyMapping;
	protected final Action action;
	protected boolean down;
	
	public KeyActionDetector(KeyMapping keyMapping, Action action)
	{
		this.keyMapping = keyMapping;
		this.action = action;
		this.down = false;
	}
	
	public void glfwKeyAction(int action)
	{
		switch (action)
		{
			case GLFW.GLFW_PRESS:
				this.press();
				break;
				
			case GLFW.GLFW_RELEASE:
				this.release();
				break;
				
			default:
				break;
		}
	}
	
	public void press()
	{
		if (!this.down)
		{
			this.down = true;
			this.act(ActionType.PRESS);
		}
	}
	
	public void release()
	{
		if (this.down)
		{
			this.down = false;
			this.act(ActionType.RELEASE);
		}
	}
	
	public void tick() {}
	
	public boolean isDown()
	{
		return this.down;
	}
	
	public void act(ActionType action)
	{
		Action.Context ctx = new Action.Context(this.keyMapping, action);
		
		this.action.act(ctx);
		
		if (ctx.shouldOverride())
		{
			while (KeyActionDetector.this.keyMapping.consumeClick()) {}
			KeyMapping.set(keyMapping.getKey(), false);
		}
	}
	
	@FunctionalInterface
	public interface Action
	{
		public void act(Context ctx);
		
		public class Context
		{
			private final KeyMapping keyMapping;
			private final ActionType action;
			private boolean override;
			
			public Context(KeyMapping keyMapping, ActionType type)
			{
				this.keyMapping = keyMapping;
				this.action = type;
				this.override = false;
			}
			
			public KeyMapping getMapping()
			{
				return this.keyMapping;
			}
			
			public ActionType getAction()
			{
				return this.action;
			}
			
			public boolean shouldOverride()
			{
				return this.override;
			}
			
			public void setOverride(boolean value)
			{
				this.override = value;
			}
			
			public boolean isDown()
			{
				return this.action.isDown();
			}
		}
	}
	
	public enum ActionType
	{
		PRESS(true),
		SHORT_PRESS(false),
		LONG_PRESS(true),
		LONG_HOLD(true),
		RELEASE(false);
		
		private boolean down;
		
		ActionType(boolean down)
		{
			this.down = down;
		}
		
		public boolean isDown()
		{
			return this.down;
		}
	}
}
