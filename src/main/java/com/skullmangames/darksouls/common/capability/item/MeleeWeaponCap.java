package com.skullmangames.darksouls.common.capability.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.capability.entity.ClientPlayerData;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MeleeWeaponCap extends WeaponCap implements IShield
{
	private final Map<AttackType, Pair<Boolean, AttackAnimation[]>> moveset;
	
	public MeleeWeaponCap(Item item, WeaponCategory category, int requiredStrength, int requiredDex,
			Scaling strengthScaling, Scaling dexScaling, float poiseDamage)
	{
		super(item, category, requiredStrength, requiredDex, strengthScaling, dexScaling, poiseDamage);
		this.moveset = this.initMoveset().build();
	}
	
	protected ImmutableMap.Builder<AttackType, Pair<Boolean, AttackAnimation[]>> initMoveset()
	{
		return ImmutableMap.builder();
	}
	
	protected void putMove(ImmutableMap.Builder<AttackType, Pair<Boolean, AttackAnimation[]>> builder, AttackType type, boolean repeat, AttackAnimation... animations)
	{
		builder.put(type, new Pair<Boolean, AttackAnimation[]>(repeat, animations));
	}
	
	@Override
	public void onHeld(PlayerData<?> playerdata)
	{
		super.onHeld(playerdata);
		AttributeInstance instance = playerdata.getOriginalEntity().getAttribute(Attributes.ATTACK_DAMAGE);
		instance.removeModifier(ModAttributes.EUIPMENT_MODIFIER_UUIDS[EquipmentSlot.MAINHAND.getIndex()]);
		instance.addTransientModifier(ModAttributes.getAttributeModifierForSlot(EquipmentSlot.MAINHAND, this.getDamage()));
	}
	
	public InteractionResult onUse(Player player, InteractionHand hand)
	{
		player.startUsingItem(hand);
		return InteractionResult.CONSUME;
	}
	
	public float getDamage()
	{
		return this.orgItem instanceof SwordItem ? ((SwordItem) this.orgItem).getDamage()
				: this.orgItem instanceof DiggerItem ? ((DiggerItem) this.orgItem).getAttackDamage()
				: 0.0F;
	}
	
	public AttackAnimation[] getAttacks(AttackType type)
	{
		return this.moveset.get(type).getSecond();
	}

	@OnlyIn(Dist.CLIENT)
	public AttackAnimation getAttack(AttackType type, ClientPlayerData playerdata)
	{
		if (!this.meetRequirements(playerdata) && this.getWeakAttack() != null) return this.getWeakAttack();

		Pair<Boolean, AttackAnimation[]> move = this.moveset.get(type);
		if (move == null) return null;
		AttackAnimation[] animations = move.getSecond();
		if (animations == null) return null;
		List<AttackAnimation> animationList = new ArrayList<AttackAnimation>(Arrays.asList(animations));
		int combo = animationList.indexOf(playerdata.getClientAnimator().baseLayer.animationPlayer.getPlay());
		if (combo + 1 < animations.length) combo += 1;
		else if (move.getFirst()) combo = 0;
		return animations[combo];
	}

	protected AttackAnimation getWeakAttack()
	{
		return null;
	}

	public List<StaticAnimation> getMountAttackMotion()
	{
		return null;
	}
	
	public SoundEvent getSwingSound()
	{
		return null;
	}

	public SoundEvent getHitSound()
	{
		return null;
	}

	public SoundEvent getSmashSound()
	{
		return null;
	}

	public Collider getWeaponCollider()
	{
		return Colliders.FIST;
	}

	@Override
	public float getPhysicalDefense()
	{
		return 0.1F;
	}

	@Override
	public ShieldType getShieldType()
	{
		return ShieldType.NONE;
	}
	
	public enum AttackType
	{
		LIGHT, HEAVY, DASH
	}
}
