package com.skullmangames.darksouls.common.capability.item;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.world.item.Item;
import net.minecraft.sounds.SoundEvent;

public class TridentCapability extends RangedWeaponCapability
{
	private static List<StaticAnimation> attackMotion;
	private static List<StaticAnimation> mountAttackMotion;
	
	public TridentCapability(Item item, int requiredStrength, int requiredDex, Scaling strengthScaling, Scaling dexScaling)
	{
		super(item, null, Animations.BIPED_SPEER_AIM, Animations.BIPED_SPEER_REBOUND, requiredStrength, requiredDex, strengthScaling, dexScaling);

		if (attackMotion == null)
		{
			attackMotion = new ArrayList<StaticAnimation> ();
		}
		
		if (mountAttackMotion == null)
		{
			mountAttackMotion = new ArrayList<StaticAnimation> ();
		}
	}
	
	@Override
	public WieldStyle getStyle(LivingData<?> entitydata)
	{
		return WieldStyle.ONE_HAND;
	}
	
	@Override
	public SoundEvent getHitSound()
	{
		return null;
	}

	@Override
	public Collider getWeaponCollider()
	{
		return null;
	}
	
	@Override
	public List<StaticAnimation> getMountAttackMotion()
	{
		return mountAttackMotion;
	}
	
	@Override
	public final HandProperty getHandProperty()
	{
		return HandProperty.MAINHAND_ONLY;
	}
}