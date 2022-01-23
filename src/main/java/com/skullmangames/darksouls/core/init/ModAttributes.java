package com.skullmangames.darksouls.core.init;

import java.util.UUID;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.entity.AsylumDemonEntity;
import com.skullmangames.darksouls.common.entity.FireKeeperEntity;
import com.skullmangames.darksouls.common.entity.HollowEntity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModAttributes
{
	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, DarkSouls.MOD_ID);
	
    public static final RegistryObject<Attribute> MAX_STUN_ARMOR = registerRangedAttribute("stun_armor", 0.0D, 0.0D, 1024.0D);
	public static final RegistryObject<Attribute> MAX_STAMINA = registerRangedAttribute("max_stamina", 20.0D, 1.0D, 1024.0D);
	
	// Defense
	public static final RegistryObject<Attribute> STANDARD_DEFENSE = registerRangedAttribute("standard_defense", 0.0D, 0.0D, 0.99D);
	public static final RegistryObject<Attribute> STRIKE_DEFENSE = registerRangedAttribute("strike_defense", 0.0D, 0.0D, 0.99D);
	public static final RegistryObject<Attribute> SLASH_DEFENSE = registerRangedAttribute("slash_defense", 0.0D, 0.0D, 0.99D);
	public static final RegistryObject<Attribute> THRUST_DEFENSE = registerRangedAttribute("thrust_defense", 0.0D, 0.0D, 0.99D);
	
	// UUID
	public static final UUID IMPACT_ID = UUID.fromString("b0a746ac-5734-11eb-ae93-0242ac130002");
	public static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	public static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    
	private static RegistryObject<Attribute> registerRangedAttribute(String name, double defaultValue, double minValue, double maxValue)
	{
		return ATTRIBUTES.register(name, () -> new RangedAttribute("attribute."+DarkSouls.MOD_ID+"."+name, defaultValue, minValue, maxValue).setSyncable(true));
	}
	
	public static void onEntityAttributeCreation(EntityAttributeCreationEvent event)
	{
		event.put(ModEntities.FIRE_KEEPER.get(), FireKeeperEntity.createAttributes().build());
		event.put(ModEntities.HOLLOW.get(), HollowEntity.createAttributes().build());
		event.put(ModEntities.ASYLUM_DEMON.get(), AsylumDemonEntity.createAttributes().build());
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
		event.add(entityType, ModAttributes.STANDARD_DEFENSE.get());
		event.add(entityType, ModAttributes.STRIKE_DEFENSE.get());
		event.add(entityType, ModAttributes.SLASH_DEFENSE.get());
		event.add(entityType, ModAttributes.THRUST_DEFENSE.get());
	}
    
    private static void withStunArmor(EntityType<? extends LivingEntity> entityType, EntityAttributeModificationEvent event)
    {
		general(entityType, event);
		event.add(entityType, ModAttributes.MAX_STUN_ARMOR.get());
	}
    
    private static void player(EntityType<? extends Player> entityType, EntityAttributeModificationEvent event)
    {
		withStunArmor(entityType, event);
		event.add(entityType, ModAttributes.MAX_STAMINA.get());
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
