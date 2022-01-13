package com.skullmangames.darksouls.client.gui.widget;

import com.skullmangames.darksouls.config.Option;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class OptionButton<T> extends Button
{
	protected final Option<T> option;
	
	public OptionButton(int p_i232255_1_, int p_i232255_2_, int p_i232255_3_, int p_i232255_4_,	Option<T> option, OnPress p_i232255_6_)
	{
		super(p_i232255_1_, p_i232255_2_, p_i232255_3_, p_i232255_4_, new TranslatableComponent(option.getName()), p_i232255_6_);
		this.option = option;
		this.refreshMessage();
	}
	
	protected abstract Component getRefreshedMessage();
	
	public void refreshMessage()
	{
		this.setMessage(this.getRefreshedMessage());
	}
}
