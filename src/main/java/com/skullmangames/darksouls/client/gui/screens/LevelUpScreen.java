package com.skullmangames.darksouls.client.gui.screens;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.entity.nbt.MobNBTManager;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.Stats;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class LevelUpScreen extends Screen
{
	private Map<Stat, Integer> displayedStats = new HashMap<>();
	private int displayedLevel;
	private final PlayerEntity player;
	
	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/level_up.png");
	private int imageWidth = 350;
	private int imageHeight = 200;
	private int buttonWidth = 70;
	private int buttonHeight = 20;
	
	public LevelUpScreen(PlayerEntity player)
	{
		super(NarratorChatListener.NO_TITLE);
		this.player = player;
		this.displayedLevel = Stats.getLevel(this.player);
	}
	
	@Override
	protected void init()
	{
		super.init();
		int buttonwidth2 = 12;
		int buttonheight2 = 20;
		
		int upDownButtonHeight = this.height / 2 - this.imageHeight / 2 + 40 - buttonheight2 / 4;
		for (Stat stat : Stats.getStats())
		{
			int statValue = stat.getValue(this.player);
			Button downButton = this.addButton(new Button(this.width / 2 - this.imageWidth / 6 - 15 - buttonwidth2 / 2, upDownButtonHeight, buttonwidth2, buttonheight2, new StringTextComponent("<"), (button) ->
			{
				this.levelDown(stat);
				button.active = this.displayedStats.getOrDefault(stat, Integer.valueOf(1)).intValue() > statValue;
		    }));
			downButton.active = this.displayedStats.getOrDefault(stat, Integer.valueOf(1)).intValue() > statValue;
			
			Button upButton = this.addButton(new Button(this.width / 2 - this.imageWidth / 6 + 15 - buttonwidth2 / 2, upDownButtonHeight, buttonwidth2, buttonheight2, new StringTextComponent(">"), (button) ->
			{
				this.levelUp(stat);
				button.active = this.displayedStats.getOrDefault(stat, Integer.valueOf(1)).intValue() < 99 && this.canEffort();
		    }));
			upButton.active = this.displayedStats.getOrDefault(stat, Integer.valueOf(1)).intValue() < 99 && this.canEffort();
			
			upDownButtonHeight += 25;
		}
		
		this.addButton(new Button(this.width / 2 - this.imageWidth / 4 - this.buttonWidth / 2, this.height / 2 + (3 * (this.buttonHeight + 5)), this.buttonWidth, this.buttonHeight, new TranslationTextComponent("gui.darksouls.accept"), (p_214187_1_) ->
		{
	         this.accept();
	    }));
	}
	
	@Override
	public void render(MatrixStack matrixstack, int mouseX, int mouseY, float partialticks)
	{
		super.renderBackground(matrixstack);
		this.renderBg(matrixstack, partialticks, mouseX, mouseY);
		
		drawCenteredString(matrixstack, this.font, "Level Up", this.width / 2 - this.imageWidth / 4, this.height / 2 - this.imageHeight / 2 + 10, 16777215);
		drawCenteredString(matrixstack, this.font, "Souls: " + (this.player.isCreative() ? "INFINITE" : MobNBTManager.getSouls(this.player)), this.width / 2 - this.imageWidth / 2 + this.imageWidth / 8, this.height / 2 - this.imageHeight / 2 + 25, 16777215);
		drawCenteredString(matrixstack, this.font, "Cost: " + this.getCost(), this.width / 2 - this.imageWidth / 6, this.height / 2 - this.imageHeight / 2 + 25, 16777215);
		
		int textheight = this.height / 2 - this.imageHeight / 2 + 40;
		for (Stat stat : Stats.getStats())
		{
			drawCenteredString(matrixstack, this.font, new TranslationTextComponent(stat.getName()), this.width / 2 - this.imageWidth / 2 + this.imageWidth / 6, textheight, 16777215);
			
			int statvalue = stat.getValue(this.player);
			int displaystatvalue = this.displayedStats.getOrDefault(stat, Integer.valueOf(statvalue)).intValue();
			int color = statvalue != displaystatvalue ? 0x8cc9ff : 16777215;
			drawCenteredString(matrixstack, this.font, String.valueOf(displaystatvalue), this.width / 2 - this.imageWidth / 6, textheight, color);
		
			textheight += 25;
		}
		
		int maxhealth = (int)this.player.getAttributeValue(Attributes.MAX_HEALTH) + this.displayedStats.getOrDefault(Stats.VIGOR, Integer.valueOf(Stats.VIGOR.getValue(this.player))).intValue() - Stats.VIGOR.getValue(this.player);
		int maxhealthcolor = (int)this.player.getAttributeValue(Attributes.MAX_HEALTH) != maxhealth ?  0x8cc9ff : 16777215;
		drawCenteredString(matrixstack, this.font, "Max Health: " + maxhealth, this.width / 2 + this.imageWidth / 6, this.height / 2 - this.imageHeight / 2 + 40, maxhealthcolor);
		
		super.render(matrixstack, mouseX, mouseY, partialticks);
	}
	
	private void renderBg(MatrixStack matrixstack, float partialticks, int mouseX, int mouseY)
	{
		this.minecraft.getTextureManager().bind(TEXTURE_LOCATION);
		int x = (this.width - this.imageWidth) / 2;
	    int y = (this.height - this.imageHeight) / 2;
	    AbstractGui.blit(matrixstack, x, y, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
	}
	
	private boolean canEffort()
	{
		return this.player.isCreative() ? true : MobNBTManager.getSouls(this.player) >= this.getCost();
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
		int displaystatvalue = this.displayedStats.getOrDefault(stat, Integer.valueOf(statvalue)).intValue();
		this.displayedStats.put(stat, this.displayedStats.getOrDefault(stat, Integer.valueOf(displaystatvalue)).intValue() - 1);
		this.displayedLevel -= 1;
	}
	
	private void accept()
	{
		if (this.displayedLevel != Stats.getLevel(this.player))
		{
			this.displayedLevel -= 1;
			MobNBTManager.shrinkSouls(this.player, this.getCost());
		}
		for (Stat stat : Stats.getStats())
		{
			int statvalue = stat.getValue(this.player);
			int displaystatvalue = this.displayedStats.getOrDefault(stat, Integer.valueOf(statvalue)).intValue();
			stat.setValue(this.player, this.displayedStats.getOrDefault(stat, Integer.valueOf(displaystatvalue)));
			Minecraft minecraft = Minecraft.getInstance();
			stat.setValue(minecraft.player, this.displayedStats.getOrDefault(stat, Integer.valueOf(displaystatvalue)));
		}
		super.onClose();
	}
	
	@Override
	public boolean isPauseScreen()
	{
		return false;
	}
	
	@Override
	public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_)
	{
		InputMappings.Input mouseKey = InputMappings.getKey(p_231046_1_, p_231046_2_);
		if (super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_)) return true;
		else if (this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) 
		{
	         this.onClose();
	         return true;
		}
		else return false;
	}
}
