package com.skullmangames.darksouls.core.init;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;

import net.minecraft.resources.ResourceLocation;

public abstract class Models<T extends Model>
{
	protected final List<T> ARMATURES = new ArrayList<T>();
	public static final ServerModels SERVER = new ServerModels();
	
	public T ENTITY_BIPED;
	public T ENTITY_BIPED_64_32_TEX;
	public T ENTITY_BIPED_SLIM_ARM;
	public T ENTITY_ASYLUM_DEMON;
	
	protected abstract T register(String name);
	protected abstract T register(String name, String armaturePath);
	protected abstract T registerMeshOnly(String name);
	
	@Nullable
	public Armature findArmature(String name)
	{
		for (T model : this.ARMATURES) if (model.getName() == name) return model.getArmature();
		return null;
	}
	
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
