package com.skullmangames.darksouls.common.capability.item;

import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
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
	public SwordCap(Item item, int reqStrength, int reqDex, int reqFaith, Scaling strengthScaling, Scaling dexScaling, Scaling faithScaling)
	{
		super(item, WeaponCategory.STRAIGHT_SWORD, reqStrength, reqDex, reqFaith, strengthScaling, dexScaling, faithScaling, 20F);
	}
	
	@Override
	protected Builder<AttackType, Pair<Boolean, AttackAnimation[]>> initMoveset()
	{
		Builder<AttackType, Pair<Boolean, AttackAnimation[]>> builder = super.initMoveset();
		this.putMove(builder, AttackType.LIGHT, true, Animations.STRAIGHT_SWORD_LIGHT_ATTACK);
		this.putMove(builder, AttackType.HEAVY, true, Animations.STRAIGHT_SWORD_HEAVY_ATTACK);
		this.putMove(builder, AttackType.DASH, true, Animations.STRAIGHT_SWORD_DASH_ATTACK);
		this.putMove(builder, AttackType.BACKSTAB, true, Animations.BACKSTAB_THRUST);
		return builder;
	}
	
	@Override
	public SoundEvent getSwingSound()
	{
		return ModSoundEvents.SWORD_SWING.get();
	}
	
	@Override
	public WieldStyle getStyle(LivingCap<?> entityCap)
	{
		WeaponCap item = entityCap.getHeldWeaponCapability(InteractionHand.OFF_HAND);
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
		return Colliders.SHORTSWORD;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canBeRenderedBoth(ItemStack item)
	{
		WeaponCap cap = ModCapabilities.getMeleeWeaponCap(item);
		return super.canBeRenderedBoth(item) || (cap != null && cap.getWeaponCategory() == WeaponCategory.STRAIGHT_SWORD);
	}

	@Override
	public float getStaminaDamage()
	{
		return 4.0F;
	}
}