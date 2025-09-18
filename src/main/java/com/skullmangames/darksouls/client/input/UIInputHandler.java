package com.skullmangames.darksouls.client.input;

import com.skullmangames.darksouls.client.gui.screens.PlayerStatsScreen;
import com.skullmangames.darksouls.client.input.detector.KeyActionDetector.Action;
import com.skullmangames.darksouls.client.input.key.ModKeys;

public class UIInputHandler
{
	private final InputManager im;
	
	private boolean showItemInfo;
	
	public UIInputHandler(InputManager im)
	{
		this.im = im;
		
		this.showItemInfo = false;
		
		this.im.addKeyAction(ModKeys.OPEN_STAT_SCREEN, this::openPlayerStatScreen);
		this.im.addGuiKeyAction(ModKeys.SHOW_ITEM_INFO, this::showItemInfoKeyAction);
	}
	
	public boolean shouldShowItemInfo()
	{
		return this.showItemInfo;
	}
	
	private void showItemInfoKeyAction(Action.Context ctx)
	{
		this.showItemInfo = ctx.isDown();
	}
	
	private void openPlayerStatScreen(Action.Context ctx)
	{
		if (ctx.isDown() || this.im.minecraft.screen != null) return;
		this.im.minecraft.setScreen(new PlayerStatsScreen());
	}
}
