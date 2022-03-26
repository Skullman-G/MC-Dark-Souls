package com.skullmangames.darksouls.core.event;

import java.util.UUID;
import java.util.function.BiFunction;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;

public class PlayerEvent implements Comparable<UUID>
{
	private UUID uuid;
	private BiFunction<PlayerCap<?>, Object[], Boolean> function;

	public PlayerEvent(UUID uuid, BiFunction<PlayerCap<?>, Object[], Boolean> function)
	{
		this.uuid = uuid;
		this.function = function;
	}

	public boolean is(UUID uuid)
	{
		return this.uuid.equals(uuid);
	}

	public boolean doIt(PlayerCap<?> player, Object... args)
	{
		return this.function.apply(player, args);
	}
	
	@Override
	public int compareTo(UUID o)
	{
		if(o.equals(this.uuid))
		{
			return 0;
		}
		else
		{
			return -1;
		}
	}
	
	public static PlayerEvent makeEvent(UUID uuid, BiFunction<PlayerCap<?>, Object[], Boolean> function)
	{
		return new PlayerEvent(uuid, function);
	}
}