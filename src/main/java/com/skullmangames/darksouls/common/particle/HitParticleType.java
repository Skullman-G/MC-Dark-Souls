package com.skullmangames.darksouls.common.particle;

import java.util.function.BiFunction;

import net.minecraft.entity.Entity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class HitParticleType extends BasicParticleType
{
	public static final BiFunction<Entity, Entity, Vector3d> DEFAULT = (e1, e2) -> {
		/*EntitySize size = e1.getSize(e1.getPose());
		return new Vector3d(size.width, size.height, 0.0D);*/
		return null;
	};
	
	public static final BiFunction<Entity, Entity, Vector3d> DIRECTIONAL = (e1, e2) -> {
		return new Vector3d(e2.getViewXRot(0.5F), e2.getViewYRot(0.5F), 0.0D);
	};
	
	public BiFunction<Entity, Entity, Vector3d> defaultGetter;
	
	public HitParticleType(boolean p_i50791_1_)
	{
		this(p_i50791_1_, DEFAULT);
	}
	
	public HitParticleType(boolean p_i50791_1_, BiFunction<Entity, Entity, Vector3d> argumentGetter)
	{
		super(p_i50791_1_);
		this.defaultGetter = argumentGetter;
	}
	
	public void spawnParticleWithArgument(ServerWorld level, BiFunction<Entity, Entity, Vector3d> argumentGetter, Entity e1, Entity e2)
	{
		Vector3d arguments = argumentGetter == null ? this.defaultGetter.apply(e1, e2) : argumentGetter.apply(e1, e2);
		level.sendParticles(this, e1.getX(), e1.getY(), e1.getZ(), 0, arguments.x, arguments.y, arguments.z, 1.0D);
	}
}