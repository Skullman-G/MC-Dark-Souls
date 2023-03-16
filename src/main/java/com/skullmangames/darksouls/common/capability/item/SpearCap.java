package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.init.WeaponMovesets;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SpearCap extends MeleeWeaponCap
{
	public SpearCap(Item item, int reqStrength, int reqDex, int reqFaith, Scaling strengthScaling, Scaling dexScaling, Scaling faithScaling)
	{
		super(item, WeaponMovesets.SPEAR, Colliders.SPEAR, reqStrength, reqDex, reqFaith, strengthScaling, dexScaling, faithScaling);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public AttackAnimation getAttack(AttackType type, LocalPlayerCap playerCap)
	{
		if (type == AttackType.LIGHT && playerCap.isBlocking()) return Animations.SPEAR_LIGHT_BLOCKING_ATTACK;
		return super.getAttack(type, playerCap);
	}
	
	@Override
	public SoundEvent getSwingSound()
	{
		return ModSoundEvents.SPEAR_SWING.get();
	}
}
