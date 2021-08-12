package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;

import net.minecraft.util.ResourceLocation;

public class ModelInit<T extends Model>
{
	public static final ServerModelInit SERVER = new ServerModelInit();
	
	public static class ServerModelInit extends ModelInit<Model>
	{
		public ServerModelInit()
		{
			ENTITY_BIPED = new Model(new ResourceLocation(DarkSouls.MOD_ID, "biped.dae"));
			ENTITY_BIPED_64_32_TEX = new Model(new ResourceLocation(DarkSouls.MOD_ID, "biped.dae"));
			ENTITY_BIPED_SLIM_ARM = new Model(new ResourceLocation(DarkSouls.MOD_ID, "biped_slim_arm.dae"));
			ENTITY_VILLAGER_ZOMBIE = new Model(new ResourceLocation(DarkSouls.MOD_ID, "zombie_villager.dae"));
			ENTITY_VILLAGER_ZOMBIE_BODY = new Model(new ResourceLocation(DarkSouls.MOD_ID, "zombie_villager_body.dae"));
			ENTITY_CREEPER = new Model(new ResourceLocation(DarkSouls.MOD_ID, "creeper.dae"));
			ENTITY_ENDERMAN = new Model(new ResourceLocation(DarkSouls.MOD_ID, "enderman.dae"));
			ENTITY_SKELETON = new Model(new ResourceLocation(DarkSouls.MOD_ID, "skeleton.dae"));
			ENTITY_SPIDER = new Model(new ResourceLocation(DarkSouls.MOD_ID, "spider.dae"));
			ENTITY_GOLEM = new Model(new ResourceLocation(DarkSouls.MOD_ID, "iron_golem.dae"));
			ENTITY_ILLAGER = new Model(new ResourceLocation(DarkSouls.MOD_ID, "illager.dae"));
			ENTITY_WITCH = new Model(new ResourceLocation(DarkSouls.MOD_ID, "witch.dae"));
			ENTITY_RAVAGER = new Model(new ResourceLocation(DarkSouls.MOD_ID, "ravager.dae"));
			ENTITY_VEX = new Model(new ResourceLocation(DarkSouls.MOD_ID, "vex.dae"));
			ENTITY_PIGLIN = new Model(new ResourceLocation(DarkSouls.MOD_ID, "piglin.dae"));
			ENTITY_HOGLIN = new Model(new ResourceLocation(DarkSouls.MOD_ID, "hoglin.dae"));
		}
	}
	
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
	
	public void buildArmatureData()
	{
		ENTITY_BIPED.loadArmatureData();
		ENTITY_BIPED_64_32_TEX.loadArmatureData(ENTITY_BIPED.getArmature());
		ENTITY_BIPED_SLIM_ARM.loadArmatureData(ENTITY_BIPED.getArmature());
		ENTITY_VILLAGER_ZOMBIE.loadArmatureData(ENTITY_BIPED.getArmature());
		ENTITY_CREEPER.loadArmatureData();
		ENTITY_SKELETON.loadArmatureData();
		ENTITY_ENDERMAN.loadArmatureData();
		ENTITY_SPIDER.loadArmatureData();
		ENTITY_GOLEM.loadArmatureData();
		ENTITY_RAVAGER.loadArmatureData();
		ENTITY_VEX.loadArmatureData();
		ENTITY_PIGLIN.loadArmatureData();
		ENTITY_ILLAGER.loadArmatureData(ENTITY_BIPED.getArmature());
		ENTITY_WITCH.loadArmatureData(ENTITY_BIPED.getArmature());
		ENTITY_HOGLIN.loadArmatureData();
	}
}
