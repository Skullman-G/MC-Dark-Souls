package com.skullmangames.darksouls.client.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.skullmangames.darksouls.common.containers.SmithingTableContainerOverride;

import net.minecraft.client.gui.screen.inventory.AbstractRepairScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SmithingTableScreenOverride extends AbstractRepairScreen<SmithingTableContainerOverride>
{
   private static final ResourceLocation SMITHING_LOCATION = new ResourceLocation("textures/gui/container/smithing.png");

   public SmithingTableScreenOverride(SmithingTableContainerOverride p_i232294_1_, PlayerInventory p_i232294_2_, ITextComponent p_i232294_3_)
   {
      super(p_i232294_1_, p_i232294_2_, p_i232294_3_, SMITHING_LOCATION);
      this.titleLabelX = 60;
      this.titleLabelY = 18;
   }

   protected void renderLabels(MatrixStack p_230451_1_, int p_230451_2_, int p_230451_3_)
   {
      RenderSystem.disableBlend();
      super.renderLabels(p_230451_1_, p_230451_2_, p_230451_3_);
   }
}