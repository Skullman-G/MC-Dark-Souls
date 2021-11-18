package com.skullmangames.darksouls.core.init;

import java.util.UUID;

import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModAttributes
{
	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, DarkSouls.MOD_ID);
	
    public static final RegistryObject<Attribute> MAX_STUN_ARMOR = registerRangedAttribute("stun_armor", 0.0D, 0.0D, 1024.0D);
    public static final RegistryObject<Attribute> WEIGHT = registerRangedAttribute("weight", 0.0D, 0.0D, 1024.0D);
    public static final RegistryObject<Attribute> MAX_STRIKES = registerRangedAttribute("max_strikes", 1.0D, 1.0D, 1024.0D);
	public static final RegistryObject<Attribute> ARMOR_NEGATION = registerRangedAttribute("armor_negation", 0.0D, 0.0D, 100.0D);
	public static final RegistryObject<Attribute> IMPACT = registerRangedAttribute("impact", 0.0D, 0.0D, 1024.0D);
	public static final RegistryObject<Attribute> OFFHAND_ATTACK_DAMAGE = registerRangedAttribute("offhand_attack_damage", 1.0D, 0.0D, 2048.0D);
	public static final RegistryObject<Attribute> OFFHAND_ATTACK_SPEED = registerRangedAttribute("offhand_attack_speed", 4.0D, 0.0D, 1024.0D);
	public static final RegistryObject<Attribute> MAX_STAMINA = registerRangedAttribute("max_stamina", 20.0D, 1.0D, 1024.0D);
	
	public static final UUID IGNORE_DEFENCE_ID = UUID.fromString("b0a7436e-5734-11eb-ae93-0242ac130002");
	public static final UUID HIT_AT_ONCE_ID = UUID.fromString("b0a745b2-5734-11eb-ae93-0242ac130002");
	public static final UUID IMPACT_ID = UUID.fromString("b0a746ac-5734-11eb-ae93-0242ac130002");
	public static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	public static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    
	private static RegistryObject<Attribute> registerRangedAttribute(String name, double defaultValue, double minValue, double maxValue)
	{
		return ATTRIBUTES.register(name, () -> new RangedAttribute("attribute."+DarkSouls.MOD_ID+"."+name, defaultValue, minValue, maxValue).setSyncable(true));
	}
	
	public static void modifyAttributeMap(EntityAttributeModificationEvent event)
	{
		general(ModEntities.HOLLOW.get(), event);
		general(ModEntities.ASYLUM_DEMON.get(), event);
		general(EntityType.CAVE_SPIDER, event);
		general(EntityType.CREEPER, event);
		general(EntityType.EVOKER, event);
		general(EntityType.IRON_GOLEM, event);
		general(EntityType.PILLAGER, event);
		general(EntityType.RAVAGER, event);
		general(EntityType.SPIDER, event);
		general(EntityType.VEX, event);
		general(EntityType.VINDICATOR, event);
		general(EntityType.WITCH, event);
		general(EntityType.HOGLIN, event);
		general(EntityType.ZOGLIN, event);
		
		withStunArmor(EntityType.DROWNED, event);
		withStunArmor(EntityType.ENDERMAN, event);
		withStunArmor(EntityType.HUSK, event);
		withStunArmor(EntityType.PIGLIN, event);
		withStunArmor(EntityType.PIGLIN_BRUTE, event);
		withStunArmor(EntityType.SKELETON, event);
		withStunArmor(EntityType.STRAY, event);
		withStunArmor(EntityType.WITHER_SKELETON, event);
		withStunArmor(EntityType.ZOMBIE, event);
		withStunArmor(EntityType.ZOMBIE_VILLAGER, event);
		withStunArmor(EntityType.ZOMBIFIED_PIGLIN, event);
		
		player(EntityType.PLAYER, event);
	}
    
    private static void general(EntityType<? extends LivingEntity> entityType, EntityAttributeModificationEvent event)
    {
		event.add(entityType, ModAttributes.WEIGHT.get());
		event.add(entityType, ModAttributes.ARMOR_NEGATION.get());
		event.add(entityType, ModAttributes.IMPACT.get());
		event.add(entityType, ModAttributes.MAX_STRIKES.get());
	}
    
    private static void withStunArmor(EntityType<? extends LivingEntity> entityType, EntityAttributeModificationEvent event)
    {
		general(entityType, event);
		event.add(entityType, ModAttributes.MAX_STUN_ARMOR.get());
	}
    
    private static void player(EntityType<? extends PlayerEntity> entityType, EntityAttributeModificationEvent event)
    {
		withStunArmor(entityType, event);
		event.add(entityType, ModAttributes.OFFHAND_ATTACK_DAMAGE.get());
		event.add(entityType, ModAttributes.OFFHAND_ATTACK_SPEED.get());
		event.add(entityType, ModAttributes.MAX_STAMINA.get());
	}
	
	public static AttributeModifier getArmorNegationModifier(double value)
	{
		return new AttributeModifier(ModAttributes.IGNORE_DEFENCE_ID, DarkSouls.MOD_ID + ":weapon_modifier", value, AttributeModifier.Operation.ADDITION);
	}

	public static AttributeModifier getMaxStrikesModifier(int value)
	{
		return new AttributeModifier(ModAttributes.HIT_AT_ONCE_ID, DarkSouls.MOD_ID + ":weapon_modifier", (double) value, AttributeModifier.Operation.ADDITION);
	}

	public static AttributeModifier getImpactModifier(double value)
	{
		return new AttributeModifier(ModAttributes.IMPACT_ID, DarkSouls.MOD_ID + ":weapon_modifier", value, AttributeModifier.Operation.ADDITION);
	}

	public static AttributeModifier getAttackDamageModifier(double value)
	{
		return new AttributeModifier(ATTACK_DAMAGE_MODIFIER, DarkSouls.MOD_ID + ":weapon_modifier", value, AttributeModifier.Operation.ADDITION);
	}

	public static AttributeModifier getAttackSpeedModifier(double value)
	{
		return new AttributeModifier(ATTACK_SPEED_MODIFIER, DarkSouls.MOD_ID + ":weapon_modifier", value, AttributeModifier.Operation.ADDITION);
	}
}
