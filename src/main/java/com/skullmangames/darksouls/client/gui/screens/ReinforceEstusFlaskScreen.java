package com.skullmangames.darksouls.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.inventory.ReinforceEstusFlaskMenu;

import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ReinforceEstusFlaskScreen extends ContainerScreen<ReinforceEstusFlaskMenu> implements IContainerListener
{
	private static final ResourceLocation REINFORCE_ESTUS_FLASK_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/containers/reinforce_estus_flask.png");
	
	public ReinforceEstusFlaskScreen(ReinforceEstusFlaskMenu p_i232291_1_, PlayerInventory p_i232291_2_, ITextComponent p_i232291_3_)
	{
		super(p_i232291_1_, p_i232291_2_, p_i232291_3_);
		this.titleLabelX = 10;
	    this.titleLabelY = 18;
	}
	
	@Override
	public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
	{
	   this.renderBackground(p_230430_1_);
	   super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
	   RenderSystem.disableBlend();
	   this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
	}

	@Override
	protected void renderBg(MatrixStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_)
	{
	    minecraft.getTextureManager().bind(REINFORCE_ESTUS_FLASK_LOCATION);
	    int x = (this.width - this.imageWidth) / 2;
	    int y = (this.height - this.imageHeight) / 2;
	    this.blit(p_230450_1_, x, y, 0, 0, this.imageWidth, this.imageHeight);
	    this.blit(p_230450_1_, x + 59, y + 20, 0, this.imageHeight + (this.menu.getSlot(0).hasItem() ? 0 : 16), 110, 16);
	    if ((this.menu.getSlot(0).hasItem() || this.menu.getSlot(1).hasItem()) && !this.menu.getSlot(2).hasItem())
	    {
	       this.blit(p_230450_1_, x + 99, y + 45, this.imageWidth, 0, 28, 21);
	    }
	}
	
	@Override
	protected void renderLabels(MatrixStack p_230451_1_, int p_230451_2_, int p_230451_3_)
	{
		RenderSystem.disableBlend();
		super.renderLabels(p_230451_1_, p_230451_2_, p_230451_3_);
	}

	@Override
	public void refreshContainer(Container container, NonNullList<ItemStack> p_71110_2_)
	{
		this.slotChanged(container, 0, container.getSlot(0).getItem());
	}

	@Override
	public void slotChanged(Container p_71111_1_, int p_71111_2_, ItemStack p_71111_3_)
	{
		this.getMenu().slotsChanged(this.inventory);
	}

	@Override
	public void setContainerData(Container p_71112_1_, int p_71112_2_, int p_71112_3_) {}
	
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
