package com.skullmangames.darksouls.core.init;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.capability.entity.EquipLoaded.EquipLoadLevel;
import com.skullmangames.darksouls.common.entity.StrayDemon;
import com.skullmangames.darksouls.common.entity.AnastaciaOfAstora;
import com.skullmangames.darksouls.common.entity.BlackKnight;
import com.skullmangames.darksouls.common.entity.Falconer;
import com.skullmangames.darksouls.common.entity.FireKeeper;
import com.skullmangames.darksouls.common.entity.Hollow;
import com.skullmangames.darksouls.common.entity.HollowLordranSoldier;
import com.skullmangames.darksouls.common.entity.HollowLordranWarrior;
import com.skullmangames.darksouls.common.entity.QuestEntity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
	
	public static final RegistryObject<Attribute> MAX_STAMINA = registerRangedAttribute("max_stamina", 80D, 1D, 100_000_000D);
	public static final RegistryObject<Attribute> POISE = registerRangedAttribute("poise", 0.0D, 0.0D, 1024.0D);
	public static final RegistryObject<Attribute> MAX_EQUIP_LOAD = registerRangedAttribute("max_equip_load", 50.0D, 50.0D, 139.0D);
	public static final RegistryObject<Attribute> EQUIP_LOAD = registerRangedAttribute("equip_load", 0.0D, 0.0D, 139.0D);
	public static final RegistryObject<Attribute> MAX_FOCUS_POINTS = registerRangedAttribute("max_focus_points", 10.0D, 1.0D, 1024.0D);
	public static final RegistryObject<Attribute> ATTUNEMENT_SLOTS = registerRangedAttribute("attunement_slots", 1.0D, 1.0D, 10.0D);
	public static final RegistryObject<Attribute> SPELL_BUFF = registerRangedAttribute("spell_buff", 0.0D, 0.0D, 100_000_000D);
	
	// Damage
	public static final RegistryObject<Attribute> MAGIC_DAMAGE = registerRangedAttribute("magic_damage", 0D, 0D, 100_000_000D);
	public static final RegistryObject<Attribute> FIRE_DAMAGE = registerRangedAttribute("fire_damage", 0D, 0D, 100_000_000D);
	public static final RegistryObject<Attribute> LIGHTNING_DAMAGE = registerRangedAttribute("lightning_damage", 0D, 0D, 100_000_000D);
	public static final RegistryObject<Attribute> DARK_DAMAGE = registerRangedAttribute("dark_damage", 0D, 0D, 100_000_000D);
	public static final RegistryObject<Attribute> HOLY_DAMAGE = registerRangedAttribute("holy_damage", 0D, 0D, 100_000_000D);
	
	// Defense
	public static final RegistryObject<Attribute> STANDARD_PROTECTION = registerRangedAttribute("standard_protection", 0.0D, 0.0D, 100_000_000D);
	public static final RegistryObject<Attribute> STRIKE_PROTECTION = registerRangedAttribute("strike_protection", 0.0D, 0.0D, 100_000_000D);
	public static final RegistryObject<Attribute> SLASH_PROTECTION = registerRangedAttribute("slash_protection", 0.0D, 0.0D, 100_000_000D);
	public static final RegistryObject<Attribute> THRUST_PROTECTION = registerRangedAttribute("thrust_protection", 0.0D, 0.0D, 100_000_000D);
	public static final RegistryObject<Attribute> MAGIC_PROTECTION = registerRangedAttribute("magic_protection", 0.0D, 0.0D, 100_000_000D);
	public static final RegistryObject<Attribute> FIRE_PROTECTION = registerRangedAttribute("fire_protection", 0.0F, 0.0F, 100_000_000D);
	public static final RegistryObject<Attribute> LIGHTNING_PROTECTION = registerRangedAttribute("lightning_protection", 0.0D, 0.0D, 100_000_000D);
	public static final RegistryObject<Attribute> DARK_PROTECTION = registerRangedAttribute("dark_protection", 0.0D, 0.0D, 100_000_000D);
	public static final RegistryObject<Attribute> HOLY_PROTECTION = registerRangedAttribute("holy_protection", 0.0D, 0.0D, 100_000_000D);
	
	public static final UUID[] EQUIPMENT_MODIFIER_UUIDS = new UUID[]
	{
			UUID.fromString("02787320-87ac-4c4f-b057-cb79a2660041"),
			UUID.fromString("f16541b7-8a55-4a2b-ad65-0c21a3a12028"),
			UUID.fromString("683ac74c-a751-497e-abfe-e0af614bef46"),
			UUID.fromString("f3d4a8bb-853c-407f-9a02-0bf11c284466"),
			UUID.fromString("26c0477e-664b-44cd-9108-140699ad6807"),
			UUID.fromString("868429ef-4804-4cb8-b253-c89ac4af6c73")
	};
	public static final UUID[] WEAPON_SCALING_MODIFIER_UUIDS = new UUID[]
	{
			UUID.fromString("a6110f39-31d9-4576-94ff-26bcd9291836"),
			UUID.fromString("614056cb-20de-41dd-b7a0-6d9726c68239"),
			UUID.fromString("74eaf54d-7947-4da7-9ec9-9cdfd87a21ac"),
			UUID.fromString("1634c7de-7f16-4540-843b-9d7be08c5a21"),
			UUID.fromString("211fc8f4-ab23-4dd8-a8fd-9718f670d80e"),
			UUID.fromString("3affa15e-37f4-4b02-9b61-4f8051b27cc4")
	};
	public static final UUID MOVEMENT_SPEED_MODIFIER_UUID = UUID.fromString("1cdc2c63-da86-47bb-a6ea-ff0a06dab01e");
	public static final int PLAYER_FIST_DAMAGE = 20;
    
	private static RegistryObject<Attribute> registerRangedAttribute(String name, double defaultValue, double minValue, double maxValue)
	{
		return ATTRIBUTES.register(name, () -> new RangedAttribute("attribute."+DarkSouls.MOD_ID+"."+name, defaultValue, minValue, maxValue).setSyncable(true));
	}
	
	public static List<Supplier<Attribute>> damageAttributes()
	{
		return ImmutableList.of
		(
			() -> Attributes.ATTACK_DAMAGE,
			ModAttributes.MAGIC_DAMAGE,
			ModAttributes.FIRE_DAMAGE,
			ModAttributes.LIGHTNING_DAMAGE,
			ModAttributes.HOLY_DAMAGE,
			ModAttributes.DARK_DAMAGE
		);
	}
	
	public static List<Supplier<Attribute>> protectionAttributes()
	{
		return ImmutableList.of
		(
			ModAttributes.STANDARD_PROTECTION,
			ModAttributes.SLASH_PROTECTION,
			ModAttributes.STRIKE_PROTECTION,
			ModAttributes.THRUST_PROTECTION,
			ModAttributes.MAGIC_PROTECTION,
			ModAttributes.FIRE_PROTECTION,
			ModAttributes.LIGHTNING_PROTECTION,
			ModAttributes.HOLY_PROTECTION,
			ModAttributes.DARK_PROTECTION
		);
	}
	
	public static boolean isDamageAttribute(Attribute attribute)
	{
		boolean flag = false;
		for (Supplier<Attribute> supplier : ModAttributes.damageAttributes())
		{
			if (attribute == supplier.get()) flag = true;
		}
		return flag;
	}
	
	public static boolean isProtectionAttribute(Attribute attribute)
	{
		boolean flag = false;
		for (Supplier<Attribute> supplier : ModAttributes.protectionAttributes())
		{
			if (attribute == supplier.get()) flag = true;
		}
		return flag;
	}
	
	public static void createAttributeMap(EntityAttributeCreationEvent event)
	{
		event.put(ModEntities.FIRE_KEEPER.get(), FireKeeper.createAttributes().build());
		event.put(ModEntities.HOLLOW.get(), Hollow.createAttributes().build());
		event.put(ModEntities.HOLLOW_LORDRAN_WARRIOR.get(), HollowLordranWarrior.createAttributes().build());
		event.put(ModEntities.HOLLOW_LORDRAN_SOLDIER.get(), HollowLordranSoldier.createAttributes().build());
		event.put(ModEntities.STRAY_DEMON.get(), StrayDemon.createAttributes().build());
		event.put(ModEntities.CRESTFALLEN_WARRIOR.get(), QuestEntity.createAttributes().build());
		event.put(ModEntities.PETRUS_OF_THOROLUND.get(), QuestEntity.createAttributes().build());
		event.put(ModEntities.ANASTACIA_OF_ASTORA.get(), AnastaciaOfAstora.createAttributes().build());
		event.put(ModEntities.FALCONER.get(), Falconer.createAttributes().build());
		event.put(ModEntities.BLACK_KNIGHT.get(), BlackKnight.createAttributes().build());
	}
	
	public static void modifyAttributeMap(EntityAttributeModificationEvent event)
	{
		general(ModEntities.HOLLOW_LORDRAN_SOLDIER.get(), event);
		general(ModEntities.HOLLOW_LORDRAN_WARRIOR.get(), event);
		general(ModEntities.HOLLOW.get(), event);
		general(ModEntities.STRAY_DEMON.get(), event);
		general(ModEntities.FIRE_KEEPER.get(), event);
		general(ModEntities.FALCONER.get(), event);
		general(ModEntities.BLACK_KNIGHT.get(), event);
		
		general(EntityType.ARMOR_STAND, event);
		general(EntityType.ZOMBIE, event);
		general(EntityType.HUSK, event);
		general(EntityType.DROWNED, event);
		
		withEquipLoad(ModEntities.CRESTFALLEN_WARRIOR.get(), event);
		withEquipLoad(ModEntities.ANASTACIA_OF_ASTORA.get(), event);
		withEquipLoad(ModEntities.PETRUS_OF_THOROLUND.get(), event);
		
		player(EntityType.PLAYER, event);
	}
    
    private static void general(EntityType<? extends LivingEntity> entityType, EntityAttributeModificationEvent event)
    {
    	event.add(entityType, ModAttributes.POISE.get());
		event.add(entityType, ModAttributes.MAX_STAMINA.get());
		event.add(entityType, ModAttributes.SPELL_BUFF.get());
		
		for (Supplier<Attribute> attribute : ModAttributes.damageAttributes())
		{
			event.add(entityType, attribute.get());
		}
		for (Supplier<Attribute> attribute : ModAttributes.protectionAttributes())
		{
			event.add(entityType, attribute.get());
		}
	}
    
    public static void withEquipLoad(EntityType<? extends LivingEntity> entityType, EntityAttributeModificationEvent event)
    {
    	general(entityType, event);
    	event.add(entityType, ModAttributes.EQUIP_LOAD.get());
    	event.add(entityType, ModAttributes.MAX_EQUIP_LOAD.get());
    }
    
    private static void player(EntityType<? extends Player> entityType, EntityAttributeModificationEvent event)
    {
    	withEquipLoad(entityType, event);
    	event.add(entityType, ModAttributes.MAX_FOCUS_POINTS.get());
    	event.add(entityType, ModAttributes.ATTUNEMENT_SLOTS.get());
	}
	
	public static AttributeModifier getAttributeModifierForSlot(EquipmentSlot slot, float value)
	{
		return new AttributeModifier(EQUIPMENT_MODIFIER_UUIDS[slot.ordinal()], DarkSouls.MOD_ID + ":equipment_modifier", value, AttributeModifier.Operation.ADDITION);
	}
	
	public static AttributeModifier getMovementSpeedModifier(EquipLoadLevel level)
	{
		double value = 1.0F;
		
		switch (level)
		{
			default: value = 0.0F; break;
			case LIGHT: value = -0.1F; break;
			case MEDIUM: value = -0.2F; break;
			case HEAVY: value = -0.35F; break;
			case OVERENCUMBERED: value = -0.5F; break;
		}
		
		return new AttributeModifier(MOVEMENT_SPEED_MODIFIER_UUID, DarkSouls.MOD_ID + ":equipment_modifier", value, AttributeModifier.Operation.MULTIPLY_TOTAL);
	}
}
