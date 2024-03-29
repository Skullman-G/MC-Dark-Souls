package com.skullmangames.darksouls.core.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.ClientModel;
import net.minecraft.resources.ResourceLocation;

public class ClientModels extends Models<ClientModel>
{
	protected final Map<ResourceLocation, ClientModel> clientModels = new HashMap<>();
	protected final List<ClientModel> meshes = new ArrayList<>();
	public static final ClientModels CLIENT = new ClientModels();
	
	public final ClientModel ENTITY_BIPED_FIRST_PERSON;
	public final ClientModel ENTITY_BIPED_OUTER_LAYER;
	public final ClientModel ITEM_HELMET;
	public final ClientModel ITEM_CHESTPLATE;
	public final ClientModel ITEM_LEGGINS;
	public final ClientModel ITEM_LEGGINS_CLOTH;
	public final ClientModel ITEM_SKIRT;
	public final ClientModel ITEM_BOOTS;
	public final ClientModel ITEM_ONE_SHOE;
	public final ClientModel ITEM_FALCONER_HELM;
	public final ClientModel ITEM_FALCONER_ARMOR;
	
	public ClientModels()
	{
		this.ENTITY_BIPED = this.register("biped");
		this.ENTITY_BIPED_64_32_TEX = this.register("biped_old_texture", "biped");
		this.ENTITY_BIPED_SLIM_ARM = this.register("biped_slim_arm", "biped");
		this.ENTITY_BIPED_FIRST_PERSON = this.registerMeshOnly("biped_firstperson");
		this.ENTITY_BIPED_OUTER_LAYER = this.registerMeshOnly("biped_outer_layer");
		this.ENTITY_STRAY_DEMON = this.register("stray_demon");
		
		this.ITEM_HELMET = this.registerMeshOnly("armor_helmet");
		this.ITEM_CHESTPLATE = this.registerMeshOnly("armor_chestplate");
		this.ITEM_LEGGINS = this.registerMeshOnly("armor_leggins");
		this.ITEM_LEGGINS_CLOTH = this.registerMeshOnly("armor_leggins_cloth");
		this.ITEM_SKIRT = this.registerMeshOnly("armor_skirt");
		this.ITEM_BOOTS = this.registerMeshOnly("armor_boots");
		this.ITEM_ONE_SHOE = this.registerMeshOnly("armor_one_shoe");
		this.ITEM_FALCONER_HELM = this.registerMeshOnly("armor_falconer_helm");
		this.ITEM_FALCONER_ARMOR = this.registerMeshOnly("armor_falconer_armor");
	}
	
	@Override
	protected ClientModel register(String name)
	{
		ResourceLocation id = DarkSouls.rl(name);
		ClientModel model = new ClientModel(id);
		this.clientModels.put(id, model);
		this.meshes.add(model);
		return model;
	}
	
	@Override
	protected ClientModel register(String name, String armaturePath)
	{
		ResourceLocation id = DarkSouls.rl(name);
		ClientModel model = new ClientModel(id);
		model.setArmatureLocation(DarkSouls.rl(armaturePath));
		this.clientModels.put(id, model);
		this.meshes.add(model);
		return model;
	}
	
	@Override
	protected ClientModel registerMeshOnly(String name)
	{
		ClientModel model = new ClientModel(DarkSouls.rl(name));
		this.meshes.add(model);
		return model;
	}
	
	@Nullable
	public ClientModel findModel(ResourceLocation id)
	{
		return this.clientModels.get(id);
	}
	
	public void buildArmatureData()
	{
		for (ClientModel model : this.clientModels.values()) model.loadArmatureData();
	}
	
	public void buildMeshData()
	{
		for (ClientModel model : this.meshes) model.loadMeshData();
	}
}
