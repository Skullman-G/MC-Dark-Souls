package com.skullmangames.darksouls.core.init;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;

import net.minecraft.resources.ResourceLocation;

public abstract class Models<T extends Model>
{
	public static final ServerModels SERVER = new ServerModels();
	
	public T ENTITY_BIPED;
	public T ENTITY_BIPED_64_32_TEX;
	public T ENTITY_BIPED_SLIM_ARM;
	public T ENTITY_ARMOR_STAND;
	public T ENTITY_STRAY_DEMON;
	public T ENTITY_TAURUS_DEMON;
	
	protected abstract T register(String name);
	protected abstract T register(String name, String armaturePath);
	protected abstract T registerMeshOnly(String name);
	
	@Nullable
	public abstract T findModel(ResourceLocation id);
	
	public abstract void buildArmatureData();
	
	public static class ServerModels extends Models<Model>
	{
		public final Map<ResourceLocation, Model> serverModels = new HashMap<>();
		
		public ServerModels()
		{
			this.ENTITY_BIPED = this.register("biped");
			this.ENTITY_BIPED_64_32_TEX = this.register("biped_old_texture", "biped");
			this.ENTITY_BIPED_SLIM_ARM = this.register("biped_slim_arm", "biped");
			this.ENTITY_ARMOR_STAND = this.register("armor_stand", "biped");
			this.ENTITY_STRAY_DEMON = this.register("stray_demon");
			this.ENTITY_TAURUS_DEMON = this.register("taurus_demon");
		}
		
		@Override
		protected Model register(String name)
		{
			ResourceLocation id = DarkSouls.rl(name);
			Model model = new Model(id);
			this.serverModels.put(id, model);
			return model;
		}
		
		@Override
		protected Model register(String name, String armaturePath)
		{
			ResourceLocation id = DarkSouls.rl(name);
			Model model = new Model(id);
			model.setArmatureLocation(DarkSouls.rl(armaturePath));
			this.serverModels.put(id, model);
			return model;
		}
		
		@Override
		protected Model registerMeshOnly(String name)
		{
			return new Model(new ResourceLocation(DarkSouls.MOD_ID, name));
		}
		
		@Nullable
		public Model findModel(ResourceLocation id)
		{
			return this.serverModels.get(id);
		}
		
		public void buildArmatureData()
		{
			for (Model model : this.serverModels.values()) model.loadArmatureData();
		}
	}
}
