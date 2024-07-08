package com.skullmangames.darksouls.core.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.skullmangames.darksouls.common.animation.Property.MovementAnimationSet;
import com.skullmangames.darksouls.common.animation.events.AnimEvent;
import net.minecraft.resources.ResourceLocation;

public interface JsonElementConverter<T>
{
	public static JsonElementConverter<Boolean> BOOLEAN = new JsonElementConverter<Boolean>()
	{
		public JsonElement toJson(Object value)
		{
			return new JsonPrimitive((boolean)value);
		}
		public Boolean fromJson(JsonElement json)
		{
			return json.getAsBoolean();
		}
	};
	
	public static JsonElementConverter<Integer> INTEGER = new JsonElementConverter<Integer>()
	{
		public JsonElement toJson(Object value)
		{
			return new JsonPrimitive((int)value);
		}
		public Integer fromJson(JsonElement json)
		{
			return json.getAsInt();
		}
	};
	
	public static JsonElementConverter<Float> FLOAT = new JsonElementConverter<Float>()
	{
		public JsonElement toJson(Object value)
		{
			return new JsonPrimitive((float)value);
		}
		public Float fromJson(JsonElement json)
		{
			return json.getAsFloat();
		}
	};
	
	public static JsonElementConverter<ResourceLocation> RESOURCE_LOCATION = new JsonElementConverter<ResourceLocation>()
	{
		public JsonElement toJson(Object value)
		{
			return new JsonPrimitive(((ResourceLocation)value).toString());
		}
		public ResourceLocation fromJson(JsonElement json)
		{
			return new ResourceLocation(json.getAsString());
		}
	};
	
	public static <T extends Enum<T>> JsonElementConverter<T> ENUM(Class<T> type)
	{
		return new JsonElementConverter<T>()
		{
			@SuppressWarnings("unchecked")
			public JsonElement toJson(Object value)
			{
				return new JsonPrimitive(((Enum<T>)value).name());
			}
			public T fromJson(JsonElement json)
			{
				return Enum.valueOf(type, json.getAsString());
			}
		};
	};
	
	public static JsonElementConverter<AnimEvent[]> EVENTS = new JsonElementConverter<AnimEvent[]>()
	{
		public JsonElement toJson(Object value)
		{
			JsonArray jsonArray = new JsonArray();
			
			for (AnimEvent event : (AnimEvent[])value)
			{
				jsonArray.add(event.toJson());
			}
			
			return jsonArray;
		}
		public AnimEvent[] fromJson(JsonElement json)
		{
			JsonArray jsonArray = json.getAsJsonArray();
			AnimEvent[] events = new AnimEvent[jsonArray.size()];
			
			for (int i = 0; i < jsonArray.size(); i++)
			{
				JsonObject event = jsonArray.get(i).getAsJsonObject();
				String type = event.get("event_type").getAsString();
				events[i] = AnimEvent.BUILDERS.get(type).apply(event);
			}
			
			return events;
		}
	};
	
	public static JsonElementConverter<MovementAnimationSet> MOVEMENT_ANIMATION_SET = new JsonElementConverter<MovementAnimationSet>()
	{
		public JsonElement toJson(Object value)
		{
			return null;
		}
		public MovementAnimationSet fromJson(JsonElement json)
		{
			return (self, entityCap, transformSheet) ->
			{
				transformSheet.readFrom(self.getTransfroms().get("Root"));
			};
		}
	};
	
	public JsonElement toJson(Object value);
	public T fromJson(JsonElement json);
}
