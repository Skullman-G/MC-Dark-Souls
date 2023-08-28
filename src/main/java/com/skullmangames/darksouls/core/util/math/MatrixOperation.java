package com.skullmangames.darksouls.core.util.math;

import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

@FunctionalInterface
public interface MatrixOperation
{
	public ModMatrix4f mul(ModMatrix4f left, ModMatrix4f right, ModMatrix4f dest);
}
