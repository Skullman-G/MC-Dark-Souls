package com.skullmangames.darksouls.common.capability.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.Multimap;
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
import com.skullmangames.darksouls.common.item.WeaponItem;
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
	protected final Map<LivingMotion, StaticAnimation> animationSet;
	
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
		
		if (ClientManager.INSTANCE.inputManager.isKeyDown(ModKeys.SHOW_ITEM_INFO))
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
		if (this.orgItem instanceof WeaponItem
				&& !((WeaponItem)this.orgItem).meetRequirements(playerdata.getOriginalEntity())) return this.getWeakAttack();
		
		switch (type)
		{
			case LIGHT:
				AttackAnimation[] animations = this.getLightAttack();
				if (animations == null) return null;
				List<AttackAnimation> animationList = new ArrayList<AttackAnimation>(Arrays.asList(animations));
				int combo = animationList.indexOf(playerdata.getClientAnimator().baseLayer.animationPlayer.getPlay());
				if (combo + 1 < animationList.size()) combo += 1;
				else if (this.repeatLightAttack()) combo = 0;
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
	
	public enum AttackType
	{
		LIGHT, HEAVY, DASH
	}
	
	public enum WeaponCategory
	{
		NONE_WEAON, AXE, FIST, HOE, PICKAXE, SHOVEL, STRAIGHT_SWORD, SHIELD, GREAT_HAMMER
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
