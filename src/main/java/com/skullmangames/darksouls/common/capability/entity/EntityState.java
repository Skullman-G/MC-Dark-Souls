package com.skullmangames.darksouls.common.capability.entity;

public enum EntityState
{
	FREE(false, false, false, false, true, false, 0),
	FREE_CAMERA(true, false, false, false, false, false, 1),
	FREE_INPUT(false, false, false, false, true, false, 3),
	PRE_CONTACT(true, false, false, false, false, false, 1),
	CONTACT(true, true, true, false, false, false, 2),
	POST_CONTACT(true, true, false, false, true, false, 3),
	HIT(true, true, false, false, false, false, 2),
	BLOCK(true, true, true, false, false, false, 2),
	INVINCIBLE(true, true, false, true, false, false, 2),
	R_DODGING(true, false, false, true, false, true, 2),
	PUNISHABLE(true, true, false, false, false, false, 0),
	DODGING(true, true, false, true, false, true, 2);
	
	private static final int M_LOCK_FLAG = 1 << 0;
	private static final int R_LOCK_FLAG = 1 << 1;
	private static final int COLLISION_FLAG = 1 << 2;
	private static final int INVINCIBLE_FLAG = 1 << 3;
	private static final int CAN_ACT_FLAG = 1 << 4;
	private static final int DODGING_FLAG = 1 << 5;

	private final int flags;
	// none : 0, preContact : 1, contact : 2, postContact : 3
	private final int contactLevel;

	EntityState
	(
		boolean movementLock,
		boolean rotationLock,
		boolean collisionDetection,
		boolean invincible,
		boolean canAct,
		boolean dodging,
		int contactLevel
	)
	{
		int f = 0;
	    if (movementLock) f |= M_LOCK_FLAG;
	    if (rotationLock) f |= R_LOCK_FLAG;
	    if (collisionDetection) f |= COLLISION_FLAG;
	    if (invincible) f |= INVINCIBLE_FLAG;
	    if (canAct) f |= CAN_ACT_FLAG;
	    if (dodging) f |= DODGING_FLAG;
	    this.flags = f;
	    this.contactLevel = contactLevel;
	}

	public boolean isMovementLocked()
	{
		return (this.flags & M_LOCK_FLAG) != 0;
	}

	public boolean isRotationLocked()
	{
		return (this.flags & R_LOCK_FLAG) != 0;
	}

	public boolean shouldDetectCollision()
	{
		return (this.flags & M_LOCK_FLAG) != 0;
	}

	public boolean isInvincible()
	{
		return (this.flags & INVINCIBLE_FLAG) != 0;
	}

	public boolean canAct()
	{
		return (this.flags & CAN_ACT_FLAG) != 0;
	}
	
	public boolean isDodging()
	{
		return (this.flags & DODGING_FLAG) != 0;
	}

	public int getContactLevel()
	{
		return this.contactLevel;
	}
}
