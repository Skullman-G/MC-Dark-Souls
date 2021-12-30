package com.skullmangames.darksouls.client.gui.screens;

import java.util.HashMap;
import java.util.Map;

import com.skullmangames.darksouls.client.gui.widget.LevelButton;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class LevelUpScreen extends PlayerStatsScreen
{
	protected final Map<LevelButton, LevelButton> levelButtons = new HashMap<LevelButton, LevelButton>();
	
	protected final int buttonWidth = 70;
	protected final int buttonHeight = 20;
	
	public LevelUpScreen(PlayerEntity player)
	{
		super(player, new StringTextComponent("Level Up"));
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
		for (Stat stat : Stats.getStats())
		{
			int statValue = stat.getValue(this.player);
			LevelButton downButton = this.addButton(new LevelButton(x + 93, upDownButtonHeight, buttonwidth2, buttonheight2, new StringTextComponent("<"), (button) ->
			{
				this.levelDown(stat);
				this.refreshLevelButtons();
		    }, stat));
			downButton.active = this.player.isCreative() ? this.displayedStats.getOrDefault(stat, 1).intValue() > 1 : this.displayedStats.getOrDefault(stat, 1).intValue() > statValue;
			
			LevelButton upButton = this.addButton(new LevelButton(x + 116, upDownButtonHeight, buttonwidth2, buttonheight2, new StringTextComponent(">"), (button) ->
			{
				this.levelUp(stat);
				this.refreshLevelButtons();
		    }, stat));
			upButton.active = this.displayedStats.getOrDefault(stat, 1).intValue() < 99 && this.canEffort();
			this.levelButtons.put(downButton, upButton);
			
			upDownButtonHeight += 10;
		}
		
		this.addButton(new Button(x + 35, y + 205, this.buttonWidth, this.buttonHeight, new TranslationTextComponent("gui.darksouls.accept"), (p_214187_1_) ->
		{
	         this.accept();
	    }));
	}
	
	private void refreshLevelButtons()
	{
		this.levelButtons.forEach((down, up) ->
		{
			int statvalue = down.getStat().getValue(this.player);
			down.active = this.player.isCreative() ? this.displayedStats.getOrDefault(down.getStat(), 1).intValue() > 1 : this.displayedStats.getOrDefault(down.getStat(), 1).intValue() > statvalue;
			up.active = this.displayedStats.getOrDefault(up.getStat(), 1).intValue() < 99 && this.canEffort();
		});
	}
	
	private boolean canEffort()
	{
		return this.player.isCreative() ? true : this.playerdata.getSouls() >= this.getCost();
	}
	
	private int getCost()
	{
		return this.displayedLevel * (10 + this.displayedLevel);
	}
	
	private void levelUp(Stat stat)
	{
		int statvalue = stat.getValue(this.player);
		int displaystatvalue = this.displayedStats.getOrDefault(stat, Integer.valueOf(statvalue)).intValue();
		this.displayedStats.put(stat, this.displayedStats.getOrDefault(stat, Integer.valueOf(displaystatvalue)).intValue() + 1);
		this.displayedLevel += 1;
	}
	
	private void levelDown(Stat stat)
	{
		int statvalue = stat.getValue(this.player);
		int displaystatvalue = this.displayedStats.getOrDefault(stat, statvalue).intValue();
		this.displayedStats.put(stat, this.displayedStats.getOrDefault(stat, displaystatvalue).intValue() - 1);
		this.displayedLevel -= 1;
	}
	
	private void accept()
	{
		if (this.displayedLevel != Stats.getLevel(this.player))
		{
			this.displayedLevel -= 1;
			this.playerdata.raiseSouls(-this.getCost());
		}
		for (Stat stat : Stats.getStats())
		{
			int statvalue = stat.getValue(this.player);
			int displaystatvalue = this.displayedStats.getOrDefault(stat, statvalue).intValue();
			stat.setValue(this.player, this.displayedStats.getOrDefault(stat, displaystatvalue));
		}
		super.onClose();
	}
	
	@Override
	public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_)
	{
		InputMappings.Input mouseKey = InputMappings.getKey(p_231046_1_, p_231046_2_);
		if (this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) 
		{
	         this.onClose();
	         return true;
		}
		else if (super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_)) return true;
		else return false;
	}
}
