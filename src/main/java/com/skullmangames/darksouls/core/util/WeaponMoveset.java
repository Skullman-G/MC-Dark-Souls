package com.skullmangames.darksouls.core.util;

import java.util.Map;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;

import net.minecraft.resources.ResourceLocation;

public class WeaponMoveset
{
	public static final WeaponMoveset EMPTY = new WeaponMoveset(new ResourceLocation("empty"), ImmutableMap.of());
	
	private final ResourceLocation name;
	private final ImmutableMap<AttackType, Pair<Boolean, AttackAnimation[]>> moveset;
	
	private WeaponMoveset(ResourceLocation name, ImmutableMap<AttackType, Pair<Boolean, AttackAnimation[]>> moveset)
	{
		this.name = name;
		this.moveset = moveset;
	}
	
	public ResourceLocation getName()
	{
		return this.name;
	}
	
	public Map<AttackType, Pair<Boolean, AttackAnimation[]>> getMoveset()
	{
		return this.moveset;
	}
	
	public AttackAnimation[] getAttacks(AttackType type)
	{
		return this.moveset.get(type).getSecond();
	}
	
	public Pair<Boolean, AttackAnimation[]> get(AttackType type)
	{
		return this.moveset.get(type);
	}
	
	public static class Builder
	{
		private static final Logger LOGGER = LogUtils.getLogger();
		private ResourceLocation location;
		private ImmutableMap.Builder<AttackType, Pair<Boolean, AttackAnimation[]>> moveset = ImmutableMap.builder();
		
		public Builder(ResourceLocation location)
		{
			this.location = location;
		}
		
		public ResourceLocation getLocation()
		{
			return this.location;
		}
		
		public Builder putMove(AttackType type, boolean repeat, AttackAnimation... animations)
		{
			this.moveset.put(type, new Pair<Boolean, AttackAnimation[]>(repeat, animations));
			return this;
		}
		
		public WeaponMoveset build()
		{
			return new WeaponMoveset(this.location, this.moveset.build());
		}
		
		public JsonObject toJson()
		{
			JsonObject root = new JsonObject();
			JsonObject moveset = new JsonObject();
			root.add("moveset", moveset);
			
			this.moveset.build().forEach((type, pair) ->
			{
				JsonObject entry = new JsonObject();
				moveset.add(type.toString(), entry);
				entry.addProperty("repeating", pair.getFirst());
				JsonArray animations = new JsonArray();
				entry.add("animations", animations);
				for (AttackAnimation a : pair.getSecond())
				{
					animations.add(a.getId().toString());
				}
			});
			
			return root;
		}
		
		public static WeaponMoveset.Builder fromJson(ResourceLocation location, JsonObject json)
		{
			WeaponMoveset.Builder builder = new WeaponMoveset.Builder(location);
			JsonObject moveset = json.get("moveset").getAsJsonObject();
			for (Map.Entry<String, JsonElement> entry : moveset.entrySet())
			{
				AttackType type = AttackType.fromString(entry.getKey());
				if (type == null)
				{
					LOGGER.error("Could not recognize AttackType with id "+entry.getKey()+" in "+location+". Skipping AttackType...");
					continue;
				}
				
				JsonObject entryObj = entry.getValue().getAsJsonObject();
				boolean repeating = entryObj.get("repeating").getAsBoolean();
				JsonArray animations = entryObj.get("animations").getAsJsonArray();
				AttackAnimation[] animList = new AttackAnimation[animations.size()];
				boolean cancel = false;
				
				for (int i = 0; i < animations.size(); i++)
				{
					JsonElement a = animations.get(i);
					StaticAnimation anim = DarkSouls.getInstance().animationManager.getAnimation(new ResourceLocation(a.getAsString()));
					if (anim instanceof AttackAnimation)
					{
						animList[i] = (AttackAnimation)anim;
					}
					else
					{
						LOGGER.error("AttackAnimation with id "+a.getAsString()+" not found. Skipping attacks for AttackType "+type+"...");
						cancel = true;
					}
				}
				
				if (!cancel) builder.putMove(type, repeating, animList);
			}
			
			return builder;
		}
	}
}
