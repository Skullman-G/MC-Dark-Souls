package com.skullmangames.darksouls.core.init;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;

public class MobAttackPatterns
{
	public static List<AttackAnimation> BIPED_ARMED_ONEHAND = new ArrayList<AttackAnimation>();
	static List<AttackAnimation> BIPED_ARMED_SPEAR = new ArrayList<AttackAnimation>();
	public static List<AttackAnimation> BIPED_MOUNT_SWORD = new ArrayList<AttackAnimation>();
	public static List<AttackAnimation> HOLLOW_LIGHT_PATTERN = new ArrayList<AttackAnimation>();
	public static List<AttackAnimation> HOLLOW_OTHER_PATTERN = new ArrayList<AttackAnimation>();
	
	public static void setVariousMobAttackPatterns()
	{
		BIPED_ARMED_ONEHAND.add((AttackAnimation)Animations.BIPED_ARMED_MOB_ATTACK1);
		BIPED_ARMED_ONEHAND.add((AttackAnimation)Animations.BIPED_ARMED_MOB_ATTACK2);
		BIPED_ARMED_SPEAR.add((AttackAnimation)Animations.SPEAR_ONEHAND_AUTO);
		BIPED_MOUNT_SWORD.add((AttackAnimation)Animations.SWORD_MOUNT_ATTACK);
		
		
		HOLLOW_LIGHT_PATTERN = Animations.HOLLOW_SWING;
		
		HOLLOW_OTHER_PATTERN.add(Animations.HOLLOW_OVERHEAD_SWING);
		HOLLOW_OTHER_PATTERN.add(Animations.HOLLOW_FURY_SWING);
	}
}