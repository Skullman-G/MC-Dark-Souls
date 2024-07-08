package com.skullmangames.darksouls.common.entity.projectile;

import com.skullmangames.darksouls.common.capability.entity.LivingCap;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public abstract class MagicProjectile extends Projectile
{
	protected MagicProjectile(EntityType<? extends Projectile> type, Level level)
	{
		super(type, level);
	}

	public abstract void initProjectile(LivingCap<?> cap);
}
