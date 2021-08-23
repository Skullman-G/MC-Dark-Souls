package com.skullmangames.darksouls.client;

import com.skullmangames.darksouls.client.input.InputManager;
import com.skullmangames.darksouls.client.renderer.RenderEngine;
import com.skullmangames.darksouls.common.capability.entity.ClientPlayerData;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientEngine
{
	public static ClientEngine INSTANCE;
	public Minecraft minecraft;
	public RenderEngine renderEngine;
	public InputManager inputController;
	
	private ClientPlayerData playerdata;
	private PlayerActingMode playerActingMode = PlayerActingMode.MINING;
	
	public ClientEngine()
	{
		INSTANCE = this;
		minecraft = Minecraft.getInstance();
		renderEngine = new RenderEngine();
		inputController = new InputManager();
	}
	
	public void switchToMiningMode()
	{
		this.playerActingMode = PlayerActingMode.MINING;
	}
	
	public void switchToBattleMode()
	{
		this.playerActingMode = PlayerActingMode.BATTLE;
	}
	
	public PlayerActingMode getPlayerActingMode()
	{
		return this.playerActingMode;
	}
	
	public boolean isBattleMode()
	{
		return this.playerActingMode == PlayerActingMode.BATTLE;
	}
	
	public void setPlayerData(ClientPlayerData playerdata)
	{
		if(this.playerdata != null && this.playerdata != playerdata)
		{
			this.playerdata.discard();
		}
		this.playerdata = playerdata;
	}
	
	public ClientPlayerData getPlayerData()
	{
		return this.playerdata;
	}
	
	public static enum PlayerActingMode
	{
		MINING, BATTLE
	}
}