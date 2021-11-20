package com.skullmangames.darksouls.client.renderer.layer;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.renderer.ModRenderTypes;
import com.skullmangames.darksouls.client.renderer.entity.model.ClientModel;
import com.skullmangames.darksouls.client.renderer.entity.model.CustomModelBakery;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.common.capability.item.ArmorCapability;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;

@OnlyIn(Dist.CLIENT)
public class WearableItemLayer<E extends LivingEntity, T extends LivingData<E>> extends Layer<E, T>
{
	private static final Map<ResourceLocation, ClientModel> ARMOR_MODEL_MAP = new HashMap<ResourceLocation, ClientModel>();
	private static final Map<BipedModel<?>, ClientModel> ARMOR_MODEL_MAP_BY_MODEL = new HashMap<BipedModel<?>, ClientModel>();
	private final EquipmentSlotType slot;
	
	public WearableItemLayer(EquipmentSlotType slotType)
	{
		this.slot = slotType;
	}
	
	private void renderArmor(MatrixStack matStack, IRenderTypeBuffer buf, int packedLightIn, boolean hasEffect, ClientModel model, float r, float g, float b, ResourceLocation armorResource, PublicMatrix4f[] poses)
	{
	
		IVertexBuilder ivertexbuilder = ModRenderTypes.getArmorVertexBuilder(buf, ModRenderTypes.getAnimatedArmorModel(armorResource), hasEffect);
		model.draw(matStack, ivertexbuilder, packedLightIn, r, g, b, 1.0F, poses);
	}
	
	@Override
	public void renderLayer(T entitydata, E entityliving, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, PublicMatrix4f[] poses, float partialTicks)
	{
		ItemStack stack = entityliving.getItemBySlot(this.slot);
		Item item = stack.getItem();
		
		matrixStackIn.pushPose();
		if(this.slot == EquipmentSlotType.HEAD && entityliving instanceof ZombieVillagerEntity)
		{
			matrixStackIn.translate(0.0D, 0.1D, 0.0D);
		}
		
		if (item instanceof ArmorItem)
		{
			ArmorItem armorItem = (ArmorItem) stack.getItem();
			ClientModel model = this.getArmorModel(entityliving, armorItem, stack);
			
			boolean hasEffect = stack.isEnchanted();
			if (armorItem instanceof IDyeableArmorItem)
			{
				int i = ((IDyeableArmorItem) armorItem).getColor(stack);
				float r = (float) (i >> 16 & 255) / 255.0F;
				float g = (float) (i >> 8 & 255) / 255.0F;
				float b = (float) (i & 255) / 255.0F;
				this.renderArmor(matrixStackIn, buffer, packedLightIn, hasEffect, model, r, g, b, this.getArmorTexture(stack, entityliving, armorItem.getSlot(), null), poses);
				this.renderArmor(matrixStackIn, buffer, packedLightIn, hasEffect, model, 1.0F, 1.0F, 1.0F, this.getArmorTexture(stack, entityliving, armorItem.getSlot(), "overlay"), poses);
			}
			else
			{
				this.renderArmor(matrixStackIn, buffer, packedLightIn, hasEffect, model, 1.0F, 1.0F, 1.0F, this.getArmorTexture(stack, entityliving, armorItem.getSlot(), null), poses);
			}
		}
		else
		{
			if (item != Items.AIR)
			{
				ClientManager.INSTANCE.renderEngine.getItemRenderer(stack.getItem()).renderItemOnHead(stack, entitydata, buffer, matrixStackIn, packedLightIn, partialTicks);
			}
		}
		
		matrixStackIn.popPose();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ClientModel getArmorModel(E entityliving, ArmorItem armorItem, ItemStack stack)
	{
		ResourceLocation registryName = armorItem.getRegistryName();
		if (ARMOR_MODEL_MAP.containsKey(registryName))
		{
			return ARMOR_MODEL_MAP.get(registryName);
		}
		else
		{
			BipedModel<E> originalModel = new BipedModel<>(0.5F);
			ClientModel model;
			LivingRenderer<E, ?> entityRenderer = (LivingRenderer<E, ?>)Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entityliving);
			
			for (LayerRenderer<E, ?> layer : entityRenderer.layers)
			{
				if (layer instanceof BipedArmorLayer)
				{
					originalModel = ((BipedArmorLayer) layer).getArmorModel(this.slot);
				}
			}
			
			BipedModel<E> customModel = armorItem.getArmorModel(entityliving, stack, slot, originalModel);
			
			if (customModel == null)
			{
				ArmorCapability cap = (ArmorCapability) stack.getCapability(ModCapabilities.CAPABILITY_ITEM, null).orElse(null);
				
				if (cap == null)
				{
					model = ArmorCapability.getBipedArmorModel(armorItem.getSlot());
				}
				else
				{
					model = cap.getArmorModel(armorItem.getSlot());
				}
				ARMOR_MODEL_MAP.put(registryName, model);
				return model;
			}
			else
			{
				if (ARMOR_MODEL_MAP_BY_MODEL.containsKey(customModel))
				{
					model = ARMOR_MODEL_MAP_BY_MODEL.get(customModel);
				}
				else
				{
					DarkSouls.LOGGER.info("baked new model for " + registryName);
					model = CustomModelBakery.bakeBipedCustomArmorModel(customModel, armorItem);
				}
				ARMOR_MODEL_MAP.put(registryName, model);
				return model;
			}
		}
	}
	
	private ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type)
	{
		ArmorItem item = (ArmorItem) stack.getItem();
		String texture = item.getMaterial().getName();
		String domain = "minecraft";
		int idx = texture.indexOf(':');

		if (idx != -1)
		{
			domain = texture.substring(0, idx);
			texture = texture.substring(idx + 1);
		}

		String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (slot == EquipmentSlotType.LEGS ? 2 : 1), type == null ? "" : String.format("_%s", type));
		s1 = ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
		ResourceLocation resourcelocation = BipedArmorLayer.ARMOR_LOCATION_CACHE.get(s1);
		if (resourcelocation == null)
		{
			resourcelocation = new ResourceLocation(s1);
			BipedArmorLayer.ARMOR_LOCATION_CACHE.put(s1, resourcelocation);
		}

		return resourcelocation;
	}
}