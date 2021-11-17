package com.skullmangames.darksouls.client.gui.widget;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.config.Option;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BooleanButton extends OptionButton<Boolean>
{
	public BooleanButton(int p_i232255_1_, int p_i232255_2_, int p_i232255_3_, int p_i232255_4_, Option<Boolean> option)
	{
		super(p_i232255_1_, p_i232255_2_, p_i232255_3_, p_i232255_4_, option, (button) ->
		{
			option.setValue(!option.getValue());
			((BooleanButton)button).refreshMessage();
		});
	}
	
	protected ITextComponent getRefreshedMessage()
	{
		if (!(this.option.getValue() instanceof Boolean)) return new TranslationTextComponent(option.getName());
		return new StringTextComponent(new TranslationTextComponent(this.option.getName()).getString()+this.onOrOff(this.option.getValue()));
	}
	
	private String onOrOff(boolean value)
	{
		return ": "+new TranslationTextComponent(value ? "gui."+DarkSouls.MOD_ID+".on" : "gui."+DarkSouls.MOD_ID+".off").getString();
	}
}