package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SwordCap extends MeleeWeaponCap
{
	public SwordCap(Item item, int requiredStrength, int requiredDex, Scaling strengthScaling, Scaling dexScaling)
	{
		super(item, WeaponCategory.STRAIGHT_SWORD, requiredStrength, requiredDex, strengthScaling, dexScaling, 20F);
	}
	
	@Override
	public SoundEvent getSwingSound()
	{
		return ModSoundEvents.SWORD_SWING;
	}
	
	@Override
	protected AttackAnimation getHeavyAttack()
	{
		return Animations.STRAIGHT_SWORD_HEAVY_ATTACK;
	}
	
	@Override
	protected AttackAnimation[] getLightAttack()
	{
		return Animations.STRAIGHT_SWORD_LIGHT_ATTACK;
	}
	
	@Override
	protected AttackAnimation getDashAttack()
	{
		return Animations.STRAIGHT_SWORD_DASH_ATTACK;
	}
	
	@Override
	public WieldStyle getStyle(LivingData<?> entitydata)
	{
		WeaponCap item = entitydata.getHeldWeaponCapability(InteractionHand.OFF_HAND);
		if(item != null && item.getWeaponCategory() == WeaponCategory.STRAIGHT_SWORD)
		{
			return WieldStyle.TWO_HAND;
		}
		else
		{
			return WieldStyle.ONE_HAND;
		}
	}
	
	@Override
	public Collider getWeaponCollider()
	{
		return Colliders.sword;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canBeRenderedBoth(ItemStack item)
	{
		WeaponCap cap = ModCapabilities.getWeaponCapability(item);
		return super.canBeRenderedBoth(item) || (cap != null && cap.getWeaponCategory() == WeaponCategory.STRAIGHT_SWORD);
	}
}