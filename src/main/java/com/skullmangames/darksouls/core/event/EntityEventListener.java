package com.skullmangames.darksouls.core.event;

import java.util.Collection;
import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;

public class EntityEventListener
{
	private Multimap<EventType, PlayerEvent> map;
	private final PlayerData<?> player;
	
	public EntityEventListener(PlayerData<?> player)
	{
		this.player = player;
		this.map = HashMultimap.create();
	}

	public void addEventListener(EventType event, PlayerEvent function)
	{
		map.put(event, function);
	}

	public void removeListener(EventType event, UUID functionUUID)
	{
		Collection<PlayerEvent> c = map.get(event);
		PlayerEvent wantToRemove = null;

		for (PlayerEvent e : c)
		{
			if (e.is(functionUUID))
			{
				wantToRemove = e;
				break;
			}
		}
		
		if(wantToRemove!=null)
		{
			c.remove(wantToRemove);
		}
	}
	
	public boolean activateEvents(EventType event, Object... args)
	{
		boolean cancel = false;
		for(PlayerEvent function : map.get(event))
		{
			if(event.isRemote == this.player.isClientSide())
			{
				cancel |= function.doIt(this.player, args);
			}
		}
		
		return cancel;
	}
	
	public enum EventType
	{
		ON_ACTION_EVENT(false), ON_ATTACK_END_EVENT(false);
		boolean isRemote;
		
		EventType(boolean isRemote)
		{
			this.isRemote = isRemote;
		}
	}
}