package com.skullmangames.darksouls.client;

import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSFinishNPCChat;

import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class NPCChat
{
	private Entity entity;
	private int time;
	private int startTime = 60;
	private String location;
	private String[] sentences;
	private int sentence;
	private boolean ticking = false;
	
	public void start(Entity entity, String location)
	{
		if (this.ticking && this.entity == entity)
		{
			if (this.sentence >= this.sentences.length) this.stop();
			else
			{
				this.time = this.startTime;
				ModNetworkManager.connection.setOverlayMessage(new StringTextComponent(sentences[this.sentence++]));
			}
			return;
		}
		
		this.entity = entity;
		this.time = this.startTime;
		this.location = location;
		this.sentences = new TranslationTextComponent(location).getString().split("%");
		this.sentence = 0;
		this.ticking = true;
		
		ModNetworkManager.connection.setOverlayMessage(new StringTextComponent(sentences[this.sentence++]));
	}
	
	public String getLocation()
	{
		return this.location;
	}
	
	public void stop()
	{
		this.time = 0;
		this.ticking = false;
		ModNetworkManager.sendToServer(new CTSFinishNPCChat(this.entity.getId(), this.location));
	}
	
	public boolean isTicking()
	{
		return this.ticking;
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent event)
	{
		if (this.entity == null || !this.entity.isAlive())
		{
			this.ticking = false;
			return;
		}
		
		if (!this.ticking || event.phase == Phase.START) return;
		if (--this.time > 0) return;
		
		if (this.sentence < this.sentences.length) ModNetworkManager.connection.setOverlayMessage(new StringTextComponent(sentences[this.sentence++]));
		else this.stop();
		
		if (this.ticking) this.time = this.startTime;
	}
}
