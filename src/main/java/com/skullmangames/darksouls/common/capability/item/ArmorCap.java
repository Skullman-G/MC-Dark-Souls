package com.skullmangames.darksouls.common.capability.item;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.input.ModKeys;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ArmorCap extends AttributeItemCap
{
	private final EquipmentSlot equipSlot;
	
	private final ImmutableMap<ArmorDefenseType, Float> defense;
	
	private float weight;
	private float poise;
	
	public ArmorCap(ArmorItem item, float weight, float poise, ImmutableMap<ArmorDefenseType, Float> defense)
	{
		super(item);
		ArmorItem armorItem = this.getOriginalItem();
		this.equipSlot = armorItem.getSlot();
		this.weight = weight;
		this.poise = poise;
		this.defense = defense;
	}
	
	@Override
	public ArmorItem getOriginalItem()
	{
		return (ArmorItem)this.orgItem;
	}
	
	@Override
	public void modifyItemTooltip(List<Component> itemTooltip, PlayerCap<?> playerdata, ItemStack stack)
	{
		if (!(this.orgItem instanceof IForgeRegistryEntry)) return;

		while (itemTooltip.size() >= 2) itemTooltip.remove(1);

		if (ClientManager.INSTANCE.inputManager.isKeyDown(ModKeys.SHOW_ITEM_INFO))
		{
			String languagePath = "tooltip." + DarkSouls.MOD_ID + "."
					+ ((IForgeRegistryEntry<Item>) this.orgItem).getRegistryName().getPath() + ".extended";
			String description = new TranslatableComponent(languagePath).getString();

			if (!description.contains(languagePath))
				itemTooltip.add(new TextComponent("\u00A77\n" + description));
		}
		else
		{
			itemTooltip.add(new TextComponent(""));
			
			itemTooltip.add(new TextComponent("\u00A77"
					+ new TranslatableComponent(ModAttributes.STANDARD_PROTECTION.get().getDescriptionId()).getString() + ": "
					+ this.defense.getOrDefault(ArmorDefenseType.REGULAR, 0F)));
			itemTooltip.add(new TextComponent("\u00A77"
					+ new TranslatableComponent(ModAttributes.STRIKE_PROTECTION.get().getDescriptionId()).getString() + ": "
					+ this.defense.getOrDefault(ArmorDefenseType.STRIKE, 0F)));
			itemTooltip.add(new TextComponent("\u00A77"
					+ new TranslatableComponent(ModAttributes.SLASH_PROTECTION.get().getDescriptionId()).getString() + ": "
					+ this.defense.getOrDefault(ArmorDefenseType.SLASH, 0F)));
			itemTooltip.add(new TextComponent("\u00A77"
					+ new TranslatableComponent(ModAttributes.THRUST_PROTECTION.get().getDescriptionId()).getString() + ": "
					+ this.defense.getOrDefault(ArmorDefenseType.THRUST, 0F)));
			itemTooltip.add(new TextComponent("\u00A73"
					+ new TranslatableComponent(ModAttributes.MAGIC_PROTECTION.get().getDescriptionId()).getString() + ": "
					+ this.defense.getOrDefault(ArmorDefenseType.MAGIC, 0F)));
			itemTooltip.add(new TextComponent("\u00A7c"
					+ new TranslatableComponent(ModAttributes.FIRE_PROTECTION.get().getDescriptionId()).getString() + ": "
					+ this.defense.getOrDefault(ArmorDefenseType.FIRE, 0F)));
			itemTooltip.add(new TextComponent("\u00A7e"
					+ new TranslatableComponent(ModAttributes.LIGHTNING_PROTECTION.get().getDescriptionId()).getString() + ": "
					+ this.defense.getOrDefault(ArmorDefenseType.LIGHTNING, 0F)));
			itemTooltip.add(new TextComponent("\u00A76"
					+ new TranslatableComponent(ModAttributes.HOLY_PROTECTION.get().getDescriptionId()).getString() + ": "
					+ this.defense.getOrDefault(ArmorDefenseType.HOLY, 0F)));
			itemTooltip.add(new TextComponent("\u00A75"
					+ new TranslatableComponent(ModAttributes.DARK_PROTECTION.get().getDescriptionId()).getString() + ": "
					+ this.defense.getOrDefault(ArmorDefenseType.DARK, 0F)));
			
			itemTooltip.add(new TextComponent(""));
			itemTooltip.add(new TranslatableComponent(ModAttributes.POISE.get().getDescriptionId()).withStyle(ChatFormatting.WHITE)
					.append(new TextComponent(ChatFormatting.WHITE+": "+MathUtils.round(this.poise, 2))));
			itemTooltip.add(new TranslatableComponent("attribute.darksouls.weight").withStyle(ChatFormatting.WHITE)
					.append(new TextComponent(ChatFormatting.WHITE+": "+MathUtils.round(this.weight, 2))));
		}
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot)
	{
		Multimap<Attribute, AttributeModifier> map = super.getAttributeModifiers(slot);
		
		if (this.equipSlot == slot)
		{
			map.put(ModAttributes.POISE.get(), ModAttributes.getAttributeModifierForSlot(this.equipSlot, this.poise));
			map.put(ModAttributes.EQUIP_LOAD.get(), ModAttributes.getAttributeModifierForSlot(this.equipSlot, this.weight));
			
			this.defense.forEach((type, def) ->
			{
				map.put(type.getDefenseAttribute(), ModAttributes.getAttributeModifierForSlot(this.equipSlot, def));
			});
		}
		
        return map;
    }
	
	public static Builder builder(Item item, float weight, float poise)
	{
		return new Builder((ArmorItem)item, weight, poise);
	}
	
	public enum ArmorDefenseType
	{
		REGULAR, STRIKE, SLASH, THRUST, MAGIC, FIRE, LIGHTNING, HOLY, DARK;
		
		private Attribute getDefenseAttribute()
		{
			switch (this)
			{
				default: case REGULAR: return ModAttributes.STANDARD_PROTECTION.get();
				case STRIKE: return ModAttributes.STRIKE_PROTECTION.get();
				case SLASH: return ModAttributes.SLASH_PROTECTION.get();
				case THRUST: return ModAttributes.THRUST_PROTECTION.get();
				case MAGIC: return ModAttributes.MAGIC_PROTECTION.get();
				case FIRE: return ModAttributes.LIGHTNING_PROTECTION.get();
				case LIGHTNING: return ModAttributes.LIGHTNING_PROTECTION.get();
				case HOLY: return ModAttributes.HOLY_PROTECTION.get();
				case DARK: return ModAttributes.DARK_PROTECTION.get();
			}
		}
	}
	
	public static class Builder
	{
		private ArmorItem item;
		private float weight;
		private float poise;
		private ImmutableMap.Builder<ArmorDefenseType, Float> defense = ImmutableMap.builder();
		
		public Builder(ArmorItem item, float weight, float poise)
		{
			this.item = item;
			this.weight = weight;
			this.poise = poise;
		}
		
		public ResourceLocation getLocation()
		{
			return this.item.getRegistryName();
		}
		
		public Builder putDefense(ArmorDefenseType type, float value)
		{
			this.defense.put(type, value);
			return this;
		}
		
		public JsonObject toJson()
		{
			JsonObject root = new JsonObject();
			
			root.addProperty("registry_name", this.item.getRegistryName().toString());
			root.addProperty("weight", this.weight);
			root.addProperty("poise", this.poise);
			
			JsonObject defense = new JsonObject();
			root.add("defense", defense);
			this.defense.build().forEach((type, def) ->
			{
				defense.addProperty(type.toString(), def);
			});
			return root;
		}
		
		public static Builder fromJson(ResourceLocation location, JsonObject json)
		{
			ResourceLocation itemId = ResourceLocation.tryParse(json.get("registry_name").getAsString());
			Item item = ForgeRegistries.ITEMS.getValue(itemId);
			
			if (!(item instanceof ArmorItem))
				DarkSouls.LOGGER.error("Error while reading weapon config for "+location+". Item is not an instance of ArmorItem");
			
			float weight = json.get("weight").getAsFloat();
			float poise = json.get("poise").getAsFloat();
			
			Builder builder = new Builder((ArmorItem)item, weight, poise);
			
			JsonObject defense = json.get("defense").getAsJsonObject();
			for (ArmorDefenseType type : ArmorDefenseType.values())
			{
				float def = Optional.ofNullable(defense.get(type.toString())).orElse(new JsonPrimitive(0)).getAsFloat();
				builder.putDefense(type, def);
			}
			
			return builder;
		}
		
		public ArmorCap build()
		{
			return new ArmorCap(this.item, this.weight, this.poise, this.defense.build());
		}
	}
}
