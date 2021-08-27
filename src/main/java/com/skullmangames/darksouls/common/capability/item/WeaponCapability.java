package com.skullmangames.darksouls.common.capability.item;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.Multimap;
import com.skullmangames.darksouls.client.ClientEngine;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.common.particle.HitParticleType;
import com.skullmangames.darksouls.common.skill.Skill;
import com.skullmangames.darksouls.common.skill.SkillExecutionHelper;
import com.skullmangames.darksouls.core.init.AttributeInit;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WeaponCapability extends CapabilityItem
{
	protected final WeaponCategory weaponCategory;
	
	public WeaponCapability(Item item, WeaponCategory category)
	{
		super(item);
		this.weaponCategory = category;
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
	public void modifyItemTooltip(List<ITextComponent> itemTooltip, LivingData<?> entitydata)
	{
		super.modifyItemTooltip(itemTooltip, entitydata);
		
		Map<Supplier<Attribute>, AttributeModifier> attribute = this.getDamageAttributesInCondition(this.getStyle(entitydata));
				
		if(attribute != null)
		{
			for(Map.Entry<Supplier<Attribute>, AttributeModifier> attr : attribute.entrySet())
			{
				if (entitydata.getOriginalEntity().getAttributes().hasAttribute(attr.getKey().get()))
				{
					double value = attr.getValue().getAmount() + entitydata.getOriginalEntity().getAttribute(attr.getKey().get()).getBaseValue();
					if (value != 0.0D)
					{
						itemTooltip.add(new TranslationTextComponent(attr.getKey().get().getDescriptionId(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(value)));
					}
				}
			}
					
			if(!attribute.keySet().contains(AttributeInit.MAX_STRIKES))
			{
				itemTooltip.add(new TranslationTextComponent(AttributeInit.MAX_STRIKES.get().getDescriptionId(), 
						ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(AttributeInit.MAX_STRIKES.get().getDefaultValue())));
			}
		}
	}
	
	@Nullable
	public Skill getLightAttack()
	{
		return null;
	}
	
	@Nullable
	public List<StaticAnimation> getMountAttackMotion()
	{
		return null;
	}
	
	@Nullable
	public Skill getHeavyAttack()
	{
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
		Skill lightAttackSkill = this.getLightAttack();
		if (lightAttackSkill != null)
		{
			
			if(SkillExecutionHelper.getActiveSkill() != lightAttackSkill)
			{
				SkillExecutionHelper.setActiveSkill(lightAttackSkill);
			}
		}
		
		Skill specialSkill = this.getHeavyAttack();
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
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canBeRenderedBoth(ItemStack item)
	{
		return !isTwoHanded() && !item.isEmpty();
	}
	
	public enum WeaponCategory
	{
		NONE_WEAON, AXE, FIST, GREATSWORD, HOE, PICKAXE, SHOVEL, SWORD, KATANA, SPEAR, TACHI, SHIELD
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
