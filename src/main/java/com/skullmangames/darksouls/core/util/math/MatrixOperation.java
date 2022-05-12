package com.skullmangames.darksouls.core.util.math;

import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

@FunctionalInterface
public interface MatrixOperation
{
	public PublicMatrix4f mul(PublicMatrix4f left, PublicMatrix4f right, PublicMatrix4f dest);
}
