package com.skullmangames.darksouls.client.renderer.layer;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.renderer.ModRenderTypes;
import com.skullmangames.darksouls.client.renderer.entity.model.ClientModel;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.item.ArmorCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;

@OnlyIn(Dist.CLIENT)
public class WearableItemLayer<E extends LivingEntity, T extends LivingCap<E>> extends Layer<E, T>
{
	private static final Map<ResourceLocation, ClientModel> ARMOR_MODEL_MAP = new HashMap<ResourceLocation, ClientModel>();
	private final EquipmentSlot slot;
	
	public WearableItemLayer(EquipmentSlot slotType)
	{
		this.slot = slotType;
	}
	
	private void renderArmor(PoseStack matStack, MultiBufferSource buf, int packedLightIn, boolean hasEffect, ClientModel model, float r, float g, float b, ResourceLocation armorResource, PublicMatrix4f[] poses)
	{
		VertexConsumer ivertexbuilder = ModRenderTypes.getArmorVertexBuilder(buf, ModRenderTypes.getAnimatedArmorModel(armorResource), hasEffect);
		model.draw(matStack, ivertexbuilder, packedLightIn, r, g, b, 1.0F, poses);
	}
	
	@Override
	public void renderLayer(T entitydata, E entityliving, PoseStack matrixStackIn, MultiBufferSource buffer, int packedLightIn, PublicMatrix4f[] poses, float partialTicks)
	{
		ItemStack stack = entityliving.getItemBySlot(this.slot);
		Item item = stack.getItem();
		
		matrixStackIn.pushPose();
		if(this.slot == EquipmentSlot.HEAD && entityliving instanceof ZombieVillager)
		{
			matrixStackIn.translate(0.0D, 0.1D, 0.0D);
		}
		
		if (item instanceof ArmorItem)
		{
			ArmorItem armorItem = (ArmorItem) stack.getItem();
			ClientModel model = this.getArmorModel(entityliving, armorItem, stack);
			
			boolean hasEffect = stack.isEnchanted();
			if (armorItem instanceof DyeableArmorItem)
			{
				int i = ((DyeableArmorItem) armorItem).getColor(stack);
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
	
	private ClientModel getArmorModel(E entityliving, ArmorItem armorItem, ItemStack stack)
	{
		ResourceLocation registryName = armorItem.getRegistryName();
		if (ARMOR_MODEL_MAP.containsKey(registryName))
		{
			return ARMOR_MODEL_MAP.get(registryName);
		}
		else
		{
			ClientModel model;
			
			ArmorCap cap = (ArmorCap) stack.getCapability(ModCapabilities.CAPABILITY_ITEM, null).orElse(null);
			
			if (cap == null)
			{
				model = ArmorCap.getBipedArmorModel(armorItem.getSlot());
			}
			else
			{
				model = cap.getArmorModel(armorItem.getSlot());
			}
			ARMOR_MODEL_MAP.put(registryName, model);
			return model;
		}
	}
	
	private ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type)
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

		String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (slot == EquipmentSlot.LEGS ? 2 : 1), type == null ? "" : String.format("_%s", type));
		s1 = ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
		ResourceLocation resourcelocation = HumanoidArmorLayer.ARMOR_LOCATION_CACHE.get(s1);
		if (resourcelocation == null)
		{
			resourcelocation = new ResourceLocation(s1);
			HumanoidArmorLayer.ARMOR_LOCATION_CACHE.put(s1, resourcelocation);
		}

		return resourcelocation;
	}
}