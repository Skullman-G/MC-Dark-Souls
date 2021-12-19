package com.skullmangames.darksouls.client.gui.widget;

import com.skullmangames.darksouls.common.entity.stats.Stat;

import net.minecraft.util.text.ITextComponent;

public class LevelButton extends ScalableButton
{
	private final Stat stat;
	
	public LevelButton(int p_i232255_1_, int p_i232255_2_, int p_i232255_3_, int p_i232255_4_, ITextComponent p_i232255_5_, IPressable p_i232255_6_, Stat stat)
	{
		super(p_i232255_1_, p_i232255_2_, p_i232255_3_, p_i232255_4_, p_i232255_5_, p_i232255_6_);
		this.stat = stat;
	}
	
	public Stat getStat()
	{
		return this.stat;
	}
}
