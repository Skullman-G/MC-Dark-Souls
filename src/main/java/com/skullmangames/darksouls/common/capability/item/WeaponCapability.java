package com.skullmangames.darksouls.common.capability.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.Multimap;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientEngine;
import com.skullmangames.darksouls.client.input.ModKeys;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.HoldingWeaponAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.common.item.WeaponItem;
import com.skullmangames.darksouls.common.particle.HitParticleType;
import com.skullmangames.darksouls.common.skill.Skill;
import com.skullmangames.darksouls.common.skill.SkillExecutionHelper;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class WeaponCapability extends CapabilityItem implements IShield
{
	protected final WeaponCategory weaponCategory;
	protected Map<LivingMotion, StaticAnimation> animationSet;
	
	public WeaponCapability(Item item, WeaponCategory category)
	{
		super(item);
		this.animationSet = new HashMap<LivingMotion, StaticAnimation>();
		this.weaponCategory = category;
	}
	
	@Nullable
	public HoldingWeaponAnimation getHoldingAnimation()
	{
		return null;
	}
	
	@Override
	public float getPhysicalDefense()
	{
		return 0.2F;
	}
	
	@Override
	public ShieldType getShieldType()
	{
		return ShieldType.NONE;
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, LivingData<?> entitydata)
	{
		Multimap<Attribute, AttributeModifier> map = super.getAttributeModifiers(equipmentSlot, entitydata);
		
		if(entitydata != null)
		{
			Map<Supplier<Attribute>, AttributeModifier> modifierMap = this.getDamageAttributesInCondition(this.getStyle(entitydata));
			if(modifierMap != null)
			{
				for(Entry<Supplier<Attribute>, AttributeModifier> entry : modifierMap.entrySet())
				{
					map.put(entry.getKey().get(), entry.getValue());
				}
			}
		}
		
		return map;
	}
	
	@Override
	public void modifyItemTooltip(List<ITextComponent> itemTooltip, LivingData<?> entitydata, ItemStack stack)
	{
		if (!(this.orgItem instanceof IForgeRegistryEntry)) return;
		
		while (itemTooltip.size() >= 2) itemTooltip.remove(1);
		
		if (ClientEngine.INSTANCE.inputController.isKeyDown(ModKeys.SHOW_ITEM_INFO))
		{
			String languagePath = "tooltip."+DarkSouls.MOD_ID+"."+((IForgeRegistryEntry<Item>)this.orgItem).getRegistryName().getPath()+".extended";
			String description = new TranslationTextComponent(languagePath).getString();
			
			if (!description.contains(languagePath)) itemTooltip.add(new StringTextComponent("\u00A77\n" + description));
		}
		else
		{
			if (!(this.orgItem instanceof WeaponItem)) return;
			WeaponItem weapon = (WeaponItem)this.orgItem;
			LivingEntity entity = entitydata.getOriginalEntity();
			itemTooltip.add(new StringTextComponent("\u00A72Physical Damage: "+weapon.getDamage()));
			itemTooltip.add(new StringTextComponent("\u00A72Durability: "+weapon.getDurabilityForDisplay(stack)));
			
			itemTooltip.add(new StringTextComponent(""));
			itemTooltip.add(new StringTextComponent("Requirements:"));
			itemTooltip.add(new StringTextComponent("  Strength: "+this.getStatValue(Stats.STRENGTH, weapon, entity)));
		}
	}
	
	private String getStatValue(Stat stat, WeaponItem weapon, LivingEntity entity)
	{
		return this.getStatColor(Stats.STRENGTH, weapon, entity)+weapon.getRequiredStat(Stats.STRENGTH);
	}
	
	private String getStatColor(Stat stat, WeaponItem weapon, LivingEntity entity)
	{
		return weapon.meetRequirement(stat, entity) ? "\u00A7f" : "\u00A74";
	}
	
	@Nullable
	public Skill getLightAttack(LivingEntity entity)
	{
		if (!(this.orgItem instanceof WeaponItem)) return null;
		if (!((WeaponItem)this.orgItem).meetRequirements(entity)) return this.getWeakAttack();
		return null;
	}
	
	@Nullable
	public Skill getWeakAttack()
	{
		return null;
	}
	
	@Nullable
	public List<StaticAnimation> getMountAttackMotion()
	{
		return null;
	}
	
	@Nullable
	public Skill getHeavyAttack(LivingEntity entity)
	{
		if (!(this.orgItem instanceof WeaponItem)) return null;
		if (!((WeaponItem)this.orgItem).meetRequirements(entity)) return this.getWeakAttack();
		return null;
	}
	
	@Nullable
	public Skill getPassiveSkill()
	{
		return null;
	}
	
	public WeaponCategory getWeaponCategory()
	{
		return this.weaponCategory;
	}
	
	public void onHeld(PlayerData<?> playerdata)
	{
		Skill lightAttackSkill = this.getLightAttack(playerdata.getOriginalEntity());
		if (lightAttackSkill != null)
		{
			
			if(SkillExecutionHelper.getActiveSkill() != lightAttackSkill)
			{
				SkillExecutionHelper.setActiveSkill(lightAttackSkill);
			}
		}
		
		Skill specialSkill = this.getHeavyAttack(playerdata.getOriginalEntity());
		if (specialSkill != null)
		{
			
			if(SkillExecutionHelper.getActiveSkill() != specialSkill)
			{
				SkillExecutionHelper.setActiveSkill(specialSkill);
			}
		}
		
		Skill skill = this.getPassiveSkill();
		
		if(skill == null)
		{
			SkillExecutionHelper.setActiveSkill(null);
		}
		else
		{
			if(SkillExecutionHelper.getActiveSkill() != skill)
			{
				SkillExecutionHelper.setActiveSkill(skill);
			}
		}
		
		if (playerdata.isClientSide() && !ClientEngine.INSTANCE.isBattleMode())
		{
			ClientEngine.INSTANCE.switchToBattleMode();
		}
	}
	
	public SoundEvent getSwingSound()
	{
		return null;
	}

	public SoundEvent getHitSound()
	{
		return null;
	}

	public Collider getWeaponCollider()
	{
		return Colliders.fist;
	}

	public HitParticleType getHitParticle()
	{
		return null;
	}
	
	public WieldStyle getStyle(LivingData<?> entitydata)
	{
		if (this.isTwoHanded())
		{
			return WieldStyle.TWO_HAND;
		}
		else
		{
			if (this.isMainhandOnly())
			{
				return entitydata.getOriginalEntity().getMainHandItem().isEmpty() ? WieldStyle.TWO_HAND : WieldStyle.ONE_HAND;
			}
			else
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
	
	public final Map<Supplier<Attribute>, AttributeModifier> getDamageAttributesInCondition(WieldStyle style)
	{
		return this.attributeMap.get(style);
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
	
	public enum WeaponCategory
	{
		NONE_WEAON, AXE, FIST, HOE, PICKAXE, SHOVEL, SWORD, SHIELD, GREAT_HAMMER
	}
	
	public enum HandProperty
	{
		TWO_HANDED, MAINHAND_ONLY, GENERAL
	}
	
	public enum WieldStyle
	{
		ONE_HAND, TWO_HAND, SHEATH, MOUNT
	}
}
