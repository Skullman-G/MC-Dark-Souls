package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.common.animation.property.Property.DamageProperty;
import com.skullmangames.darksouls.common.skill.DodgeSkill;
import com.skullmangames.darksouls.common.skill.FatalDrawSkill;
import com.skullmangames.darksouls.common.skill.LethalSlicingSkill;
import com.skullmangames.darksouls.common.skill.LightAttackSkill;
import com.skullmangames.darksouls.common.skill.SimpleHeavyAttackSkill;
import com.skullmangames.darksouls.common.skill.Skill;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.math.ValueCorrector;

public class Skills
{
	public static final Skill TOOL_LIGHT_ATTACK = new LightAttackSkill(0, "tool_light_attack", Animations.TOOL_LIGHT_ATTACK, Animations.TOOL_DASH_ATTACK);
	
	public static final Skill FIST_LIGHT_ATTACK = new LightAttackSkill(0, "fist_light_attack", Animations.FIST_LIGHT_ATTACK);
	
	public static final Skill AXE_LIGHT_ATTACK = new LightAttackSkill(0, "axe_light_attack", Animations.AXE_LIGHT_ATTACK, Animations.AXE_DASH_ATTACK);
	
	public static final Skill SWORD_LIGHT_ATTACK = new LightAttackSkill(0, "sword_light_attack", Animations.SWORD_LIGHT_ATTACK, Animations.SWORD_DASH_ATTACK);
	
	public static final Skill ROLL = new DodgeSkill("roll", Animations.BIPED_ROLL_FORWARD, Animations.BIPED_ROLL_BACKWARD);
	
	public static final Skill GREAT_HAMMER_HEAVY_ATTACK = new SimpleHeavyAttackSkill(0, "great_hammer_heavy_attack", Animations.GREAT_HAMMER_HEAVY_ATTACK);
	
	public static final Skill SWEEPING_EDGE = new SimpleHeavyAttackSkill("sweeping_edge", Animations.SWEEPING_EDGE)
			.newPropertyLine()
			.addProperty(DamageProperty.MAX_STRIKES, ValueCorrector.getAdder(1))
			.addProperty(DamageProperty.DAMAGE, ValueCorrector.getMultiplier(1.0F))
			.addProperty(DamageProperty.ARMOR_NEGATION, ValueCorrector.getAdder(20.0F))
			.addProperty(DamageProperty.STUN_TYPE, StunType.LONG).registerPropertiesToAnimation();
	
	public static final Skill GUILLOTINE_AXE = new SimpleHeavyAttackSkill("guillotine_axe", Animations.GUILLOTINE_AXE)
			.newPropertyLine()
			.addProperty(DamageProperty.MAX_STRIKES, ValueCorrector.getSetter(1))
			.addProperty(DamageProperty.DAMAGE, ValueCorrector.getMultiplier(1.5F))
			.addProperty(DamageProperty.ARMOR_NEGATION, ValueCorrector.getAdder(20.0F))
			.addProperty(DamageProperty.STUN_TYPE, StunType.LONG).registerPropertiesToAnimation();
	
	public static final Skill DANCING_EDGE = new SimpleHeavyAttackSkill("dancing_edge", Animations.DANCING_EDGE)
			.newPropertyLine()
			.addProperty(DamageProperty.MAX_STRIKES, ValueCorrector.getAdder(1))
			.addProperty(DamageProperty.IMPACT, ValueCorrector.getAdder(0.5F)).registerPropertiesToAnimation();
	
	public static final Skill SLAUGHTER_STANCE = new SimpleHeavyAttackSkill("slaughter_stance", Animations.SPEAR_SLASH)
			.newPropertyLine()
			.addProperty(DamageProperty.MAX_STRIKES, ValueCorrector.getAdder(5))
			.addProperty(DamageProperty.DAMAGE, ValueCorrector.getMultiplier(0.25F))
			.registerPropertiesToAnimation();
	
	public static final Skill HEARTPIERCER = new SimpleHeavyAttackSkill("heartpiercer", Animations.SPEAR_THRUST)
			.newPropertyLine()
			.addProperty(DamageProperty.ARMOR_NEGATION, ValueCorrector.getAdder(10.0F))
			.addProperty(DamageProperty.STUN_TYPE, StunType.HOLD).registerPropertiesToAnimation();
	
	public static final Skill GIANT_WHIRLWIND = new SimpleHeavyAttackSkill("giant_whirlwind", Animations.GIANT_WHIRLWIND)
			.newPropertyLine();
	
	public static final Skill FATAL_DRAW = new FatalDrawSkill("fatal_draw")
			.newPropertyLine()
			.addProperty(DamageProperty.DAMAGE, ValueCorrector.getMultiplier(1.0F))
			.addProperty(DamageProperty.ARMOR_NEGATION, ValueCorrector.getAdder(50.0F))
			.addProperty(DamageProperty.MAX_STRIKES, ValueCorrector.getAdder(6))
			.addProperty(DamageProperty.STUN_TYPE, StunType.HOLD).registerPropertiesToAnimation();
	
	public static final Skill LETHAL_SLICING = new LethalSlicingSkill("lethal_slicing")
			.newPropertyLine()
			.addProperty(DamageProperty.MAX_STRIKES, ValueCorrector.getSetter(2))
			.addProperty(DamageProperty.IMPACT, ValueCorrector.getSetter(0.5F))
			.addProperty(DamageProperty.DAMAGE, ValueCorrector.getSetter(1.0F))
			.addProperty(DamageProperty.STUN_TYPE, StunType.LONG)
			.addProperty(DamageProperty.HIT_SOUND, null)
			.addProperty(DamageProperty.PARTICLE, null)
			.newPropertyLine()
			.addProperty(DamageProperty.ARMOR_NEGATION, ValueCorrector.getAdder(50.0F))
			.addProperty(DamageProperty.MAX_STRIKES, ValueCorrector.getAdder(2))
			.addProperty(DamageProperty.DAMAGE, ValueCorrector.getMultiplier(0.7F))
			.addProperty(DamageProperty.SWING_SOUND, null)
			.registerPropertiesToAnimation();
	
	public static final Skill RELENTLESS_COMBO = new SimpleHeavyAttackSkill("relentless_combo", Animations.RELENTLESS_COMBO)
			.newPropertyLine()
			.addProperty(DamageProperty.MAX_STRIKES, ValueCorrector.getSetter(1))
			.addProperty(DamageProperty.STUN_TYPE, StunType.HOLD)
			.addProperty(DamageProperty.PARTICLE, null)
			.registerPropertiesToAnimation();
	
	public static final Skill SHIELD_ATTACK = new LightAttackSkill(0, "shield_attack", Animations.SHIELD_LIGHT_ATTACK);
	
	public static final Skill GREAT_HAMMER_WEAK_ATTACK = new LightAttackSkill(0, "great_hammer_weak_attack", Animations.GREAT_HAMMER_WEAK_ATTACK);
}