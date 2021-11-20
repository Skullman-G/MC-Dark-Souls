package com.skullmangames.darksouls.client.gui.screens;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModWorldSelectionScreen extends Screen
{
	protected final Screen lastScreen;
	private List<IReorderingProcessor> toolTip;
	private Button deleteButton;
	private Button selectButton;
	private Button renameButton;
	private Button copyButton;
	protected TextFieldWidget searchBox;
	private ModWorldSelectionList list;

	public ModWorldSelectionScreen(Screen p_i46592_1_)
	{
		super(new TranslationTextComponent("selectWorld.title"));
		this.lastScreen = p_i46592_1_;
	}

	public boolean mouseScrolled(double p_231043_1_, double p_231043_3_, double p_231043_5_)
	{
		return super.mouseScrolled(p_231043_1_, p_231043_3_, p_231043_5_);
	}

	public void tick()
	{
		this.searchBox.tick();
	}

	protected void init()
	{
		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
		this.searchBox = new TextFieldWidget(this.font, this.width / 2 - 100, 22, 200, 20, this.searchBox,
				new TranslationTextComponent("selectWorld.search"));
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
		this.children.add(this.searchBox);
		this.children.add(this.list);
		this.selectButton = this.addButton(new Button(this.width / 2 - 154, this.height - 52, 150, 20,
				new TranslationTextComponent("selectWorld.select"), (p_214325_1_) ->
				{
					this.list.getSelectedOpt().ifPresent(ModWorldSelectionList.Entry::joinWorld);
				}));
		this.addButton(new Button(this.width / 2 + 4, this.height - 52, 150, 20,
				new TranslationTextComponent("selectWorld.create"), (p_214326_1_) ->
				{
					this.minecraft.setScreen(CreateWorldScreen.create(this));
				}));
		this.renameButton = this.addButton(new Button(this.width / 2 - 154, this.height - 28, 72, 20,
				new TranslationTextComponent("selectWorld.edit"), (p_214323_1_) ->
				{
					this.list.getSelectedOpt().ifPresent(ModWorldSelectionList.Entry::editWorld);
				}));
		this.deleteButton = this.addButton(new Button(this.width / 2 - 76, this.height - 28, 72, 20,
				new TranslationTextComponent("selectWorld.delete"), (p_214330_1_) ->
				{
					this.list.getSelectedOpt().ifPresent(ModWorldSelectionList.Entry::deleteWorld);
				}));
		this.copyButton = this.addButton(new Button(this.width / 2 + 4, this.height - 28, 72, 20,
				new TranslationTextComponent("selectWorld.recreate"), (p_214328_1_) ->
				{
					this.list.getSelectedOpt().ifPresent(ModWorldSelectionList.Entry::recreateWorld);
				}));
		this.addButton(
				new Button(this.width / 2 + 82, this.height - 28, 72, 20, DialogTexts.GUI_CANCEL, (p_214327_1_) ->
				{
					this.minecraft.setScreen(this.lastScreen);
				}));
		this.updateButtonStatus(false);
		this.setInitialFocus(this.searchBox);
	}

	public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_)
	{
		return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_) ? true
				: this.searchBox.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
	}

	public void onClose()
	{
		this.minecraft.setScreen(this.lastScreen);
	}

	public boolean charTyped(char p_231042_1_, int p_231042_2_)
	{
		return this.searchBox.charTyped(p_231042_1_, p_231042_2_);
	}

	public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
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

	public void setToolTip(List<IReorderingProcessor> p_239026_1_)
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

	public void removed()
	{
		if (this.list != null)
		{
			this.list.children().forEach(ModWorldSelectionList.Entry::close);
		}

	}
}
