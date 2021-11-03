package com.skullmangames.darksouls.client.gui.widget;

import com.skullmangames.darksouls.config.Option;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class OptionButton<T> extends Button
{
	protected final Option<T> option;
	
	public OptionButton(int p_i232255_1_, int p_i232255_2_, int p_i232255_3_, int p_i232255_4_,	Option<T> option, IPressable p_i232255_6_)
	{
		super(p_i232255_1_, p_i232255_2_, p_i232255_3_, p_i232255_4_, new TranslationTextComponent(option.getName()), p_i232255_6_);
		this.option = option;
		this.refreshMessage();
	}
	
	protected abstract ITextComponent getRefreshedMessage();
	
	public void refreshMessage()
	{
		this.setMessage(this.getRefreshedMessage());
	}
}
