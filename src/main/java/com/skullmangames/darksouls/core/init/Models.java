package com.skullmangames.darksouls.core.init;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;

import net.minecraft.util.ResourceLocation;

public abstract class Models<T extends Model>
{
	protected final List<T> ARMATURES = new ArrayList<T>();
	public static final ServerModels SERVER = new ServerModels();
	
	public T ENTITY_BIPED;
	public T ENTITY_BIPED_64_32_TEX;
	public T ENTITY_BIPED_SLIM_ARM;
	public T ENTITY_VILLAGER_ZOMBIE;
	public T ENTITY_VILLAGER_ZOMBIE_BODY;
	public T ENTITY_CREEPER;
	public T ENTITY_ENDERMAN;
	public T ENTITY_SKELETON;
	public T ENTITY_SPIDER;
	public T ENTITY_GOLEM;
	public T ENTITY_ILLAGER;
	public T ENTITY_WITCH;
	public T ENTITY_RAVAGER;
	public T ENTITY_VEX;
	public T ENTITY_PIGLIN;
	public T ENTITY_HOGLIN;
	public T ENTITY_ASYLUM_DEMON;
	
	protected abstract T register(String name);
	protected abstract T register(String name, String armaturePath);
	protected abstract T registerMeshOnly(String name);
	
	public void buildArmatureData()
	{
		for (T model : this.ARMATURES) model.loadArmatureData();
	}
	
	public static class ServerModels extends Models<Model>
	{
		public ServerModels()
		{
			this.ENTITY_BIPED = this.register("biped");
			this.ENTITY_BIPED_64_32_TEX = this.register("biped", "biped");
			this.ENTITY_BIPED_SLIM_ARM = this.register("biped_slim_arm", "biped");
			this.ENTITY_VILLAGER_ZOMBIE = this.register("zombie_villager", "biped");
			this.ENTITY_VILLAGER_ZOMBIE_BODY = this.registerMeshOnly("zombie_villager_body");
			this.ENTITY_CREEPER = this.register("creeper");
			this.ENTITY_ENDERMAN = this.register("enderman");
			this.ENTITY_SKELETON = this.register("skeleton");
			this.ENTITY_SPIDER = this.register("spider");
			this.ENTITY_GOLEM = this.register("iron_golem");
			this.ENTITY_ILLAGER = this.register("illager", "biped");
			this.ENTITY_WITCH = this.register("witch", "biped");
			this.ENTITY_RAVAGER = this.register("ravager");
			this.ENTITY_VEX = this.register("vex");
			this.ENTITY_PIGLIN = this.register("piglin");
			this.ENTITY_HOGLIN = this.register("hoglin");
			this.ENTITY_ASYLUM_DEMON = this.register("asylum_demon");
		}
		
		@Override
		protected Model register(String name)
		{
			Model model = new Model(new ResourceLocation(DarkSouls.MOD_ID, name));
			this.ARMATURES.add(model);
			return model;
		}
		
		@Override
		protected Model register(String name, String armaturePath)
		{
			Model model = new Model(new ResourceLocation(DarkSouls.MOD_ID, name));
			model.setArmatureLocation(new ResourceLocation(DarkSouls.MOD_ID, armaturePath));
			this.ARMATURES.add(model);
			return model;
		}
		
		@Override
		protected Model registerMeshOnly(String name)
		{
			return new Model(new ResourceLocation(DarkSouls.MOD_ID, name));
		}
	}
}
