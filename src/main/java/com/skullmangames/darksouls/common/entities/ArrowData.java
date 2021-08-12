package com.skullmangames.darksouls.common.entities;

import net.minecraft.entity.projectile.AbstractArrowEntity;

public class ArrowData extends CapabilityProjectile<AbstractArrowEntity> {
	@Override
	protected void setMaxStrikes(AbstractArrowEntity projectileEntity, int maxStrikes) {
		projectileEntity.setPierceLevel((byte)(maxStrikes - 1));
	}
}