package com.skullmangames.darksouls.common.capability.projectile;

import net.minecraft.entity.projectile.AbstractArrowEntity;

public class ArrowData extends CapabilityProjectile<AbstractArrowEntity>
{
	@Override
	protected void setMaxStrikes(AbstractArrowEntity projectileEntity, int maxStrikes)
	{
		projectileEntity.setPierceLevel((byte)0);
	}
}