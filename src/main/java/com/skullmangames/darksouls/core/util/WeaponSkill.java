package com.skullmangames.darksouls.core.util;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.animation.AnimationManager;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

public abstract class WeaponSkill
{
	private final ResourceLocation id;
	
	public WeaponSkill(ResourceLocation id)
	{
		this.id = id;
	}
	
	public ResourceLocation getId()
	{
		return this.id;
	}
	
	public abstract void perform(LivingCap<?> cap, InteractionHand hand);
	
	public static WeaponSkill.Builder basicBuilder(ResourceLocation location, ResourceLocation rightId)
	{
		return new Builder(location, rightId);
	}
	
	public static WeaponSkill.Builder mirrorBuilder(ResourceLocation location, ResourceLocation rightId, ResourceLocation leftId)
	{
		return new Builder(location, rightId, leftId);
	}
	
	public static class BaseWeaponSkill extends WeaponSkill
	{
		private final StaticAnimation animation;
		
		public BaseWeaponSkill(ResourceLocation id, StaticAnimation animation)
		{
			super(id);
			this.animation = animation;
		}
		
		public void perform(LivingCap<?> cap, InteractionHand hand)
		{
			if (this.animation != null)
			{
				cap.playAnimationSynchronized(this.animation, 0.0F);
			}
		}
	}
	
	public static class MirrorWeaponSkill extends WeaponSkill
	{
		private final StaticAnimation left;
		private final StaticAnimation right;
		
		public MirrorWeaponSkill(ResourceLocation id, StaticAnimation left, StaticAnimation right)
		{
			super(id);
			this.left = left;
			this.right = right;
		}
		
		@Override
		public void perform(LivingCap<?> cap, InteractionHand hand)
		{
			switch(hand)
			{
				default: case MAIN_HAND:
					if (this.right != null) cap.playAnimationSynchronized(this.right, 0.0F);
					break;
				case OFF_HAND:
					if (this.left != null) cap.playAnimationSynchronized(this.left, 0.0F);
					break;
			}
		}
	}
	
	public static enum WeaponSkillType
	{
		BASIC, TWO_SIDES;
		
		public static WeaponSkillType fromString(String id)
		{
			for (WeaponSkillType type : WeaponSkillType.values())
			{
				if (type.toString().equals(id)) return type;
			}
			return null;
		}
		
		@Override
		public String toString()
		{
			return super.toString().toLowerCase();
		}
	}
	
	public static class Builder implements JsonBuilder<WeaponSkill>
	{
		private ResourceLocation location;
		private WeaponSkillType skillType;
		private ResourceLocation rightAnimId;
		@Nullable private ResourceLocation leftAnimId;
		
		private Builder(ResourceLocation location)
		{
			this.location = location;
		}
		
		private Builder(ResourceLocation location, ResourceLocation rightAnimId)
		{
			this(location);
			this.skillType = WeaponSkillType.BASIC;
			this.rightAnimId = rightAnimId;
		}
		
		private Builder(ResourceLocation location, ResourceLocation rightAnimId, ResourceLocation leftAnimId)
		{
			this(location);
			this.skillType = WeaponSkillType.TWO_SIDES;
			this.rightAnimId = rightAnimId;
			this.leftAnimId = leftAnimId;
		}
		
		private Builder(ResourceLocation location, JsonObject json)
		{
			this.skillType = WeaponSkillType.fromString(json.get("skill_type").getAsString());
			
			switch (this.skillType)
			{
				default: case BASIC:
					this.rightAnimId = ResourceLocation.tryParse(json.get("right_animation").getAsString());
					break;
				case TWO_SIDES:
					this.rightAnimId = ResourceLocation.tryParse(json.get("right_animation").getAsString());
					this.leftAnimId = ResourceLocation.tryParse(json.get("left_animation").getAsString());
					break;
			}
		}
		
		public ResourceLocation getId()
		{
			return this.location;
		}
		
		@Override
		public JsonObject toJson()
		{
			JsonObject json = new JsonObject();
			json.addProperty("skill_type", this.skillType.toString());
			
			switch (this.skillType)
			{
				case BASIC:
					json.addProperty("right_animation", this.rightAnimId.toString());
					break;
					
				case TWO_SIDES:
					json.addProperty("right_animation", this.rightAnimId.toString());
					json.addProperty("left_animation", this.leftAnimId.toString());
					break;
			}
			
			return json;
		}
		
		public static Builder fromJson(ResourceLocation location, JsonObject json)
		{
			return new Builder(location, json);
		}
		
		@Override
		public WeaponSkill build()
		{
			switch (this.skillType)
			{
				default:
				case BASIC:
					return new BaseWeaponSkill(this.location, AnimationManager.getAnimation(this.rightAnimId));
				case TWO_SIDES:
					return new MirrorWeaponSkill(this.location, AnimationManager.getAnimation(this.leftAnimId), AnimationManager.getAnimation(this.rightAnimId));
			}
		}
	}
}
