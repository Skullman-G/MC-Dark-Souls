package com.skullmangames.darksouls.common.animation.events;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;

public abstract class AnimEvent implements Comparable<AnimEvent>
{
	public static final ImmutableMap<String, Function<JsonObject, AnimEvent>> BUILDERS = ImmutableMap.ofEntries
	(
			new SimpleImmutableEntry<String, Function<JsonObject, AnimEvent>>(PlaySoundEvent.TYPE, PlaySoundEvent::new),
			new SimpleImmutableEntry<String, Function<JsonObject, AnimEvent>>(ChangeItemEvent.TYPE, ChangeItemEvent::new),
			new SimpleImmutableEntry<String, Function<JsonObject, AnimEvent>>(ImpactParticleEvent.TYPE, ImpactParticleEvent::new),
			new SimpleImmutableEntry<String, Function<JsonObject, AnimEvent>>(TeleportEvent.TYPE, TeleportEvent::new),
			new SimpleImmutableEntry<String, Function<JsonObject, AnimEvent>>(TeleportParticleEvent.TYPE, TeleportParticleEvent::new),
			new SimpleImmutableEntry<String, Function<JsonObject, AnimEvent>>(ShootThrowableProjectileEvent.TYPE, ShootThrowableProjectileEvent::new),
			new SimpleImmutableEntry<String, Function<JsonObject, AnimEvent>>(ShootMagicProjectileEvent.TYPE, ShootMagicProjectileEvent::new),
			new SimpleImmutableEntry<String, Function<JsonObject, AnimEvent>>(ShakeCamEvent.TYPE, ShakeCamEvent::new),
			new SimpleImmutableEntry<String, Function<JsonObject, AnimEvent>>(ShakeCamGlobalEvent.TYPE, ShakeCamGlobalEvent::new),
			new SimpleImmutableEntry<String, Function<JsonObject, AnimEvent>>(SetLightSourceEvent.TYPE, SetLightSourceEvent::new),
			new SimpleImmutableEntry<String, Function<JsonObject, AnimEvent>>(EntityboundParticleEvent.TYPE, EntityboundParticleEvent::new),
			new SimpleImmutableEntry<String, Function<JsonObject, AnimEvent>>(SimpleParticleEvent.TYPE, SimpleParticleEvent::new),
			new SimpleImmutableEntry<String, Function<JsonObject, AnimEvent>>(ShockWaveEvent.TYPE, ShockWaveEvent::new),
			new SimpleImmutableEntry<String, Function<JsonObject, AnimEvent>>(TeleportToSpawnEvent.TYPE, TeleportToSpawnEvent::new),
			new SimpleImmutableEntry<String, Function<JsonObject, AnimEvent>>(HealSelfEvent.TYPE, HealSelfEvent::new),
			new SimpleImmutableEntry<String, Function<JsonObject, AnimEvent>>(HealInRadiusEvent.TYPE, HealInRadiusEvent::new)
	);
	
	public static final float ON_BEGIN = Float.MIN_VALUE;
	public static final float ON_END = Float.MAX_VALUE;
	public final float time;
	private final Side executionSide;

	public AnimEvent(float time, Side executionSide)
	{
		this.time = time;
		this.executionSide = executionSide;
	}
	
	public AnimEvent(JsonObject json)
	{
		this.time = json.get("time").getAsFloat();
		this.executionSide = Side.valueOf(json.get("execution_side").getAsString());
	}
	
	public JsonObject toJson()
	{
		JsonObject json = new JsonObject();
		json.addProperty("event_type", this.getType());
		json.addProperty("time", this.time);
		json.addProperty("execution_side", this.executionSide.name());
		return json;
	}

	@Override
	public int compareTo(AnimEvent arg0)
	{
		if (this.time == arg0.time) return 0;
		else return this.time > arg0.time ? 1 : -1;
	}

	public void tryExecuting(LivingCap<?> cap)
	{
		if (this.executionSide.predicate.test(cap.isClientSide()))
		{
			this.invoke(cap);
		}
	}
	
	protected abstract void invoke(LivingCap<?> cap);
	
	protected abstract String getType();

	public enum Side
	{
		CLIENT((isLogicalClient) -> isLogicalClient), SERVER((isLogicalClient) -> !isLogicalClient),
		BOTH((isLogicalClient) -> true);

		Predicate<Boolean> predicate;

		Side(Predicate<Boolean> predicate)
		{
			this.predicate = predicate;
		}
	}
}
