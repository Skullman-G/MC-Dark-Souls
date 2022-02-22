package com.skullmangames.darksouls.client.gui.screens;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.input.ModKeys;
import com.skullmangames.darksouls.common.capability.entity.ClientPlayerData;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.util.math.MathUtils;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class PlayerStatsScreen extends Screen
{
	protected final Map<Stat, Integer> displayedStats = new HashMap<Stat, Integer>();
	protected int displayedLevel;
	protected final ClientPlayerData playerdata;
	protected final LocalPlayer player;

	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/level_up.png");
	public static final ResourceLocation DS_TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/ds_level_up.png");

	protected final int imageWidth;
	protected final int imageHeight;
	
	protected final int maxHealthBase;
	protected final int maxStaminaBase;
	protected final double attackDamageMods;

	protected final int color;

	public PlayerStatsScreen()
	{
		this(new TextComponent("Status"));
	}
	
	public PlayerStatsScreen(TextComponent title)
	{
		super(title);
		this.playerdata = ClientManager.INSTANCE.getPlayerData();
		this.player = this.playerdata.getOriginalEntity();
		this.displayedLevel = this.playerdata.getSoulLevel();
		for (Stat stat : Stats.STATS)
			this.displayedStats.put(stat, this.playerdata.getStats().getStatValue(stat));

		this.imageWidth = 418;
		this.imageHeight = 240;

		this.color = DarkSouls.CLIENT_INGAME_CONFIG.darkSoulsUI.getValue() ? 16777215 : 4210752;
		
		this.maxHealthBase = (int)this.player.getAttributeBaseValue(Attributes.MAX_HEALTH);
		this.maxStaminaBase = (int)this.player.getAttributeBaseValue(ModAttributes.MAX_STAMINA.get());
		this.attackDamageMods = Stats.getTotalDamageAmount(this.player, this.displayedStats.get(Stats.STRENGTH), this.displayedStats.get(Stats.DEXTERITY));
	}

	@Override
	public void render(PoseStack matrixstack, int mouseX, int mouseY, float partialticks)
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
		this.font.draw(matrixstack, "Souls: " + (this.playerdata.isCreativeOrSpectator() ? "INFINITE" : this.playerdata.getSouls()), firstX, y + 60,
				this.color);
		this.font.draw(matrixstack, "Cost: " + Stats.getCost(this.displayedLevel), firstX, y + 72, this.color);
		this.font.draw(matrixstack, "Attributes", firstX, y + 128, this.color);

		int textheight = y + 143;
		for (Stat stat : Stats.STATS)
		{
			this.font.draw(matrixstack, new TranslatableComponent(stat.toString()), firstX, textheight, this.color);

			int statvalue = this.playerdata.getStats().getStatValue(stat);
			int displaystatvalue = this.displayedStats.get(stat).intValue();
			int color = statvalue != displaystatvalue ? 0x8cc9ff : this.color;
			this.font.draw(matrixstack, String.valueOf(displaystatvalue), (float)(firstX + 120 - this.font.width(String.valueOf(displaystatvalue)) / 2), textheight, color);

			textheight += 12;
		}

		int secondX = x + 195;
		
		this.font.draw(matrixstack, "Base power", secondX, y + 36, this.color);
		
		int maxhealth = this.maxHealthBase + (int)Stats.VIGOR.getModifyValue(null, this.displayedStats.get(Stats.VIGOR).intValue());
		int maxhealthcolor = (int) this.player.getAttributeValue(Attributes.MAX_HEALTH) != maxhealth ? 0x8cc9ff : this.color;
		this.font.draw(matrixstack, "Max health: " + maxhealth, secondX, y + 52, maxhealthcolor);

		int maxstamina = this.maxStaminaBase + (int)Stats.ENDURANCE.getModifyValue(null, this.displayedStats.get(Stats.ENDURANCE).intValue());
		int maxstaminacolor = (int) this.player.getAttributeValue(ModAttributes.MAX_STAMINA.get()) != maxstamina ? 0x8cc9ff : this.color;
		this.font.draw(matrixstack, "Max stamina: " + maxstamina, secondX, y + 64, maxstaminacolor);

		this.font.draw(matrixstack, "Attack power", secondX, y + 144, this.color);
		double attackdamage = MathUtils.round(this.player.getAttributeValue(Attributes.ATTACK_DAMAGE) - this.attackDamageMods
				+ Stats.getTotalDamageAmount(this.player, this.displayedStats.get(Stats.STRENGTH), this.displayedStats.get(Stats.DEXTERITY)), 100);
		int attackdamagecolor = MathUtils.round(this.player.getAttributeValue(Attributes.ATTACK_DAMAGE), 100) != attackdamage ? 0x8cc9ff : this.color;
		this.font.draw(matrixstack, "Mainhand: " + attackdamage, secondX, y + 160, attackdamagecolor);

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

	private void renderBg(PoseStack matrixstack, float partialticks, int mouseX, int mouseY)
	{
		if (DarkSouls.CLIENT_INGAME_CONFIG.darkSoulsUI.getValue())
			RenderSystem.setShaderTexture(0, DS_TEXTURE_LOCATION);
		else
			RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		GuiComponent.blit(matrixstack, x, y, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
	}

	@Override
	public boolean isPauseScreen()
	{
		return false;
	}

	@Override
	public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_)
	{
		InputConstants.Key mouseKey = InputConstants.getKey(p_231046_1_, p_231046_2_);
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
