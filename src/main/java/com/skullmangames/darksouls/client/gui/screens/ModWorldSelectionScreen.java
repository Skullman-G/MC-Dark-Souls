package com.skullmangames.darksouls.client.gui.screens;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModWorldSelectionScreen extends Screen
{
	protected final Screen lastScreen;
	private List<FormattedCharSequence> toolTip;
	private Button deleteButton;
	private Button selectButton;
	private Button renameButton;
	private Button copyButton;
	protected EditBox searchBox;
	private ModWorldSelectionList list;

	public ModWorldSelectionScreen(Screen p_i46592_1_)
	{
		super(new TranslatableComponent("selectWorld.title"));
		this.lastScreen = p_i46592_1_;
	}

	@Override
	public boolean mouseScrolled(double p_231043_1_, double p_231043_3_, double p_231043_5_)
	{
		return super.mouseScrolled(p_231043_1_, p_231043_3_, p_231043_5_);
	}

	@Override
	public void tick()
	{
		this.searchBox.tick();
	}

	@Override
	protected void init()
	{
		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
		this.searchBox = new EditBox(this.font, this.width / 2 - 100, 22, 200, 20, this.searchBox,
				new TranslatableComponent("selectWorld.search"));
		this.searchBox.setResponder((p_214329_1_) ->
		{
			this.list.refreshList(() ->
			{
				return p_214329_1_;
			}, false);
		});
		this.list = new ModWorldSelectionList(this, this.minecraft, this.width, this.height, 48, this.height - 64, 36,
				() ->
				{
					return this.searchBox.getValue();
				}, this.list);
		this.addWidget(this.searchBox);
		this.addWidget(this.list);
		this.selectButton = this.addRenderableWidget(new Button(this.width / 2 - 154, this.height - 52, 150, 20,
				new TranslatableComponent("selectWorld.select"), (p_214325_1_) ->
				{
					this.list.getSelectedOpt().ifPresent(ModWorldSelectionList.Entry::joinWorld);
				}));
		this.addRenderableWidget(new Button(this.width / 2 + 4, this.height - 52, 150, 20,
				new TranslatableComponent("selectWorld.create"), (p_214326_1_) ->
				{
					this.minecraft.setScreen(CreateWorldScreen.createFresh(this));
				}));
		this.renameButton = this.addRenderableWidget(new Button(this.width / 2 - 154, this.height - 28, 72, 20,
				new TranslatableComponent("selectWorld.edit"), (p_214323_1_) ->
				{
					this.list.getSelectedOpt().ifPresent(ModWorldSelectionList.Entry::editWorld);
				}));
		this.deleteButton = this.addRenderableWidget(new Button(this.width / 2 - 76, this.height - 28, 72, 20,
				new TranslatableComponent("selectWorld.delete"), (p_214330_1_) ->
				{
					this.list.getSelectedOpt().ifPresent(ModWorldSelectionList.Entry::deleteWorld);
				}));
		this.copyButton = this.addRenderableWidget(new Button(this.width / 2 + 4, this.height - 28, 72, 20,
				new TranslatableComponent("selectWorld.recreate"), (p_214328_1_) ->
				{
					this.list.getSelectedOpt().ifPresent(ModWorldSelectionList.Entry::recreateWorld);
				}));
		this.addRenderableWidget(
				new Button(this.width / 2 + 82, this.height - 28, 72, 20, CommonComponents.GUI_CANCEL, (p_214327_1_) ->
				{
					this.minecraft.setScreen(this.lastScreen);
				}));
		this.updateButtonStatus(false);
		this.setInitialFocus(this.searchBox);
	}

	@Override
	public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_)
	{
		return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_) ? true
				: this.searchBox.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
	}

	@Override
	public void onClose()
	{
		this.minecraft.setScreen(this.lastScreen);
	}

	@Override
	public boolean charTyped(char p_231042_1_, int p_231042_2_)
	{
		return this.searchBox.charTyped(p_231042_1_, p_231042_2_);
	}

	@Override
	public void render(PoseStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
	{
		this.toolTip = null;
		this.list.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		this.searchBox.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 8, 16777215);
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		if (this.toolTip != null)
		{
			this.renderTooltip(p_230430_1_, this.toolTip, p_230430_2_, p_230430_3_);
		}

	}

	public void setToolTip(List<FormattedCharSequence> p_239026_1_)
	{
		this.toolTip = p_239026_1_;
	}

	public void updateButtonStatus(boolean p_214324_1_)
	{
		this.selectButton.active = p_214324_1_;
		this.deleteButton.active = p_214324_1_;
		this.renameButton.active = p_214324_1_;
		this.copyButton.active = p_214324_1_;
	}

	@Override
	public void removed()
	{
		if (this.list != null)
		{
			this.list.children().forEach(ModWorldSelectionList.Entry::close);
		}

	}
}
