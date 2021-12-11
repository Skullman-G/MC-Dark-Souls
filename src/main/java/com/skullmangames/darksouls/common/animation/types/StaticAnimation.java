package com.skullmangames.darksouls.common.animation.types;

import java.util.List;

import com.google.common.collect.Lists;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.animation.MixPart;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.config.IngameConfig;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.parser.xml.collada.AnimationDataExtractor;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;

public class StaticAnimation extends DynamicAnimation
{
	protected String path;
	protected final int id;
	public List<SoundKey> soundStream;
	protected final boolean clientOnly;
	protected final String armature;
	private final MixPart mixPart;
	
	public StaticAnimation()
	{
		super();
		this.id = -1;
		this.clientOnly = true;
		this.armature = "";
		this.mixPart = MixPart.FULL;
	}
	
	public StaticAnimation(boolean register, float convertTime, boolean isRepeat, String path, String armature, boolean clientOnly)
	{
		this(register, convertTime, isRepeat, path, armature, clientOnly, MixPart.FULL);
	}
	
	public StaticAnimation(boolean register, float convertTime, boolean isRepeat, String path, String armature, boolean clientOnly, MixPart mixPart)
	{
		super(convertTime, isRepeat);
		
		this.clientOnly = clientOnly;
		this.armature = armature;
		this.path = this.makeDataPath(path);
		this.totalTime = 0;
		this.mixPart = mixPart;
		
		if (register)
		{
			this.id = Animations.ANIMATIONS.size();
			Animations.ANIMATIONS.add(this);
		}
		else this.id = -1;
	}
	
	public StaticAnimation(String path)
	{
		this();
		this.path = this.makeDataPath(path);
	}
	
	public StaticAnimation(boolean register, boolean repeatPlay, String path, String armature, boolean clientOnly)
	{
		this(register, repeatPlay, path, armature, clientOnly, MixPart.FULL);
	}
	
	public StaticAnimation(boolean register, boolean repeatPlay, String path, String armature, boolean clientOnly, MixPart mixPart)
	{
		this(register, IngameConfig.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path, armature, clientOnly, mixPart);
	}
	
	public MixPart getMixPart()
	{
		return this.mixPart;
	}
	
	private String makeDataPath(String path)
	{
		return "models/animations/"+path+".dae";
	}
	
	public void bind(Dist dist)
	{
		if (this.clientOnly && dist != Dist.CLIENT) return;
		
		if(path != null)
		{
			Models<?> modeldata = dist == Dist.CLIENT ? ClientModels.CLIENT : Models.SERVER;
			Armature armature = modeldata.findArmature(this.armature);
			AnimationDataExtractor.extractAnimation(new ResourceLocation(DarkSouls.MOD_ID, path), this, armature);
			path = null;
		}
		
		if(this.soundStream != null) this.soundStream.sort(null);
		return;
	}
	
	@Override
	public void onUpdate(LivingData<?> entitydata)
	{
		if(this.soundStream != null)
		{
			AnimationPlayer player = entitydata.getAnimator().getPlayerFor(this);
			float prevElapsed = player.getPrevElapsedTime();
			float elapsed = player.getElapsedTime();
			
			for(SoundKey key : this.soundStream)
			{
				if(key.time < prevElapsed || key.time >= elapsed) continue;
				else
				{
					if(entitydata.isClientSide() == key.isClientSide)
					{
						entitydata.playSound(key.sound, 0.0F, 0.0F);
					}
				}
			}
		}
	}
	
	public int getId()
	{
		return id;
	}
	
	public StaticAnimation registerSound(SoundEvent sound, float time, boolean isRemote)
	{
		if(this.soundStream == null) this.soundStream = Lists.<SoundKey>newArrayList();
		this.soundStream.add(new SoundKey(time, sound, isRemote));
		return this;
	}
	
	@Override
	public String toString()
	{
		return this.id+" "+this.path;
	}
	
	protected static class SoundKey implements Comparable<SoundKey>
	{
		float time;
		SoundEvent sound;
		boolean isClientSide;
		
		protected SoundKey(float time, SoundEvent sound, boolean isRemote)
		{
			this.time = time;
			this.sound = sound;
			this.isClientSide = isRemote;
		}

		@Override
		public int compareTo(SoundKey arg0)
		{
			if(this.time == arg0.time) return 0;
			else return this.time > arg0.time ? 1 : -1;
		}
	}
}