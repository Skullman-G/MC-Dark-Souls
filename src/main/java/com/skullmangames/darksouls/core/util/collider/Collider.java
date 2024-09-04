package com.skullmangames.darksouls.core.util.collider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.common.animation.Animator;
import com.skullmangames.darksouls.common.capability.entity.EntityCapability;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.init.data.Colliders;
import com.skullmangames.darksouls.core.util.JsonBuilder;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Collider
{
	private final ResourceLocation id;
	private Vec3 worldCenter = Vec3.ZERO;
	protected Vec3[] vertices;
	protected Vec3[] modelVertices;
	
	/**
	 * Test hitbox.
	 * Bigger than collider hitbox.
	 * Uses mass center instead of world center.
	 **/
	protected final AABB outerAABB;

	public Collider(ResourceLocation id, AABB outerAABB)
	{
		this.id = id;
		this.outerAABB = outerAABB;
	}
	
	@Nullable
	public ResourceLocation getId()
	{
		return this.id;
	}
	
	public Vec3 getWorldCenter()
	{
		return this.worldCenter;
	}
	
	public void transform(ModMatrix4f mat)
	{
		Vec3 pos = mat.transform(Vec3.ZERO);
		this.moveTo(new Vec3(-pos.x, pos.y, -pos.z));
	}
	
	protected void moveTo(Vec3 pos)
	{
		this.worldCenter = pos;
		for (int i = 0; i < this.vertices.length; i++)
		{
			this.vertices[i] = this.vertices[i].add(this.worldCenter);
		}
	}
	
	public BlockHitResult getBlockCollisions(BlockGetter level)
	{
		AABB aabb = this.getHitboxAABB();
		BlockHitResult hitResult = level.clip(new ClipContext(new Vec3(aabb.minX, aabb.minY, aabb.minZ), new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ),
				ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
		return hitResult;
	}
	
	public List<Entity> getShieldCollisions(Entity self)
	{
		List<Entity> list = self.level.getEntities(self, this.getHitboxAABB().inflate(5));
		List<Entity> newList = new ArrayList<>();
		for (Entity e : list)
		{
			if (e instanceof LivingEntity)
			{
				LivingCap<?> cap = (LivingCap<?>)e.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
				if (cap != null && cap.isBlocking())
				{
					ModMatrix4f modelMat = cap.getModelMatrix(1.0F).rotateDeg(90, Vector3f.YP);
					ModMatrix4f mat = modelMat.translate(0.4F, e.getBbHeight() / 2, 0);
					Collider shieldCollider = Colliders.SHIELD.get();
					shieldCollider.transform(mat);
					if (this.collidesWith(shieldCollider)) newList.add(e);
				}
			}
		}
		return newList;
	}

	public List<Entity> getEntityCollisions(Entity self)
	{
		List<Entity> list = self.level.getEntities(self, this.getHitboxAABB());
		this.filterHitEntities(list);
		return list;
	}
	
	protected void filterHitEntities(List<Entity> entities)
	{
		entities.removeIf((entity) -> !this.collidesWith(entity));
	}
	
	protected abstract Vec3 min();
	
	protected abstract Vec3 max();
	
	public abstract Vec3 top();
	
	public abstract Vec3 bottom();
	
	public abstract boolean collidesWith(Collider other);
	
	public abstract Vec3 collide(Vec3 movement, List<ColliderHolder> others);

	protected abstract boolean collidesWith(Entity opponent);

	public AABB getHitboxAABB()
	{
		return this.outerAABB.move(this.getMassCenter());
	}
	
	public abstract Vec3 getMassCenter();
	
	public ModMatrix4f update(EntityCapability<?> entityCap, String jointName, float partialTicks)
	{
		ModMatrix4f transformMatrix;
		
		if (entityCap instanceof LivingCap<?> livingCap)
		{
			Armature armature = livingCap.getEntityModel(Models.SERVER).getArmature();
			int pathIndex = armature.searchPathIndex(jointName);

			if (pathIndex == -1) transformMatrix = new ModMatrix4f();
			else transformMatrix = Animator.getParentboundTransform(livingCap.getAnimator().getPose(partialTicks), armature, pathIndex);
			
			float scale = livingCap.getModelScale();
			transformMatrix.mulFront(entityCap.getModelMatrix(partialTicks).scale(scale, scale, scale));
		}
		else transformMatrix = entityCap.getModelMatrix(partialTicks);
		
		this.transform(transformMatrix);
		
		return transformMatrix;
	}

	@OnlyIn(Dist.CLIENT)
	public void draw(LivingCap<?> entityCap, String jointName, float partialTicks)
	{
		boolean red = entityCap.getEntityState() == EntityState.CONTACT;
		this.update(entityCap, jointName, partialTicks);

		this.drawInternal(red);
	}
	
	@OnlyIn(Dist.CLIENT)
	public abstract void drawInternal(boolean red);
	
	@Override
	public String toString()
	{
		return this.id.toString();
	}
	
	public static Builder capsuleBuilder(ResourceLocation id, double radius, double height, Vec3 base, float xRot, float yRot)
	{
		return new CapsuleCollider.Builder(id, radius, height, base, xRot, yRot);
	}
	
	public static Builder capsuleBuilder(ResourceLocation id, double radius, double height, Vec3 base)
	{
		return capsuleBuilder(id, radius, height, base, 0F, 0F);
	}
	
	public static Builder cubeBuilder(ResourceLocation id, double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
	{
		return new CubeCollider.Builder(id, minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	public static CoreBuilder multiBuilder(ResourceLocation id, Builder... builders)
	{
		return new CoreBuilder(id, builders);
	}
	
	public static enum ColliderType
	{
		CUBE, CAPSULE
	}
	
	public static class CoreBuilder implements JsonBuilder<Collider>
	{
		private final ResourceLocation id;
		private final List<Builder> colliders;
		
		private CoreBuilder(ResourceLocation id, Builder... builders)
		{
			this.id = id;
			this.colliders = Arrays.asList(builders);
		}
		
		private CoreBuilder(ResourceLocation location, JsonObject json)
		{
			this.id = location;
			this.colliders = new ArrayList<>();
			
			JsonElement arrayElement = json.get("multiple");
			Iterable<JsonElement> array = arrayElement == null ? Arrays.asList(json) : arrayElement.getAsJsonArray();
			
			for (JsonElement e : array)
			{
				JsonObject o = e.getAsJsonObject();
				ColliderType type = ColliderType.valueOf(o.get("type").getAsString());
				
				switch (type)
				{
					case CUBE:
						this.colliders.add(new CubeCollider.Builder(location, o));
						break;
						
					case CAPSULE:
						this.colliders.add(new CapsuleCollider.Builder(location, o));
						break;
				}
			}
		}
		
		@Override
		public ResourceLocation getId()
		{
			return this.id;
		}
		
		public static CoreBuilder fromJson(ResourceLocation location, JsonObject json)
		{
			return new CoreBuilder(location, json);
		}
		
		@Override
		public JsonObject toJson()
		{
			JsonObject json = new JsonObject();
			
			JsonArray array = new JsonArray();
			json.add("multiple", array);
			
			for (Builder collider : this.colliders)
			{
				array.add(collider.toJson());
			}
			
			return json;
		}
		
		@Override
		public Collider build()
		{
			if (this.colliders.size() > 1)
			{
				Collider[] array = new Collider[this.colliders.size()];
				for (int i = 0; i < array.length; i++)
				{
					array[i] = this.colliders.get(i).build();
				}
				
				return new MultiCollider(this.getId(), array);
			}
			else
			{
				return this.colliders.get(0).build();
			}
		}
	}
	
	public static abstract class Builder implements JsonBuilder<Collider>
	{
		private ResourceLocation id;
		
		protected Builder(ResourceLocation id)
		{
			this.id = id;
		}
		
		protected Builder(ResourceLocation location, JsonObject json)
		{
			this.id = location;
		}
		
		protected abstract ColliderType getType();
		
		@Override
		public JsonObject toJson()
		{
			JsonObject json = new JsonObject();
			json.addProperty("type", this.getType().name());
			return json;
		}
		
		@Override
		public ResourceLocation getId()
		{
			return this.id;
		}
	}
}