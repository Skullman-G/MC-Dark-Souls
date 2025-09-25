package com.skullmangames.darksouls.core.data_provider;

import java.util.List;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.data.WeaponMovesets;
import com.skullmangames.darksouls.core.util.WeaponMoveset;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;

public class WeaponMovesetProvider extends ConfigDataProvider<WeaponMoveset.Builder>
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public WeaponMovesetProvider(DataGenerator generator)
	{
		super(generator);
	}
	
	@Override
	protected Logger getLogger()
	{
		return LOGGER;
	}
	
	@Override
	protected String getSubFolder()
	{
		return "weapon_movesets";
	}

	@Override
	public String getName()
	{
		return "WeaponMovesets";
	}
	
	@Override
	protected List<WeaponMoveset.Builder> data()
	{
		return ImmutableList.of
		(
			new WeaponMoveset.Builder(WeaponMovesets.FIST.getId())
			.putMove(AttackType.LIGHT, true, Animations.FIST_LIGHT_ATTACK.getIds())
			.putMove(AttackType.HEAVY, true, Animations.FIST_HEAVY_ATTACK.getId())
			.putMove(AttackType.DASH, true, Animations.FIST_DASH_ATTACK.getId())
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_STRIKE.getId())
			.putMove(AttackType.PUNISH, true, Animations.PUNISH_STRIKE.getId()),
			new WeaponMoveset.Builder(WeaponMovesets.STRAIGHT_SWORD.getId())
			.putMove(AttackType.LIGHT, true, Animations.STRAIGHT_SWORD_LIGHT_ATTACK.getIds())
			.putMove(AttackType.HEAVY, true, Animations.STRAIGHT_SWORD_HEAVY_ATTACK.getId())
			.putMove(AttackType.DASH, true, Animations.STRAIGHT_SWORD_DASH_ATTACK.getId())
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_THRUST.getId())
			.putMove(AttackType.PUNISH, true, Animations.PUNISH_THRUST.getId())
			.putMove(AttackType.TWO_HANDED_LIGHT, true, Animations.STRAIGHT_SWORD_TH_LIGHT_ATTACK.getIds())
			.putMove(AttackType.TWO_HANDED_HEAVY, true, Animations.STRAIGHT_SWORD_TH_HEAVY_ATTACK.getId())
			.putMove(AttackType.TWO_HANDED_DASH, true, Animations.STRAIGHT_SWORD_TH_DASH_ATTACK.getId()),
			new WeaponMoveset.Builder(WeaponMovesets.AXE.getId())
			.putMove(AttackType.LIGHT, true, Animations.AXE_LIGHT_ATTACK.getIds())
			.putMove(AttackType.HEAVY, true, Animations.AXE_HEAVY_ATTACK.getId())
			.putMove(AttackType.DASH, true, Animations.AXE_DASH_ATTACK.getId())
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_STRIKE.getId())
			.putMove(AttackType.PUNISH, true, Animations.PUNISH_STRIKE.getId())
			.putMove(AttackType.TWO_HANDED_LIGHT, true, Animations.AXE_TH_LIGHT_ATTACK.getIds())
			.putMove(AttackType.TWO_HANDED_HEAVY, true, Animations.AXE_TH_HEAVY_ATTACK.getId())
			.putMove(AttackType.TWO_HANDED_DASH, true, Animations.AXE_TH_DASH_ATTACK.getId()),
			new WeaponMoveset.Builder(WeaponMovesets.BLACK_KNIGHT_SWORD.getId())
			.putMove(AttackType.LIGHT, true, Animations.GREATSWORD_LIGHT_ATTACK.getIds())
			.putMove(AttackType.HEAVY, true, Animations.GREATSWORD_STYLISH_THRUST.getId())
			.putMove(AttackType.DASH, true, Animations.GREATSWORD_DASH_ATTACK.getId())
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_THRUST.getId())
			.putMove(AttackType.PUNISH, true, Animations.PUNISH_THRUST.getId())
			.putMove(AttackType.TWO_HANDED_LIGHT, true, Animations.GREATSWORD_TH_LIGHT_ATTACK.getIds())
			.putMove(AttackType.TWO_HANDED_HEAVY, true, Animations.GREATSWORD_TH_THRUST_ATTACK.getIds())
			.putMove(AttackType.TWO_HANDED_DASH, true, Animations.GREATSWORD_TH_DASH_ATTACK.getId()),
			new WeaponMoveset.Builder(WeaponMovesets.CLAYMORE.getId())
			.putMove(AttackType.LIGHT, true, Animations.GREATSWORD_LIGHT_ATTACK.getIds())
			.putMove(AttackType.HEAVY, true, new ResourceLocation[] {Animations.GREATSWORD_THRUST.getId(), Animations.GREATSWORD_UPWARD_SLASH.getId()})
			.putMove(AttackType.DASH, true, Animations.GREATSWORD_DASH_ATTACK.getId())
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_THRUST.getId())
			.putMove(AttackType.PUNISH, true, Animations.PUNISH_THRUST.getId())
			.putMove(AttackType.TWO_HANDED_LIGHT, true, Animations.GREATSWORD_TH_LIGHT_ATTACK.getIds())
			.putMove(AttackType.TWO_HANDED_HEAVY, true, Animations.GREATSWORD_TH_THRUST_ATTACK.getIds())
			.putMove(AttackType.TWO_HANDED_DASH, true, Animations.GREATSWORD_TH_DASH_ATTACK.getId()),
			new WeaponMoveset.Builder(WeaponMovesets.ULTRA_GREATSWORD.getId())
			.putMove(AttackType.LIGHT, true, Animations.ULTRA_GREATSWORD_LIGHT_ATTACK.getIds())
			.putMove(AttackType.HEAVY, false, Animations.ULTRA_GREATSWORD_HEAVY_ATTACK.getIds())
			.putMove(AttackType.DASH, true, Animations.ULTRA_GREATSWORD_DASH_ATTACK.getId())
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_STRIKE.getId())
			.putMove(AttackType.PUNISH, true, Animations.PUNISH_STRIKE.getId())
			.putMove(AttackType.TWO_HANDED_LIGHT, true, Animations.ULTRA_GREATSWORD_TH_LIGHT_ATTACK.getIds())
			.putMove(AttackType.TWO_HANDED_HEAVY, false, Animations.ULTRA_GREATSWORD_TH_HEAVY_ATTACK.getIds())
			.putMove(AttackType.TWO_HANDED_DASH, true, Animations.ULTRA_GREATSWORD_TH_DASH_ATTACK.getId()),
			new WeaponMoveset.Builder(WeaponMovesets.SHIELD.getId())
			.putMove(AttackType.LIGHT, true, Animations.SHIELD_LIGHT_ATTACK.getId())
			.putMove(AttackType.HEAVY, true, Animations.SHIELD_HEAVY_ATTACK.getIds())
			.putMove(AttackType.DASH, true, Animations.SHIELD_DASH_ATTACK.getId())
			.putMove(AttackType.TWO_HANDED_LIGHT, true, Animations.SHIELD_TH_LIGHT_ATTACK.getId())
			.putMove(AttackType.TWO_HANDED_HEAVY, true, Animations.SHIELD_TH_HEAVY_ATTACK.getIds())
			.putMove(AttackType.TWO_HANDED_DASH, true, Animations.SHIELD_TH_DASH_ATTACK.getId()),
			new WeaponMoveset.Builder(WeaponMovesets.GREATSHIELD.getId())
			.putMove(AttackType.LIGHT, true, Animations.GREATSHIELD_LIGHT_ATTACK.getId())
			.putMove(AttackType.HEAVY, true, Animations.GREATSHIELD_HEAVY_ATTACK.getId())
			.putMove(AttackType.DASH, true, Animations.GREATSHIELD_DASH_ATTACK.getId())
			.putMove(AttackType.TWO_HANDED_LIGHT, true, Animations.GREATSHIELD_TH_LIGHT_ATTACK.getId())
			.putMove(AttackType.TWO_HANDED_HEAVY, true, Animations.GREATSHIELD_TH_HEAVY_ATTACK.getId())
			.putMove(AttackType.TWO_HANDED_DASH, true, Animations.GREATSHIELD_TH_DASH_ATTACK.getId()),
			new WeaponMoveset.Builder(WeaponMovesets.SPEAR.getId())
			.putMove(AttackType.LIGHT, true, Animations.SPEAR_LIGHT_ATTACK.getId())
			.putMove(AttackType.HEAVY, true, Animations.SPEAR_HEAVY_ATTACK.getId())
			.putMove(AttackType.DASH, true, Animations.SPEAR_DASH_ATTACK.getId())
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_THRUST.getId())
			.putMove(AttackType.PUNISH, true, Animations.PUNISH_THRUST.getId())
			.putMove(AttackType.TWO_HANDED_LIGHT, true, Animations.SPEAR_TH_LIGHT_ATTACK.getId())
			.putMove(AttackType.TWO_HANDED_HEAVY, true, Animations.SPEAR_TH_HEAVY_ATTACK.getId())
			.putMove(AttackType.TWO_HANDED_DASH, true, Animations.SPEAR_TH_DASH_ATTACK.getId()),
			new WeaponMoveset.Builder(WeaponMovesets.HAMMER.getId())
			.putMove(AttackType.LIGHT, true, Animations.HAMMER_LIGHT_ATTACK.getId())
			.putMove(AttackType.HEAVY, true, Animations.HAMMER_HEAVY_ATTACK.getId())
			.putMove(AttackType.DASH, true, Animations.HAMMER_DASH_ATTACK.getId())
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_STRIKE.getId())
			.putMove(AttackType.PUNISH, true, Animations.PUNISH_STRIKE.getId())
			.putMove(AttackType.TWO_HANDED_LIGHT, true, Animations.HAMMER_TH_LIGHT_ATTACK.getId())
			.putMove(AttackType.TWO_HANDED_HEAVY, true, Animations.HAMMER_TH_HEAVY_ATTACK.getId())
			.putMove(AttackType.TWO_HANDED_DASH, true, Animations.HAMMER_TH_DASH_ATTACK.getId()),
			new WeaponMoveset.Builder(WeaponMovesets.DAGGER.getId())
			.putMove(AttackType.LIGHT, true, Animations.DAGGER_LIGHT_ATTACK.getIds())
			.putMove(AttackType.HEAVY, true, Animations.DAGGER_HEAVY_ATTACK.getId())
			.putMove(AttackType.DASH, true, Animations.STRAIGHT_SWORD_DASH_ATTACK.getId())
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_THRUST.getId()),
			new WeaponMoveset.Builder(WeaponMovesets.GREAT_HAMMER.getId())
			.putMove(AttackType.LIGHT, false, Animations.GREAT_HAMMER_LIGHT_ATTACK.getIds())
			.putMove(AttackType.HEAVY, false, Animations.GREAT_HAMMER_HEAVY_ATTACK.getId())
			.putMove(AttackType.DASH, true, Animations.GREAT_HAMMER_DASH_ATTACK.getId())
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_STRIKE.getId())
			.putMove(AttackType.PUNISH, true, Animations.PUNISH_STRIKE.getId())
			.putMove(AttackType.TWO_HANDED_LIGHT, true, Animations.GREAT_HAMMER_TH_LIGHT_ATTACK.getIds())
			.putMove(AttackType.TWO_HANDED_HEAVY, true, Animations.GREAT_HAMMER_TH_HEAVY_ATTACK.getId())
			.putMove(AttackType.TWO_HANDED_DASH, true, Animations.GREAT_HAMMER_TH_DASH_ATTACK.getId()),
			new WeaponMoveset.Builder(WeaponMovesets.THRUSTING_SWORD.getId())
			.putMove(AttackType.LIGHT, true, Animations.THRUSTING_SWORD_LIGHT_ATTACK.getId())
			.putMove(AttackType.HEAVY, true, Animations.THRUSTING_SWORD_HEAVY_ATTACK.getIds())
			.putMove(AttackType.DASH, true, Animations.THRUSTING_SWORD_DASH_ATTACK.getId())
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_THRUST.getId())
			.putMove(AttackType.PUNISH, true, Animations.PUNISH_THRUST.getId())
			.putMove(AttackType.TWO_HANDED_LIGHT, true, Animations.THRUSTING_SWORD_TH_LIGHT_ATTACK.getId())
			.putMove(AttackType.TWO_HANDED_HEAVY, true, Animations.THRUSTING_SWORD_TH_HEAVY_ATTACK.getIds())
			.putMove(AttackType.TWO_HANDED_DASH, true, Animations.THRUSTING_SWORD_TH_DASH_ATTACK.getId()),
			new WeaponMoveset.Builder(WeaponMovesets.GREATAXE.getId())
			.putMove(AttackType.LIGHT, true, Animations.GREATAXE_LIGHT_ATTACK.getIds())
			.putMove(AttackType.HEAVY, true, Animations.GREATAXE_HEAVY_ATTACK.getId())
			.putMove(AttackType.DASH, true, Animations.GREATAXE_DASH_ATTACK.getId())
			.putMove(AttackType.BACKSTAB, true, Animations.BACKSTAB_STRIKE.getId())
			.putMove(AttackType.PUNISH, true, Animations.PUNISH_STRIKE.getId())
			.putMove(AttackType.TWO_HANDED_LIGHT, true, Animations.GREATAXE_TH_LIGHT_ATTACK.getIds())
			.putMove(AttackType.TWO_HANDED_HEAVY, true, Animations.GREATAXE_TH_HEAVY_ATTACK.getId())
			.putMove(AttackType.TWO_HANDED_DASH, true, Animations.GREATAXE_TH_DASH_ATTACK.getId())
		);
	}
}
