package com.skullmangames.darksouls.common.animation;

import com.skullmangames.darksouls.common.animation.events.Anchor;
import com.skullmangames.darksouls.common.animation.events.AnimEvent;
import com.skullmangames.darksouls.common.animation.events.AnimEvent.Side;
import com.skullmangames.darksouls.common.animation.events.PlaySoundEvent;
import com.skullmangames.darksouls.common.animation.events.ShakeCamGlobalEvent;
import com.skullmangames.darksouls.common.animation.events.SimpleParticleEvent;
import com.skullmangames.darksouls.common.animation.events.SimpleParticleEvent.Spawner;
import com.skullmangames.darksouls.core.init.ModParticles;
import com.skullmangames.darksouls.core.init.ModSoundEvents;

@FunctionalInterface
public interface SmashEvents 
{
	public AnimEvent[] getEvents(float time);
	
	public default AnimEvent[] appendTo(float time, AnimEvent[] other)
	{
		AnimEvent[] own = getEvents(time);
		AnimEvent[] list = new AnimEvent[other.length + own.length];
		
		for (int i = 0; i < other.length; i++)
		{
			list[i] = other[i];
		}
		for (int i = 0; i < own.length; i++)
		{
			list[other.length + i] = own[i];
		}
		
		return list;
	}
			
	public static final SmashEvents BIG_SWORD = (t) ->
	{
		return new AnimEvent[]
		{
				new ShakeCamGlobalEvent(t, 20, 1.0F, Anchor.WEAPON),
				new SimpleParticleEvent(t, ModParticles.DUST_CLOUD, Spawner.CIRCLE, Anchor.WEAPON, 0, 0, 0, 0.1F, 0.1F, 0.1F),
				new PlaySoundEvent(t, Side.CLIENT, ModSoundEvents.ULTRA_GREATSWORD_SMASH, Anchor.WEAPON, 0.75F, false)
		};
	};
	
	public static final SmashEvents BIG_HAMMER = (t) ->
	{
		return new AnimEvent[]
		{
				new ShakeCamGlobalEvent(t, 20, 1.0F, Anchor.WEAPON),
				new SimpleParticleEvent(t, ModParticles.DUST_CLOUD, Spawner.CIRCLE, Anchor.WEAPON, 0, 0, 0, 0.1F, 0.1F, 0.1F),
				new PlaySoundEvent(t, Side.CLIENT, ModSoundEvents.GREAT_HAMMER_SMASH, Anchor.WEAPON, 0.75F, false)
		};
	};
	
	public static final SmashEvents BIG_MONSTER_HAMMER = (t) ->
	{
		return new AnimEvent[]
		{
				new ShakeCamGlobalEvent(t, 25, 1.5F, Anchor.WEAPON),
				new SimpleParticleEvent(t, ModParticles.DUST_CLOUD, Spawner.CIRCLE, Anchor.WEAPON, 0, 0, 0, 0.1F, 0.1F, 0.1F),
				new PlaySoundEvent(t, Side.CLIENT, ModSoundEvents.STRAY_DEMON_SMASH, Anchor.WEAPON, 0.75F, false)
		};
	};
	
	public static final SmashEvents BIG_MONSTER_HAMMER_SWING = (t) ->
	{
		return new AnimEvent[]
		{
				new ShakeCamGlobalEvent(t, 20, 1.0F, Anchor.WEAPON),
				new SimpleParticleEvent(t, ModParticles.DUST_CLOUD, Spawner.CIRCLE, Anchor.WEAPON, 0, 0, 0, 0.1F, 0.1F, 0.1F),
				new PlaySoundEvent(t, Side.CLIENT, ModSoundEvents.STRAY_DEMON_SWING, Anchor.WEAPON, 0.75F, false)
		};
	};
	
	public static final SmashEvents BIG_MONSTER_LAND = (t) ->
	{
		return new AnimEvent[]
		{
				new ShakeCamGlobalEvent(t, 40, 3.0F, Anchor.WEAPON),
				new SimpleParticleEvent(t, ModParticles.DUST_CLOUD, Spawner.CIRCLE, Anchor.WEAPON, 0, 0, 0, 0.25F, 0.25F, 0.25F),
				new PlaySoundEvent(t, Side.CLIENT, ModSoundEvents.STRAY_DEMON_LAND, Anchor.WEAPON, 0.75F, false)
		};
	};
}
