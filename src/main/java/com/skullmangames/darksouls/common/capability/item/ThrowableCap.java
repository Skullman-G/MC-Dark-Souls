package com.skullmangames.darksouls.common.capability.item;

import java.util.function.Supplier;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.util.json.JsonBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ThrowableCap extends ItemCapability
{
	private final EntityType<?> projectile;
	private final Supplier<SoundEvent> throwSound;
	
	public ThrowableCap(Item item, EntityType<?> projectile, Supplier<SoundEvent> throwSound)
	{
		super(item);
		this.projectile = projectile;
		this.throwSound = throwSound;
		
	}
	
	public void use(LivingCap<?> cap)
	{
		cap.playAnimationSynchronized(Animations.BIPED_THROW.get(), 0.0F);
	}
	
	public void spawnProjectile(LivingCap<?> cap)
	{
		cap.playSound(this.throwSound.get());
		LivingEntity entity = cap.getOriginalEntity();
		
		Entity e = this.projectile.create(entity.level);
		if (e instanceof ThrowableItemProjectile p)
		{
			ItemStack itemStack = entity.getMainHandItem();
			p.setPos(entity.getX(), entity.getEyeY() - (double)0.1F, entity.getZ());
			p.setOwner(entity);
			p.setItem(itemStack);
			p.shootFromRotation(entity, cap.getXRot(), cap.getYRot(), 0.0F, 1.5F, 1.0F);
			entity.level.addFreshEntity(p);
			
			if (entity instanceof Player player)
			{
				player.awardStat(Stats.ITEM_USED.get(this.orgItem));
				if (!player.getAbilities().instabuild)
				{
					itemStack.shrink(1);
				}
			}
		}
	}
	
	public static Builder builder(Item item, EntityType<?> projectile, Supplier<SoundEvent> throwSound)
	{
		return new Builder(item, projectile, throwSound);
	}
	
	public static class Builder implements JsonBuilder<ThrowableCap>
	{
		protected Item item;
		protected EntityType<?> projectile;
		protected Supplier<SoundEvent> throwSound;
		
		protected Builder(Item item, EntityType<?> projectile, Supplier<SoundEvent> throwSound)
		{
			this.item = item;
			this.projectile = projectile;
			this.throwSound = throwSound;
		}
		
		protected Builder(ResourceLocation location, JsonObject json)
		{
			ResourceLocation itemId = ResourceLocation.tryParse(json.get("registry_name").getAsString());
			this.item = ForgeRegistries.ITEMS.getValue(itemId);
			
			ResourceLocation projectileId = ResourceLocation.tryParse(json.get("projectile").getAsString());
			this.projectile = ForgeRegistries.ENTITIES.getValue(projectileId);
			
			ResourceLocation throwSoundId = ResourceLocation.tryParse(json.get("throw_sound").getAsString());
			this.throwSound = () -> ForgeRegistries.SOUND_EVENTS.getValue(throwSoundId);
		}
		
		@Override
		public ResourceLocation getId()
		{
			return this.item.getRegistryName();
		}
		
		@Override
		public JsonObject toJson()
		{
			JsonObject json = new JsonObject();
			
			json.addProperty("registry_name", this.item.getRegistryName().toString());
			json.addProperty("projectile", this.projectile.getRegistryName().toString());
			json.addProperty("throw_sound", this.throwSound.get().getRegistryName().toString());
			
			return json;
		}
		
		public static Builder fromJson(ResourceLocation location, JsonObject json)
		{
			return new Builder(location, json);
		}
		
		@Override
		public ThrowableCap build()
		{
			return new ThrowableCap(this.item, this.projectile, this.throwSound);
		}
	}
}
