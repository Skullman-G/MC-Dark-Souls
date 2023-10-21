package com.skullmangames.darksouls.core.util;

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
			for (WeaponSkillType category : WeaponSkillType.values())
			{
				if (category.toString().equals(id)) return category;
			}
			return null;
		}
		
		@Override
		public String toString()
		{
			return super.toString().toLowerCase();
		}
	}
	
	public static abstract class Builder
	{
		protected final ResourceLocation location;
		
		public Builder(ResourceLocation location)
		{
			this.location = location;
		}
		
		public ResourceLocation getLocation()
		{
			return this.location;
		}
		
		public static Builder fromJson(ResourceLocation location, JsonObject json)
		{
			WeaponSkillType type = WeaponSkillType.fromString(json.get("skill_type").getAsString());
			
			switch(type)
			{
			default: case BASIC:
				StaticAnimation animation = AnimationManager.getInstance().getAnimation(new ResourceLocation(json.get("animation").getAsString()));
				return new BaseBuilder(location, animation);
			case TWO_SIDES:
				StaticAnimation left = AnimationManager.getInstance().getAnimation(new ResourceLocation(json.get("left").getAsString()));
				StaticAnimation right = AnimationManager.getInstance().getAnimation(new ResourceLocation(json.get("right").getAsString()));
				return new MirrorBuilder(location, left, right);
			}
		}
		
		public abstract JsonObject toJson();
		
		public abstract WeaponSkill build();
	}
	
	public static class BaseBuilder extends Builder
	{
		protected final StaticAnimation animation;
		
		public BaseBuilder(ResourceLocation location, StaticAnimation animation)
		{
			super(location);
			this.animation = animation;
		}
		
		public JsonObject toJson()
		{
			JsonObject root = new JsonObject();
			root.addProperty("skill_type", WeaponSkillType.BASIC.toString());
			root.addProperty("animation", this.animation.getId().toString());
			return root;
		}
		
		public WeaponSkill build()
		{
			return new BaseWeaponSkill(this.location, this.animation);
		}
	}
	
	public static class MirrorBuilder extends Builder
	{
		protected final StaticAnimation left;
		protected final StaticAnimation right;
		
		public MirrorBuilder(ResourceLocation location, StaticAnimation left, StaticAnimation right)
		{
			super(location);
			this.left = left;
			this.right = right;
		}
		
		public JsonObject toJson()
		{
			JsonObject root = new JsonObject();
			root.addProperty("skill_type", WeaponSkillType.TWO_SIDES.toString());
			root.addProperty("left", this.left.getId().toString());
			root.addProperty("right", this.right.getId().toString());
			return root;
		}
		
		public WeaponSkill build()
		{
			return new MirrorWeaponSkill(this.location, this.left, this.right);
		}
	}
}
