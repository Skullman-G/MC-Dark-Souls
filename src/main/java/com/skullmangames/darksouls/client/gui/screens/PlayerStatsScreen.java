package com.skullmangames.darksouls.client.gui.screens;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.input.ModKeys;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class PlayerStatsScreen extends Screen
{
	protected final Map<Stat, Integer> displayedStats = new HashMap<Stat, Integer>();
	protected int displayedLevel;
	protected final PlayerEntity player;
	protected final PlayerData<?> playerdata;

	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/level_up.png");
	public static final ResourceLocation DS_TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/ds_level_up.png");

	protected final int imageWidth;
	protected final int imageHeight;

	protected final int color;

	public PlayerStatsScreen(PlayerEntity player)
	{
		this(player, new StringTextComponent("Status"));
	}
	
	public PlayerStatsScreen(PlayerEntity player, ITextComponent title)
	{
		super(title);
		this.player = player;
		this.playerdata = (PlayerData<?>)player.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		this.displayedLevel = Stats.getLevel(this.player);
		for (Stat stat : Stats.getStats())
			this.displayedStats.put(stat, stat.getValue(this.player));

		this.imageWidth = 418;
		this.imageHeight = 240;

		this.color = DarkSouls.CLIENT_INGAME_CONFIG.darkSoulsUI.getValue() ? 16777215 : 4210752;
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

		this.font.draw(matrixstack, this.title, x + 10, y + 10, 16777215);

		int firstX = x + 19;
		this.font.draw(matrixstack, "Level: " + this.displayedLevel, firstX, y + 36, this.color);
		this.font.draw(matrixstack, "Souls: " + (this.player.isCreative() ? "INFINITE" : this.playerdata.getSouls()), firstX, y + 60,
				this.color);
		this.font.draw(matrixstack, "Attributes", firstX, y + 128, this.color);

		int textheight = y + 143;
		for (Stat stat : Stats.getStats())
		{
			this.font.draw(matrixstack, new TranslationTextComponent(stat.toString()), firstX, textheight, this.color);

			int statvalue = stat.getValue(this.player);
			int displaystatvalue = this.displayedStats.getOrDefault(stat, Integer.valueOf(statvalue)).intValue();
			int color = statvalue != displaystatvalue ? 0x8cc9ff : this.color;
			this.font.draw(matrixstack, String.valueOf(displaystatvalue), (float)(firstX + 120 - this.font.width(String.valueOf(displaystatvalue)) / 2), textheight, color);

			textheight += 12;
		}

		int secondX = x + 195;

		this.font.draw(matrixstack, "Base power", secondX, y + 36, this.color);

		int maxhealth = (int) this.player.getAttributeValue(Attributes.MAX_HEALTH)
				+ this.displayedStats.getOrDefault(Stats.VIGOR, Integer.valueOf(Stats.VIGOR.getValue(this.player))).intValue()
				- Stats.VIGOR.getValue(this.player);
		int maxhealthcolor = (int) this.player.getAttributeValue(Attributes.MAX_HEALTH) != maxhealth ? 0x8cc9ff : this.color;
		this.font.draw(matrixstack, "Max health: " + maxhealth, secondX, y + 52, maxhealthcolor);

		int maxstamina = (int) this.player.getAttributeValue(ModAttributes.MAX_STAMINA.get())
				+ this.displayedStats.getOrDefault(Stats.ENDURANCE, Integer.valueOf(Stats.ENDURANCE.getValue(this.player))).intValue()
				- Stats.ENDURANCE.getValue(this.player);
		int maxstaminacolor = (int) this.player.getAttributeValue(ModAttributes.MAX_STAMINA.get()) != maxstamina ? 0x8cc9ff : this.color;
		this.font.draw(matrixstack, "Max stamina: " + maxstamina, secondX, y + 64, maxstaminacolor);

		this.font.draw(matrixstack, "Attack power", secondX, y + 144, this.color);
		this.font.draw(matrixstack, "Mainhand: " + this.player.getAttributeValue(Attributes.ATTACK_DAMAGE), secondX, y + 160, this.color);

		int thirdX = x + 366;
		int fourthX = thirdX + 12;

		this.font.draw(matrixstack, "Defense", thirdX, y + 36, this.color);
		this.font.draw(matrixstack, "Physical: " + (int) (this.player.getAttributeValue(ModAttributes.STANDARD_DEFENSE.get()) * 100) + "%",
				thirdX, y + 52, this.color);
		this.font.draw(matrixstack, "VS strike: " + (int) (this.player.getAttributeValue(ModAttributes.STRIKE_DEFENSE.get()) * 100) + "%",
				fourthX, y + 64, this.color);
		this.font.draw(matrixstack, "VS slash: " + (int) (this.player.getAttributeValue(ModAttributes.SLASH_DEFENSE.get()) * 100) + "%",
				fourthX, y + 76, this.color);
		this.font.draw(matrixstack, "VS thrust: " + (int) (this.player.getAttributeValue(ModAttributes.THRUST_DEFENSE.get()) * 100) + "%",
				fourthX, y + 88, this.color);

		matrixstack.popPose();

		super.render(matrixstack, mouseX, mouseY, partialticks);
	}

	private void renderBg(MatrixStack matrixstack, float partialticks, int mouseX, int mouseY)
	{
		if (DarkSouls.CLIENT_INGAME_CONFIG.darkSoulsUI.getValue())
			this.minecraft.getTextureManager().bind(DS_TEXTURE_LOCATION);
		else
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
		if (super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_))
			return true;
		else if (ModKeys.OPEN_STAT_SCREEN.isActiveAndMatches(mouseKey))
		{
			this.onClose();
			return true;
		} else
			return false;
	}
}
