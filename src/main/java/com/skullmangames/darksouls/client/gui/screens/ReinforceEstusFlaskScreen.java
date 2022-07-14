package com.skullmangames.darksouls.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.inventory.container.ReinforceEstusFlaskContainer;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ReinforceEstusFlaskScreen extends ContainerScreen<ReinforceEstusFlaskContainer> implements IContainerListener
{
	private static final ResourceLocation REINFORCE_ESTUS_FLASK_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/containers/reinforce_estus_flask.png");
	
	public ReinforceEstusFlaskScreen(ReinforceEstusFlaskContainer container, PlayerInventory inventory, ITextComponent title)
	{
		super(container, inventory, title);
		this.titleLabelX = 10;
	    this.titleLabelY = 18;
	}
	
	@Override
	public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTicks)
	{
	   this.renderBackground(poseStack);
	   super.render(poseStack, mouseX, mouseY, partialTicks);
	   RenderSystem.disableBlend();
	   this.renderTooltip(poseStack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(MatrixStack poseStack, float partialTicks, int mouseX, int mouseY)
	{
	    minecraft.getTextureManager().bind(REINFORCE_ESTUS_FLASK_LOCATION);
	    int x = (this.width - this.imageWidth) / 2;
	    int y = (this.height - this.imageHeight) / 2;
	    this.blit(poseStack, x, y, 0, 0, this.imageWidth, this.imageHeight);
	    this.blit(poseStack, x + 59, y + 20, 0, this.imageHeight + (this.menu.getSlot(0).hasItem() ? 0 : 16), 110, 16);
	    if ((this.menu.getSlot(0).hasItem() || this.menu.getSlot(1).hasItem()) && !this.menu.getSlot(2).hasItem())
	    {
	       this.blit(poseStack, x + 99, y + 45, this.imageWidth, 0, 28, 21);
	    }
	}
	
	@Override
	protected void renderLabels(MatrixStack poseStack, int mouseX, int mouseY)
	{
		RenderSystem.disableBlend();
		super.renderLabels(poseStack, mouseX, mouseY);
	}

	@Override
	public void refreshContainer(Container p_71110_1_, NonNullList<ItemStack> p_71110_2_)
	{
		this.slotChanged(p_71110_1_, 0, p_71110_1_.getSlot(0).getItem());
	}

	@Override
	public void slotChanged(Container p_71111_1_, int p_71111_2_, ItemStack p_71111_3_)
	{
		
	}

	@Override
	public void setContainerData(Container p_71112_1_, int p_71112_2_, int p_71112_3_)
	{
		
	}
	
	@Override
	protected void init()
	{
		super.init();
		this.menu.addSlotListener(this);
	}
	
	@Override
	public void removed()
	{
		super.removed();
		this.menu.removeSlotListener(this);
	}
}
