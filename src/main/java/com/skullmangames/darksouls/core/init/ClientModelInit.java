package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.ClientModel;

import net.minecraft.util.ResourceLocation;

public class ClientModelInit extends ModelInit<ClientModel>
{
	public static final ClientModelInit CLIENT = new ClientModelInit();
	
	public final ClientModel ENTITY_BIPED_FIRST_PERSON;
	public final ClientModel ENTITY_BIPED_OUTER_LAYER;
	public final ClientModel ITEM_HELMET;
	public final ClientModel ITEM_CHESTPLATE;
	public final ClientModel ITEM_LEGGINS;
	public final ClientModel ITEM_LEGGINS_CLOTH;
	public final ClientModel ITEM_BOOTS;
	
	public ClientModelInit()
	{
		ENTITY_BIPED = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "biped.dae"));
		ENTITY_BIPED_64_32_TEX = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "biped_old_texture.dae"));
		ENTITY_BIPED_SLIM_ARM = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "biped_slim_arm.dae"));
		ENTITY_VILLAGER_ZOMBIE = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "zombie_villager.dae"));
		ENTITY_VILLAGER_ZOMBIE_BODY = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "zombie_villager_body.dae"));
		ENTITY_CREEPER = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "creeper.dae"));
		ENTITY_ENDERMAN = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "enderman.dae"));
		ENTITY_SKELETON = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "skeleton.dae"));
		ENTITY_SPIDER = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "spider.dae"));
		ENTITY_GOLEM = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "iron_golem.dae"));
		ENTITY_ILLAGER = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "illager.dae"));
		ENTITY_WITCH = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "witch.dae"));
		ENTITY_RAVAGER = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "ravager.dae"));
		ENTITY_VEX = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "vex.dae"));
		ENTITY_PIGLIN = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "piglin.dae"));
		ENTITY_HOGLIN = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "hoglin.dae"));
		ENTITY_BIPED_FIRST_PERSON = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "biped_firstperson.dae"));
		ENTITY_BIPED_OUTER_LAYER = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "biped_outer_layer.dae"));
		
		ITEM_HELMET = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "armor_helmet.dae"));
		ITEM_CHESTPLATE = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "armor_chestplate.dae"));
		ITEM_LEGGINS = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "armor_leggins.dae"));
		ITEM_LEGGINS_CLOTH = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "armor_leggins_cloth.dae"));
		ITEM_BOOTS = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, "armor_boots.dae"));
	}
	
	public void buildMeshData()
	{
		ENTITY_BIPED.loadMeshData();
		ENTITY_BIPED_64_32_TEX.loadMeshData();
		ENTITY_BIPED_SLIM_ARM.loadMeshData();
		ENTITY_BIPED_FIRST_PERSON.loadMeshData();
		ENTITY_BIPED_OUTER_LAYER.loadMeshData();
		ENTITY_CREEPER.loadMeshData();
		ENTITY_SKELETON.loadMeshData();
		ENTITY_VILLAGER_ZOMBIE.loadMeshData();
		ENTITY_VILLAGER_ZOMBIE_BODY.loadMeshData();
		ENTITY_ENDERMAN.loadMeshData();
		ENTITY_SPIDER.loadMeshData();
		ENTITY_GOLEM.loadMeshData();
		ENTITY_WITCH.loadMeshData();
		ENTITY_RAVAGER.loadMeshData();
		ENTITY_VEX.loadMeshData();
		ENTITY_PIGLIN.loadMeshData();
		ENTITY_ILLAGER.loadMeshData();
		ENTITY_HOGLIN.loadMeshData();
		
		ITEM_HELMET.loadMeshData();
		ITEM_CHESTPLATE.loadMeshData();
		ITEM_LEGGINS.loadMeshData();
		ITEM_LEGGINS_CLOTH.loadMeshData();
		ITEM_BOOTS.loadMeshData();
	}
}
