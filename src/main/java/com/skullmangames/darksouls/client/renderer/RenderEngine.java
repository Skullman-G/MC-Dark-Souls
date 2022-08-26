package com.skullmangames.darksouls.client.renderer;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.renderer.item.AimHelperRenderer;
import com.skullmangames.darksouls.client.renderer.item.RenderBow;
import com.skullmangames.darksouls.client.renderer.item.RenderCrossbow;
import com.skullmangames.darksouls.client.renderer.item.RenderElytra;
import com.skullmangames.darksouls.client.renderer.item.RenderHat;
import com.skullmangames.darksouls.client.renderer.item.RenderItemBase;
import com.skullmangames.darksouls.client.renderer.item.RenderShield;
import com.skullmangames.darksouls.client.renderer.item.RenderTrident;
import com.skullmangames.darksouls.client.renderer.entity.ArmatureRenderer;
import com.skullmangames.darksouls.client.renderer.entity.AsylumDemonRenderer;
import com.skullmangames.darksouls.client.renderer.entity.PlayerRenderer;
import com.skullmangames.darksouls.client.renderer.entity.SimpleHumanoidRenderer;
import com.skullmangames.darksouls.client.renderer.entity.additional.AdditionalEntityRenderer;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.common.capability.entity.AnastaciaOfAstoraCap;
import com.skullmangames.darksouls.common.capability.entity.FireKeeperCap;
import com.skullmangames.darksouls.common.capability.entity.HollowCap;
import com.skullmangames.darksouls.common.capability.entity.HollowLordranSoldierCap;
import com.skullmangames.darksouls.common.capability.entity.HollowLordranWarriorCap;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.entity.SimpleHumanoidCap;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.entity.AnastaciaOfAstora;
import com.skullmangames.darksouls.common.entity.CrestfallenWarrior;
import com.skullmangames.darksouls.common.entity.AbstractFireKeeper;
import com.skullmangames.darksouls.common.entity.Hollow;
import com.skullmangames.darksouls.common.entity.HollowLordranSoldier;
import com.skullmangames.darksouls.common.entity.HollowLordranWarrior;
import com.skullmangames.darksouls.common.entity.PetrusOfThorolund;
import com.skullmangames.darksouls.core.init.ModEntities;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;
import com.skullmangames.darksouls.core.util.math.vector.Vector3fHelper;

import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
public class RenderEngine
{
	private static final Vector3f AIMING_CORRECTION = new Vector3f(-1.5F, 0.0F, 1.25F);
	public static final ResourceLocation NULL_TEXTURE = new ResourceLocation(DarkSouls.MOD_ID, "textures/gui/null.png");
	public AimHelperRenderer aimHelper;
	private Minecraft minecraft;
	private PublicMatrix4f projectionMatrix;
	@SuppressWarnings("rawtypes")
	private Map<EntityType<?>, ArmatureRenderer> entityRendererMap;
	private Map<Item, RenderItemBase> itemRendererMapByInstance;
	private Map<Class<? extends Item>, RenderItemBase> itemRendererMapByClass;
	private FirstPersonRenderer firstPersonRenderer;
	private boolean aiming;
	private int zoomOutTimer = 0;
	private int zoomCount;
	private int zoomMaxCount = 20;
	
	@SuppressWarnings("rawtypes")
	public RenderEngine()
	{
		Events.renderEngine = this;
		RenderItemBase.renderEngine = this;
		AdditionalEntityRenderer.init();
		this.minecraft = Minecraft.getInstance();
		this.entityRendererMap = new HashMap<EntityType<?>, ArmatureRenderer>();
		this.itemRendererMapByInstance = new HashMap<Item, RenderItemBase>();
		this.itemRendererMapByClass = new HashMap<Class<? extends Item>, RenderItemBase>();
		this.projectionMatrix = new PublicMatrix4f();
		this.firstPersonRenderer = new FirstPersonRenderer();
		
		this.minecraft.renderBuffers().fixedBuffers.put(ModRenderTypes.getEnchantedArmor(), 
				new BufferBuilder(ModRenderTypes.getEnchantedArmor().bufferSize()));
	}
	
	public void buildRenderer()
	{
		this.entityRendererMap.put(EntityType.PLAYER, new PlayerRenderer());
		this.entityRendererMap.put(ModEntities.HOLLOW.get(), new SimpleHumanoidRenderer<Hollow, HollowCap>("hollow/hollow"));
		this.entityRendererMap.put(ModEntities.HOLLOW_LORDRAN_WARRIOR.get(), new SimpleHumanoidRenderer<HollowLordranWarrior, HollowLordranWarriorCap>("hollow/lordran_hollow"));
		this.entityRendererMap.put(ModEntities.HOLLOW_LORDRAN_SOLDIER.get(), new SimpleHumanoidRenderer<HollowLordranSoldier, HollowLordranSoldierCap>("hollow/lordran_hollow"));
		this.entityRendererMap.put(ModEntities.CRESTFALLEN_WARRIOR.get(), new SimpleHumanoidRenderer<CrestfallenWarrior, SimpleHumanoidCap<CrestfallenWarrior>>("quest_entity/crestfallen_warrior"));
		this.entityRendererMap.put(ModEntities.ANASTACIA_OF_ASTORA.get(), new SimpleHumanoidRenderer<AnastaciaOfAstora, AnastaciaOfAstoraCap>("quest_entity/anastacia_of_astora"));
		this.entityRendererMap.put(ModEntities.STRAY_DEMON.get(), new AsylumDemonRenderer());
		this.entityRendererMap.put(ModEntities.FIRE_KEEPER.get(), new SimpleHumanoidRenderer<AbstractFireKeeper, FireKeeperCap>("fire_keeper"));
		this.entityRendererMap.put(ModEntities.PETRUS_OF_THOROLUND.get(), new SimpleHumanoidRenderer<PetrusOfThorolund, SimpleHumanoidCap<PetrusOfThorolund>>("quest_entity/petrus_of_thorolund"));
		
		RenderBow bowRenderer = new RenderBow();
		RenderCrossbow crossbowRenderer = new RenderCrossbow();
		RenderElytra elytraRenderer = new RenderElytra();
		RenderHat hatRenderer = new RenderHat();
		RenderShield shieldRenderer = new RenderShield();
		RenderTrident tridentRenderer = new RenderTrident();
		
		itemRendererMapByInstance.put(Items.AIR, new RenderItemBase());
		itemRendererMapByInstance.put(Items.BOW, bowRenderer);
		itemRendererMapByInstance.put(Items.ELYTRA, elytraRenderer);
		itemRendererMapByInstance.put(Items.CREEPER_HEAD, hatRenderer);
		itemRendererMapByInstance.put(Items.DRAGON_HEAD, hatRenderer);
		itemRendererMapByInstance.put(Items.PLAYER_HEAD, hatRenderer);
		itemRendererMapByInstance.put(Items.ZOMBIE_HEAD, hatRenderer);
		itemRendererMapByInstance.put(Items.SKELETON_SKULL, hatRenderer);
		itemRendererMapByInstance.put(Items.WITHER_SKELETON_SKULL, hatRenderer);
		itemRendererMapByInstance.put(Items.CARVED_PUMPKIN, hatRenderer);
		itemRendererMapByInstance.put(Items.CROSSBOW, crossbowRenderer);
		itemRendererMapByInstance.put(Items.TRIDENT, tridentRenderer);
		
		itemRendererMapByInstance.put(Items.SHIELD, shieldRenderer);
		itemRendererMapByInstance.put(ModItems.HEATER_SHIELD.get(), shieldRenderer);
		itemRendererMapByInstance.put(ModItems.CRACKED_ROUND_SHIELD.get(), shieldRenderer);
		itemRendererMapByInstance.put(ModItems.LORDRAN_SOLDIER_SHIELD.get(), shieldRenderer);
		
		itemRendererMapByClass.put(BlockItem.class, hatRenderer);
		itemRendererMapByClass.put(BowItem.class, bowRenderer);
		itemRendererMapByClass.put(CrossbowItem.class, crossbowRenderer);
		itemRendererMapByClass.put(ElytraItem.class, elytraRenderer);
		itemRendererMapByClass.put(ShieldItem.class, shieldRenderer);
		itemRendererMapByClass.put(TridentItem.class, tridentRenderer);
		aimHelper = new AimHelperRenderer();
	}
	
	public RenderItemBase getItemRenderer(Item item)
	{
		RenderItemBase renderItem = itemRendererMapByInstance.get(item);
		if (renderItem == null)
		{
			renderItem = this.findMatchingRendererByClass(item.getClass());
			if (renderItem == null)
			{
				renderItem = itemRendererMapByInstance.get(Items.AIR);
			}
			this.itemRendererMapByInstance.put(item, renderItem);
		}
		
		return renderItem;
	}

	private RenderItemBase findMatchingRendererByClass(Class<?> clazz)
	{
		RenderItemBase renderer = null;
		for (; clazz != null && renderer == null; clazz = clazz.getSuperclass())
			renderer = itemRendererMapByClass.getOrDefault(clazz, null);
		
		return renderer;
	}
	
	@SuppressWarnings("unchecked")
	public void renderEntityArmatureModel(LivingEntity livingEntity, LivingCap<?> entityCap, EntityRenderer<? extends Entity> renderer, MultiBufferSource buffer, PoseStack matStack, int packedLightIn, float partialTicks)
	{
		this.entityRendererMap.get(livingEntity.getType()).render(livingEntity, entityCap, renderer, buffer, matStack, packedLightIn, partialTicks);
	}
	
	public boolean isEntityContained(Entity entity)
	{
		return this.entityRendererMap.containsKey(entity.getType());
	}
	
	public void zoomIn()
	{
		aiming = true;
		zoomCount = zoomCount == 0 ? 1 : zoomCount;
		zoomOutTimer = 0;
	}

	public void zoomOut(int timer)
	{
		aiming = false;
		zoomOutTimer = timer;
	}
	
	private void updateCamera(CameraSetup event, CameraType pov, double partialTicks)
	{
		Camera camera = event.getCamera();
		Entity entity = minecraft.getCameraEntity();
		
		Vec3 camPos = camera.getPosition();
		
		if (pov == CameraType.THIRD_PERSON_BACK && zoomCount > 0 && this.aiming)
		{
			double posX = camPos.x;
			double posY = camPos.y;
			double posZ = camPos.z;
			
			double entityPosX = entity.xOld + (entity.getX() - entity.xOld) * partialTicks;
			double entityPosY = entity.yOld + (entity.getY() - entity.yOld) * partialTicks + entity.getEyeHeight();
			double entityPosZ = entity.zOld + (entity.getZ() - entity.zOld) * partialTicks;
			
			float interpolation = (float)zoomCount / (float)zoomMaxCount;
			Vector3f interpolatedCorrection = Vector3fHelper.scale(AIMING_CORRECTION, interpolation);
			PublicMatrix4f rotationMatrix = ClientManager.INSTANCE.getPlayerCap().getMatrix((float)partialTicks);
			Vector4f scaleVec = new Vector4f(interpolatedCorrection.x(), interpolatedCorrection.y(), interpolatedCorrection.z(), 1.0F);
			Vector4f rotateVec = PublicMatrix4f.transform(rotationMatrix, scaleVec);
			
			double d3 = Math.sqrt((rotateVec.x() * rotateVec.x()) + (rotateVec.y() * rotateVec.y()) + (rotateVec.z() * rotateVec.z()));
			double smallest = d3;
			
			double d00 = posX + rotateVec.x();
			double d11 = posY - rotateVec.y();
			double d22 = posZ + rotateVec.z();
			for (int i = 0; i < 8; ++i)
			{
				float f = (float) ((i & 1) * 2 - 1);
				float f1 = (float) ((i >> 1 & 1) * 2 - 1);
				float f2 = (float) ((i >> 2 & 1) * 2 - 1);
				f = f * 0.1F;
				f1 = f1 * 0.1F;
				f2 = f2 * 0.1F;
				HitResult raytraceresult = minecraft.level.clip(new ClipContext(new Vec3(entityPosX + f, entityPosY + f1, entityPosZ + f2),
							new Vec3(d00 + f + f2, d11 + f1, d22 + f2), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
				if (raytraceresult != null)
				{
					double d7 = raytraceresult.getLocation().distanceTo(new Vec3(entityPosX, entityPosY, entityPosZ));
					if (d7 < smallest) smallest = d7;
				}
			}
		}
	}

	public PublicMatrix4f getCurrentProjectionMatrix()
	{
		return this.projectionMatrix;
	}

	@Mod.EventBusSubscriber(modid = DarkSouls.MOD_ID, value = Dist.CLIENT)
	public static class Events
	{
		private static RenderEngine renderEngine;
		private static final Minecraft minecraft = Minecraft.getInstance();
		
		@SubscribeEvent
		public static void renderLivingEvent(RenderLivingEvent.Pre<? extends LivingEntity, ? extends EntityModel<? extends LivingEntity>> event)
		{
			LivingEntity livingentity = event.getEntity();
			if (renderEngine.isEntityContained(livingentity))
			{
				if (livingentity instanceof LocalPlayer && event.getPartialTick() == 1.0F) return;
				
				LivingCap<?> entityCap = (LivingCap<?>) livingentity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				if (entityCap != null)
				{
					event.setCanceled(true);
					renderEngine.renderEntityArmatureModel(livingentity, entityCap, event.getRenderer(), event.getMultiBufferSource(), event.getPoseStack(), event.getPackedLight(), event.getPartialTick());
				}
			}
			
			for (AdditionalEntityRenderer additionalRenderer : AdditionalEntityRenderer.ADDITIONAL_ENTITY_RENDERERS)
			{
				if (additionalRenderer.shouldDraw(event.getEntity()))
				{
					additionalRenderer.draw(event.getEntity(), event.getPoseStack(), event.getMultiBufferSource(), event.getPartialTick());
				}
			}
		}

		@SubscribeEvent
		public static void itemTooltip(ItemTooltipEvent event)
		{
			if (event.getPlayer() != null)
			{
				ItemCapability cap = ModCapabilities.getItemCapability(event.getItemStack());
				LocalPlayerCap playerCap = (LocalPlayerCap) event.getPlayer().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				
				if (cap != null && ClientManager.INSTANCE.getPlayerCap() != null) cap.modifyItemTooltip(event.getToolTip(), playerCap, event.getItemStack());
			}
		}
		
		@SubscribeEvent
		public static void cameraSetupEvent(CameraSetup event)
		{
			renderEngine.updateCamera(event, minecraft.options.getCameraType(), event.getPartialTicks());
			if (renderEngine.zoomCount > 0)
			{
				if (renderEngine.zoomOutTimer > 0)
				{
					renderEngine.zoomOutTimer--;
				}
				else
				{
					renderEngine.zoomCount = renderEngine.aiming ? renderEngine.zoomCount + 1 : renderEngine.zoomCount - 1;
				}
				renderEngine.zoomCount = Math.min(renderEngine.zoomMaxCount, renderEngine.zoomCount);
			}
		}
		
		@SubscribeEvent
		public static void renderHand(RenderHandEvent event)
		{
			LocalPlayerCap playerCap = ClientManager.INSTANCE.getPlayerCap();
			if (!DarkSouls.CLIENT_INGAME_CONFIG.firstPerson3D.getValue() && !ClientManager.INSTANCE.isCombatModeActive()) return;
			if (event.getHand() == InteractionHand.MAIN_HAND)
			{
				renderEngine.firstPersonRenderer.render(minecraft.player, playerCap, null, event.getMultiBufferSource(),
						event.getPoseStack(), event.getPackedLight(), event.getPartialTicks());
			}
			event.setCanceled(true);
		}
		
		@SubscribeEvent
		public static void renderWorldLast(RenderLevelLastEvent event)
		{
			if (minecraft.options.getCameraType() == CameraType.THIRD_PERSON_BACK && ClientManager.INSTANCE.getPlayerCap().getClientAnimator().isAiming())
			{
				renderEngine.aimHelper.doRender(event.getPoseStack(), event.getPartialTick());
			}
		}
	}
}