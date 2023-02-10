package com.skullmangames.darksouls.client.particles;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class EntityboundParticle extends SpriteTexturedParticle
{
	protected final Entity entity;
	
	public EntityboundParticle(ClientWorld level, int entityId, double posX, double posY, double posZ,
			double speedX, double speedY, double speedZ)
	{
		super(level, posX, posY, posZ, speedX, speedY, speedZ);
		this.entity = level.getEntity(entityId);
	}
}
