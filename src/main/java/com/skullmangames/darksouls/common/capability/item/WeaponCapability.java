package com.skullmangames.darksouls.common.capability.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.input.ModKeys;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.HoldingWeaponAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.capability.entity.ClientPlayerData;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class WeaponCapability extends AttributeItemCapability
{
	protected final WeaponCategory weaponCategory;
	protected final Map<LivingMotion, StaticAnimation> animationSet = new HashMap<LivingMotion, StaticAnimation>();
	protected final Map<Stat, Pair<Integer, Scaling>> statInfo;

	public WeaponCapability(Item item, WeaponCategory category, int requiredStrength, int requiredDex, Scaling strengthScaling, Scaling dexScaling)
	{
		super(item);
		this.weaponCategory = category;
		this.statInfo = ImmutableMap.<Stat, Pair<Integer, Scaling>>builder()
				.put(Stats.STRENGTH, new Pair<Integer, Scaling>(MathUtils.clamp(requiredStrength, 0, 99), strengthScaling))
				.put(Stats.DEXTERITY, new Pair<Integer, Scaling>(MathUtils.clamp(requiredDex, 0, 99), dexScaling)).build();
	}
	
	@Override
	public void onHeld(PlayerData<?> playerdata)
	{
		super.onHeld(playerdata);
		AttributeInstance instance = playerdata.getOriginalEntity().getAttribute(Attributes.ATTACK_DAMAGE);
		instance.removeModifier(ModAttributes.ATTACK_DAMAGE_MODIFIER);
		instance.addTransientModifier(ModAttributes.getAttackDamageModifier(this.getDamage()));
	}
	
	public Scaling getScaling(Stat stat)
	{
		return this.statInfo.get(stat).getSecond();
	}

	public boolean meetRequirements(PlayerData<?> playerdata)
	{
		for (Stat stat : this.statInfo.keySet())
			if (!this.meetsRequirement(stat, playerdata))
				return false;
		return true;
	}

	public boolean meetsRequirement(Stat stat, PlayerData<?> playerdata)
	{
		return this.statInfo.get(stat).getFirst() <= playerdata.getStats().getStatValue(stat);
	}

	@Nullable
	public HoldingWeaponAnimation getHoldingAnimation()
	{
		return null;
	}

	public InteractionResult onUse(Player player, InteractionHand hand)
	{
		player.startUsingItem(hand);
		return InteractionResult.CONSUME;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot, LivingData<?> entitydata)
	{
		Multimap<Attribute, AttributeModifier> map = super.getAttributeModifiers(equipmentSlot, entitydata);
		if (entitydata == null) return map;
		
		for (Entry<Supplier<Attribute>, AttributeModifier> entry : this.attributeMap.entrySet())
		{
			map.put(entry.getKey().get(), entry.getValue());
		}

		return map;
	}

	@Override
	public void modifyItemTooltip(List<Component> itemTooltip, PlayerData<?> playerdata, ItemStack stack)
	{
		if (!(this.orgItem instanceof IForgeRegistryEntry))
			return;

		while (itemTooltip.size() >= 2)
			itemTooltip.remove(1);

		if (ClientManager.INSTANCE.inputManager.isKeyDown(ModKeys.SHOW_ITEM_INFO))
		{
			String languagePath = "tooltip." + DarkSouls.MOD_ID + "."
					+ ((IForgeRegistryEntry<Item>) this.orgItem).getRegistryName().getPath() + ".extended";
			String description = new TranslatableComponent(languagePath).getString();

			if (!description.contains(languagePath))
				itemTooltip.add(new TextComponent("\u00A77\n" + description));
		} else
		{
			itemTooltip.add(new TextComponent("\u00A72Physical Damage: " + this.getDamage()));

			itemTooltip.add(new TextComponent(""));
			itemTooltip.add(new TextComponent("Requirements:"));
			itemTooltip.add(new TextComponent("  " + new TranslatableComponent(Stats.STRENGTH.toString()).getString() + ": "
					+ this.getStatStringValue(Stats.STRENGTH, playerdata)));
			itemTooltip.add(new TextComponent("  " + new TranslatableComponent(Stats.DEXTERITY.toString()).getString() + ": "
					+ this.getStatStringValue(Stats.DEXTERITY, playerdata)));
			
			itemTooltip.add(new TextComponent(""));
			itemTooltip.add(new TextComponent("Scaling:"));
			itemTooltip.add(new TextComponent("  " + new TranslatableComponent(Stats.STRENGTH.toString()).getString() + ": "
					+ this.statInfo.get(Stats.STRENGTH).getSecond()));
			itemTooltip.add(new TextComponent("  " + new TranslatableComponent(Stats.DEXTERITY.toString()).getString() + ": "
					+ this.statInfo.get(Stats.DEXTERITY).getSecond()));
		}
	}
	
	public float getDamage()
	{
		return this.orgItem instanceof SwordItem ? ((SwordItem) this.orgItem).getDamage()
				: this.orgItem instanceof DiggerItem ? ((DiggerItem) this.orgItem).getAttackDamage()
				: 0.0F;
	}

	private String getStatStringValue(Stat stat, PlayerData<?> playerdata)
	{
		return this.getStatColor(stat, playerdata) + this.statInfo.get(stat).getFirst();
	}

	private String getStatColor(Stat stat, PlayerData<?> playerdata)
	{
		return this.meetsRequirement(stat, playerdata) ? "\u00A7f" : "\u00A74";
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

	public WeaponCategory getWeaponCategory()
	{
		return this.weaponCategory;
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

	public WieldStyle getStyle(LivingData<?> entitydata)
	{
		if (this.isTwoHanded())
		{
			return WieldStyle.TWO_HAND;
		} else
		{
			if (this.isMainhandOnly())
			{
				return entitydata.getOriginalEntity().getMainHandItem().isEmpty() ? WieldStyle.TWO_HAND
						: WieldStyle.ONE_HAND;
			} else
			{
				return WieldStyle.ONE_HAND;
			}
		}
	}

	@Override
	public boolean canUsedInOffhand()
	{
		return this.getHandProperty() == HandProperty.GENERAL ? true : false;
	}

	@Override
	public boolean isTwoHanded()
	{
		return this.getHandProperty() == HandProperty.TWO_HANDED;
	}

	public final boolean isMainhandOnly()
	{
		return this.getHandProperty() == HandProperty.MAINHAND_ONLY;
	}

	@Override
	public boolean canUseOnMount()
	{
		return !this.isTwoHanded();
	}

	public HandProperty getHandProperty()
	{
		return HandProperty.GENERAL;
	}

	@Override
	public Map<LivingMotion, StaticAnimation> getLivingMotionChanges(PlayerData<?> playerdata)
	{
		return animationSet;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canBeRenderedBoth(ItemStack item)
	{
		return !isTwoHanded() && !item.isEmpty();
	}

	public enum AttackType
	{
		LIGHT, HEAVY, DASH
	}

	public enum WeaponCategory
	{
		NONE_WEAON, AXE, FIST, HOE, PICKAXE, SHOVEL, STRAIGHT_SWORD, SHIELD, GREAT_HAMMER, DAGGER
	}

	public enum HandProperty
	{
		TWO_HANDED, MAINHAND_ONLY, GENERAL
	}

	public enum WieldStyle
	{
		ONE_HAND, TWO_HAND, SHEATH, MOUNT
	}
	
	public enum Scaling
	{
		S(1.5F), A(1F), B(0.8F), C(0.5F), D(0.3F), E(0.1F), NONE(0F);
		
		private final float percentage;
		
		private Scaling(float per)
		{
			this.percentage = per;
		}
		
		public float getPercentage()
		{
			return this.percentage;
		}
		
		@Override
		public String toString()
		{
			switch (this)
			{
				case S: return "S";
				case A: return "A";
				case B: return "B";
				case C: return "C";
				case D: return "D";
				case E: return "E";
				default: return "-";
			}
		}
	}
}
