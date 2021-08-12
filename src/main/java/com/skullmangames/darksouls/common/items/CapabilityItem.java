package com.skullmangames.darksouls.common.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.animation.LivingMotion;
import com.skullmangames.darksouls.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.entities.LivingData;
import com.skullmangames.darksouls.common.entities.PlayerData;
import com.skullmangames.darksouls.common.particle.HitParticleType;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.AttributeInit;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.physics.Collider;
import com.skullmangames.darksouls.skill.Skill;
import com.skullmangames.darksouls.skill.SkillContainer;
import com.skullmangames.darksouls.skill.SkillSlot;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class CapabilityItem
{
	protected static List<StaticAnimation> commonAutoAttackMotion;
	protected final WeaponCategory weaponCategory;
	
	static {
		commonAutoAttackMotion = new ArrayList<StaticAnimation> ();
		commonAutoAttackMotion.add(Animations.FIST_AUTO_1);
		commonAutoAttackMotion.add(Animations.FIST_AUTO_2);
		commonAutoAttackMotion.add(Animations.FIST_AUTO_3);
		commonAutoAttackMotion.add(Animations.FIST_DASH);
	}
	
	public static List<StaticAnimation> getBasicAutoAttackMotion() {
		return commonAutoAttackMotion;
	}

	protected void loadClientThings() {
		
	}
	
	protected Map<WieldStyle, Map<Supplier<Attribute>, AttributeModifier>> attributeMap;
	
	public CapabilityItem(WeaponCategory category) {
		if (DarkSouls.isPhysicalClient()) {
			loadClientThings();
		}
		this.attributeMap = Maps.<WieldStyle, Map<Supplier<Attribute>, AttributeModifier>>newHashMap();
		this.weaponCategory = category;
		registerAttribute();
	}
	
	public CapabilityItem(Item item, WeaponCategory category) {
		this.attributeMap = Maps.<WieldStyle, Map<Supplier<Attribute>, AttributeModifier>>newHashMap();
		this.weaponCategory = category;
	}

	protected void registerAttribute() {
		
	}
	
	public void modifyItemTooltip(List<ITextComponent> itemTooltip, LivingData<?> entitydata)
	{
		if(this.isTwoHanded())
		{
			itemTooltip.add(1, new TranslationTextComponent("attribute.name."+DarkSouls.MOD_ID+".twohanded").withStyle(TextFormatting.DARK_GRAY));
		}
		else if(this.isMainhandOnly())
		{
			itemTooltip.add(1, new TranslationTextComponent("attribute.name."+DarkSouls.MOD_ID+".mainhand_only").withStyle(TextFormatting.DARK_GRAY));
		}
		
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
	
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata) {
		return getBasicAutoAttackMotion();
	}

	public List<StaticAnimation> getMountAttackMotion() {
		return null;
	}

	public Skill getSpecialAttack(PlayerData<?> playerdata)
	{
		return null;
	}

	public Skill getPassiveSkill() {
		return null;
	}
	
	public WeaponCategory getWeaponCategory() {
		return this.weaponCategory;
	}
	
	public void onHeld(PlayerData<?> playerdata)
	{
		Skill specialSkill = this.getSpecialAttack(playerdata);
		if (specialSkill != null) {
			SkillContainer skillContainer = playerdata.getSkill(SkillSlot.WEAPON_SPECIAL_ATTACK);
			
			if(skillContainer.getContaining() != specialSkill) {
				skillContainer.setSkill(specialSkill);
			}
		}
		
		Skill skill = this.getPassiveSkill();
		SkillContainer skillContainer = playerdata.getSkill(SkillSlot.WEAPON_GIMMICK);
		
		if(skill == null) {
			skillContainer.setSkill(null);
		} else {
			if(skillContainer.getContaining() != skill) {
				skillContainer.setSkill(skill);
			}
		}
	}
	
	public SoundEvent getSmashingSound() {
		return null;
	}

	public SoundEvent getHitSound() {
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
	
	public void addStyleAttibute(WieldStyle style, Pair<Supplier<Attribute>, AttributeModifier> attributePair) {
		this.attributeMap.computeIfAbsent(style, (key) -> Maps.<Supplier<Attribute>, AttributeModifier>newHashMap());
		this.attributeMap.get(style).put(attributePair.getFirst(), attributePair.getSecond());
	}
	
	public void addStyleAttributeSimple(WieldStyle style, double armorNegation, double impact, int maxStrikes) {
		this.addStyleAttibute(style, Pair.of(AttributeInit.ARMOR_NEGATION, AttributeInit.getArmorNegationModifier(armorNegation)));
		this.addStyleAttibute(style, Pair.of(AttributeInit.IMPACT, AttributeInit.getImpactModifier(impact)));
		this.addStyleAttibute(style, Pair.of(AttributeInit.MAX_STRIKES, AttributeInit.getMaxStrikesModifier(maxStrikes)));
	}
	
	public final Map<Supplier<Attribute>, AttributeModifier> getDamageAttributesInCondition(WieldStyle style) {
		return this.attributeMap.get(style);
	}
	
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, LivingData<?> entitydata) {
		Multimap<Attribute, AttributeModifier> map = HashMultimap.<Attribute, AttributeModifier>create();
		
		if(entitydata != null) {
			Map<Supplier<Attribute>, AttributeModifier> modifierMap = this.getDamageAttributesInCondition(this.getStyle(entitydata));
			if(modifierMap != null) {
				for(Entry<Supplier<Attribute>, AttributeModifier> entry : modifierMap.entrySet()) {
					map.put(entry.getKey().get(), entry.getValue());
				}
			}
		}
		
		return map;
    }
	
	public Map<LivingMotion, StaticAnimation> getLivingMotionChanges(PlayerData<?> player)
	{
		return null;
	}
	
	public WieldStyle getStyle(LivingData<?> entitydata) {
		if (this.isTwoHanded()) {
			return WieldStyle.TWO_HAND;
		} else {
			if (this.isMainhandOnly()) {
				return entitydata.getOriginalEntity().getMainHandItem().isEmpty() ? WieldStyle.TWO_HAND : WieldStyle.ONE_HAND;
			} else {
				return WieldStyle.ONE_HAND;
			}
		}
	}
	
	public final boolean canUsedInOffhand() {
		return this.getHandProperty() == HandProperty.GENERAL ? true : false;
	}

	public final boolean isTwoHanded() {
		return this.getHandProperty() == HandProperty.TWO_HANDED;
	}
	
	public final boolean isMainhandOnly() {
		return this.getHandProperty() == HandProperty.MAINHAND_ONLY;
	}
	
	public boolean canUseOnMount() {
		return !this.isTwoHanded();
	}
	
	public HandProperty getHandProperty() {
		return HandProperty.GENERAL;
	}
	
	@OnlyIn(Dist.CLIENT)
	public boolean canBeRenderedBoth(ItemStack item) {
		return !isTwoHanded() && !item.isEmpty();
	}
	
	public enum WeaponCategory {
		NONE_WEAON, AXE, FIST, GREATSWORD, HOE, PICKAXE, SHOVEL, SWORD, KATANA, SPEAR, TACHI
	}
	
	public enum HandProperty {
		TWO_HANDED, MAINHAND_ONLY, GENERAL
	}
	
	public enum WieldStyle {
		ONE_HAND, TWO_HAND, SHEATH, MOUNT
	}
}