package com.skullmangames.darksouls.common.capability.item;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModAttributes;
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

public class StraightSwordCapability extends MaterialItemCapability
{
	public StraightSwordCapability(Item item)
	{
		super(item, WeaponCategory.STRAIGHT_SWORD);
	}
	
	@Override
	public SoundEvent getSwingSound()
	{
		return ModSoundEvents.SWORD_SWING;
	}
	
	@Override
	protected void registerAttribute()
	{
		int i = this.itemTier.getLevel();
		this.addStyleAttibute(WieldStyle.ONE_HAND, Pair.of(ModAttributes.IMPACT, ModAttributes.getImpactModifier(0.5D + 0.2D * i)));
		this.addStyleAttibute(WieldStyle.TWO_HAND, Pair.of(ModAttributes.IMPACT, ModAttributes.getImpactModifier(0.5D + 0.2D * i)));
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
		WeaponCapability item = entitydata.getHeldWeaponCapability(InteractionHand.OFF_HAND);
		if(item != null && item.weaponCategory == WeaponCategory.STRAIGHT_SWORD)
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
		WeaponCapability cap = ModCapabilities.getWeaponCapability(item);
		return super.canBeRenderedBoth(item) || (cap != null && cap.weaponCategory == WeaponCategory.STRAIGHT_SWORD);
	}
}