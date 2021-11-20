package com.skullmangames.darksouls.client;

import com.skullmangames.darksouls.client.gui.ScreenManager;
import com.skullmangames.darksouls.client.input.InputManager;
import com.skullmangames.darksouls.client.input.MouseInputManager;
import com.skullmangames.darksouls.client.renderer.Camera;
import com.skullmangames.darksouls.client.renderer.RenderEngine;
import com.skullmangames.darksouls.common.capability.entity.ClientPlayerData;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.PointOfView;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientManager
{
	public static ClientManager INSTANCE;
	public final Minecraft minecraft;
	public final RenderEngine renderEngine;
	public final InputManager inputManager;
	public final ScreenManager screenManager;
	public final Camera mainCamera;
	private GameSettings options;
	
	private ClientPlayerData playerdata;
	
	public ClientManager()
	{
		INSTANCE = this;
		this.minecraft = Minecraft.getInstance();
		this.renderEngine = new RenderEngine();
		this.inputManager = new InputManager();
		this.screenManager = new ScreenManager();
		this.options = this.minecraft.options;
		
		this.minecraft.gameRenderer.mainCamera = new Camera();
		this.mainCamera = (Camera)this.minecraft.gameRenderer.mainCamera;
		this.minecraft.mouseHandler = new MouseInputManager(this.minecraft);
		this.minecraft.mouseHandler.setup(this.minecraft.getWindow().getWindow());
	}
	
	public void switchToFirstPerson()
	{
		this.options.setCameraType(PointOfView.FIRST_PERSON);
		this.playerdata.getOriginalEntity().abilities.mayBuild = true;
	}
	
	public void switchToThirdPerson()
	{
		this.options.setCameraType(PointOfView.THIRD_PERSON_BACK);
		this.playerdata.getOriginalEntity().abilities.mayBuild = false;

		this.playerdata.getOriginalEntity().xRot = 0.0F;
		this.playerdata.getOriginalEntity().xRotO = 0.0F;
		float x = this.playerdata.getOriginalEntity().yRot;
		this.playerdata.rotateTo(x, 180.0F, true);
	}
	
	public void setPlayerData(ClientPlayerData playerdata)
	{
		if(this.playerdata != null && this.playerdata != playerdata) this.playerdata.discard();
		this.playerdata = playerdata;
	}
	
	public ClientPlayerData getPlayerData()
	{
		return this.playerdata;
	}
}