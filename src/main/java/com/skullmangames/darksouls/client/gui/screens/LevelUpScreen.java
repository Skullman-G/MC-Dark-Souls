package com.skullmangames.darksouls.client.gui.screens;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.platform.InputConstants;
import com.skullmangames.darksouls.client.gui.widget.LevelButton;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSLevelUp;
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
			int statValue = this.playerCap.getStats().getStatValue(stat);
			LevelButton downButton = this.addRenderableWidget(new LevelButton(x + 93, upDownButtonHeight, buttonwidth2, buttonheight2, new TextComponent("<"), (button) ->
			{
				this.levelDown(stat);
				this.refreshLevelButtons();
		    }, stat));
			downButton.active = this.playerCap.isCreativeOrSpectator() ? this.displayedStats.getOrDefault(stat, 10).intValue() > 10 : this.displayedStats.getOrDefault(stat, 10).intValue() > statValue;
			
			LevelButton upButton = this.addRenderableWidget(new LevelButton(x + 116, upDownButtonHeight, buttonwidth2, buttonheight2, new TextComponent(">"), (button) ->
			{
				this.levelUp(stat);
				this.refreshLevelButtons();
		    }, stat));
			upButton.active = this.displayedStats.getOrDefault(stat, 1).intValue() < 99 && this.playerCap.hasEnoughSouls(Stats.getCost(this.displayedLevel));
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
			int statvalue = this.playerCap.getStats().getStatValue(down.getStat());
			int displayedstatvalue = this.displayedStats.get(down.getStat()).intValue();
			down.active = this.playerCap.isCreativeOrSpectator() ? displayedstatvalue > 1 : displayedstatvalue > statvalue;
			up.active = displayedstatvalue < 99 && this.playerCap.hasEnoughSouls(Stats.getCost(this.displayedLevel));
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
		int[] additions = new int[Stats.STATS.size()];
		for (int i = 0; i < additions.length; i++)
		{
			int statvalue = this.playerCap.getStats().getStatValue(i);
			int addition = this.displayedStats.getOrDefault(Stats.STATS.get(i), statvalue) - statvalue;
			additions[i] = addition;
		}
		ModNetworkManager.sendToServer(new CTSLevelUp(additions));
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
