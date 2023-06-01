package com.skullmangames.darksouls.client.gui.screens;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.input.ModKeys;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.config.ConfigManager;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.util.math.MathUtils;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerStatsScreen extends Screen
{
	protected final Map<Stat, Integer> displayedStats = new HashMap<Stat, Integer>();
	protected int displayedLevel;
	protected final LocalPlayerCap playerCap;
	protected final LocalPlayer player;

	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/level_up.png");
	public static final ResourceLocation DS_TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/ds_level_up.png");

	protected final int imageWidth;
	protected final int imageHeight;
	
	protected final int maxHealthBase;
	protected final int maxStaminaBase;
	protected final int maxFPBase;
	protected final float maxEquipLoadBase;
	protected final int maxAttunementSlotsBase;
	protected final double physDmgMod;

	protected final int color;

	public PlayerStatsScreen()
	{
		this(new TextComponent("Status"));
	}
	
	public PlayerStatsScreen(TextComponent title)
	{
		super(title);
		this.playerCap = ClientManager.INSTANCE.getPlayerCap();
		this.player = this.playerCap.getOriginalEntity();
		this.displayedLevel = this.playerCap.getSoulLevel();
		for (Stat stat : Stats.STATS.values())
			this.displayedStats.put(stat, this.playerCap.getStats().getStatValue(stat));

		this.imageWidth = 418;
		this.imageHeight = 240;

		this.color = ConfigManager.INGAME_CONFIG.darkSoulsUI.getValue() ? 16777215 : 4210752;
		
		this.maxHealthBase = (int)this.player.getAttributeBaseValue(Attributes.MAX_HEALTH);
		this.maxStaminaBase = (int)this.player.getAttributeBaseValue(ModAttributes.MAX_STAMINA.get());
		this.maxFPBase = (int)this.player.getAttributeBaseValue(ModAttributes.MAX_FOCUS_POINTS.get());
		this.maxEquipLoadBase = (float)this.player.getAttributeBaseValue(ModAttributes.MAX_EQUIP_LOAD.get());
		this.maxAttunementSlotsBase = (int)this.player.getAttributeBaseValue(ModAttributes.ATTUNEMENT_SLOTS.get());
		this.physDmgMod = Stats.getDamageMultiplier(this.player, Attributes.ATTACK_DAMAGE, (stat) -> this.displayedStats.get(stat));
	}
	
	protected int upgradeCost()
	{
		int upgradeCost = 0;
		for (int i = this.playerCap.getSoulLevel(); i < this.displayedLevel; i++)
		{
			upgradeCost += Stats.getCost(i);
		}
		return upgradeCost;
	}
	
	protected boolean canUpgrade()
	{
		return this.playerCap.hasEnoughSouls(this.upgradeCost() + Stats.getCost(this.displayedLevel));
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialticks)
	{
		super.renderBackground(poseStack);

		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		this.renderBg(poseStack, partialticks, x, y);

		poseStack.pushPose();
		float scale = 0.8F;
		poseStack.scale(scale, scale, 1.0F);
		x /= scale;
		y /= scale;

		this.font.draw(poseStack, this.title, x + 10, y + 10, 16777215);

		int firstX = x + 19;
		this.font.draw(poseStack, "Level: " + this.displayedLevel, firstX, y + 36, this.color);
		int soulsColor = !this.canUpgrade() ? 0xde2f18 : this.color;
		this.font.draw(poseStack, "Souls: " + (this.playerCap.isCreativeOrSpectator() ? "INFINITE" : this.playerCap.getSouls() - this.upgradeCost()), firstX, y + 60,
				soulsColor);
		this.font.draw(poseStack, "Cost: " + Stats.getCost(this.displayedLevel), firstX, y + 72, this.color);
		this.font.draw(poseStack, "Attributes", firstX, y + 128, this.color);

		int textheight = y + 143;
		for (Stat stat : Stats.STATS.values())
		{
			this.font.draw(poseStack, new TranslatableComponent(stat.toString()), firstX, textheight, this.color);

			int statvalue = this.playerCap.getStats().getStatValue(stat);
			int displaystatvalue = this.displayedStats.get(stat).intValue();
			int color = statvalue != displaystatvalue ? 0x8cc9ff : this.color;
			this.font.draw(poseStack, String.valueOf(displaystatvalue), (float)(firstX + 120 - this.font.width(String.valueOf(displaystatvalue)) / 2), textheight, color);

			textheight += 12;
		}

		int secondX = x + 195;
		
		this.font.draw(poseStack, "Base Power", secondX, y + 36, this.color);
		
		int maxhealth = this.maxHealthBase + (int)Stats.VIGOR.getModifyValue(this.player, null, this.displayedStats.get(Stats.VIGOR));
		int maxhealthcolor = (int)this.player.getAttributeValue(Attributes.MAX_HEALTH) != maxhealth ? 0x8cc9ff : this.color;
		this.font.draw(poseStack, "Max Health: " + maxhealth, secondX, y + 52, maxhealthcolor);
		
		int maxfp = this.maxFPBase + (int)Stats.ATTUNEMENT.getModifyValue(this.player, ModAttributes.MAX_FOCUS_POINTS.get(), this.displayedStats.get(Stats.ATTUNEMENT));
		int maxfpcolor = (int)this.player.getAttributeValue(ModAttributes.MAX_FOCUS_POINTS.get()) != maxfp ? 0x8cc9ff : this.color;
		this.font.draw(poseStack, "Max Focus Points: " + maxfp, secondX, y + 64, maxfpcolor);

		int maxstamina = this.maxStaminaBase + (int)Stats.ENDURANCE.getModifyValue(this.player, null, this.displayedStats.get(Stats.ENDURANCE));
		int maxstaminacolor = (int)this.player.getAttributeValue(ModAttributes.MAX_STAMINA.get()) != maxstamina ? 0x8cc9ff : this.color;
		this.font.draw(poseStack, "Max Stamina: " + maxstamina, secondX, y + 76, maxstaminacolor);
		
		float maxEquipLoad = MathUtils.round(this.maxEquipLoadBase + (float)Stats.VITALITY.getModifyValue(this.player, null, this.displayedStats.get(Stats.VITALITY)), 1);
		int maxEquipLoadColor = MathUtils.round((float)this.player.getAttributeValue(ModAttributes.MAX_EQUIP_LOAD.get()), 1) != maxEquipLoad ? 0x8cc9ff : this.color;
		double equipLoad = this.player.getAttributeValue(ModAttributes.EQUIP_LOAD.get());
		this.font.draw(poseStack, "Equip Load: " + MathUtils.round(equipLoad, 1) + " / " + maxEquipLoad, secondX, y + 88, maxEquipLoadColor);
		
		int maxAttunementSlots = this.maxAttunementSlotsBase + (int)Stats.ATTUNEMENT.getModifyValue(this.player, ModAttributes.ATTUNEMENT_SLOTS.get(), this.displayedStats.get(Stats.ATTUNEMENT));
		int maxAttunementSlotsColor = (int)this.player.getAttributeValue(ModAttributes.ATTUNEMENT_SLOTS.get()) != maxAttunementSlots ? 0x8cc9ff : this.color;
		this.font.draw(poseStack, "Attunement Slots: " + maxAttunementSlots, secondX, y + 100, maxAttunementSlotsColor);
		
		this.font.draw(poseStack, "Covenant: " + this.playerCap.getCovenant().getRegistryName(), secondX, y + 112, this.color);
		
		this.font.draw(poseStack, "Attack power", secondX, y + 144, this.color);
		double physDmg = MathUtils.round(this.player.getAttributeValue(Attributes.ATTACK_DAMAGE) / this.physDmgMod
				* Stats.getDamageMultiplier(this.player, Attributes.ATTACK_DAMAGE, (stat) -> this.displayedStats.get(stat)), 2);
		int attackdamagecolor = MathUtils.round(this.player.getAttributeValue(Attributes.ATTACK_DAMAGE), 2) != physDmg ? 0x8cc9ff : this.color;
		this.font.draw(poseStack, "Mainhand:", secondX, y + 160, attackdamagecolor);
		this.font.draw(poseStack, "Physical: " + physDmg, secondX, y + 172, attackdamagecolor);

		int thirdX = x + 366;
		int fourthX = thirdX + 12;

		this.font.draw(poseStack, "Defense", thirdX, y + 36, this.color);
		this.font.draw(poseStack, "Physical: " + MathUtils.round(this.player.getAttributeValue(ModAttributes.STANDARD_PROTECTION.get()), 2),
				thirdX, y + 52, this.color);
		this.font.draw(poseStack, "VS Strike: " + MathUtils.round(this.player.getAttributeValue(ModAttributes.STRIKE_PROTECTION.get()), 2),
				fourthX, y + 64, this.color);
		this.font.draw(poseStack, "VS Slash: " + MathUtils.round(this.player.getAttributeValue(ModAttributes.SLASH_PROTECTION.get()), 2),
				fourthX, y + 76, this.color);
		this.font.draw(poseStack, "VS Thrust: " + MathUtils.round(this.player.getAttributeValue(ModAttributes.THRUST_PROTECTION.get()), 2),
				fourthX, y + 88, this.color);
		this.font.draw(poseStack, "Magic: " + MathUtils.round(this.player.getAttributeValue(ModAttributes.MAGIC_PROTECTION.get()), 2),
				thirdX, y + 100, this.color);
		this.font.draw(poseStack, "Fire: " + MathUtils.round(this.player.getAttributeValue(ModAttributes.FIRE_PROTECTION.get()), 2),
				thirdX, y + 112, this.color);
		this.font.draw(poseStack, "Lightning: " + MathUtils.round(this.player.getAttributeValue(ModAttributes.LIGHTNING_PROTECTION.get()), 2),
				thirdX, y + 124, this.color);
		this.font.draw(poseStack, "Dark: " + MathUtils.round(this.player.getAttributeValue(ModAttributes.DARK_DAMAGE.get()), 2),
				thirdX, y + 136, this.color);

		poseStack.popPose();

		super.render(poseStack, mouseX, mouseY, partialticks);
	}

	private void renderBg(PoseStack matrixstack, float partialticks, int mouseX, int mouseY)
	{
		if (ConfigManager.INGAME_CONFIG.darkSoulsUI.getValue())
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
