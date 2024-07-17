package com.skullmangames.darksouls.client.gui.screens;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;
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
import com.skullmangames.darksouls.core.util.math.ModMath;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
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
	
	protected final ImmutableMap<Attribute, Double> damageMod;
	protected final ImmutableMap<Attribute, Double> defMod;

	protected final int color;
	protected final String modColor;

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

		this.color = ConfigManager.CLIENT_CONFIG.darkSoulsUI.getValue() ? 16777215 : 4210752;
		this.modColor = ConfigManager.CLIENT_CONFIG.darkSoulsUI.getValue() ? "\u00A7f" : "\u00A70";
		
		this.maxHealthBase = (int)this.player.getAttributeBaseValue(Attributes.MAX_HEALTH);
		this.maxStaminaBase = (int)this.player.getAttributeBaseValue(ModAttributes.MAX_STAMINA.get());
		this.maxFPBase = (int)this.player.getAttributeBaseValue(ModAttributes.MAX_FOCUS_POINTS.get());
		this.maxEquipLoadBase = (float)this.player.getAttributeBaseValue(ModAttributes.MAX_EQUIP_LOAD.get());
		this.maxAttunementSlotsBase = (int)this.player.getAttributeBaseValue(ModAttributes.ATTUNEMENT_SLOTS.get());
		
		ImmutableMap.Builder<Attribute, Double> damageModBuilder = ImmutableMap.builder();
		for (Supplier<Attribute> attribute : ModAttributes.damageAttributes())
		{
			damageModBuilder.put(attribute.get(), Stats.getDamageMultiplier(this.playerCap, attribute.get(), (stat) -> this.displayedStats.get(stat)));
		}
		this.damageMod = damageModBuilder.build();
		
		ImmutableMap.Builder<Attribute, Double> defModBuilder = ImmutableMap.builder();
		for (Supplier<Attribute> attribute : ModAttributes.protectionAttributes())
		{
			double mod = 0D;
			for (Stat stat : Stats.getForAttribute(attribute.get()))
			{
				mod += stat.getModifyValue(this.playerCap, attribute.get(), this.displayedStats.get(stat));
			}
			defModBuilder.put(attribute.get(), mod);
		}
		this.defMod = defModBuilder.build();
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
		return this.canPurchase() && this.displayedLevel < ClientManager.INSTANCE.maxPlayerLevel;
	}
	
	protected boolean canPurchase()
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
		this.font.draw(poseStack, "Level: " + this.displayedLevel + " / " + ClientManager.INSTANCE.maxPlayerLevel, firstX, y + 36, this.color);
		int soulsColor = !this.canPurchase() ? 0xde2f18 : this.color;
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
		
		int i = 0;
		this.font.draw(poseStack, "Base Power", secondX, y + 36, this.color);
		
		int maxhealth = this.maxHealthBase + (int)Stats.VIGOR.getModifyValue(this.playerCap, null, this.displayedStats.get(Stats.VIGOR));
		int maxhealthcolor = (int)this.player.getAttributeValue(Attributes.MAX_HEALTH) != maxhealth ? 0x8cc9ff : this.color;
		this.font.draw(poseStack, "Max Health: " + maxhealth, secondX, y + 52 + 12 * i++, maxhealthcolor);
		
		int health = (int)this.player.getHealth();
		this.font.draw(poseStack, "Health: "+ health, secondX, y + 52 + 12 * i++, this.color);
		
		int maxfp = this.maxFPBase + (int)Stats.ATTUNEMENT.getModifyValue(this.playerCap, ModAttributes.MAX_FOCUS_POINTS.get(), this.displayedStats.get(Stats.ATTUNEMENT));
		int maxfpcolor = (int)this.player.getAttributeValue(ModAttributes.MAX_FOCUS_POINTS.get()) != maxfp ? 0x8cc9ff : this.color;
		this.font.draw(poseStack, "Max Focus Points: " + maxfp, secondX, y + 52 + 12 * i++, maxfpcolor);
		
		int fp = (int)this.playerCap.getFP();
		this.font.draw(poseStack, "Focus Points: "+ fp, secondX, y + 52 + 12 * i++, this.color);

		int maxstamina = this.maxStaminaBase + (int)Stats.ENDURANCE.getModifyValue(this.playerCap, null, this.displayedStats.get(Stats.ENDURANCE));
		int maxstaminacolor = (int)this.player.getAttributeValue(ModAttributes.MAX_STAMINA.get()) != maxstamina ? 0x8cc9ff : this.color;
		this.font.draw(poseStack, "Max Stamina: " + maxstamina, secondX, y + 52 + 12 * i++, maxstaminacolor);
		
		float maxEquipLoad = ModMath.round(this.maxEquipLoadBase + (float)Stats.VITALITY.getModifyValue(this.playerCap, null, this.displayedStats.get(Stats.VITALITY)), 1);
		int maxEquipLoadColor = ModMath.round((float)this.player.getAttributeValue(ModAttributes.MAX_EQUIP_LOAD.get()), 1) != maxEquipLoad ? 0x8cc9ff : this.color;
		double equipLoad = this.player.getAttributeValue(ModAttributes.EQUIP_LOAD.get());
		this.font.draw(poseStack, "Equip Load: " + ModMath.round(equipLoad, 1) + " / " + maxEquipLoad, secondX, y + 52 + 12 * i++, maxEquipLoadColor);
		
		int maxAttunementSlots = this.maxAttunementSlotsBase + (int)Stats.ATTUNEMENT.getModifyValue(this.playerCap, ModAttributes.ATTUNEMENT_SLOTS.get(), this.displayedStats.get(Stats.ATTUNEMENT));
		int maxAttunementSlotsColor = (int)this.player.getAttributeValue(ModAttributes.ATTUNEMENT_SLOTS.get()) != maxAttunementSlots ? 0x8cc9ff : this.color;
		this.font.draw(poseStack, "Attunement Slots: " + maxAttunementSlots, secondX, y + 52 + 12 * i++, maxAttunementSlotsColor);
		
		this.font.draw(poseStack, "Covenant: " + this.playerCap.getCovenant().getRegistryName(), secondX, y + 52 + 12 * i++, this.color);
		
		Map<Attribute, String> damageValues = new HashMap<>();
		this.damageMod.forEach((attribute, mod) ->
		{
			double displayMod = Stats.getDamageMultiplier(this.playerCap, attribute, (stat) -> this.displayedStats.get(stat));
			int value = (int)Math.round(this.player.getAttributeValue(attribute) / mod
					* displayMod);
			
			String color = mod != displayMod ? "\u00A7b" : this.modColor;
			
			damageValues.put(attribute, color + value);
		});
		
		i = 0;
		int y0 = y + 184;
		this.font.draw(poseStack, "Attack power", secondX, y + 168, this.color);
		this.font.draw(poseStack, "Mainhand:", secondX, y0 + 12 * i++, this.color);
		this.font.draw(poseStack, "\u00A77Physical: " + damageValues.get(Attributes.ATTACK_DAMAGE), secondX, y0 + 12 * i++, this.color);
		this.font.draw(poseStack, "\u00A73Magic: " + damageValues.get(ModAttributes.MAGIC_DAMAGE.get()), secondX, y0 + 12 * i++, this.color);
		this.font.draw(poseStack, "\u00A7cFire: " + damageValues.get(ModAttributes.FIRE_DAMAGE.get()), secondX, y0 + 12 * i++, this.color);
		this.font.draw(poseStack, "\u00A7eLightning: " + damageValues.get(ModAttributes.LIGHTNING_DAMAGE.get()), secondX, y0 + 12 * i++, this.color);
		this.font.draw(poseStack, "\u00A76Holy: " + damageValues.get(ModAttributes.HOLY_DAMAGE.get()), secondX, y0 + 12 * i++, this.color);
		this.font.draw(poseStack, "\u00A75Dark: " + damageValues.get(ModAttributes.DARK_DAMAGE.get()), secondX, y0 + 12 * i++, this.color);

		int thirdX = x + 366;
		int fourthX = thirdX + 12;

		
		Map<Attribute, String> defValues = new HashMap<>();
		this.defMod.forEach((attribute, mod) ->
		{
			double mod2 = 0D;
			for (Stat stat : Stats.getForAttribute(attribute))
			{
				mod2 += stat.getModifyValue(this.playerCap, attribute, this.displayedStats.get(stat));
			}
			
			int value = (int)Math.round(this.player.getAttributeValue(attribute) - mod
					+ mod2);
			
			String color = (int)Math.round(this.player.getAttributeValue(attribute)) != value ? "\u00A7b" : this.modColor;
			
			defValues.put(attribute, color + value);
		});
		this.font.draw(poseStack, "Defense", thirdX, y + 36, this.color);
		this.font.draw(poseStack, "\u00A77Physical: " + defValues.get(ModAttributes.STANDARD_PROTECTION.get()),
				thirdX, y + 52, this.color);
		this.font.draw(poseStack, "\u00A77VS Strike: " + defValues.get(ModAttributes.STRIKE_PROTECTION.get()),
				fourthX, y + 64, this.color);
		this.font.draw(poseStack, "\u00A77VS Slash: " + defValues.get(ModAttributes.SLASH_PROTECTION.get()),
				fourthX, y + 76, this.color);
		this.font.draw(poseStack, "\u00A77VS Thrust: " + defValues.get(ModAttributes.THRUST_PROTECTION.get()),
				fourthX, y + 88, this.color);
		this.font.draw(poseStack, "\u00A73Magic: " + defValues.get(ModAttributes.MAGIC_PROTECTION.get()),
				thirdX, y + 100, this.color);
		this.font.draw(poseStack, "\u00A7cFire: " + defValues.get(ModAttributes.FIRE_PROTECTION.get()),
				thirdX, y + 112, this.color);
		this.font.draw(poseStack, "\u00A7eLightning: " + defValues.get(ModAttributes.LIGHTNING_PROTECTION.get()),
				thirdX, y + 124, this.color);
		this.font.draw(poseStack, "\u00A76Holy: " + defValues.get(ModAttributes.HOLY_PROTECTION.get()),
				thirdX, y + 136, this.color);
		this.font.draw(poseStack, "\u00A75Dark: " + defValues.get(ModAttributes.DARK_PROTECTION.get()),
				thirdX, y + 148, this.color);

		poseStack.popPose();

		super.render(poseStack, mouseX, mouseY, partialticks);
	}

	private void renderBg(PoseStack matrixstack, float partialticks, int mouseX, int mouseY)
	{
		if (ConfigManager.CLIENT_CONFIG.darkSoulsUI.getValue())
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
