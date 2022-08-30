package com.skullmangames.darksouls.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.common.inventory.SoulMerchantMenu;
import com.skullmangames.darksouls.common.inventory.SoulMerchantOffer;
import com.skullmangames.darksouls.common.inventory.SoulMerchantOffers;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSSelectTrade;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoulMerchantScreen extends AbstractContainerScreen<SoulMerchantMenu>
{
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/containers/shop.png");
	private static final Component TRADES_LABEL = new TranslatableComponent("merchant.trades");
	private int shopItem;
	private final TradeOfferButton[] tradeOfferButtons = new TradeOfferButton[7];
	private int scrollOff;
	private boolean isDragging;
	private final LocalPlayerCap playerCap;
	
	public SoulMerchantScreen(SoulMerchantMenu menu, Inventory inventory, Component component)
	{
		super(menu, inventory, component);
		this.imageWidth = 276;
		this.inventoryLabelX = 107;
		this.playerCap = ClientManager.INSTANCE.getPlayerCap();
	}
	
	private void postButtonClick()
	{
		this.menu.setSelectionHint(this.shopItem);
		ModNetworkManager.sendToServer(new CTSSelectTrade(this.shopItem));
	}

	protected void init()
	{
		super.init();
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		int k = j + 16 + 2;

		for (int l = 0; l < 7; ++l)
		{
			this.tradeOfferButtons[l] = this
					.addRenderableWidget(new TradeOfferButton(i + 5, k, l, (button) ->
					{
						if (button instanceof TradeOfferButton)
						{
							this.shopItem = ((TradeOfferButton) button).getIndex() + this.scrollOff;
							this.postButtonClick();
						}
					}));
			k += 20;
		}
	}

	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY)
	{
		this.font.draw(poseStack, this.title, (float) (49 + this.imageWidth / 2 - this.font.width(this.title) / 2), 6.0F, 4210752);

		this.font.draw(poseStack, this.playerInventoryTitle, (float) this.inventoryLabelX, (float) this.inventoryLabelY, 4210752);
		int l = this.font.width(TRADES_LABEL);
		this.font.draw(poseStack, TRADES_LABEL, (float) (5 - l / 2 + 48), 6.0F, 4210752);
	}

	protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY)
	{
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		blit(poseStack, i, j, this.getBlitOffset(), 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 256);
		SoulMerchantOffers offers = this.menu.getOffers();
		if (!offers.isEmpty())
		{
			int k = this.shopItem;
			if (k < 0 || k >= offers.size()) return;
		}
	}

	private void renderScroller(PoseStack poseStack, int mouseX, int mouseY, SoulMerchantOffers offers)
	{
		int i = offers.size() + 1 - 7;
		if (i > 1)
		{
			int j = 139 - (27 + (i - 1) * 139 / i);
			int k = 1 + j / i + 139 / i;
			int i1 = Math.min(113, this.scrollOff * k);
			if (this.scrollOff == i - 1)
			{
				i1 = 113;
			}

			blit(poseStack, mouseX + 94, mouseY + 18 + i1, this.getBlitOffset(), 0.0F, 199.0F, 6, 27, 512, 256);
		}
		else
		{
			blit(poseStack, mouseX + 94, mouseY + 18, this.getBlitOffset(), 6.0F, 199.0F, 6, 27, 512, 256);
		}

	}

	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
	{
		this.renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTicks);
		
		int x = this.width / 2 - this.imageWidth / 2;
		int y = this.height / 2 - this.imageHeight / 2;
		TextComponent souls = new TextComponent(String.valueOf(this.playerCap.getSouls()));
		int l0 = this.font.width(souls);
		this.font.draw(poseStack, souls, (float)(x + 136 - l0 / 2), (float)(y + 40), 4210752);
		
		SoulMerchantOffers merchantoffers = this.menu.getOffers();
		if (!merchantoffers.isEmpty())
		{
			int i = (this.width - this.imageWidth) / 2;
			int j = (this.height - this.imageHeight) / 2;
			int k = j + 16 + 1;
			int l = i + 5 + 5;
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
			this.renderScroller(poseStack, i, j, merchantoffers);
			int i1 = 0;

			for (SoulMerchantOffer merchantoffer : merchantoffers)
			{
				if (this.canScroll(merchantoffers.size()) && (i1 < this.scrollOff || i1 >= 7 + this.scrollOff))
				{
					++i1;
				}
				else
				{
					ItemStack cost = new ItemStack(ModItems.SOUL_OF_A_LOST_UNDEAD.get(), merchantoffer.getCost());
					ItemStack result = merchantoffer.getResult();
					this.itemRenderer.blitOffset = 100.0F;
					int j1 = k + 2;
					this.itemRenderer.renderAndDecorateFakeItem(cost, l, j1);
					this.itemRenderer.renderGuiItemDecorations(this.font, cost, l, j1);

					this.renderButtonArrows(poseStack, merchantoffer, i, j1);
					this.itemRenderer.renderAndDecorateFakeItem(result, i + 5 + 68, j1);
					this.itemRenderer.renderGuiItemDecorations(this.font, result, i + 5 + 68, j1);
					this.itemRenderer.blitOffset = 0.0F;
					k += 20;
					++i1;
				}
			}

			for (TradeOfferButton button : this.tradeOfferButtons)
			{
				if (button.isHoveredOrFocused())
				{
					button.renderToolTip(poseStack, mouseX, mouseY);
				}

				button.visible = button.index < this.menu.getOffers().size();
			}

			RenderSystem.enableDepthTest();
		}

		this.renderTooltip(poseStack, mouseX, mouseY);
	}

	private void renderButtonArrows(PoseStack poseStack, SoulMerchantOffer offer, int x, int y)
	{
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
		blit(poseStack, x + 5 + 35 + 20, y + 3, this.getBlitOffset(), 15.0F, 171.0F, 10, 9, 512, 256);

	}

	private boolean canScroll(int p_99141_)
	{
		return p_99141_ > 7;
	}

	public boolean mouseScrolled(double p_99127_, double p_99128_, double p_99129_)
	{
		int i = this.menu.getOffers().size();
		if (this.canScroll(i))
		{
			int j = i - 7;
			this.scrollOff = Mth.clamp((int) ((double) this.scrollOff - p_99129_), 0, j);
		}

		return true;
	}

	public boolean mouseDragged(double p_99135_, double p_99136_, int p_99137_, double p_99138_, double p_99139_)
	{
		int i = this.menu.getOffers().size();
		if (this.isDragging)
		{
			int j = this.topPos + 18;
			int k = j + 139;
			int l = i - 7;
			float f = ((float) p_99136_ - (float) j - 13.5F) / ((float) (k - j) - 27.0F);
			f = f * (float) l + 0.5F;
			this.scrollOff = Mth.clamp((int) f, 0, l);
			return true;
		}
		else
		{
			return super.mouseDragged(p_99135_, p_99136_, p_99137_, p_99138_, p_99139_);
		}
	}

	public boolean mouseClicked(double p_99131_, double p_99132_, int p_99133_)
	{
		this.isDragging = false;
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		if (this.canScroll(this.menu.getOffers().size()) && p_99131_ > (double) (i + 94)
				&& p_99131_ < (double) (i + 94 + 6) && p_99132_ > (double) (j + 18)
				&& p_99132_ <= (double) (j + 18 + 139 + 1))
		{
			this.isDragging = true;
		}

		return super.mouseClicked(p_99131_, p_99132_, p_99133_);
	}

	@OnlyIn(Dist.CLIENT)
	private class TradeOfferButton extends Button
	{
		final int index;

		public TradeOfferButton(int x, int y, int index, Button.OnPress onPress)
		{
			super(x, y, 89, 20, TextComponent.EMPTY, onPress);
			this.index = index;
			this.visible = false;
		}

		public int getIndex()
		{
			return this.index;
		}

		public void renderToolTip(PoseStack poseStack, int mouseX, int mouseY)
		{
			if (this.isHovered
					&& SoulMerchantScreen.this.menu.getOffers().size() > this.index + SoulMerchantScreen.this.scrollOff)
			{
				if (mouseX > this.x + 65)
				{
					ItemStack itemstack1 = SoulMerchantScreen.this.menu.getOffers()
							.get(this.index + SoulMerchantScreen.this.scrollOff).getResult();
					SoulMerchantScreen.this.renderTooltip(poseStack, itemstack1, mouseX, mouseY);
				}
			}
		}
	}
}
