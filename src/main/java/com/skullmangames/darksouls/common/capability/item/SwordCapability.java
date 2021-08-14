package com.skullmangames.darksouls.common.capability.item;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.common.particle.HitParticleType;
import com.skullmangames.darksouls.common.skill.Skill;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.AttributeInit;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.Skills;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SwordCapability extends MaterialItemCapability
{
	private static List<StaticAnimation> swordAttackMotion;
	private static List<StaticAnimation> dualSwordAttackMotion;
	
	public SwordCapability(Item item)
	{
		super(item, WeaponCategory.SWORD);
		if (swordAttackMotion == null)
		{
			swordAttackMotion = new ArrayList<StaticAnimation> ();
			swordAttackMotion.add(Animations.SWORD_AUTO_1);
			swordAttackMotion.add(Animations.SWORD_AUTO_2);
			swordAttackMotion.add(Animations.SWORD_AUTO_3);
			swordAttackMotion.add(Animations.SWORD_DASH);
			dualSwordAttackMotion = new ArrayList<StaticAnimation> ();
			dualSwordAttackMotion.add(Animations.SWORD_DUAL_AUTO_1);
			dualSwordAttackMotion.add(Animations.SWORD_DUAL_AUTO_2);
			dualSwordAttackMotion.add(Animations.SWORD_DUAL_AUTO_3);
			dualSwordAttackMotion.add(Animations.SWORD_DUAL_DASH);
		}
	}
	
	@Override
	protected void registerAttribute()
	{
		int i = this.itemTier.getLevel();
		this.addStyleAttibute(WieldStyle.ONE_HAND, Pair.of(AttributeInit.MAX_STRIKES, AttributeInit.getMaxStrikesModifier(1)));
		this.addStyleAttibute(WieldStyle.ONE_HAND, Pair.of(AttributeInit.IMPACT, AttributeInit.getImpactModifier(0.5D + 0.2D * i)));
		this.addStyleAttibute(WieldStyle.TWO_HAND, Pair.of(AttributeInit.MAX_STRIKES, AttributeInit.getMaxStrikesModifier(1)));
		this.addStyleAttibute(WieldStyle.TWO_HAND, Pair.of(AttributeInit.IMPACT, AttributeInit.getImpactModifier(0.5D + 0.2D * i)));
	}
	
	@Override
	public Skill getSpecialAttack(PlayerData<?> playerdata)
	{
		if(this.getStyle(playerdata) == WieldStyle.ONE_HAND)
		{
			return Skills.SWEEPING_EDGE;
		}
		else
		{
			return Skills.DANCING_EDGE;
		}
	}
	
	@Override
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata)
	{
		if(this.getStyle(playerdata) == WieldStyle.ONE_HAND)
		{
			return swordAttackMotion;
		}
		else
		{
			return dualSwordAttackMotion;
		}
	}
	
	@Override
	public WieldStyle getStyle(LivingData<?> entitydata)
	{
		CapabilityItem item = entitydata.getHeldItemCapability(Hand.OFF_HAND);
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
	public SoundEvent getHitSound()
	{
		return null;
	}
	
	@Override
	public HitParticleType getHitParticle()
	{
		return null;
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
		CapabilityItem cap = item.getCapability(ModCapabilities.CAPABILITY_ITEM).orElse(null);
		return super.canBeRenderedBoth(item) || (cap != null && cap.weaponCategory == WeaponCategory.SWORD);
	}
}