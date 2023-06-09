package com.skullmangames.darksouls.common.attribute;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.skullmangames.darksouls.DarkSouls;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = DarkSouls.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class VanillaAttributeFix
{
	private static final Map<String, Double> attributes = ImmutableMap.of
	(
			"generic.max_health", 100_000_000D,
			"generic.attack_damage", 100_000_000D
	);
	
	private static final Map<ResourceLocation, Integer> maxHealth = ImmutableMap.of
	(
			new ResourceLocation("player"), 400
	);
	
	@SubscribeEvent
    public static void onLoadComplete(FMLLoadCompleteEvent event)
	{
        DarkSouls.LOGGER.info("Changing range of "+ attributes.size() +" vanilla attributes");
        attributes.forEach((string, entry) ->
        {
        	ResourceLocation id = ResourceLocation.tryParse(string);
        	Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(id);
        	
        	if (attribute instanceof RangedAttribute ranged)
        	{
                double maxValue = entry;
                
                if (maxValue != ranged.getMaxValue())
                {
                    DarkSouls.LOGGER.info("Changing max value for "+ id +" from "+ ranged.getMaxValue() +" to "+ maxValue +".");
                    ranged.maxValue = maxValue;
                }
            }
        });
    }
	
	private static boolean containsMaxHealth(ResourceLocation id)
	{
		for (ResourceLocation rl : maxHealth.keySet())
		{
			if (id.compareTo(rl) == 0) return true;
		}
		return false;
	}
	
	@SubscribeEvent
	public static void modifyAttributes(EntityAttributeModificationEvent event)
	{
		@SuppressWarnings("deprecation")
		Map<EntityType<? extends LivingEntity>, AttributeSupplier> values = ForgeHooks.getAttributesView();
		for (EntityType<?> entry : ForgeRegistries.ENTITIES)
		{
			try
			{
				@SuppressWarnings("unchecked")
				EntityType<LivingEntity> entity = (EntityType<LivingEntity>)entry;
				ResourceLocation id = entity.getRegistryName();
				if (!id.getNamespace().equals(DarkSouls.MOD_ID))
				{
					if (containsMaxHealth(id))
					{
						event.add(entity, Attributes.MAX_HEALTH, maxHealth.get(id));
					}
					else
					{
						AttributeSupplier supplier = values.get(entity);
						if (supplier != null)
						{
							double value = supplier.getBaseValue(Attributes.MAX_HEALTH) * 100D;
							event.add(entity, Attributes.MAX_HEALTH, value);
						}
					}
				}
			}
			catch (ClassCastException e) {}
		}
		
		for (Item item : ForgeRegistries.ITEMS)
		{
			if (item instanceof SwordItem sword)
			{
				float value = 0;
				sword.attackDamage = value;
				Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
				AttributeModifier[] modifiers = new AttributeModifier[sword.defaultModifiers.get(Attributes.ATTACK_DAMAGE).size()];
				int i = 0;
				for (AttributeModifier modifier : sword.defaultModifiers.get(Attributes.ATTACK_DAMAGE))
				{
					modifiers[i] = new AttributeModifier(modifier.getId(), modifier.getName(), value, modifier.getOperation());
					i++;
				}
				builder.putAll(Attributes.ATTACK_DAMAGE, modifiers);
			    builder.putAll(Attributes.ATTACK_SPEED, sword.defaultModifiers.get(Attributes.ATTACK_SPEED));
				sword.defaultModifiers = builder.build();
			}
			else if (item instanceof DiggerItem tool)
			{
				float value = 0;
				tool.attackDamageBaseline = value;
				Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
				AttributeModifier[] modifiers = new AttributeModifier[tool.defaultModifiers.get(Attributes.ATTACK_DAMAGE).size()];
				int i = 0;
				for (AttributeModifier modifier : tool.defaultModifiers.get(Attributes.ATTACK_DAMAGE))
				{
					modifiers[i] = new AttributeModifier(modifier.getId(), modifier.getName(), value, modifier.getOperation());
					i++;
				}
				builder.putAll(Attributes.ATTACK_DAMAGE, modifiers);
			    builder.putAll(Attributes.ATTACK_SPEED, tool.defaultModifiers.get(Attributes.ATTACK_SPEED));
			    tool.defaultModifiers = builder.build();
			}
		}
	}
}
