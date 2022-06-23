package com.skullmangames.darksouls.core.init;

import java.util.UUID;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.capability.entity.EquipLoaded.EquipLoadLevel;
import com.skullmangames.darksouls.common.entity.StrayDemon;
import com.skullmangames.darksouls.common.entity.AnastaciaOfAstora;
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
	
	public static final RegistryObject<Attribute> MAX_STAMINA = registerRangedAttribute("max_stamina", 20.0D, 1.0D, 1024.0D);
	public static final RegistryObject<Attribute> POISE = registerRangedAttribute("poise", 0.0D, 0.0D, 1024.0D);
	public static final RegistryObject<Attribute> POISE_DAMAGE = registerRangedAttribute("poise_damage", 1.0D, 1.0D, 1024.0D);
	public static final RegistryObject<Attribute> MAX_EQUIP_LOAD = registerRangedAttribute("max_equip_load", 50.0D, 50.0D, 150.0D);
	public static final RegistryObject<Attribute> EQUIP_LOAD = registerRangedAttribute("equip_load", 0.0D, 0.0D, 150.0D);
	
	// Defense
	public static final RegistryObject<Attribute> STANDARD_DEFENSE = registerRangedAttribute("standard_defense", 0.0D, 0.0D, 0.99D);
	public static final RegistryObject<Attribute> STRIKE_DEFENSE = registerRangedAttribute("strike_defense", 0.0D, 0.0D, 0.99D);
	public static final RegistryObject<Attribute> SLASH_DEFENSE = registerRangedAttribute("slash_defense", 0.0D, 0.0D, 0.99D);
	public static final RegistryObject<Attribute> THRUST_DEFENSE = registerRangedAttribute("thrust_defense", 0.0D, 0.0D, 0.99D);
	
	public static final UUID[] EUIPMENT_MODIFIER_UUIDS = new UUID[]
	{
			UUID.fromString("02787320-87ac-4c4f-b057-cb79a2660041"),
			UUID.fromString("f16541b7-8a55-4a2b-ad65-0c21a3a12028"),
			UUID.fromString("683ac74c-a751-497e-abfe-e0af614bef46"),
			UUID.fromString("f3d4a8bb-853c-407f-9a02-0bf11c284466"),
			UUID.fromString("26c0477e-664b-44cd-9108-140699ad6807"),
			UUID.fromString("868429ef-4804-4cb8-b253-c89ac4af6c73")
	};
	public static final UUID MOVEMENT_SPEED_MODIFIER_UUID = UUID.fromString("1cdc2c63-da86-47bb-a6ea-ff0a06dab01e");
    
	private static RegistryObject<Attribute> registerRangedAttribute(String name, double defaultValue, double minValue, double maxValue)
	{
		return ATTRIBUTES.register(name, () -> new RangedAttribute("attribute."+DarkSouls.MOD_ID+"."+name, defaultValue, minValue, maxValue).setSyncable(true));
	}
	
	public static void createAttributeMap(EntityAttributeCreationEvent event)
	{
		event.put(ModEntities.FIRE_KEEPER.get(), FireKeeper.createAttributes().build());
		event.put(ModEntities.HOLLOW.get(), Hollow.createAttributes().build());
		event.put(ModEntities.HOLLOW_LORDRAN_WARRIOR.get(), HollowLordranWarrior.createAttributes().build());
		event.put(ModEntities.HOLLOW_LORDRAN_SOLDIER.get(), HollowLordranSoldier.createAttributes().build());
		event.put(ModEntities.STRAY_DEMON.get(), StrayDemon.createAttributes().build());
		event.put(ModEntities.CRESTFALLEN_WARRIOR.get(), QuestEntity.createAttributes().build());
		event.put(ModEntities.ANASTACIA_OF_ASTORA.get(), AnastaciaOfAstora.createAttributes().build());
	}
	
	public static void modifyAttributeMap(EntityAttributeModificationEvent event)
	{
		general(ModEntities.HOLLOW_LORDRAN_SOLDIER.get(), event);
		general(ModEntities.HOLLOW_LORDRAN_WARRIOR.get(), event);
		general(ModEntities.HOLLOW.get(), event);
		general(ModEntities.STRAY_DEMON.get(), event);
		general(ModEntities.FIRE_KEEPER.get(), event);
		/*general(EntityType.CAVE_SPIDER, event);
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
		
		general(EntityType.DROWNED, event);
		general(EntityType.ENDERMAN, event);
		general(EntityType.HUSK, event);
		general(EntityType.PIGLIN, event);
		general(EntityType.PIGLIN_BRUTE, event);
		general(EntityType.SKELETON, event);
		general(EntityType.STRAY, event);
		general(EntityType.WITHER_SKELETON, event);
		general(EntityType.ZOMBIE, event);
		general(EntityType.ZOMBIE_VILLAGER, event);
		general(EntityType.ZOMBIFIED_PIGLIN, event);*/
		
		withEquipLoad(ModEntities.CRESTFALLEN_WARRIOR.get(), event);
		withEquipLoad(ModEntities.ANASTACIA_OF_ASTORA.get(), event);
		
		player(EntityType.PLAYER, event);
	}
    
    private static void general(EntityType<? extends LivingEntity> entityType, EntityAttributeModificationEvent event)
    {
    	event.add(entityType, ModAttributes.POISE.get());
		event.add(entityType, ModAttributes.POISE_DAMAGE.get());
    	event.add(entityType, ModAttributes.STANDARD_DEFENSE.get());
		event.add(entityType, ModAttributes.STRIKE_DEFENSE.get());
		event.add(entityType, ModAttributes.SLASH_DEFENSE.get());
		event.add(entityType, ModAttributes.THRUST_DEFENSE.get());
		event.add(entityType, ModAttributes.MAX_STAMINA.get());
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
	}
	
	public static AttributeModifier getAttributeModifierForSlot(EquipmentSlot slot, float value)
	{
		return new AttributeModifier(EUIPMENT_MODIFIER_UUIDS[slot.ordinal()], DarkSouls.MOD_ID + ":equipment_modifier", value, AttributeModifier.Operation.ADDITION);
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
