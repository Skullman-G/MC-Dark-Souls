package com.skullmangames.darksouls.client;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.gui.GameOverlayManager;
import com.skullmangames.darksouls.client.gui.ScreenManager;
import com.skullmangames.darksouls.client.input.InputManager;
import com.skullmangames.darksouls.client.input.MouseInputManager;
import com.skullmangames.darksouls.client.renderer.ModCamera;
import com.skullmangames.darksouls.client.renderer.RenderEngine;
import com.skullmangames.darksouls.common.capability.entity.ClientPlayerData;
import com.skullmangames.darksouls.common.item.EstusFlaskItem;
import com.skullmangames.darksouls.core.init.ModContainers;
import com.skullmangames.darksouls.core.init.ModItems;

import net.minecraft.client.Options;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
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
	public final ModCamera mainCamera;
	private Options options;
	
	private ClientPlayerData playerdata;
	
	public ClientManager()
	{
		INSTANCE = this;
		this.minecraft = Minecraft.getInstance();
		this.renderEngine = new RenderEngine();
		this.inputManager = new InputManager();
		this.screenManager = new ScreenManager();
		this.options = this.minecraft.options;
		
		this.minecraft.gameRenderer.mainCamera = new ModCamera();
		this.mainCamera = (ModCamera)this.minecraft.gameRenderer.mainCamera;
		this.minecraft.mouseHandler = new MouseInputManager(this.minecraft);
		this.minecraft.mouseHandler.setup(this.minecraft.getWindow().getWindow());
		
		GameOverlayManager.registerOverlayElements();
		ModContainers.registerScreens();
		
		ItemProperties.register(ModItems.ESTUS_FLASK.get(), new ResourceLocation(DarkSouls.MOD_ID, "usage"), (stack, level, living, id) ->
	    {
	    	return (float)EstusFlaskItem.getUses(stack) / (float)EstusFlaskItem.getTotalUses(stack);
	    });
	}
	
	public void switchToFirstPerson()
	{
		this.options.setCameraType(CameraType.FIRST_PERSON);
		this.playerdata.getOriginalEntity().getAbilities().mayBuild = true;
	}
	
	public void switchToThirdPerson()
	{
		this.options.setCameraType(CameraType.THIRD_PERSON_BACK);
		this.playerdata.getOriginalEntity().getAbilities().mayBuild = false;

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