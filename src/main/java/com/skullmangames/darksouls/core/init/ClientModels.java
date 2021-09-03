package com.skullmangames.darksouls.core.init;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
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
		this.ENTITY_BIPED_64_32_TEX = this.register("biped_old_texture", ENTITY_BIPED.getArmature());
		this.ENTITY_BIPED_SLIM_ARM = this.register("biped_slim_arm", ENTITY_BIPED.getArmature());
		this.ENTITY_VILLAGER_ZOMBIE = this.register("zombie_villager", ENTITY_BIPED.getArmature());
		this.ENTITY_VILLAGER_ZOMBIE_BODY = this.register("zombie_villager_body", null);
		this.ENTITY_CREEPER = this.register("creeper");
		this.ENTITY_ENDERMAN = this.register("enderman");
		this.ENTITY_SKELETON = this.register("skeleton");
		this.ENTITY_SPIDER = this.register("spider");
		this.ENTITY_GOLEM = this.register("iron_golem");
		this.ENTITY_ILLAGER = this.register("illager", ENTITY_BIPED.getArmature());
		this.ENTITY_WITCH = this.register("witch", ENTITY_BIPED.getArmature());
		this.ENTITY_RAVAGER = this.register("ravager");
		this.ENTITY_VEX = this.register("vex");
		this.ENTITY_PIGLIN = this.register("piglin");
		this.ENTITY_HOGLIN = this.register("hoglin");
		this.ENTITY_BIPED_FIRST_PERSON = this.register("biped_firstperson", null);
		this.ENTITY_BIPED_OUTER_LAYER = this.register("biped_outer_layer", null);
		
		this.ITEM_HELMET = this.register("armor_helmet", null);
		this.ITEM_CHESTPLATE = this.register("armor_chestplate", null);
		this.ITEM_LEGGINS = this.register("armor_leggins", null);
		this.ITEM_LEGGINS_CLOTH = this.register("armor_leggins_cloth", null);
		this.ITEM_BOOTS = this.register("armor_boots", null);
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
	protected ClientModel register(String name, Armature armature)
	{
		ClientModel model = new ClientModel(new ResourceLocation(DarkSouls.MOD_ID, name));
		model.loadArmatureData(armature);
		this.MESHES.add(model);
		return model;
	}
	
	public void buildMeshData()
	{
		for (ClientModel model : this.MESHES) model.loadMeshData();
	}
}
