package com.skullmangames.darksouls.core.util.json;

import java.lang.reflect.Field;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class JsonMapper
{
	private static final Gson gson = new Gson();

	public static JsonObject toJson(Object obj)
	{
		JsonObject json = new JsonObject();
		for (Field field : obj.getClass().getDeclaredFields())
		{
			if (field.isAnnotationPresent(JsonKey.class))
			{
				field.setAccessible(true);
				JsonKey key = field.getAnnotation(JsonKey.class);
				try
				{
					Object value = field.get(obj);
					if (value instanceof EntityType<?> entityType)
					{
						json.addProperty(key.value(), entityType.getRegistryName().toString());
					}
					else
					{
						json.add(key.value(), gson.toJsonTree(value));
					}
				}
				catch (IllegalAccessException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		return json;
	}

	public static <T extends JsonBuilder<?>> T fromJson(JsonObject json, Class<T> clazz)
	{
		try
		{
			T instance = clazz.getDeclaredConstructor().newInstance();
			for (Field field : clazz.getDeclaredFields())
			{
				if (field.isAnnotationPresent(JsonKey.class))
				{
					field.setAccessible(true);
					JsonKey key = field.getAnnotation(JsonKey.class);
					JsonElement element = json.get(key.value());
					if (element != null)
					{
						if (field.getType().equals(EntityType.class))
						{
							EntityType<?> type = ForgeRegistries.ENTITIES
									.getValue(new ResourceLocation(element.getAsString()));
							field.set(instance, type);
						}
						else
						{
							Object value = gson.fromJson(element, field.getGenericType());
							field.set(instance, value);
						}
					}
				}
			}
			return instance;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
