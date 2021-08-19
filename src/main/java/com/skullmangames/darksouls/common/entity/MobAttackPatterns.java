package com.skullmangames.darksouls.common.entity;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.core.init.Animations;

public class MobAttackPatterns
{
	public static List<AttackAnimation> BIPED_ARMED_ONEHAND = new ArrayList<AttackAnimation> ();
	static List<AttackAnimation> BIPED_ARMED_SPEAR = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> BIPED_MOUNT_SWORD = new ArrayList<AttackAnimation> ();
	static List<AttackAnimation> ENDERMAN_PATTERN1 = new ArrayList<AttackAnimation> ();
	static List<AttackAnimation> ENDERMAN_PATTERN2 = new ArrayList<AttackAnimation> ();
	static List<AttackAnimation> ENDERMAN_PATTERN3 = new ArrayList<AttackAnimation> ();
	static List<AttackAnimation> ENDERMAN_PATTERN4 = new ArrayList<AttackAnimation> ();
	static List<AttackAnimation> GOLEM_PATTERN1 = new ArrayList<AttackAnimation> ();
	static List<AttackAnimation> GOLEM_PATTERN2 = new ArrayList<AttackAnimation> ();
	static List<AttackAnimation> GOLEM_PATTERN3 = new ArrayList<AttackAnimation> ();
	static List<AttackAnimation> HOGLIN_PATTERN = new ArrayList<AttackAnimation> ();
	static List<AttackAnimation> RAVAGER_PATTERN1 = new ArrayList<AttackAnimation> ();
	static List<AttackAnimation> RAVAGER_PATTERN2 = new ArrayList<AttackAnimation> ();
	static List<AttackAnimation> SPIDER_PATTERN = new ArrayList<AttackAnimation> ();
	static List<AttackAnimation> SPIDER_JUMP_PATTERN = new ArrayList<AttackAnimation> ();
	static List<AttackAnimation> VINDICATOR_PATTERN = new ArrayList<AttackAnimation> ();
	static List<AttackAnimation> WITHER_SKELETON_PATTERN = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> ZOMBIE_NORAML = new ArrayList<AttackAnimation> ();
	
	public static void setVariousMobAttackPatterns()
	{
		BIPED_ARMED_ONEHAND.add((AttackAnimation) Animations.BIPED_ARMED_MOB_ATTACK1);
		BIPED_ARMED_ONEHAND.add((AttackAnimation) Animations.BIPED_ARMED_MOB_ATTACK2);
		BIPED_ARMED_SPEAR.add((AttackAnimation) Animations.SPEAR_ONEHAND_AUTO);
		BIPED_MOUNT_SWORD.add((AttackAnimation) Animations.SWORD_MOUNT_ATTACK);
		ENDERMAN_PATTERN1.add((AttackAnimation) Animations.ENDERMAN_KNEE);
		ENDERMAN_PATTERN2.add((AttackAnimation) Animations.ENDERMAN_KICK_COMBO);
		ENDERMAN_PATTERN3.add((AttackAnimation) Animations.ENDERMAN_KICK1);
		ENDERMAN_PATTERN4.add((AttackAnimation) Animations.ENDERMAN_KICK2);
		GOLEM_PATTERN1.add((AttackAnimation)Animations.GOLEM_ATTACK1);
		GOLEM_PATTERN2.add((AttackAnimation)Animations.GOLEM_ATTACK2);
		GOLEM_PATTERN3.add((AttackAnimation)Animations.GOLEM_ATTACK3);
		GOLEM_PATTERN3.add((AttackAnimation)Animations.GOLEM_ATTACK4);
		RAVAGER_PATTERN1.add((AttackAnimation)Animations.RAVAGER_ATTACK1);
		RAVAGER_PATTERN1.add((AttackAnimation)Animations.RAVAGER_ATTACK2);
		RAVAGER_PATTERN2.add((AttackAnimation)Animations.RAVAGER_ATTACK3);
		SPIDER_PATTERN.add((AttackAnimation) Animations.SPIDER_ATTACK);
		SPIDER_JUMP_PATTERN.add((AttackAnimation) Animations.SPIDER_JUMP_ATTACK);
		VINDICATOR_PATTERN.add((AttackAnimation) Animations.VINDICATOR_SWING_AXE1);
		VINDICATOR_PATTERN.add((AttackAnimation) Animations.VINDICATOR_SWING_AXE2);
		VINDICATOR_PATTERN.add((AttackAnimation) Animations.VINDICATOR_SWING_AXE3);
		WITHER_SKELETON_PATTERN.add((AttackAnimation) Animations.WITHER_SKELETON_ATTACK1);
		WITHER_SKELETON_PATTERN.add((AttackAnimation) Animations.WITHER_SKELETON_ATTACK2);
		WITHER_SKELETON_PATTERN.add((AttackAnimation) Animations.WITHER_SKELETON_ATTACK3);
		ZOMBIE_NORAML.add((AttackAnimation) Animations.ZOMBIE_ATTACK1);
		ZOMBIE_NORAML.add((AttackAnimation) Animations.ZOMBIE_ATTACK2);
		ZOMBIE_NORAML.add((AttackAnimation) Animations.ZOMBIE_ATTACK3);
		HOGLIN_PATTERN.add((AttackAnimation) Animations.HOGLIN_ATTACK);
	}
}