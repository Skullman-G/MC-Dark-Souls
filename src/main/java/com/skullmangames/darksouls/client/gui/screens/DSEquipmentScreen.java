package com.skullmangames.darksouls.client.gui.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.item.Shield;
import com.skullmangames.darksouls.common.capability.item.WeaponCap;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DSEquipmentScreen extends Screen
{
	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/ds_equipment.png");
	
	protected final LocalPlayerCap playerCap;
	protected final LocalPlayer player;
	
	protected final int textureWidth;
	protected final int textureHeight;
	
	protected final int imageWidth;
	protected final int imageHeight;
	
	protected boolean itemSelect;
	protected Slot selected;
	
	protected ItemStack hovered = ItemStack.EMPTY;
	
	protected final Map<ImageButton, Integer> equipButtons = new HashMap<>();
	protected final List<ImageButton> itemButtons = new ArrayList<>();
	
	protected final List<Slot> invItems = new ArrayList<>();
	protected final List<Slot> shownInvItems = new ArrayList<>();
	
	public DSEquipmentScreen()
	{
		super(new TextComponent("Equipment"));
		
		this.textureWidth = 349;
		this.textureHeight = 385;
		
		this.imageWidth = 333;
		this.imageHeight = 200;
		
		this.playerCap = ClientManager.INSTANCE.getPlayerCap();
		this.player = this.playerCap.getOriginalEntity();
		this.invItems.addAll(this.player.inventoryMenu.slots);
	}
	
	@Override
	protected void init()
	{
		super.init();
		int buttonwidth = 19;
		int buttonheight = 22;
		
		// Equip Screen
		int x = this.width / 2 - 119;
		int y = this.height / 2 - 14;
		this.equipButtons.clear();
		ImageButton main1 = new ImageButton(x - 20, y - 48, buttonwidth, buttonheight, 297, 209, 22, TEXTURE_LOCATION,
				this.textureWidth, this.textureHeight, this::openSelectTab,
				(btn, ps, i0, i2) -> this.hovered = this.player.inventoryMenu.getSlot(this.equipButtons.get(btn)).getItem(), TextComponent.EMPTY);
		main1.visible = !this.itemSelect;
		this.equipButtons.put(main1, 36);
		this.addRenderableWidget(main1);
		ImageButton main2 = new ImageButton(x, y - 48, buttonwidth, buttonheight, 297, 209, 22, TEXTURE_LOCATION,
				this.textureWidth, this.textureHeight, this::openSelectTab,
				(btn, ps, i0, i2) -> this.hovered = this.player.inventoryMenu.getSlot(this.equipButtons.get(btn)).getItem(), TextComponent.EMPTY);
		main2.visible = !this.itemSelect;
		this.equipButtons.put(main2, 37);
		this.addRenderableWidget(main2);
		ImageButton main3 = new ImageButton(x + 20, y - 48, buttonwidth, buttonheight, 297, 209, 22, TEXTURE_LOCATION,
				this.textureWidth, this.textureHeight, this::openSelectTab,
				(btn, ps, i0, i2) -> this.hovered = this.player.inventoryMenu.getSlot(this.equipButtons.get(btn)).getItem(), TextComponent.EMPTY);
		main3.visible = !this.itemSelect;
		this.equipButtons.put(main3, 38);
		this.addRenderableWidget(main3);
		ImageButton main4 = new ImageButton(x - 20, y - 24, buttonwidth, buttonheight, 297, 209, 22, TEXTURE_LOCATION,
				this.textureWidth, this.textureHeight, this::openSelectTab,
				(btn, ps, i0, i2) -> this.hovered = this.player.inventoryMenu.getSlot(this.equipButtons.get(btn)).getItem(), TextComponent.EMPTY);
		main4.visible = !this.itemSelect;
		this.equipButtons.put(main4, 39);
		this.addRenderableWidget(main4);
		ImageButton main5 = new ImageButton(x, y - 24, buttonwidth, buttonheight, 297, 209, 22, TEXTURE_LOCATION,
				this.textureWidth, this.textureHeight, this::openSelectTab,
				(btn, ps, i0, i2) -> this.hovered = this.player.inventoryMenu.getSlot(this.equipButtons.get(btn)).getItem(), TextComponent.EMPTY);
		main5.visible = !this.itemSelect;
		this.equipButtons.put(main5, 40);
		this.addRenderableWidget(main5);
		ImageButton main6 = new ImageButton(x + 20, y - 24, buttonwidth, buttonheight, 297, 209, 22, TEXTURE_LOCATION,
				this.textureWidth, this.textureHeight, this::openSelectTab,
				(btn, ps, i0, i2) -> this.hovered = this.player.inventoryMenu.getSlot(this.equipButtons.get(btn)).getItem(), TextComponent.EMPTY);
		main6.visible = !this.itemSelect;
		this.equipButtons.put(main6, 41);
		this.addRenderableWidget(main6);
		ImageButton main7 = new ImageButton(x - 20, y, buttonwidth, buttonheight, 297, 209, 22, TEXTURE_LOCATION,
				this.textureWidth, this.textureHeight, this::openSelectTab,
				(btn, ps, i0, i2) -> this.hovered = this.player.inventoryMenu.getSlot(this.equipButtons.get(btn)).getItem(), TextComponent.EMPTY);
		main7.visible = !this.itemSelect;
		this.equipButtons.put(main7, 42);
		this.addRenderableWidget(main7);
		ImageButton main8 = new ImageButton(x, y, buttonwidth, buttonheight, 297, 209, 22, TEXTURE_LOCATION,
				this.textureWidth, this.textureHeight, this::openSelectTab,
				(btn, ps, i0, i2) -> this.hovered = this.player.inventoryMenu.getSlot(this.equipButtons.get(btn)).getItem(), TextComponent.EMPTY);
		main8.visible = !this.itemSelect;
		this.equipButtons.put(main8, 43);
		this.addRenderableWidget(main8);
		ImageButton main9 = new ImageButton(x + 20, y, buttonwidth, buttonheight, 297, 209, 22, TEXTURE_LOCATION,
				this.textureWidth, this.textureHeight, this::openSelectTab,
				(btn, ps, i0, i2) -> this.hovered = this.player.inventoryMenu.getSlot(this.equipButtons.get(btn)).getItem(), TextComponent.EMPTY);
		main9.visible = !this.itemSelect;
		this.equipButtons.put(main9, 44);
		this.addRenderableWidget(main9);
		
		ImageButton off = new ImageButton(x, y + 29, buttonwidth, buttonheight, 297, 209, 22, TEXTURE_LOCATION,
				this.textureWidth, this.textureHeight, this::openSelectTab,
				(btn, ps, i0, i2) -> this.hovered = this.player.inventoryMenu.getSlot(this.equipButtons.get(btn)).getItem(), TextComponent.EMPTY);
		off.visible = !this.itemSelect;
		this.equipButtons.put(off, 45);
		this.addRenderableWidget(off);
		
		ImageButton armor1 = new ImageButton(x - 30, y + 58, buttonwidth, buttonheight, 297, 209, 22, TEXTURE_LOCATION,
				this.textureWidth, this.textureHeight, this::openSelectTab,
				(btn, ps, i0, i2) -> this.hovered = this.player.inventoryMenu.getSlot(this.equipButtons.get(btn)).getItem(), TextComponent.EMPTY);
		armor1.visible = !this.itemSelect;
		this.equipButtons.put(armor1, 5);
		this.addRenderableWidget(armor1);
		ImageButton armor2 = new ImageButton(x - 10, y + 58, buttonwidth, buttonheight, 297, 209, 22, TEXTURE_LOCATION,
				this.textureWidth, this.textureHeight, this::openSelectTab,
				(btn, ps, i0, i2) -> this.hovered = this.player.inventoryMenu.getSlot(this.equipButtons.get(btn)).getItem(), TextComponent.EMPTY);
		armor2.visible = !this.itemSelect;
		this.equipButtons.put(armor2, 6);
		this.addRenderableWidget(armor2);
		ImageButton armor3 = new ImageButton(x + 10, y + 58, buttonwidth, buttonheight, 297, 209, 22, TEXTURE_LOCATION,
				this.textureWidth, this.textureHeight, this::openSelectTab,
				(btn, ps, i0, i2) -> this.hovered = this.player.inventoryMenu.getSlot(this.equipButtons.get(btn)).getItem(), TextComponent.EMPTY);
		armor2.visible = !this.itemSelect;
		this.equipButtons.put(armor3, 7);
		this.addRenderableWidget(armor3);
		ImageButton armor4 = new ImageButton(x + 30, y + 58, buttonwidth, buttonheight, 297, 209, 22, TEXTURE_LOCATION,
				this.textureWidth, this.textureHeight, this::openSelectTab,
				(btn, ps, i0, i2) -> this.hovered = this.player.inventoryMenu.getSlot(this.equipButtons.get(btn)).getItem(), TextComponent.EMPTY);
		armor4.visible = !this.itemSelect;
		this.equipButtons.put(armor4, 8);
		this.addRenderableWidget(armor4);
		
		// Items Tab
		x = this.width / 2 - 159;
		y = this.height / 2 - 40;
		this.itemButtons.clear();
		for (int i = 0; i < 5; i++)
		{
			for (int s = 0; s < 5; s++)
			{
				ImageButton itemBtn = new ImageButton(x + 20 * s, y + 23 * i, buttonwidth, buttonheight, 277, 209, 22, TEXTURE_LOCATION,
						this.textureWidth, this.textureHeight, this::closeSelectTab,
						(btn, ps, i0, i2) ->
				{
					int btnIndex = this.itemButtons.indexOf(btn);
					this.hovered = btnIndex < this.shownInvItems.size() ? this.shownInvItems.get(btnIndex).getItem() : null;
				}, TextComponent.EMPTY);
				itemBtn.visible = this.itemSelect;
				this.itemButtons.add(itemBtn);
				this.addRenderableWidget(itemBtn);
			}
		}
		
		ClientManager.INSTANCE.mainCamera.forceShoulderSurf(true);
	}
	
	private void openSelectTab(Button btn)
	{
		this.selected = this.player.inventoryMenu.getSlot(this.equipButtons.get(btn));
		for (ImageButton b : this.equipButtons.keySet()) b.visible = false;
		
		this.shownInvItems.clear();
		for (Slot slot : this.invItems)
		{
			if (slot.hasItem() && slot.mayPickup(this.player) && this.selected.mayPlace(slot.getItem())
				&& this.selected.mayPickup(this.player) && slot.mayPlace(this.selected.getItem()))
			{
				this.shownInvItems.add(slot);
			}
		}
		for (int i = 0; i < this.itemButtons.size(); i++)
		{
			ImageButton b = this.itemButtons.get(i);
			b.visible = i < this.shownInvItems.size();
		}
		this.itemSelect = true;
	}
	
	private void closeSelectTab(Button btn)
	{
		Slot slot = this.shownInvItems.get(this.itemButtons.indexOf(btn));
		if (slot == this.selected)
		{
			for (int i = 9; i < 36; i++)
			{
				Slot freeSlot = this.player.inventoryMenu.getSlot(i);
				if (!freeSlot.hasItem())
				{
					freeSlot.set(this.selected.getItem());
					freeSlot.setChanged();
					this.selected.set(ItemStack.EMPTY);
					this.selected.setChanged();
					break;
				}
			}
		}
		else
		{
			ItemStack itemStack = this.selected.getItem();
			this.selected.set(slot.getItem());
			this.selected.setChanged();
			slot.set(itemStack);
			slot.setChanged();
		}
		this.player.inventoryMenu.broadcastChanges();
		for (ImageButton b : this.itemButtons) b.visible = false;
		for (ImageButton b : this.equipButtons.keySet()) b.visible = true;
		this.itemSelect = false;
	}
	
	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialticks)
	{
		this.renderBg(poseStack, partialticks, mouseX, mouseY);
		
		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		
		poseStack.pushPose();
		float scale = 0.7F;
		poseStack.scale(scale, scale, 1.0F);
		x /= scale;
		y /= scale;
		this.font.draw(poseStack, this.title, x + 27, y + 8, 16777215);
		poseStack.popPose();
		
		if (this.hovered != null && !this.hovered.isEmpty())
		{
			poseStack.pushPose();
			scale = 0.5F;
			poseStack.scale(scale, scale, 1.0F);
			x = Math.round(((this.width - this.imageWidth) / 2 + 130) / scale);
			y = Math.round(((this.height - this.imageHeight) / 2 + 24) / scale);
			this.font.draw(poseStack, this.hovered.getHoverName(), x, y, 16777215);
			
			ItemCapability itemCap = this.hovered.getCapability(ModCapabilities.CAPABILITY_ITEM).orElse(null);
			
			if (itemCap instanceof WeaponCap weaponCap)
			{
				String category = "";
				for (String s : weaponCap.getWeaponCategory().toString().split("_")) category += s.substring(0, 1).toUpperCase()+s.substring(1)+" ";
				this.font.draw(poseStack, category, x, y + 11 / scale, 16777215);
				this.font.draw(poseStack, new TranslatableComponent("attribute.darksouls.weight").getString()+"             "+weaponCap.weight, x, y + 19 / scale, 16777215);
				
				int yOffset = 47;
				int numberOff = 32;
				int incr = 1;
				int indivOff = 6;
				this.font.draw(poseStack, "Attack power", x, y + yOffset / scale, 16777215);
				this.font.draw(poseStack, "\u00A77Physical", x, y + (yOffset + indivOff * incr) / scale, 16777215);
				this.drawStringBackFormat(poseStack, "\u00A77" + weaponCap.getDamage(CoreDamageType.PHYSICAL), x + numberOff / scale, y + (yOffset + indivOff * incr++) / scale, 16777215);
				this.font.draw(poseStack, "\u00A73Magic", x, y + (yOffset + indivOff * incr) / scale, 16777215);
				this.drawStringBackFormat(poseStack, "\u00A73" + weaponCap.getDamage(CoreDamageType.MAGIC), x + numberOff / scale, y + (yOffset + indivOff * incr++) / scale, 16777215);
				this.font.draw(poseStack, "\u00A7cFire", x, y + (yOffset + indivOff * incr) / scale, 16777215);
				this.drawStringBackFormat(poseStack, "\u00A7c" + weaponCap.getDamage(CoreDamageType.FIRE), x + numberOff / scale, y + (yOffset + indivOff * incr++) / scale, 16777215);
				this.font.draw(poseStack, "\u00A7eLightning", x, y + (yOffset + indivOff * incr) / scale, 16777215);
				this.drawStringBackFormat(poseStack, "\u00A7e" + weaponCap.getDamage(CoreDamageType.LIGHTNING), x + numberOff / scale, y + (yOffset + indivOff * incr++) / scale, 16777215);
				this.font.draw(poseStack, "\u00A76Holy", x, y + (yOffset + indivOff * incr) / scale, 16777215);
				this.drawStringBackFormat(poseStack, "\u00A76" + weaponCap.getDamage(CoreDamageType.HOLY), x + numberOff / scale, y + (yOffset + indivOff * incr++) / scale, 16777215);
				this.font.draw(poseStack, "\u00A75Dark", x, y + (yOffset + indivOff * incr) / scale, 16777215);
				this.drawStringBackFormat(poseStack, "\u00A75" + weaponCap.getDamage(CoreDamageType.DARK), x + numberOff / scale, y + (yOffset + indivOff * incr++) / scale, 16777215);
				this.font.draw(poseStack, "Critical", x, y + (yOffset + indivOff * incr) / scale, 16777215);
				this.drawStringBackFormat(poseStack, Math.round(weaponCap.getCritical() * 100) + "%", x + numberOff / scale, y + (yOffset + indivOff * incr++) / scale, 16777215);
				
				Shield shield = weaponCap instanceof Shield ? (Shield)weaponCap : Shield.EMPTY_SHIELD;
				
				int xOffset = 55;
				numberOff = 37;
				incr = 1;
				this.font.draw(poseStack, "Guard absorbtion", x + xOffset / scale, y + yOffset / scale, 16777215);
				this.font.draw(poseStack, "\u00A77Physical", x + xOffset / scale, y + (yOffset + indivOff * incr) / scale, 16777215);
				this.drawStringBackFormat(poseStack, "\u00A77" + Math.round(shield.getDefense(CoreDamageType.PHYSICAL) * 100) + "%", x + (xOffset + numberOff) / scale, y + (yOffset + indivOff * incr++) / scale, 16777215);
				this.font.draw(poseStack, "\u00A73Magic", x + xOffset / scale, y + (yOffset + indivOff * incr) / scale, 16777215);
				this.drawStringBackFormat(poseStack, "\u00A73" + Math.round(shield.getDefense(CoreDamageType.MAGIC) * 100) + "%", x + (xOffset + numberOff) / scale, y + (yOffset + indivOff * incr++) / scale, 16777215);
				this.font.draw(poseStack, "\u00A7cFire", x + xOffset / scale, y + (yOffset + indivOff * incr) / scale, 16777215);
				this.drawStringBackFormat(poseStack, "\u00A7c" + Math.round(shield.getDefense(CoreDamageType.FIRE) * 100) + "%", x + (xOffset + numberOff) / scale, y + (yOffset + indivOff * incr++) / scale, 16777215);
				this.font.draw(poseStack, "\u00A7eLightning", x + xOffset / scale, y + (yOffset + indivOff * incr) / scale, 16777215);
				this.drawStringBackFormat(poseStack, "\u00A7e" + Math.round(shield.getDefense(CoreDamageType.LIGHTNING) * 100) + "%", x + (xOffset + numberOff) / scale, y + (yOffset + indivOff * incr++) / scale, 16777215);
				this.font.draw(poseStack, "\u00A76Holy", x + xOffset / scale, y + (yOffset + indivOff * incr) / scale, 16777215);
				this.drawStringBackFormat(poseStack, "\u00A76" + Math.round(shield.getDefense(CoreDamageType.HOLY) * 100) + "%", x + (xOffset + numberOff) / scale, y + (yOffset + indivOff * incr++) / scale, 16777215);
				this.font.draw(poseStack, "\u00A75Dark", x + xOffset / scale, y + (yOffset + indivOff * incr) / scale, 16777215);
				this.drawStringBackFormat(poseStack, "\u00A75" + Math.round(shield.getDefense(CoreDamageType.DARK) * 100) + "%", x + (xOffset + numberOff) / scale, y + (yOffset + indivOff * incr++) / scale, 16777215);
				this.font.draw(poseStack, "Stability", x + xOffset / scale, y + (yOffset + indivOff * incr) / scale, 16777215);
				this.drawStringBackFormat(poseStack, Math.round(shield.getStability() * 100) + "%", x + (xOffset + numberOff) / scale, y + (yOffset + indivOff * incr++) / scale, 16777215);
			
				yOffset = 99;
				this.font.draw(poseStack, "Stat bonus", x, y + yOffset / scale, 16777215);
				yOffset += 7;
				incr = 0;
				indivOff = 26;
				numberOff = 16;
				this.font.draw(poseStack, "Str", x + (indivOff * incr) / scale, y + yOffset / scale, 16777215);
				this.drawStringBackFormat(poseStack, weaponCap.getScaling(Stats.STRENGTH).toString(), x + (numberOff + indivOff * incr++) / scale, y + yOffset / scale, 16777215);
				this.font.draw(poseStack, "Dex", x + (indivOff * incr) / scale, y + yOffset / scale, 16777215);
				this.drawStringBackFormat(poseStack, weaponCap.getScaling(Stats.DEXTERITY).toString(), x + (numberOff + indivOff * incr++) / scale, y + yOffset / scale, 16777215);
				
				this.font.draw(poseStack, "Int", x + (indivOff * incr) / scale, y + yOffset / scale, 16777215);
				this.drawStringBackFormat(poseStack, weaponCap.getScaling(Stats.INTELLIGENCE).toString(), x + (numberOff + indivOff * incr++) / scale, y + yOffset / scale, 16777215);
				this.font.draw(poseStack, "Fth", x + (indivOff * incr) / scale, y + yOffset / scale, 16777215);
				this.drawStringBackFormat(poseStack, weaponCap.getScaling(Stats.FAITH).toString(), x + (numberOff + indivOff * incr++) / scale, y + yOffset / scale, 16777215);
				
				yOffset = 116;
				this.font.draw(poseStack, "Stat requirement", x, y + yOffset / scale, 16777215);
				yOffset += 7;
				incr = 0;
				indivOff = 26;
				numberOff = 16;
				this.font.draw(poseStack, "Str", x + (indivOff * incr) / scale, y + yOffset / scale, 16777215);
				this.drawStringBackFormat(poseStack, weaponCap.getStatStringValue(Stats.STRENGTH, this.playerCap).toString(), x + (numberOff + indivOff * incr++) / scale, y + yOffset / scale, 16777215);
				this.font.draw(poseStack, "Dex", x + (indivOff * incr) / scale, y + yOffset / scale, 16777215);
				this.drawStringBackFormat(poseStack, weaponCap.getStatStringValue(Stats.DEXTERITY, this.playerCap).toString(), x + (numberOff + indivOff * incr++) / scale, y + yOffset / scale, 16777215);
				
				this.font.draw(poseStack, "Int", x + (indivOff * incr) / scale, y + yOffset / scale, 16777215);
				this.drawStringBackFormat(poseStack, weaponCap.getStatStringValue(Stats.INTELLIGENCE, this.playerCap).toString(), x + (numberOff + indivOff * incr++) / scale, y + yOffset / scale, 16777215);
				this.font.draw(poseStack, "Fth", x + (indivOff * incr) / scale, y + yOffset / scale, 16777215);
				this.drawStringBackFormat(poseStack, weaponCap.getStatStringValue(Stats.FAITH, this.playerCap).toString(), x + (numberOff + indivOff * incr++) / scale, y + yOffset / scale, 16777215);
			}
			else
			{
				int yOffset = 55;
				int incr = 0;
				List<Component> tooltip = this.hovered.getTooltipLines(this.player, TooltipFlag.Default.NORMAL);
				tooltip.remove(0);
				for (Component c : tooltip)
				{
					
					if (this.font.width(c.getString()) > 10)
					{
						for (FormattedCharSequence s : this.font.split(c, 200))
						{
							this.font.draw(poseStack, s, x, y + (yOffset + 8 * incr++) / scale, 16777215);
						}
					}
					else this.font.draw(poseStack, c, x, y + (yOffset + 8 * incr++) / scale, 16777215);
				}
			}
			
			poseStack.popPose();
			
			RenderSystem.getModelViewStack().pushPose();
			scale = 1.75F;
			RenderSystem.getModelViewStack().scale(scale, scale, 1.0F);
			x = Math.round(((this.width - this.imageWidth) / 2 + 190) / scale);
			y = Math.round(((this.height - this.imageHeight) / 2 + 32) / scale);
			this.itemRenderer.renderAndDecorateFakeItem(this.hovered, x, y);
			RenderSystem.getModelViewStack().popPose();
			RenderSystem.applyModelViewMatrix();
		}
		
		poseStack.popPose();
		
		super.render(poseStack, mouseX, mouseY, partialticks);
		
		if (this.itemSelect)
		{
			for (int i = 0; i < this.itemButtons.size() && i < this.shownInvItems.size(); i++)
			{
				ImageButton btn = this.itemButtons.get(i);
				int btnX = btn.x + 1;
				int btnY = btn.y + 2;
				Slot slot = this.shownInvItems.get(i);
				ItemStack item = slot.getItem();
				this.itemRenderer.renderAndDecorateFakeItem(item, btnX, btnY);
				this.itemRenderer.renderGuiItemDecorations(this.font, item, btnX, btnY);
				if (this.selected == slot)
				{
					RenderSystem.setShader(GameRenderer::getPositionTexShader);
				    RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
				    int buttonwidth = 19;
					int buttonheight = 22;
				    blit(poseStack, btnX, btnY, 258, 211, buttonwidth, buttonheight, this.textureWidth, this.textureHeight);
				}
			}
		}
		else
		{
			this.equipButtons.forEach((btn, i) ->
			{
				int btnX = btn.x + 1;
				int btnY = btn.y + 2;
				this.itemRenderer.renderAndDecorateFakeItem(this.player.inventoryMenu.getSlot(i).getItem(), btnX, btnY);
				this.itemRenderer.renderGuiItemDecorations(this.font, this.player.inventoryMenu.getSlot(i).getItem(), btnX, btnY);
			});
		}
	}
	
	private void drawStringBackFormat(PoseStack poseStack, String string, float x, float y, int color)
	{
		x -= this.font.width(string);
		this.font.draw(poseStack, string, x, y, color);
	}
	
	@Override
	public void onClose()
	{
		super.onClose();
		ClientManager.INSTANCE.mainCamera.forceShoulderSurf(false);
	}
	
	public boolean isPauseScreen()
	{
		return false;
	}
	
	private void renderBg(PoseStack poseStack, float partialticks, int mouseX, int mouseY)
	{
		RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		GuiComponent.blit(poseStack, x, y, 0, 0, this.imageWidth, this.imageHeight, this.textureWidth, this.textureHeight);
		
		if (this.itemSelect)
		{
			GuiComponent.blit(poseStack, x + 1, y + 15, 1, 201, 115, 184, this.textureWidth, this.textureHeight);
		}
		
		if (this.hovered != null && !this.hovered.isEmpty())
		{
			ItemCapability itemCap = this.hovered.getCapability(ModCapabilities.CAPABILITY_ITEM).orElse(null);
			if (!(itemCap instanceof WeaponCap))
			{
				GuiComponent.blit(poseStack, x + 116, y + 15, 116, 201, 115, 184, this.textureWidth, this.textureHeight);
			}
		}
	}
	
	@Override
	public boolean keyPressed(int p_96552_, int p_96553_, int p_96554_)
	{
		InputConstants.Key key = InputConstants.getKey(p_96552_, p_96553_);
		if (this.minecraft.options.keyInventory.isActiveAndMatches(key))
		{
			this.onClose();
			return true;
		}
		else return super.keyPressed(p_96552_, p_96553_, p_96554_);
	}
}
