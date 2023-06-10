package com.skullmangames.darksouls.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class EntityboundParticle extends TextureSheetParticle
{
	protected final Entity entity;
	
	protected EntityboundParticle(ClientLevel level, int entityId, double posX, double posY, double posZ,
			double speedX, double speedY, double speedZ)
	{
		super(level, posX, posY, posZ, speedX, speedY, speedZ);
		this.entity = level.getEntity(entityId);
	}
}
