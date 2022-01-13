package com.skullmangames.darksouls.client.gui.widget;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.config.Option;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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
	
	@Override
	protected Component getRefreshedMessage()
	{
		if (!(this.option.getValue() instanceof Boolean)) return new TranslatableComponent(option.getName());
		return new TextComponent(new TranslatableComponent(this.option.getName()).getString()+this.onOrOff(this.option.getValue()));
	}
	
	private String onOrOff(boolean value)
	{
		return ": "+new TranslatableComponent(value ? "gui."+DarkSouls.MOD_ID+".on" : "gui."+DarkSouls.MOD_ID+".off").getString();
	}
}
