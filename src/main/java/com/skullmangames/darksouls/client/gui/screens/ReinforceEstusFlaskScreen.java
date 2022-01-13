package com.skullmangames.darksouls.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.inventory.container.ReinforceEstusFlaskContainer;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ReinforceEstusFlaskScreen extends AbstractContainerScreen<ReinforceEstusFlaskContainer> implements MenuAccess<ReinforceEstusFlaskContainer>
{
	private static final ResourceLocation REINFORCE_ESTUS_FLASK_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/containers/reinforce_estus_flask.png");
	
	public ReinforceEstusFlaskScreen(ReinforceEstusFlaskContainer p_i232291_1_, Inventory p_i232291_2_, Component p_i232291_3_)
	{
		super(p_i232291_1_, p_i232291_2_, p_i232291_3_);
		this.titleLabelX = 10;
	    this.titleLabelY = 18;
	}
	
	@Override
	public void render(PoseStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
	{
	   this.renderBackground(p_230430_1_);
	   super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
	   RenderSystem.disableBlend();
	   this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
	}

	@Override
	protected void renderBg(PoseStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_)
	{
	    this.minecraft.getTextureManager().bindForSetup(REINFORCE_ESTUS_FLASK_LOCATION);
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
	protected void renderLabels(PoseStack p_230451_1_, int p_230451_2_, int p_230451_3_)
	{
		RenderSystem.disableBlend();
		super.renderLabels(p_230451_1_, p_230451_2_, p_230451_3_);
	}
}
