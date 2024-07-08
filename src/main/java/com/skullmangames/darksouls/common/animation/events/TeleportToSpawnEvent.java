package com.skullmangames.darksouls.common.animation.events;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

public class TeleportToSpawnEvent extends AnimEvent
{
	public static final String TYPE = "teleport_to_spawn";
	
	public TeleportToSpawnEvent(float time)
	{
		super(time, Side.SERVER);
	}
	
	public TeleportToSpawnEvent(JsonObject json)
	{
		super(json);
	}

	@Override
	protected void invoke(LivingCap<?> cap)
	{
		if (cap.getOriginalEntity() instanceof ServerPlayer)
		{
			BlockPos pos = ((ServerPlayer)cap.getOriginalEntity()).getRespawnPosition();
			assert pos != null;
			cap.getOriginalEntity().teleportTo(pos.getX(), pos.getY(), pos.getZ());
		}
	}

	@Override
	protected String getType()
	{
		return TYPE;
	}
}
