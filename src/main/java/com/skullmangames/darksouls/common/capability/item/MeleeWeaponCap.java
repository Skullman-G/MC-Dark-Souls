package com.skullmangames.darksouls.common.capability.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.input.ModKeys;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.init.WeaponMovesets;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.physics.Collider;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSPlayAnimation;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class MeleeWeaponCap extends WeaponCap implements IShield, ReloadableCap
{
	private final ResourceLocation movesetId;
	private WeaponMoveset moveset;
	private final Collider collider;
	private final float staminaDamage;
	
	public MeleeWeaponCap(Item item, ResourceLocation moveset, Collider collider, int reqStrength, int reqDex, int reqFaith,
			Scaling strengthScaling, Scaling dexScaling, Scaling faithScaling)
	{
		super(item, WeaponCategory.MELEE_WEAPON, reqStrength, reqDex, reqFaith, strengthScaling, dexScaling, faithScaling);
		this.movesetId = moveset;
		this.moveset = WeaponMovesets.getByLocation(this.movesetId).orElse(WeaponMoveset.EMPTY);
		this.staminaDamage = this.weight * 2;
		this.collider = collider;
	}
	
	public void reload()
	{
		this.moveset = WeaponMovesets.getByLocation(this.movesetId).orElse(WeaponMoveset.EMPTY);
	}
	
	@Override
	public void modifyItemTooltip(List<Component> itemTooltip, PlayerCap<?> playerCap, ItemStack stack)
	{
		if (!(this.orgItem instanceof IForgeRegistryEntry)) return;
		super.modifyItemTooltip(itemTooltip, playerCap, stack);
		if (!ClientManager.INSTANCE.inputManager.isKeyDown(ModKeys.SHOW_ITEM_INFO))
		{
			itemTooltip.add(2, new TextComponent("\u00A72Physical Defense: " + (int)(this.getDefense(DamageType.REGULAR) * 100) + "%"));
		}
	}
	
	@Override
	public void onHeld(PlayerCap<?> playerCap)
	{
		super.onHeld(playerCap);
		if (playerCap.isClientSide())
		{
			AttributeInstance instance = playerCap.getOriginalEntity().getAttribute(Attributes.ATTACK_DAMAGE);
			instance.removeModifier(ModAttributes.EQUIPMENT_MODIFIER_UUIDS[EquipmentSlot.MAINHAND.ordinal()]);
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
		return this.moveset.getAttacks(type);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
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
		if (playerCap.isMounted())
		{
			List<AttackAnimation> animations = new ArrayList<AttackAnimation>(Arrays.asList(Animations.HORSEBACK_LIGHT_ATTACK));
			int combo = animations.indexOf(playerCap.getClientAnimator().baseLayer.animationPlayer.getPlay());
			if (combo + 1 < animations.size()) combo += 1;
			else combo = 0;
			return animations.get(combo);
		}
		
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
		return this.collider;
	}
	
	public ResourceLocation getWeaponMovesetId()
	{
		return this.movesetId;
	}
	
	@Override
	public SoundEvent getBlockSound()
	{
		return ModSoundEvents.WEAPON_BLOCK.get();
	}
	
	@Override
	public float getStaminaDamage()
	{
		return this.staminaDamage;
	}
	
	@Override
	public float getDefense(DamageType damageType)
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
		LIGHT("light"), HEAVY("heavy"), DASH("dash"), BACKSTAB("backstab");
		
		private String id;
		
		private AttackType(String id)
		{
			this.id = id;
		}
		
		public String toString()
		{
			return this.id;
		}
		
		public static AttackType fromString(String id)
		{
			for (AttackType type : AttackType.values())
			{
				if (type.id.equals(id)) return type;
			}
			return null;
		}
	}
}
