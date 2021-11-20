package com.skullmangames.darksouls.client.renderer;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.gui.EntityIndicator;
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
import com.skullmangames.darksouls.client.renderer.entity.SimpleTexturedBipedRenderer;
import com.skullmangames.darksouls.common.capability.entity.ClientPlayerData;
import com.skullmangames.darksouls.common.capability.entity.HollowData;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.common.capability.item.CapabilityItem;
import com.skullmangames.darksouls.common.entity.HollowEntity;
import com.skullmangames.darksouls.core.init.ModEntities;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;
import com.skullmangames.darksouls.core.util.math.vector.Vector3fHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.TridentItem;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
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
		EntityIndicator.init();
		minecraft = Minecraft.getInstance();
		entityRendererMap = new HashMap<EntityType<?>, ArmatureRenderer>();
		itemRendererMapByInstance = new HashMap<Item, RenderItemBase>();
		itemRendererMapByClass = new HashMap<Class<? extends Item>, RenderItemBase>();
		projectionMatrix = new PublicMatrix4f();
		firstPersonRenderer = new FirstPersonRenderer();
		
		this.minecraft.renderBuffers().fixedBuffers.put(ModRenderTypes.getEnchantedArmor(), 
				new BufferBuilder(ModRenderTypes.getEnchantedArmor().bufferSize()));
	}
	
	public void buildRenderer()
	{
		this.entityRendererMap.put(EntityType.PLAYER, new PlayerRenderer());
		this.entityRendererMap.put(ModEntities.HOLLOW.get(), new SimpleTexturedBipedRenderer<HollowEntity, HollowData>(new ResourceLocation(DarkSouls.MOD_ID, "textures/entities/hollow/hollow.png")));
		this.entityRendererMap.put(ModEntities.ASYLUM_DEMON.get(), new AsylumDemonRenderer());
		
		RenderBow bowRenderer = new RenderBow();
		RenderCrossbow crossbowRenderer = new RenderCrossbow();
		RenderElytra elytraRenderer = new RenderElytra();
		RenderHat hatRenderer = new RenderHat();
		RenderShield shieldRenderer = new RenderShield();
		RenderTrident tridentRenderer = new RenderTrident();
		
		itemRendererMapByInstance.put(Items.AIR, new RenderItemBase());
		itemRendererMapByInstance.put(Items.BOW, bowRenderer);
		itemRendererMapByInstance.put(Items.SHIELD, shieldRenderer);
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
	public void renderEntityArmatureModel(LivingEntity livingEntity, LivingData<?> entitydata, EntityRenderer<? extends Entity> renderer, IRenderTypeBuffer buffer, MatrixStack matStack, int packedLightIn, float partialTicks)
	{
		this.entityRendererMap.get(livingEntity.getType()).render(livingEntity, entitydata, renderer, buffer, matStack, packedLightIn, partialTicks);
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
	
	private void updateCamera(CameraSetup event, PointOfView pov, double partialTicks)
	{
		ActiveRenderInfo camera = event.getInfo();
		Entity entity = minecraft.getCameraEntity();
		
		Vector3d camPos = camera.getPosition();
		
		if (pov == PointOfView.THIRD_PERSON_BACK && zoomCount > 0 && this.aiming)
		{
			double posX = camPos.x;
			double posY = camPos.y;
			double posZ = camPos.z;
			
			double entityPosX = entity.xOld + (entity.getX() - entity.xOld) * partialTicks;
			double entityPosY = entity.yOld + (entity.getY() - entity.yOld) * partialTicks + entity.getEyeHeight();
			double entityPosZ = entity.zOld + (entity.getZ() - entity.zOld) * partialTicks;
			
			float interpolation = (float)zoomCount / (float)zoomMaxCount;
			Vector3f interpolatedCorrection = Vector3fHelper.scale(AIMING_CORRECTION, interpolation);
			PublicMatrix4f rotationMatrix = ClientManager.INSTANCE.getPlayerData().getMatrix((float)partialTicks);
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
				RayTraceResult raytraceresult = minecraft.level.clip(new RayTraceContext(new Vector3d(entityPosX + f, entityPosY + f1, entityPosZ + f2),
							new Vector3d(d00 + f + f2, d11 + f1, d22 + f2), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity));
				if (raytraceresult != null)
				{
					double d7 = raytraceresult.getLocation().distanceTo(new Vector3d(entityPosX, entityPosY, entityPosZ));
					if (d7 < smallest) smallest = d7;
				}
			}
		}
		
		FloatBuffer fb = GLAllocation.createFloatBuffer(16);
		GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, fb);
		this.projectionMatrix.load(fb.asReadOnlyBuffer());
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
				if (livingentity instanceof ClientPlayerEntity && event.getPartialRenderTick() == 1.0F) return;
				
				LivingData<?> entitydata = (LivingData<?>) livingentity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				if (entitydata != null)
				{
					event.setCanceled(true);
					renderEngine.renderEntityArmatureModel(livingentity, entitydata, event.getRenderer(), event.getBuffers(), event.getMatrixStack(), event.getLight(), event.getPartialRenderTick());
				}
			}
			
			if (!minecraft.options.hideGui)
			{
				for (EntityIndicator entityIndicator : EntityIndicator.ENTITY_INDICATOR_RENDERERS)
				{
					if (entityIndicator.shouldDraw(event.getEntity()))
					{
						entityIndicator.drawIndicator(event.getEntity(), event.getMatrixStack(), event.getBuffers(), event.getPartialRenderTick());
					}
				}
			}
		}

		@SubscribeEvent
		public static void itemTooltip(ItemTooltipEvent event)
		{
			if (event.getPlayer() != null)
			{
				CapabilityItem cap = ModCapabilities.stackCapabilityGetter(event.getItemStack());
				ClientPlayerData playerCap = (ClientPlayerData) event.getPlayer().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				
				if (cap != null && ClientManager.INSTANCE.getPlayerData() != null) cap.modifyItemTooltip(event.getToolTip(), playerCap, event.getItemStack());
			}
		}
		
		@SubscribeEvent
		public static void cameraSetupEvent(CameraSetup event)
		{
			renderEngine.updateCamera(event, minecraft.options.getCameraType(), event.getRenderPartialTicks());
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
			if (event.getHand() == Hand.MAIN_HAND)
			{
				renderEngine.firstPersonRenderer.render(minecraft.player, ClientManager.INSTANCE.getPlayerData(), null, event.getBuffers(),
						event.getMatrixStack(), event.getLight(), event.getPartialTicks());
			}
			event.setCanceled(true);
		}
		
		@SubscribeEvent
		public static void renderWorldLast(RenderWorldLastEvent event)
		{
			if (renderEngine.zoomCount > 0 && minecraft.options.getCameraType() == PointOfView.THIRD_PERSON_BACK && renderEngine.aiming)
			{
				renderEngine.aimHelper.doRender(event.getMatrixStack(), event.getPartialTicks());
			}
		}
	}
}