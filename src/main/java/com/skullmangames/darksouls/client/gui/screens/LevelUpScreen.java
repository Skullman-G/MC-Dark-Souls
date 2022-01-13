package com.skullmangames.darksouls.client.gui.screens;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.platform.InputConstants;
import com.skullmangames.darksouls.client.gui.widget.LevelButton;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSStat;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class LevelUpScreen extends PlayerStatsScreen
{
	protected final Map<LevelButton, LevelButton> levelButtons = new HashMap<LevelButton, LevelButton>();
	
	protected final int buttonWidth = 70;
	protected final int buttonHeight = 20;
	
	public LevelUpScreen()
	{
		super(new TextComponent("Level Up"));
	}
	
	@Override
	protected void init()
	{
		super.init();
		int x = (this.width - this.imageWidth) / 2;
	    int y = (this.height - this.imageHeight) / 2;
		int buttonwidth2 = 10;
		int buttonheight2 = 10;
		
		int upDownButtonHeight = y + 112;
		for (Stat stat : Stats.STATS)
		{
			int statValue = this.playerdata.getStats().getStatValue(stat);
			LevelButton downButton = this.addRenderableWidget(new LevelButton(x + 93, upDownButtonHeight, buttonwidth2, buttonheight2, new TextComponent("<"), (button) ->
			{
				this.levelDown(stat);
				this.refreshLevelButtons();
		    }, stat));
			downButton.active = this.playerdata.isCreativeOrSpectator() ? this.displayedStats.getOrDefault(stat, 1).intValue() > 1 : this.displayedStats.getOrDefault(stat, 1).intValue() > statValue;
			
			LevelButton upButton = this.addRenderableWidget(new LevelButton(x + 116, upDownButtonHeight, buttonwidth2, buttonheight2, new TextComponent(">"), (button) ->
			{
				this.levelUp(stat);
				this.refreshLevelButtons();
		    }, stat));
			upButton.active = this.displayedStats.getOrDefault(stat, 1).intValue() < 99 && this.playerdata.hasEnoughSouls(this.getCost());
			this.levelButtons.put(downButton, upButton);
			
			upDownButtonHeight += 10;
		}
		
		this.addRenderableWidget(new Button(x + 35, y + 205, this.buttonWidth, this.buttonHeight, new TranslatableComponent("gui.darksouls.accept"), (p_214187_1_) ->
		{
	         this.accept();
	    }));
	}
	
	private void refreshLevelButtons()
	{
		this.levelButtons.forEach((down, up) ->
		{
			int statvalue = this.playerdata.getStats().getStatValue(down.getStat());
			int displayedstatvalue = this.displayedStats.get(down.getStat()).intValue();
			down.active = this.playerdata.isCreativeOrSpectator() ? displayedstatvalue > 1 : displayedstatvalue > statvalue;
			up.active = displayedstatvalue < 99 && this.playerdata.hasEnoughSouls(this.getCost());
		});
	}
	
	private void levelUp(Stat stat)
	{
		this.displayedStats.put(stat, this.displayedStats.get(stat).intValue() + 1);
		this.displayedLevel += 1;
	}
	
	private void levelDown(Stat stat)
	{
		this.displayedStats.put(stat, this.displayedStats.get(stat).intValue() - 1);
		this.displayedLevel -= 1;
	}
	
	private void accept()
	{
		if (this.displayedLevel != this.playerdata.getSoulLevel())
		{
			this.displayedLevel -= 1;
			if (!this.playerdata.isCreativeOrSpectator()) this.playerdata.raiseSouls(-this.getCost());
		}
		for (Stat stat : Stats.STATS)
		{
			int statvalue = this.playerdata.getStats().getStatValue(stat);
			int value = this.displayedStats.getOrDefault(stat, statvalue).intValue();
			this.playerdata.setStatValue(stat, value);
			ModNetworkManager.sendToServer(new CTSStat(stat.toString(), value));
		}
		super.onClose();
	}
	
	@Override
	public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_)
	{
		InputConstants.Key mouseKey = InputConstants.getKey(p_231046_1_, p_231046_2_);
		if (this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) 
		{
	         this.onClose();
	         return true;
		}
		else if (super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_)) return true;
		else return false;
	}
}
