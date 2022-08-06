package com.skullmangames.darksouls.common.capability.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.util.physics.Collider;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSPlayAnimation;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class MeleeWeaponCap extends WeaponCap implements IShield
{
	private final Map<AttackType, Pair<Boolean, AttackAnimation[]>> moveset;
	
	public MeleeWeaponCap(Item item, WeaponCategory category, int reqStrength, int reqDex, int reqFaith,
			Scaling strengthScaling, Scaling dexScaling, Scaling faithScaling, float poiseDamage)
	{
		super(item, category, reqStrength, reqDex, reqFaith, strengthScaling, dexScaling, faithScaling, poiseDamage);
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
	public void onHeld(PlayerCap<?> playerCap)
	{
		super.onHeld(playerCap);
		if (playerCap.isClientSide())
		{
			AttributeInstance instance = playerCap.getOriginalEntity().getAttribute(Attributes.ATTACK_DAMAGE);
			instance.removeModifier(ModAttributes.EUIPMENT_MODIFIER_UUIDS[EquipmentSlot.MAINHAND.ordinal()]);
			instance.addTransientModifier(ModAttributes.getAttributeModifierForSlot(EquipmentSlot.MAINHAND, this.getDamage()));
		}
	}
	
	public InteractionResult onUse(PlayerCap<?> playerCap, InteractionHand hand)
	{
		if (!playerCap.canBlock()) return InteractionResult.PASS;
		playerCap.getOriginalEntity().startUsingItem(hand);
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
	
	@Override
	public void performAttack(AttackType type, LocalPlayerCap playerCap)
	{
		AttackAnimation animation = this.getAttack(type, playerCap);
		if (animation == null) return;
		playerCap.getAnimator().playAnimation(animation, 0.0F);
		ModNetworkManager.sendToServer(new CTSPlayAnimation(animation, 0.0F, false, false));
	};

	@OnlyIn(Dist.CLIENT)
	public AttackAnimation getAttack(AttackType type, LocalPlayerCap playerCap)
	{
		Pair<Boolean, AttackAnimation[]> move = this.moveset.get(type);
		if (move == null) return null;
		AttackAnimation[] animations = move.getSecond();
		if (animations == null) return null;
		List<AttackAnimation> animationList = new ArrayList<AttackAnimation>(Arrays.asList(animations));
		int combo = animationList.indexOf(playerCap.getClientAnimator().baseLayer.animationPlayer.getPlay());
		if (combo + 1 < animations.length) combo += 1;
		else if (move.getFirst()) combo = 0;
		return animations[combo];
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
	public SoundEvent getBlockSound()
	{
		return ModSoundEvents.WEAPON_BLOCK.get();
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
