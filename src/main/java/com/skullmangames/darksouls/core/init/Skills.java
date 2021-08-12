package com.skullmangames.darksouls.core.init;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.animation.property.Property.DamageProperty;
import com.skullmangames.darksouls.skill.FatalDrawSkill;
import com.skullmangames.darksouls.skill.KatanaPassive;
import com.skullmangames.darksouls.skill.LethalSlicingSkill;
import com.skullmangames.darksouls.skill.SimpleSpecialAttackSkill;
import com.skullmangames.darksouls.skill.Skill;
import com.skullmangames.darksouls.skill.DodgeSkill;
import com.skullmangames.darksouls.skill.SkillSlot;
import com.skullmangames.darksouls.util.IExtendedDamageSource.StunType;
import com.skullmangames.darksouls.util.math.ValueCorrector;

import net.minecraft.util.ResourceLocation;

public class Skills
{
	public static final Map<ResourceLocation, Skill> MODIFIABLE_SKILLS = new HashMap<ResourceLocation, Skill> ();
	public static Skill ROLL;
	public static Skill GUILLOTINE_AXE;
	public static Skill SWEEPING_EDGE;
	public static Skill DANCING_EDGE;
	public static Skill SLAUGHTER_STANCE;
	public static Skill HEARTPIERCER;
	public static Skill GIANT_WHIRLWIND;
	public static Skill FATAL_DRAW;
	public static Skill KATANA_GIMMICK;
	public static Skill LETHAL_SLICING;
	public static Skill RELENTLESS_COMBO;
	
	public static void init()
	{
		ROLL = makeSkill("roll", (skillName) ->
				new DodgeSkill(SkillSlot.DODGE, 2.0F, skillName, Animations.BIPED_ROLL_FORWARD, Animations.BIPED_ROLL_BACKWARD), true);
		
		SWEEPING_EDGE = makeSkill("sweeping_edge", (skillName) ->
			new SimpleSpecialAttackSkill(30.0F, skillName, Animations.SWEEPING_EDGE)
				.newPropertyLine()
				.addProperty(DamageProperty.MAX_STRIKES, ValueCorrector.getAdder(1))
				.addProperty(DamageProperty.DAMAGE, ValueCorrector.getMultiplier(1.0F))
				.addProperty(DamageProperty.ARMOR_NEGATION, ValueCorrector.getAdder(20.0F))
				.addProperty(DamageProperty.STUN_TYPE, StunType.LONG).registerPropertiesToAnimation(), false);
		
		DANCING_EDGE = makeSkill("dancing_edge", (skillName) ->
			new SimpleSpecialAttackSkill(30.0F, skillName, Animations.DANCING_EDGE)
				.newPropertyLine()
				.addProperty(DamageProperty.MAX_STRIKES, ValueCorrector.getAdder(1))
				.addProperty(DamageProperty.IMPACT, ValueCorrector.getAdder(0.5F)).registerPropertiesToAnimation(), false);
		
		GUILLOTINE_AXE = makeSkill("guillotine_axe", (skillName) ->
			new SimpleSpecialAttackSkill(20.0F, skillName, Animations.GUILLOTINE_AXE)
				.newPropertyLine()
				.addProperty(DamageProperty.MAX_STRIKES, ValueCorrector.getSetter(1))
				.addProperty(DamageProperty.DAMAGE, ValueCorrector.getMultiplier(1.5F))
				.addProperty(DamageProperty.ARMOR_NEGATION, ValueCorrector.getAdder(20.0F))
				.addProperty(DamageProperty.STUN_TYPE, StunType.LONG).registerPropertiesToAnimation(), false);
		
		SLAUGHTER_STANCE = makeSkill("slaughter_stance", (skillName) ->
			new SimpleSpecialAttackSkill(40.0F, skillName, Animations.SPEAR_SLASH)
				.newPropertyLine()
				.addProperty(DamageProperty.MAX_STRIKES, ValueCorrector.getAdder(5))
				.addProperty(DamageProperty.DAMAGE, ValueCorrector.getMultiplier(0.25F))
				.registerPropertiesToAnimation(), false);
		
		HEARTPIERCER = makeSkill("heartpiercer", (skillName) ->
			new SimpleSpecialAttackSkill(40.0F, skillName, Animations.SPEAR_THRUST)
				.newPropertyLine()
				.addProperty(DamageProperty.ARMOR_NEGATION, ValueCorrector.getAdder(10.0F))
				.addProperty(DamageProperty.STUN_TYPE, StunType.HOLD).registerPropertiesToAnimation(), false);
		
		GIANT_WHIRLWIND = makeSkill("giant_whirlwind", (skillName) ->
			new SimpleSpecialAttackSkill(60.0F, skillName, Animations.GIANT_WHIRLWIND)
				.newPropertyLine(), false);
		
		FATAL_DRAW = makeSkill("fatal_draw", (skillName) ->
			new FatalDrawSkill(skillName)
				.newPropertyLine()
				.addProperty(DamageProperty.DAMAGE, ValueCorrector.getMultiplier(1.0F))
				.addProperty(DamageProperty.ARMOR_NEGATION, ValueCorrector.getAdder(50.0F))
				.addProperty(DamageProperty.MAX_STRIKES, ValueCorrector.getAdder(6))
				.addProperty(DamageProperty.STUN_TYPE, StunType.HOLD).registerPropertiesToAnimation(), false);
		
		KATANA_GIMMICK = new KatanaPassive();
		
		LETHAL_SLICING = makeSkill("lethal_slicing", (skillName) ->
			new LethalSlicingSkill(50.0F, skillName)
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
				.registerPropertiesToAnimation(), false);
		
		RELENTLESS_COMBO = makeSkill("relentless_combo", (skillName) ->
			new SimpleSpecialAttackSkill(20.0F, skillName, Animations.RELENTLESS_COMBO)
				.newPropertyLine()
				.addProperty(DamageProperty.MAX_STRIKES, ValueCorrector.getSetter(1))
				.addProperty(DamageProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(DamageProperty.PARTICLE, null)
				.registerPropertiesToAnimation(), false);
	}
	
	public static Skill makeSkill(String skillName, Function<String, Skill> object, boolean registerSkillBook)
	{
		if (registerSkillBook)
		{
			MODIFIABLE_SKILLS.put(new ResourceLocation(DarkSouls.MOD_ID, skillName), object.apply(skillName));
		}
		
		return object.apply(skillName);
	}
}