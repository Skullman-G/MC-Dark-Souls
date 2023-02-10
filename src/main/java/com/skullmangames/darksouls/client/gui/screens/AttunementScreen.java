package com.skullmangames.darksouls.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.inventory.AttunementsMenu;
import com.skullmangames.darksouls.core.init.ModAttributes;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.entity.player.PlayerEntity;

public class AttunementScreen extends ContainerScreen<AttunementsMenu>
{
	private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation("textures/gui/container/generic_54.png");
	private static final ResourceLocation CONTAINER_LOCKED = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/containers/container_locked.png");
	private final PlayerEntity player;
	
	public AttunementScreen(AttunementsMenu menu, PlayerInventory inventory, ITextComponent component)
	{
		super(menu, inventory, component);
		int containerRows = menu.getRowCount();
		this.imageHeight = 114 + containerRows * 18;
		this.inventoryLabelY = this.imageHeight - 94;
		this.player = inventory.player;
	}
	
	@Override
	public void render(MatrixStack poseStack, int x, int y, float partialTicks)
	{
		this.renderBackground(poseStack);
		super.render(poseStack, x, y, partialTicks);
		
		this.renderTooltip(poseStack, x, y);
	}
	
	@SuppressWarnings("deprecation")
	public void renderFg(MatrixStack poseStack, int x, int y)
	{
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(CONTAINER_LOCKED);
		
		for (int k = (int)this.player.getAttributeValue(ModAttributes.ATTUNEMENT_SLOTS.get()); k < this.menu.slots.size(); ++k)
		{
			Slot slot = this.menu.slots.get(k);
			if (slot.container == this.menu.getContainer())
			{
				this.blit(poseStack, slot.x, slot.y, 1, 1, 17, 17);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void renderBg(MatrixStack poseStack, float partialTicks, int x, int y)
	{
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(CONTAINER_BACKGROUND);
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(poseStack, i, j, 0, 0, this.imageWidth, 18 + 17);
		this.blit(poseStack, i, j + 18 + 17, 0, 126, this.imageWidth, 96);
	}
}
