package com.skullmangames.darksouls.client.gui.screens;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.input.ModKeys;
import com.skullmangames.darksouls.common.entity.nbt.MobNBTManager;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.init.ModAttributes;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class PlayerStatsScreen extends Screen
{
	private Map<Stat, Integer> displayedStats = new HashMap<Stat, Integer>();
	private int displayedLevel;
	private final PlayerEntity player;
	
	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/ds_level_up.png");
	private final int imageWidth;
	private final int imageHeight;
	
	public PlayerStatsScreen(PlayerEntity player)
	{
		super(NarratorChatListener.NO_TITLE);
		this.player = player;
		this.displayedLevel = Stats.getLevel(this.player);
		for (Stat stat : Stats.getStats()) this.displayedStats.put(stat, stat.getValue(this.player));
		
		this.imageWidth = 418;
		this.imageHeight = 240;
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
		
		drawString(matrixstack, this.font, "Status", x + 10, y + 10, 16777215);
		
		int firstX = x + 19;
		drawString(matrixstack, this.font, "Level: "+this.displayedLevel, firstX, y + 36, 16777215);
		drawString(matrixstack, this.font, "Souls: " + (this.player.isCreative() ? "INFINITE" : MobNBTManager.getSouls(this.player)), firstX, y + 60, 16777215);
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
	
	private void renderBg(MatrixStack matrixstack, float partialticks, int mouseX, int mouseY)
	{
		this.minecraft.getTextureManager().bind(TEXTURE_LOCATION);
		int x = (this.width - this.imageWidth) / 2;
	    int y = (this.height - this.imageHeight) / 2;
	    AbstractGui.blit(matrixstack, x, y, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
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
		else if (ModKeys.OPEN_STAT_SCREEN.isActiveAndMatches(mouseKey)) 
		{
	         this.onClose();
	         return true;
		}
		else return false;
	}
}
