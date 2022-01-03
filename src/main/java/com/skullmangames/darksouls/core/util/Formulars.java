package com.skullmangames.darksouls.core.util;

import com.skullmangames.darksouls.common.capability.entity.EntityData;
import com.skullmangames.darksouls.common.world.ModGamerules;

import net.minecraft.util.math.MathHelper;

public class Formulars
{
	public static double getAttackSpeedPenalty(double weight, double weaponAttackSpeed, EntityData<?> entity)
	{
		double attenuation = (double)MathHelper.clamp(entity.getOriginalEntity().level.getGameRules().getInt(ModGamerules.SPEED_PENALTY_PERCENT), 0, 100) / 100.0D;
		if(weight > 40.0D)
		{
			return -0.1D * (weight / 40.0D) * (Math.max(weaponAttackSpeed - 0.8D, 0.0D) * 1.5D) * attenuation;
		}
		else
		{ 
			return 0.0D;
		}
	}
	
	public static float getRollAnimationSpeedPenalty(float weight, EntityData<?> entity)
	{
		float attenuation = (float)MathHelper.clamp(entity.getOriginalEntity().level.getGameRules().getInt(ModGamerules.SPEED_PENALTY_PERCENT), 0, 100) / 100.0F;
		weight = MathHelper.lerp(attenuation, 40.0F, weight);
		return 1.0F + (60.0F - weight) / (weight * 2.0F);
	}
}