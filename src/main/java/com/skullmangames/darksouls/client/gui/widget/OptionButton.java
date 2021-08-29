package com.skullmangames.darksouls.client.gui.widget;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.config.Option;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OptionButton extends Button
{
	protected final Option<?> option;
	
	public OptionButton(int p_i232255_1_, int p_i232255_2_, int p_i232255_3_, int p_i232255_4_,	Option<?> option, IPressable p_i232255_6_)
	{
		super(p_i232255_1_, p_i232255_2_, p_i232255_3_, p_i232255_4_, new TranslationTextComponent(option.getName()), p_i232255_6_);
		this.option = option;
		this.refreshMessage();
	}
	
	protected ITextComponent getRefreshedMessage()
	{
		if (!(this.option.getValue() instanceof Boolean)) return new TranslationTextComponent("gui."+DarkSouls.MOD_ID+"."+option.getName());
		return new StringTextComponent(new TranslationTextComponent(this.option.getName()).getString()+this.onOrOff((boolean)this.option.getValue()));
	}
	
	public void refreshMessage()
	{
		this.setMessage(this.getRefreshedMessage());
	}
	
	private String onOrOff(boolean value)
	{
		return ": "+new TranslationTextComponent(value ? "gui."+DarkSouls.MOD_ID+".on" : "gui."+DarkSouls.MOD_ID+".off").getString();
	}
}
