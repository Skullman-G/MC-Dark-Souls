package com.skullmangames.darksouls.common.animation.events;

import java.util.function.Supplier;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class PlaySoundEvent extends AnimEvent
{
	public static final String TYPE = "play_sound";
	
	private final Supplier<SoundEvent> sound;
	private final Anchor anchor;
	private final float volume;
	private final boolean moves;
	
	public PlaySoundEvent(float time, Side side, Supplier<SoundEvent> sound)
	{
		this(time, side, sound, Anchor.ENTITY, 1.0F, false);
	}
	
	public PlaySoundEvent(float time, Side side, Supplier<SoundEvent> sound, Anchor anchor, float volume, boolean moves)
	{
		super(time, side);
		this.sound = sound;
		this.anchor = anchor;
		this.volume = volume;
		this.moves = moves;
	}
	
	public PlaySoundEvent(JsonObject json)
	{
		super(json);
		this.sound = () -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(json.get("sound").getAsString()));
		this.anchor = Anchor.valueOf(json.get("anchor").getAsString());
		this.volume = json.get("volume").getAsFloat();
		this.moves = json.get("moves").getAsBoolean();
	}
	
	@Override
	public JsonObject toJson()
	{
		JsonObject json = super.toJson();
		json.addProperty("sound", this.sound.get().getLocation().toString());
		json.addProperty("anchor", this.anchor.name());
		json.addProperty("volume", this.volume);
		json.addProperty("moves", this.moves);
		return json;
	}

	@Override
	protected void invoke(LivingCap<?> cap)
	{
		switch (this.anchor)
		{
		case ENTITY:
			cap.playSound(this.sound.get(), this.volume, this.moves);
			break;
		case WEAPON:
			if (cap.weaponCollider.isEmpty()) return;
			Vec3 pos = cap.weaponCollider.getMassCenter();
			cap.getLevel().playLocalSound(pos.x, pos.y, pos.z, this.sound.get(), cap.getOriginalEntity().getSoundSource(),
					this.volume, 1.0F, false);
		}
	}
	
	@Override
	protected String getType()
	{
		return TYPE;
	}
}
