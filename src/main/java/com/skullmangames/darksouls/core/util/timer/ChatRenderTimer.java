package com.skullmangames.darksouls.core.util.timer;

import java.util.function.IntConsumer;

import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ChatRenderTimer
{
	private int time;
	private int startTime;
	private String[] sentences;
	private int sentence;
	private boolean ticking = false;
	
	private IntConsumer onFinish;
	
	public void start(int time, IntConsumer onFinish, String... sentences)
	{
		this.start(time, sentences);
		this.onFinish = onFinish;
	}
	
	public void start(int time, String... sentences)
	{
		this.startTime = time;
		this.time = time;
		this.sentences = sentences;
		this.sentence = 0;
		this.ticking = true;
		
		ModNetworkManager.connection.setOverlayMessage(new TextComponent(sentences[this.sentence++]));
	}
	
	public void stop()
	{
		this.time = 0;
		this.ticking = false;
	}
	
	public boolean isTicking()
	{
		return this.ticking;
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent event)
	{
		if (!this.ticking || event.phase == Phase.START) return;
		if (--this.time > 0) return;
		
		if (this.sentence < this.sentences.length) ModNetworkManager.connection.setOverlayMessage(new TextComponent(sentences[this.sentence++]));
		else if (this.onFinish != null)
		{
			this.onFinish.accept(this.time);
			this.onFinish = null;
		}
		else this.ticking = false;
		
		if (this.ticking) this.time = this.startTime;
	}
}
