package com.skullmangames.darksouls.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.common.inventory.AttunementsMenu;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AttunementScreen extends AbstractContainerScreen<AttunementsMenu>
{
	private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation("textures/gui/container/generic_54.png");
	
	public AttunementScreen(AttunementsMenu menu, Inventory inventory, Component component)
	{
		super(menu, inventory, component);
		int containerRows = menu.getRowCount();
		this.imageHeight = 114 + containerRows * 18;
		this.inventoryLabelY = this.imageHeight - 94;
	}
	
	@Override
	public void render(PoseStack poseStack, int x, int y, float partialTicks)
	{
		this.renderBackground(poseStack);
		super.render(poseStack, x, y, partialTicks);
		this.renderTooltip(poseStack, x, y);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTicks, int x, int y)
	{
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, CONTAINER_BACKGROUND);
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(poseStack, i, j, 0, 0, this.imageWidth, 18 + 17);
		this.blit(poseStack, i, j + 18 + 17, 0, 126, this.imageWidth, 96);
	}
}
