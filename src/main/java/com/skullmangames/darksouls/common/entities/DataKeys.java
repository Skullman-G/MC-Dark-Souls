package com.skullmangames.darksouls.common.entities;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;

public class DataKeys
{
	public static final DataParameter<Float> STUN_ARMOR = new DataParameter<Float> (252, DataSerializers.FLOAT);
}