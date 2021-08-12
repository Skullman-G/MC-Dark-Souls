package com.skullmangames.darksouls.client.event.engine;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientEngine;
import com.skullmangames.darksouls.client.gui.BattleModeGui;
import com.skullmangames.darksouls.client.gui.EntityIndicator;
import com.skullmangames.darksouls.client.input.ModKeys;
import com.skullmangames.darksouls.client.renderer.AimHelperRenderer;
import com.skullmangames.darksouls.client.renderer.FirstPersonRenderer;
import com.skullmangames.darksouls.client.renderer.ModRenderTypes;
import com.skullmangames.darksouls.client.renderer.RenderBow;
import com.skullmangames.darksouls.client.renderer.RenderCrossbow;
import com.skullmangames.darksouls.client.renderer.RenderElytra;
import com.skullmangames.darksouls.client.renderer.RenderHat;
import com.skullmangames.darksouls.client.renderer.RenderItemBase;
import com.skullmangames.darksouls.client.renderer.RenderShield;
import com.skullmangames.darksouls.client.renderer.RenderTrident;
import com.skullmangames.darksouls.client.renderer.entity.ArmatureRenderer;
import com.skullmangames.darksouls.client.renderer.entity.PlayerRenderer;
import com.skullmangames.darksouls.common.ModCapabilities;
import com.skullmangames.darksouls.common.entities.ClientPlayerData;
import com.skullmangames.darksouls.common.entities.LivingData;
import com.skullmangames.darksouls.common.items.CapabilityItem;
import com.skullmangames.darksouls.core.util.Formulars;
import com.skullmangames.darksouls.util.math.vector.PublicMatrix4f;

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
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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
	public BattleModeGui guiSkillBar = new BattleModeGui();
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
	
	@SuppressWarnings({ "resource", "rawtypes" })
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
		
		Minecraft.getInstance().renderBuffers().fixedBuffers.put(ModRenderTypes.getEnchantedArmor(), 
				new BufferBuilder(ModRenderTypes.getEnchantedArmor().bufferSize()));
	}
	
	public void buildRenderer()
	{
		entityRendererMap.put(EntityType.PLAYER, new PlayerRenderer());
		/*entityRendererMap.put(EntityType.CREEPER, new CreeperRenderer());
		entityRendererMap.put(EntityType.ENDERMAN, new EndermanRenderer());
		entityRendererMap.put(EntityType.ZOMBIE, new SimpleTexturBipedRenderer<ZombieEntity, ZombieData<ZombieEntity>>("textures/entity/zombie/zombie.png"));
		entityRendererMap.put(EntityType.ZOMBIE_VILLAGER, new ZombieVillagerRenderer());
		entityRendererMap.put(EntityType.ZOMBIFIED_PIGLIN, new SimpleTexturBipedRenderer<ZombifiedPiglinEntity, ZombieData<ZombifiedPiglinEntity>>(DarkSouls.MOD_ID + ":textures/entity/zombified_piglin.png"));
		entityRendererMap.put(EntityType.HUSK, new SimpleTexturBipedRenderer<HuskEntity, ZombieData<HuskEntity>>("textures/entity/zombie/husk.png"));
		entityRendererMap.put(EntityType.SKELETON, new SimpleTexturBipedRenderer<SkeletonEntity, SkeletonData<SkeletonEntity>>("textures/entity/skeleton/skeleton.png"));
		entityRendererMap.put(EntityType.WITHER_SKELETON, new SimpleTexturBipedRenderer<WitherSkeletonEntity, WitherSkeletonData>("textures/entity/skeleton/wither_skeleton.png"));
		entityRendererMap.put(EntityType.STRAY, new SimpleTexturBipedRenderer<StrayEntity, SkeletonData<StrayEntity>>("textures/entity/skeleton/stray.png"));
		entityRendererMap.put(EntityType.SPIDER, new SpiderRenderer());
		entityRendererMap.put(EntityType.CAVE_SPIDER, new CaveSpiderRenderer());
		entityRendererMap.put(EntityType.IRON_GOLEM, new IronGolemRenderer());
		entityRendererMap.put(EntityType.VINDICATOR, new SimpleTexturBipedRenderer<VindicatorEntity, VindicatorData>("textures/entity/illager/vindicator.png"));
		entityRendererMap.put(EntityType.EVOKER, new SimpleTexturBipedRenderer<EvokerEntity, EvokerData>("textures/entity/illager/evoker.png"));
		entityRendererMap.put(EntityType.WITCH, new SimpleTexturBipedRenderer<WitchEntity, WitchData>(DarkSouls.MOD_ID + ":textures/entity/witch.png"));
		entityRendererMap.put(EntityType.DROWNED, new DrownedRenderer());
		entityRendererMap.put(EntityType.PILLAGER, new SimpleTexturBipedRenderer<PillagerEntity, PillagerData>("textures/entity/illager/pillager.png"));
		entityRendererMap.put(EntityType.RAVAGER, new RavagerRenderer());
		entityRendererMap.put(EntityType.VEX, new VexRenderer());
		entityRendererMap.put(EntityType.PIGLIN, new SimpleTexturBipedRenderer<PiglinEntity, PiglinData>("textures/entity/piglin/piglin.png"));
		entityRendererMap.put(EntityType.field_242287_aj, new SimpleTexturBipedRenderer<PiglinBruteEntity, PiglinBruteData>(DarkSouls.MOD_ID + ":textures/entity/piglin_brute.png"));
		entityRendererMap.put(EntityType.HOGLIN, new HoglinRenderer<HoglinEntity, HoglinData>("textures/entity/hoglin/hoglin.png"));
		entityRendererMap.put(EntityType.ZOGLIN, new HoglinRenderer<ZoglinEntity, ZoglinData>("textures/entity/hoglin/zoglin.png"));*/
		
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
	
	private void updateCameraInfo(CameraSetup event, PointOfView pov, double partialTicks)
	{
		ActiveRenderInfo info = event.getInfo();
		Entity entity = minecraft.getCameraEntity();
		Vector3d vector = info.getPosition();
		double totalX = vector.x();
		double totalY = vector.y();
		double totalZ = vector.z();
		if (pov == PointOfView.THIRD_PERSON_BACK && zoomCount > 0)
		{
			double posX = info.getPosition().x;
			double posY = info.getPosition().y;
			double posZ = info.getPosition().z;
			double entityPosX = entity.xOld + (entity.getX() - entity.xOld) * partialTicks;
			double entityPosY = entity.yOld + (entity.getY() - entity.yOld) * partialTicks + entity.getEyeHeight();
			double entityPosZ = entity.zOld + (entity.getZ() - entity.zOld) * partialTicks;
			float intpol = pov == PointOfView.THIRD_PERSON_BACK ? ((float) zoomCount / (float) zoomMaxCount) : 0;
			Vector3f interpolatedCorrection = new Vector3f(AIMING_CORRECTION.x() * intpol, AIMING_CORRECTION.y() * intpol, AIMING_CORRECTION.z() * intpol);
			PublicMatrix4f rotationMatrix = ClientEngine.INSTANCE.getPlayerData().getMatrix((float)partialTicks);
			Vector4f rotateVec = PublicMatrix4f.transform(rotationMatrix, new Vector4f(interpolatedCorrection.x(), interpolatedCorrection.y(), interpolatedCorrection.z(), 1.0F), null);
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
					if (d7 < smallest)
					{
						smallest = d7;
					}
				}
			}
			
			float dist = d3 == 0 ? 0 : (float) (smallest / d3);
			totalX += rotateVec.x() * dist;
			totalY -= rotateVec.y() * dist;
			totalZ += rotateVec.z() * dist;
		}
		
		info.setPosition(totalX, totalY, totalZ);
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
		static RenderEngine renderEngine;
		
		@SuppressWarnings("resource")
		@SubscribeEvent
		public static void renderLivingEvent(RenderLivingEvent.Pre<? extends LivingEntity, ? extends EntityModel<? extends LivingEntity>> event)
		{
			LivingEntity livingentity = event.getEntity();
			if (renderEngine.isEntityContained(livingentity))
			{
				if (livingentity instanceof ClientPlayerEntity)
				{
					if (event.getPartialRenderTick() == 1.0F)
					{
						return;
					}
					else if (!ClientEngine.INSTANCE.isBattleMode() && DarkSouls.CLIENT_INGAME_CONFIG.filterAnimation.getValue())
					{
						return;
					}
				}
				
				LivingData<?> entitydata = (LivingData<?>) livingentity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				if (entitydata != null)
				{
					event.setCanceled(true);
					renderEngine.renderEntityArmatureModel(livingentity, entitydata, event.getRenderer(), event.getBuffers(), event.getMatrixStack(), event.getLight(), event.getPartialRenderTick());
				}
			}
			
			if (!Minecraft.getInstance().options.hideGui)
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
			if (event.getPlayer() != null) {
				CapabilityItem cap = ModCapabilities.stackCapabilityGetter(event.getItemStack());
				ClientPlayerData playerCap = (ClientPlayerData) event.getPlayer().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				
				if (cap != null && ClientEngine.INSTANCE.getPlayerData() != null)
				{
					if (ClientEngine.INSTANCE.inputController.isKeyDown(ModKeys.SPECIAL_ATTACK_TOOLTIP))
					{
						if (cap.getSpecialAttack(ClientEngine.INSTANCE.getPlayerData()) != null)
						{
							event.getToolTip().clear();
							List<ITextComponent> skilltooltip = cap.getSpecialAttack(ClientEngine.INSTANCE.getPlayerData()).getTooltipOnItem(event.getItemStack(), cap, playerCap);
							
							for (ITextComponent s : skilltooltip)
							{
								event.getToolTip().add(s);
							}
						}
					}
					else
					{
						List<ITextComponent> tooltip = event.getToolTip();
						cap.modifyItemTooltip(event.getToolTip(), playerCap);
						
						for (int i = 0; i < tooltip.size(); i++)
						{
							ITextComponent textComp = tooltip.get(i);
							
							if (textComp.getSiblings().size() > 0)
							{
								ITextComponent sibling = textComp.getSiblings().get(0);
								if (sibling instanceof TranslationTextComponent)
								{
									TranslationTextComponent translationComponent = (TranslationTextComponent)sibling;
									if (translationComponent.getArgs().length > 1 &&
											translationComponent.getArgs()[1] instanceof TranslationTextComponent)
									{
										if(((TranslationTextComponent)translationComponent.getArgs()[1]).getKey().equals(Attributes.ATTACK_SPEED.getDescriptionId()))
										{
											double weaponSpeed = playerCap.getOriginalEntity().getAttribute(Attributes.ATTACK_SPEED).getBaseValue();
											
											for (AttributeModifier modifier : event.getItemStack().getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_SPEED))
											{
												weaponSpeed += modifier.getAmount();
											}
											
											double attackSpeedPanelty = Formulars.getAttackSpeedPenalty(playerCap.getWeight(), weaponSpeed, playerCap);
											weaponSpeed += attackSpeedPanelty;
											tooltip.remove(i);
											tooltip.add(i, new StringTextComponent(String.format(" %.2f ", weaponSpeed))
													.append(new TranslationTextComponent(Attributes.ATTACK_SPEED.getDescriptionId())));
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		@SuppressWarnings("resource")
		@SubscribeEvent
		public static void cameraSetupEvent(CameraSetup event)
		{
			renderEngine.updateCameraInfo(event, Minecraft.getInstance().options.getCameraType(), event.getRenderPartialTicks());
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
		public static void renderGameOverlay(RenderGameOverlayEvent.Pre event)
		{
			if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR)
			{
				ClientPlayerData playerdata = ClientEngine.INSTANCE.getPlayerData();
				if (playerdata != null)
				{
					if (Minecraft.renderNames())
					{
						renderEngine.guiSkillBar.renderGui(playerdata, event.getPartialTicks());
					}
				}
			}
		}
		
		@SuppressWarnings("resource")
		@SubscribeEvent
		public static void renderHand(RenderHandEvent event)
		{
			boolean isBattleMode = ClientEngine.INSTANCE.isBattleMode();
			
			if(isBattleMode)
			{
				if (event.getHand() == Hand.MAIN_HAND)
				{
					renderEngine.firstPersonRenderer.render(Minecraft.getInstance().player, ClientEngine.INSTANCE.getPlayerData(), null, event.getBuffers(),
							event.getMatrixStack(), event.getLight(), event.getPartialTicks());
				}
				event.setCanceled(true);
			}
		}
		
		@SubscribeEvent
		public static void renderWorldLast(RenderWorldLastEvent event)
		{
			if (renderEngine.zoomCount > 0 && renderEngine.minecraft.options.getCameraType() == PointOfView.THIRD_PERSON_BACK)
			{
				renderEngine.aimHelper.doRender(event.getMatrixStack(), event.getPartialTicks());
			}
		}
	}
}