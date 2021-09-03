package com.skullmangames.darksouls.core.init;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.ClientModel;
import net.minecraft.util.ResourceLocation;

public class ClientModels extends Models<ClientModel>
{
	protected final List<ClientModel> MESHES = new ArrayList<ClientModel>();
	public static final ClientModels CLIENT = new ClientModels();
	
	public final ClientModel ENTITY_BIPED_FIRST_PERSON;
	public final ClientModel ENTITY_BIPED_OUTER_LAYER;
	public final ClientModel ITEM_HELMET;
	public final ClientModel ITEM_CHESTPLATE;
	public final ClientModel ITEM_LEGGINS;
	public final ClientModel ITEM_LEGGINS_CLOTH;
	public final ClientModel ITEM_BOOTS;
	
	public ClientModels()
	{
		this.ENTITY_BIPED = this.register("biped");
		this.ENTITY_BIPED_64_32_TEX = this.register("biped_old_texture", "biped");
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
		this.ENTITY_BIPED_FIRST_PERSON = this.registerMeshOnly("biped_firstperson");
		this.ENTITY_BIPED_OUTER_LAYER = this.registerMeshOnly("biped_outer_layer");
		this.ENTITY_ASYLUM_DEMON = this.register("asylum_demon");
		
		this.ITEM_HELMET = this.registerMeshOnly("armor_helmet");
		this.ITEM_CHESTPLATE = this.registerMeshOnly("armor_chestplate");
		this.ITEM_LEGGINS = this.registerMeshOnly("armor_leggins");
		this.ITEM_LEGGINS_CLOTH = this.registerMeshOnly("armor_leggins_cloth");
		this.ITEM_BOOTS = this.registerMeshOnly("armor_boots");
	}
	
	@Override
	protected ClientModel register(String name)
	{
		ClientModel model = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, name));
		this.ARMATURES.add(model);
		this.MESHES.add(model);
		return model;
	}
	
	@Override
	protected ClientModel register(String name, String armaturePath)
	{
		ClientModel model = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, name));
		model.setArmatureLocation(new ResourceLocation(DarkSouls.MOD_ID, armaturePath));
		this.ARMATURES.add(model);
		this.MESHES.add(model);
		return model;
	}
	
	@Override
	protected ClientModel registerMeshOnly(String name)
	{
		ClientModel model = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, name));
		this.MESHES.add(model);
		return model;
	}
	
	public void buildMeshData()
	{
		for (ClientModel model : this.MESHES) model.loadMeshData();
	}
}