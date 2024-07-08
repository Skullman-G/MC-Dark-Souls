package com.skullmangames.darksouls.common.animation;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.animation.types.ActionAnimation;
import com.skullmangames.darksouls.common.animation.types.AdaptableAnimation;
import com.skullmangames.darksouls.common.animation.types.AimingAnimation;
import com.skullmangames.darksouls.common.animation.types.BlockedAnimation;
import com.skullmangames.darksouls.common.animation.types.DeathAnimation;
import com.skullmangames.darksouls.common.animation.types.DodgingAnimation;
import com.skullmangames.darksouls.common.animation.types.InvincibleAnimation;
import com.skullmangames.darksouls.common.animation.types.MirrorAnimation;
import com.skullmangames.darksouls.common.animation.types.MovementAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.BackstabCheckAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.CriticalHitAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.ParryAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.PunishCheckAnimation;

import net.minecraft.resources.ResourceLocation;

public enum AnimationType
{
	STATIC("static", StaticAnimation.Builder::new),
	MOVEMENT("movement", MovementAnimation.Builder::new),
	DEATH("death", DeathAnimation.Builder::new),
	ATTACK("attack", AttackAnimation.Builder::new),
	CRITICAL_HIT("critical_hit", CriticalHitAnimation.Builder::new),
	ACTION("action", ActionAnimation.Builder::new),
	MIRROR("mirror", MirrorAnimation.Builder::new),
	ADAPTABLE("adaptable", AdaptableAnimation.Builder::new),
	BLOCKED("blocked", BlockedAnimation.Builder::new),
	INVINCIBLE("invincible", InvincibleAnimation.Builder::new),
	PUNISH_CHECK("punish_check", PunishCheckAnimation.Builder::new),
	BACKSTAB_CHECK("backstab_check", BackstabCheckAnimation.Builder::new),
	PARRY("parry", ParryAnimation.Builder::new),
	AIMING("aiming", AimingAnimation.Builder::new),
	DODGE("dodge", DodgingAnimation.Builder::new);
	
	private final String id;
	private final BuilderCreator builder;
	
	private AnimationType(String id, BuilderCreator builder)
	{
		this.id = id;
		this.builder = builder;
	}
	
	public static AnimationType fromString(String id)
	{
		for (AnimationType type : AnimationType.values())
		{
			if (type.id.equals(id)) return type;
		}
		return null;
	}
	
	public String toString()
	{
		return this.id;
	}
	
	public AnimBuilder getAnimBuilder(ResourceLocation id, JsonObject json)
	{
		return this.builder.get(id, json);
	}
	
	@FunctionalInterface
	private interface BuilderCreator
	{
		public AnimBuilder get(ResourceLocation id, JsonObject json);
	}
}
