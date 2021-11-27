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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SwordCapability extends MaterialItemCapability
{
	public SwordCapability(Item item)
	{
		super(item, WeaponCategory.SWORD);
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
		this.addStyleAttibute(WieldStyle.ONE_HAND, Pair.of(ModAttributes.MAX_STRIKES, ModAttributes.getMaxStrikesModifier(1)));
		this.addStyleAttibute(WieldStyle.ONE_HAND, Pair.of(ModAttributes.IMPACT, ModAttributes.getImpactModifier(0.5D + 0.2D * i)));
		this.addStyleAttibute(WieldStyle.TWO_HAND, Pair.of(ModAttributes.MAX_STRIKES, ModAttributes.getMaxStrikesModifier(1)));
		this.addStyleAttibute(WieldStyle.TWO_HAND, Pair.of(ModAttributes.IMPACT, ModAttributes.getImpactModifier(0.5D + 0.2D * i)));
	}
	
	@Override
	protected AttackAnimation getHeavyAttack()
	{
		return Animations.SWEEPING_EDGE;
	}
	
	@Override
	protected AttackAnimation[] getLightAttack()
	{
		return Animations.SWORD_LIGHT_ATTACK;
	}
	
	@Override
	protected AttackAnimation getDashAttack()
	{
		return Animations.SWORD_DASH_ATTACK;
	}
	
	@Override
	public WieldStyle getStyle(LivingData<?> entitydata)
	{
		WeaponCapability item = entitydata.getHeldWeaponCapability(Hand.OFF_HAND);
		if(item != null && item.weaponCategory == WeaponCategory.SWORD)
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
		WeaponCapability cap = ModCapabilities.stackWeaponCapabilityGetter(item);
		return super.canBeRenderedBoth(item) || (cap != null && cap.weaponCategory == WeaponCategory.SWORD);
	}
}