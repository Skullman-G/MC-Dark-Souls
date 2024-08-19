package com.skullmangames.darksouls.core.util;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.animation.AnimationManager;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;

import net.minecraft.resources.ResourceLocation;

public class WeaponMoveset
{
	public static final WeaponMoveset EMPTY = new WeaponMoveset(new ResourceLocation("empty"), ImmutableMap.of());
	
	private final ResourceLocation id;
	private final ImmutableMap<AttackType, Pair<Boolean, AttackAnimation[]>> moveset;
	
	private WeaponMoveset(ResourceLocation name, ImmutableMap<AttackType, Pair<Boolean, AttackAnimation[]>> moveset)
	{
		this.id = name;
		this.moveset = moveset;
	}
	
	public ResourceLocation getId()
	{
		return this.id;
	}
	
	public AttackAnimation[] getAttacks(AttackType type)
	{
		return this.moveset.get(type).getSecond();
	}
	
	public Pair<Boolean, AttackAnimation[]> get(AttackType type)
	{
		return this.moveset.get(type);
	}
	
	public static class Builder implements JsonBuilder<WeaponMoveset>
	{
		private static final Logger LOGGER = LogUtils.getLogger();
		private final ResourceLocation id;
		private final Map<AttackType, Pair<Boolean, ResourceLocation[]>> moveset = new HashMap<>();
		
		public Builder(ResourceLocation id)
		{
			this.id = id;
		}
		
		private Builder(ResourceLocation location, JsonObject json)
		{
			this.id = location;
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
				ResourceLocation[] animIds = new ResourceLocation[animations.size()];
				
				for (int i = 0; i < animations.size(); i++)
				{
					animIds[i] = ResourceLocation.tryParse(animations.get(i).getAsString());
				}
				
				this.putMove(type, repeating, animIds);
			}
		}
		
		public ResourceLocation getId()
		{
			return this.id;
		}
		
		public Builder putMove(AttackType type, boolean repeat, ResourceLocation... animationIds)
		{
			this.moveset.put(type, new Pair<Boolean, ResourceLocation[]>(repeat, animationIds));
			return this;
		}
		
		public WeaponMoveset build()
		{
			ImmutableMap.Builder<AttackType, Pair<Boolean, AttackAnimation[]>> moveset = ImmutableMap.builder();
			
			this.moveset.forEach((attackType, pair) ->
			{
				ResourceLocation[] ids = pair.getSecond();
				AttackAnimation[] anims = new AttackAnimation[ids.length];
				for (int i = 0; i < anims.length; i++) anims[i] = AnimationManager.getAttackAnimation(ids[i]);
				moveset.put(attackType, new Pair<Boolean, AttackAnimation[]>(pair.getFirst(), anims));
			});
			
			return new WeaponMoveset(this.id, moveset.build());
		}
		
		public JsonObject toJson()
		{
			JsonObject root = new JsonObject();
			JsonObject moveset = new JsonObject();
			root.add("moveset", moveset);
			
			this.moveset.forEach((type, pair) ->
			{
				JsonObject entry = new JsonObject();
				moveset.add(type.toString(), entry);
				entry.addProperty("repeating", pair.getFirst());
				JsonArray animations = new JsonArray();
				entry.add("animations", animations);
				for (ResourceLocation a : pair.getSecond())
				{
					animations.add(a.toString());
				}
			});
			
			return root;
		}
		
		public static WeaponMoveset.Builder fromJson(ResourceLocation location, JsonObject json)
		{
			return new WeaponMoveset.Builder(location, json);
		}
	}
}
