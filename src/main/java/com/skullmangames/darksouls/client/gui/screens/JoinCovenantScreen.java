package com.skullmangames.darksouls.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.common.entity.Covenant;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSCovenant;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JoinCovenantScreen extends AbstractCovenantScreen
{
	private Button yes;
	private Button no;
	
	public JoinCovenantScreen(Covenant covenant)
	{
		super(covenant);
	}
	
	@Override
	protected void init()
	{
		super.init();
		this.yes = new Button((this.width / 2) - (this.buttonWidth / 2) - 30, this.height / 2 + 85, this.buttonWidth, this.buttonHeight, new TranslatableComponent("gui.darksouls.yes"), (button) ->
		{
			this.changeCovenant();
		});
		this.addRenderableWidget(this.yes);
		this.no = new Button((this.width / 2) - (this.buttonWidth / 2) + 30, this.height / 2 + 85, this.buttonWidth, this.buttonHeight, new TranslatableComponent("gui.darksouls.no"), (button) ->
		{
			this.onClose();
		});
		this.addRenderableWidget(this.no);
	}
	
	@Override
	protected void toggleShowProgress()
	{
		super.toggleShowProgress();
		this.yes.visible = !this.showProgress;
		this.no.visible = !this.showProgress;
	}
	
	private void changeCovenant()
	{
		ModNetworkManager.sendToServer(new CTSCovenant(this.covenant));
		this.onClose();
	}
	
	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialticks)
	{
		super.render(poseStack, mouseX, mouseY, partialticks);
		int y = (this.height - this.imageHeight) / 2;
		if (!this.showProgress) this.font.draw(poseStack, "Join Covenant?", (float) (this.width / 2 - this.font.width("Join Covenant?") / 2), y + 185, this.color);
	}
}
