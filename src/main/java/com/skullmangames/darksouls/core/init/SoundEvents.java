package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class SoundEvents
{
	public static final SoundEvent BONFIRE_LIT = registerSound("block.bonfire.lit");
	public static final SoundEvent BONFIRE_AMBIENT = registerSound("block.bonfire.ambient");

	public static final SoundEvent DARKSIGN_USE = registerSound("item.darksign.use");
	public static final SoundEvent SOUL_CONTAINER_USE = registerSound("item.soul_container.use");
	public static final SoundEvent SOUL_CONTAINER_FINISH = registerSound("item.soul_container.finish");
	
	public static final SoundEvent HOLLOW_AMBIENT = registerSound("entity.hollow.ambient");
	public static final SoundEvent HOLLOW_DEATH = registerSound("entity.hollow.death");
	public static final SoundEvent HOLLOW_PREPARE = registerSound("entity.hollow.prepare");

	private static SoundEvent registerSound(String name)
	{
		ResourceLocation res = new ResourceLocation(DarkSouls.MOD_ID, name);
		SoundEvent soundEvent = new SoundEvent(res).setRegistryName(name);
		
		return soundEvent;
	}
}
