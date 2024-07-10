package com.skullmangames.darksouls.common.capability.entity;

public enum EntityState
{
	FREE(false, false, false, false, true, 0), FREE_CAMERA(true, false, false, false, false, 1),
	FREE_INPUT(false, false, false, false, true, 3), PRE_CONTACT(true, false, false, false, false, 1),
	CONTACT(true, true, true, false, false, 2), POST_CONTACT(true, true, false, false, true, 3),
	HIT(true, true, false, false, false, 2), BLOCK(true, true, true, false, false, 2),
	INVINCIBLE(true, true, false, true, false, 2), R_DODGING(true, false, false, true, false, 2),
	PUNISHABLE(true, true, false, false, false, 0), DODGING(true, true, false, true, false, 2);

	private boolean movementLock;
	private boolean rotationLock;
	private boolean collisionDetection;
	private boolean invincible;
	private boolean canAct;
	// none : 0, preContact : 1, contact : 2, postContact : 3
	int contactLevel;

	EntityState(boolean movementLock, boolean rotationLock, boolean collisionDetection, boolean invincible,
			boolean canAct, int level)
	{
		this.movementLock = movementLock;
		this.rotationLock = rotationLock;
		this.collisionDetection = collisionDetection;
		this.invincible = invincible;
		this.canAct = canAct;
		this.contactLevel = level;
	}

	public boolean isMovementLocked()
	{
		return this.movementLock;
	}

	public boolean isRotationLocked()
	{
		return this.rotationLock;
	}

	public boolean shouldDetectCollision()
	{
		return this.collisionDetection;
	}

	public boolean isInvincible()
	{
		return this.invincible;
	}

	public boolean canAct()
	{
		return this.canAct;
	}

	public int getContactLevel()
	{
		return this.contactLevel;
	}
}
