package com.skullmangames.darksouls.client;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.gui.screens.IngameConfigurationScreen;
import com.skullmangames.darksouls.client.input.InputManager;
import com.skullmangames.darksouls.client.input.MouseInputManager;
import com.skullmangames.darksouls.client.renderer.ModCamera;
import com.skullmangames.darksouls.client.renderer.RenderEngine;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.common.item.EstusFlaskItem;
import com.skullmangames.darksouls.core.init.ModContainers;
import com.skullmangames.darksouls.core.init.ModItems;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class ClientManager
{
	public static ClientManager INSTANCE;
	public final Minecraft minecraft;
	public final RenderEngine renderEngine;
	public final InputManager inputManager;
	public final ModCamera mainCamera;
	public final NPCChat npcChat;
	private GameSettings options;
	private boolean combatModeActive;
	
	private LocalPlayerCap playerCap;
	
	public ClientManager()
	{
		INSTANCE = this;
		this.minecraft = Minecraft.getInstance();
		this.renderEngine = new RenderEngine();
		this.inputManager = new InputManager();
		this.npcChat = new NPCChat();
		MinecraftForge.EVENT_BUS.register(this.npcChat);
		this.options = this.minecraft.options;
		
		this.minecraft.gameRenderer.mainCamera = new ModCamera();
		this.mainCamera = (ModCamera)this.minecraft.gameRenderer.mainCamera;
		this.minecraft.mouseHandler = new MouseInputManager(this.minecraft);
		this.minecraft.mouseHandler.setup(this.minecraft.getWindow().getWindow());
		
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> IngameConfigurationScreen::new);
		
		ModContainers.registerScreens();
		
		ItemModelsProperties.register(ModItems.ESTUS_FLASK.get(), new ResourceLocation(DarkSouls.MOD_ID, "usage"), (stack, level, living) ->
	    {
	    	return (float)EstusFlaskItem.getUses(stack) / (float)EstusFlaskItem.getTotalUses(stack);
	    });
	}
	
	public void toggleCombatMode()
	{
		this.combatModeActive = !this.combatModeActive;
	}
	
	public boolean isCombatModeActive()
	{
		return this.combatModeActive;
	}
	
	public void switchToFirstPerson()
	{
		this.options.setCameraType(PointOfView.FIRST_PERSON);
		this.playerCap.getOriginalEntity().abilities.mayBuild = true;
		this.combatModeActive = false;
	}
	
	public void switchToThirdPerson()
	{
		this.options.setCameraType(PointOfView.THIRD_PERSON_BACK);
		this.playerCap.getOriginalEntity().abilities.mayBuild = false;

		this.playerCap.getOriginalEntity().xRot = 0.0F;
		this.playerCap.getOriginalEntity().xRotO = 0.0F;
		float x = this.playerCap.getOriginalEntity().yRot;
		this.playerCap.rotateTo(x, 180.0F, true);
		this.combatModeActive = true;
	}
	
	public void setPlayerCap(LocalPlayerCap playerCap)
	{
		if(this.playerCap != null && this.playerCap != playerCap) this.playerCap.discard();
		this.playerCap = playerCap;
	}
	
	public LocalPlayerCap getPlayerCap()
	{
		return this.playerCap;
	}
}