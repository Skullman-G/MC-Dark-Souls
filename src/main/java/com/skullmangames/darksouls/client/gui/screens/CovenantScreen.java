package com.skullmangames.darksouls.client.gui.screens;

import com.skullmangames.darksouls.common.entity.Covenant;
import com.skullmangames.darksouls.common.entity.Covenants;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSCovenant;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CovenantScreen extends AbstractCovenantScreen
{
	private Button leave;
	
	public CovenantScreen(Covenant covenant)
	{
		super(covenant);
	}
	
	@Override
	protected void init()
	{
		super.init();
		this.leave = new Button((this.width / 2) - 50, this.height / 2 + 85, 100, this.buttonHeight, new TranslatableComponent("gui.darksouls.leave_covenant"), (button) ->
		{
			ModNetworkManager.sendToServer(new CTSCovenant(Covenants.NONE));
			this.onClose();
		});
		this.addRenderableWidget(this.leave);
	}
	
	@Override
	protected void toggleShowProgress()
	{
		super.toggleShowProgress();
		this.leave.visible = !this.showProgress;
	}
}
