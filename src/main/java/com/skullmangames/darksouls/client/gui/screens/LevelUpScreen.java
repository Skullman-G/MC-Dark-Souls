package com.skullmangames.darksouls.client.gui.screens;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.gui.widget.LevelButton;
import com.skullmangames.darksouls.common.entity.nbt.MobNBTManager;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.init.ModAttributes;

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
	private Map<Stat, Integer> displayedStats = new HashMap<Stat, Integer>();
	private int displayedLevel;
	private final PlayerEntity player;
	
	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/ds_level_up.png");
	private final Map<LevelButton, LevelButton> levelButtons = new HashMap<LevelButton, LevelButton>();
	private final int imageWidth;
	private final int imageHeight;
	private final int buttonWidth = 70;
	private final int buttonHeight = 20;
	
	public LevelUpScreen(PlayerEntity player)
	{
		super(NarratorChatListener.NO_TITLE);
		this.player = player;
		this.displayedLevel = Stats.getLevel(this.player);
		for (Stat stat : Stats.getStats()) this.displayedStats.put(stat, stat.getValue(this.player));
		
		this.imageWidth = 418;
		this.imageHeight = 240;
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
			LevelButton downButton = this.addButton(new LevelButton(x + 95, upDownButtonHeight, buttonwidth2, buttonheight2, new StringTextComponent("<"), (button) ->
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
	
	@Override
	public void render(MatrixStack matrixstack, int mouseX, int mouseY, float partialticks)
	{
		super.renderBackground(matrixstack);
		
		int x = (this.width - this.imageWidth) / 2;
	    int y = (this.height - this.imageHeight) / 2;
		this.renderBg(matrixstack, partialticks, x, y);
		
		matrixstack.pushPose();
		float scale = 0.8F;
		matrixstack.scale(scale, scale, 1.0F);
		x /= scale;
		y /= scale;
		
		drawString(matrixstack, this.font, "Level Up", x + 10, y + 10, 16777215);
		
		int firstX = x + 19;
		drawString(matrixstack, this.font, "Level: "+this.displayedLevel, firstX, y + 36, 16777215);
		drawString(matrixstack, this.font, "Souls: " + (this.player.isCreative() ? "INFINITE" : MobNBTManager.getSouls(this.player)), firstX, y + 60, 16777215);
		drawString(matrixstack, this.font, "Required Souls: " + this.getCost(), firstX, y + 72, 16777215);
		drawString(matrixstack, this.font, "Attributes", firstX, y + 128, 16777215);
		
		int textheight = y + 143;
		for (Stat stat : Stats.getStats())
		{
			drawString(matrixstack, this.font, new TranslationTextComponent(stat.toString()), firstX, textheight, 16777215);
			
			int statvalue = stat.getValue(this.player);
			int displaystatvalue = this.displayedStats.getOrDefault(stat, Integer.valueOf(statvalue)).intValue();
			int color = statvalue != displaystatvalue ? 0x8cc9ff : 16777215;
			drawCenteredString(matrixstack, this.font, String.valueOf(displaystatvalue), firstX + 120, textheight, color);
		
			textheight += 12;
		}
		
		int secondX = x + 195;
		
		drawString(matrixstack, this.font, "Base power", secondX, y + 36, 16777215);
		
		int maxhealth = (int)this.player.getAttributeValue(Attributes.MAX_HEALTH) + this.displayedStats.getOrDefault(Stats.VIGOR, Integer.valueOf(Stats.VIGOR.getValue(this.player))).intValue() - Stats.VIGOR.getValue(this.player);
		int maxhealthcolor = (int)this.player.getAttributeValue(Attributes.MAX_HEALTH) != maxhealth ?  0x8cc9ff : 16777215;
		drawString(matrixstack, this.font, "Max health: " + maxhealth, secondX, y + 52, maxhealthcolor);
		
		int maxstamina = (int)this.player.getAttributeValue(ModAttributes.MAX_STAMINA.get()) + this.displayedStats.getOrDefault(Stats.ENDURANCE, Integer.valueOf(Stats.ENDURANCE.getValue(this.player))).intValue() - Stats.ENDURANCE.getValue(this.player);
		int maxstaminacolor = (int)this.player.getAttributeValue(ModAttributes.MAX_STAMINA.get()) != maxstamina ?  0x8cc9ff : 16777215;
		drawString(matrixstack, this.font, "Max stamina: " + maxstamina, secondX, y + 64, maxstaminacolor);
		
		
		drawString(matrixstack, this.font, "Attack power", secondX, y + 144, 16777215);
		drawString(matrixstack, this.font, "Mainhand: "+this.player.getAttributeValue(Attributes.ATTACK_DAMAGE), secondX, y + 160, 16777215);
		
		int thirdX = x + 366;
		int fourthX = thirdX + 12;
		
		drawString(matrixstack, this.font, "Defense", thirdX, y + 36, 16777215);
		drawString(matrixstack, this.font, "Physical: "+(int)(this.player.getAttributeValue(ModAttributes.STANDARD_DEFENSE.get())*100)+"%", thirdX, y + 52, 16777215);
		drawString(matrixstack, this.font, "VS strike: "+(int)(this.player.getAttributeValue(ModAttributes.STRIKE_DEFENSE.get())*100)+"%", fourthX, y + 64, 16777215);
		drawString(matrixstack, this.font, "VS slash: "+(int)(this.player.getAttributeValue(ModAttributes.SLASH_DEFENSE.get())*100)+"%", fourthX, y + 76, 16777215);
		drawString(matrixstack, this.font, "VS thrust: "+(int)(this.player.getAttributeValue(ModAttributes.THRUST_DEFENSE.get())*100)+"%", fourthX, y + 88, 16777215);
		
		matrixstack.popPose();
		
		super.render(matrixstack, mouseX, mouseY, partialticks);
	}
	
	private void renderBg(MatrixStack matrixstack, float partialticks, int x, int y)
	{
		this.minecraft.getTextureManager().bind(TEXTURE_LOCATION);
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
		int displaystatvalue = this.displayedStats.getOrDefault(stat, statvalue).intValue();
		this.displayedStats.put(stat, this.displayedStats.getOrDefault(stat, displaystatvalue).intValue() - 1);
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
			int displaystatvalue = this.displayedStats.getOrDefault(stat, statvalue).intValue();
			stat.setValue(this.player, this.displayedStats.getOrDefault(stat, displaystatvalue));
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
