package com.skullmangames.darksouls.common.animation.types;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.animation.MixPart;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.config.IngameConfig;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.parser.xml.collada.AnimationDataExtractor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.registries.RegistryObject;

public class StaticAnimation extends DynamicAnimation
{
	protected String path;
	protected final int id;
	protected List<SoundKey> soundStream;
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
	public void onUpdate(LivingCap<?> entitydata)
	{
		if(this.soundStream != null)
		{
			AnimationPlayer player = entitydata.getAnimator().getPlayerFor(this);
			float prevElapsed = player.getPrevElapsedTime();
			float elapsed = player.getElapsedTime();
			
			for(SoundKey key : this.soundStream)
			{
				if((key.time >= prevElapsed && key.time < elapsed) && !entitydata.isClientSide())
				{
					entitydata.playSound(key.sound.get(), 0.0F, 0.0F);
				}
			}
		}
	}
	
	public int getId()
	{
		return id;
	}
	
	public StaticAnimation registerSound(RegistryObject<SoundEvent> sound, float time, boolean isRemote)
	{
		if(this.soundStream == null) this.soundStream = new ArrayList<SoundKey>();
		this.soundStream.add(new SoundKey(sound, time, isRemote));
		return this;
	}
	
	@Override
	public String toString()
	{
		return this.id+" "+this.path;
	}
	
	protected static class SoundKey implements Comparable<SoundKey>
	{
		RegistryObject<SoundEvent> sound;
		float time;
		boolean isClientSide;
		
		protected SoundKey(RegistryObject<SoundEvent> sound, float time, boolean isClientSide)
		{
			this.sound = sound;
			this.time = time;
			this.isClientSide = isClientSide;
		}
		@Override
		public int compareTo(SoundKey arg0)
		{
			if(this.time == arg0.time) return 0;
			else return this.time > arg0.time ? 1 : -1;
		}
	}
}