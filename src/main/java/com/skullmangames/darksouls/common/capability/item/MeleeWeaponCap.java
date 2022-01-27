package com.skullmangames.darksouls.common.capability.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	public MeleeWeaponCap(Item item, WeaponCategory category, int requiredStrength, int requiredDex,
			Scaling strengthScaling, Scaling dexScaling, float poiseDamage)
	{
		super(item, category, requiredStrength, requiredDex, strengthScaling, dexScaling, poiseDamage);
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
	
	protected AttackAnimation[] getLightAttack()
	{
		return null;
	}

	protected boolean repeatLightAttack()
	{
		return true;
	}

	protected AttackAnimation getDashAttack()
	{
		return null;
	}

	@OnlyIn(Dist.CLIENT)
	public AttackAnimation getAttack(AttackType type, ClientPlayerData playerdata)
	{
		if (!this.meetRequirements(playerdata) && this.getWeakAttack() != null)
			return this.getWeakAttack();

		switch (type)
		{
		case LIGHT:
			AttackAnimation[] animations = this.getLightAttack();
			if (animations == null)
				return null;
			List<AttackAnimation> animationList = new ArrayList<AttackAnimation>(Arrays.asList(animations));
			int combo = animationList.indexOf(playerdata.getClientAnimator().baseLayer.animationPlayer.getPlay());
			if (combo + 1 < animationList.size())
				combo += 1;
			else if (this.repeatLightAttack())
				combo = 0;
			return animationList.get(combo);

		case HEAVY:
			return this.getHeavyAttack();

		case DASH:
			return this.getDashAttack();

		default:
			throw new IndexOutOfBoundsException("Incorrect attack type.");
		}
	}

	protected AttackAnimation getWeakAttack()
	{
		return null;
	}

	public List<StaticAnimation> getMountAttackMotion()
	{
		return null;
	}

	protected AttackAnimation getHeavyAttack()
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
		return Colliders.fist;
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
}
