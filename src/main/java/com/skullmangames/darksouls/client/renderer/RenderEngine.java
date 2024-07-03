package com.skullmangames.darksouls.client.renderer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
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
import com.skullmangames.darksouls.client.renderer.entity.ArmorStandRenderer;
import com.skullmangames.darksouls.client.renderer.entity.BellGargoyleRenderer;
import com.skullmangames.darksouls.client.renderer.entity.BerenikeKnightRenderer;
import com.skullmangames.darksouls.client.renderer.entity.BlackKnightRenderer;
import com.skullmangames.darksouls.client.renderer.entity.HollowRenderer;
import com.skullmangames.darksouls.client.renderer.entity.StrayDemonRenderer;
import com.skullmangames.darksouls.client.renderer.entity.TaurusDemonRenderer;
import com.skullmangames.darksouls.client.renderer.entity.PlayerRenderer;
import com.skullmangames.darksouls.client.renderer.entity.SimpleHumanoidRenderer;
import com.skullmangames.darksouls.client.renderer.entity.additional.AdditionalEntityRenderer;
import com.skullmangames.darksouls.client.renderer.entity.model.ClientModel;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.common.capability.entity.AnastaciaOfAstoraCap;
import com.skullmangames.darksouls.common.capability.entity.BalderKnightCap;
import com.skullmangames.darksouls.common.capability.entity.FalconerCap;
import com.skullmangames.darksouls.common.capability.entity.FireKeeperCap;
import com.skullmangames.darksouls.common.capability.entity.HollowLordranSoldierCap;
import com.skullmangames.darksouls.common.capability.entity.HollowLordranWarriorCap;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.entity.SimpleHumanoidCap;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.entity.AnastaciaOfAstora;
import com.skullmangames.darksouls.common.entity.BalderKnight;
import com.skullmangames.darksouls.common.entity.CrestfallenWarrior;
import com.skullmangames.darksouls.common.entity.Falconer;
import com.skullmangames.darksouls.common.entity.AbstractFireKeeper;
import com.skullmangames.darksouls.common.entity.HollowLordranSoldier;
import com.skullmangames.darksouls.common.entity.HollowLordranWarrior;
import com.skullmangames.darksouls.common.entity.PetrusOfThorolund;
import com.skullmangames.darksouls.common.item.HasDarkSoulsUseAction;
import com.skullmangames.darksouls.common.item.ModShieldItem;
import com.skullmangames.darksouls.core.init.ModEntities;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
public class RenderEngine
{
	public static final ResourceLocation NULL_TEXTURE = new ResourceLocation(DarkSouls.MOD_ID, "textures/gui/null.png");
	public final AimHelperRenderer aimHelper;
	private final Minecraft minecraft;
	private final ModMatrix4f projectionMatrix;
	private final Map<EntityType<?>, ArmatureRenderer<?, ?>> entityRendererMap;
	private final Map<Item, RenderItemBase> itemRendererMapByInstance;
	private final Map<Class<? extends Item>, RenderItemBase> itemRendererMapByClass;
	private final Map<Item, Pair<Function<ClientModels, ClientModel>, Set<Integer>>> armorModelMap;
	private final FirstPersonRenderer firstPersonRenderer;
	private final PlayerGuiRenderer playerGuiRenderer;
	private boolean aiming;
	private int zoomOutTimer = 0;
	private int zoomCount;
	private int zoomMaxCount = 20;
	
	public RenderEngine()
	{
		Events.renderEngine = this;
		RenderItemBase.renderEngine = this;
		AdditionalEntityRenderer.init();
		this.minecraft = Minecraft.getInstance();
		this.entityRendererMap = new HashMap<>();
		this.itemRendererMapByInstance = new HashMap<>();
		this.itemRendererMapByClass = new HashMap<>();
		this.armorModelMap = new HashMap<>();
		this.projectionMatrix = new ModMatrix4f();
		this.firstPersonRenderer = new FirstPersonRenderer();
		this.playerGuiRenderer = new PlayerGuiRenderer();
		this.aimHelper = new AimHelperRenderer();
		
		this.minecraft.renderBuffers().fixedBuffers.put(ModRenderTypes.getEnchantedArmor(), 
				new BufferBuilder(ModRenderTypes.getEnchantedArmor().bufferSize()));
	}
	
	public void buildRenderer()
	{
		// Init Entity Renderers
		this.entityRendererMap.put(EntityType.PLAYER, new PlayerRenderer());
		this.entityRendererMap.put(EntityType.ARMOR_STAND, new ArmorStandRenderer());
		this.entityRendererMap.put(EntityType.ZOMBIE, new SimpleHumanoidRenderer<Zombie, SimpleHumanoidCap<Zombie>>("zombie/zombie"));
		this.entityRendererMap.put(EntityType.HUSK, new SimpleHumanoidRenderer<Husk, SimpleHumanoidCap<Husk>>("zombie/husk"));
		this.entityRendererMap.put(EntityType.DROWNED, new SimpleHumanoidRenderer<Drowned, SimpleHumanoidCap<Drowned>>("zombie/drowned"));
		this.entityRendererMap.put(ModEntities.HOLLOW.get(), new HollowRenderer());
		this.entityRendererMap.put(ModEntities.HOLLOW_LORDRAN_WARRIOR.get(), new SimpleHumanoidRenderer<HollowLordranWarrior, HollowLordranWarriorCap>(DarkSouls.MOD_ID, "hollow/lordran_hollow"));
		this.entityRendererMap.put(ModEntities.HOLLOW_LORDRAN_SOLDIER.get(), new SimpleHumanoidRenderer<HollowLordranSoldier, HollowLordranSoldierCap>(DarkSouls.MOD_ID, "hollow/lordran_hollow"));
		this.entityRendererMap.put(ModEntities.CRESTFALLEN_WARRIOR.get(), new SimpleHumanoidRenderer<CrestfallenWarrior, SimpleHumanoidCap<CrestfallenWarrior>>(DarkSouls.MOD_ID, "quest_entity/crestfallen_warrior"));
		this.entityRendererMap.put(ModEntities.ANASTACIA_OF_ASTORA.get(), new SimpleHumanoidRenderer<AnastaciaOfAstora, AnastaciaOfAstoraCap>(DarkSouls.MOD_ID, "quest_entity/anastacia_of_astora"));
		this.entityRendererMap.put(ModEntities.STRAY_DEMON.get(), new StrayDemonRenderer());
		this.entityRendererMap.put(ModEntities.FIRE_KEEPER.get(), new SimpleHumanoidRenderer<AbstractFireKeeper, FireKeeperCap>(DarkSouls.MOD_ID, "fire_keeper"));
		this.entityRendererMap.put(ModEntities.PETRUS_OF_THOROLUND.get(), new SimpleHumanoidRenderer<PetrusOfThorolund, SimpleHumanoidCap<PetrusOfThorolund>>(DarkSouls.MOD_ID, "quest_entity/petrus_of_thorolund"));
		this.entityRendererMap.put(ModEntities.FALCONER.get(), new SimpleHumanoidRenderer<Falconer, FalconerCap>(DarkSouls.MOD_ID, "hollow/lordran_hollow"));
		this.entityRendererMap.put(ModEntities.BLACK_KNIGHT.get(), new BlackKnightRenderer());
		this.entityRendererMap.put(ModEntities.TAURUS_DEMON.get(), new TaurusDemonRenderer());
		this.entityRendererMap.put(ModEntities.BALDER_KNIGHT.get(), new SimpleHumanoidRenderer<BalderKnight, BalderKnightCap>(DarkSouls.MOD_ID, "hollow/lordran_hollow"));
		this.entityRendererMap.put(ModEntities.BERENIKE_KNIGHT.get(), new BerenikeKnightRenderer());
		this.entityRendererMap.put(ModEntities.BELL_GARGOYLE.get(), new BellGargoyleRenderer());
		
		// Init Item Renderers
		RenderBow bowRenderer = new RenderBow();
		RenderCrossbow crossbowRenderer = new RenderCrossbow();
		RenderElytra elytraRenderer = new RenderElytra();
		RenderHat hatRenderer = new RenderHat();
		RenderShield shieldRenderer = new RenderShield();
		RenderTrident tridentRenderer = new RenderTrident();
		
		this.itemRendererMapByInstance.put(Items.AIR, new RenderItemBase());
		this.itemRendererMapByInstance.put(Items.BOW, bowRenderer);
		this.itemRendererMapByInstance.put(Items.ELYTRA, elytraRenderer);
		this.itemRendererMapByInstance.put(Items.CREEPER_HEAD, hatRenderer);
		this.itemRendererMapByInstance.put(Items.DRAGON_HEAD, hatRenderer);
		this.itemRendererMapByInstance.put(Items.PLAYER_HEAD, hatRenderer);
		this.itemRendererMapByInstance.put(Items.ZOMBIE_HEAD, hatRenderer);
		this.itemRendererMapByInstance.put(Items.SKELETON_SKULL, hatRenderer);
		this.itemRendererMapByInstance.put(Items.WITHER_SKELETON_SKULL, hatRenderer);
		this.itemRendererMapByInstance.put(Items.CARVED_PUMPKIN, hatRenderer);
		this.itemRendererMapByInstance.put(Items.CROSSBOW, crossbowRenderer);
		this.itemRendererMapByInstance.put(Items.TRIDENT, tridentRenderer);
		
		this.itemRendererMapByClass.put(BlockItem.class, hatRenderer);
		this.itemRendererMapByClass.put(BowItem.class, bowRenderer);
		this.itemRendererMapByClass.put(CrossbowItem.class, crossbowRenderer);
		this.itemRendererMapByClass.put(ElytraItem.class, elytraRenderer);
		this.itemRendererMapByClass.put(ShieldItem.class, shieldRenderer);
		this.itemRendererMapByClass.put(ModShieldItem.class, shieldRenderer);
		this.itemRendererMapByClass.put(TridentItem.class, tridentRenderer);
		
		// Init Armor Models
		this.putArmorModel(ModItems.BLOOD_STAINED_SKIRT.get(), (models) -> models.ITEM_SKIRT);
		this.putArmorModel(ModItems.LORDRAN_SOLDIER_WAISTCLOTH.get(), (models) -> models.ITEM_SKIRT);
		this.putArmorModel(ModItems.LORDRAN_WARRIOR_WAISTCLOTH.get(), (models) -> models.ITEM_SKIRT);
		this.putArmorModel(ModItems.LORDRAN_WARRIOR_BOOTS.get(), (models) -> models.ITEM_ONE_SHOE);
		this.putArmorModel(ModItems.ELITE_CLERIC_LEGGINGS.get(), (models) -> models.ITEM_SKIRT);
		this.putArmorModel(ModItems.FALCONER_HELM.get(), (models) -> models.ITEM_FALCONER_HELM);
		this.putArmorModel(ModItems.FALCONER_ARMOR.get(), (models) -> models.ITEM_FALCONER_ARMOR);
		this.putArmorModel(ModItems.BLACK_KNIGHT_HELM.get(), (models) -> models.BLACK_KNIGHT_HELM, 9);
		this.putArmorModel(ModItems.BLACK_KNIGHT_ARMOR.get(), (models) -> models.BLACK_KNIGHT_ARMOR, 7, 8, 11, 12, 16, 17);
		this.putArmorModel(ModItems.BLACK_KNIGHT_LEGGINGS.get(), (models) -> models.BLACK_KNIGHT_LEGGINGS, 1, 2, 4, 5);
		this.putArmorModel(ModItems.BALDER_HELM.get(), (models) -> models.BALDER_HELM);
		this.putArmorModel(ModItems.BALDER_ARMOR.get(), (models) -> models.BALDER_ARMOR);
		this.putArmorModel(ModItems.BALDER_LEGGINGS.get(), (models) -> models.BALDER_LEGGINGS);
		this.putArmorModel(ModItems.BALDER_BOOTS.get(), (models) -> models.BALDER_BOOTS);
		this.putArmorModel(ModItems.FANG_BOAR_HELM.get(), (models) -> models.FANG_BOAR_HELM);
		this.putArmorModel(ModItems.BERENIKE_HELM.get(), (models) -> models.BERENIKE_HELM);
		this.putArmorModel(ModItems.BERENIKE_ARMOR.get(), (models) -> models.BERENIKE_ARMOR, 7, 8, 11, 12, 16, 17);
		this.putArmorModel(ModItems.BERENIKE_LEGGINGS.get(), (models) -> models.BERENIKE_LEGGINGS, 1, 2, 4, 5);
	}
	
	private void putArmorModel(Item item, Function<ClientModels, ClientModel> model, int... jointMask)
	{
		Set<Integer> maskSet = new HashSet<>();
		for (int j : jointMask) maskSet.add(j);
		this.armorModelMap.put(item, new Pair<>(model, maskSet));
	}
	
	public ClientModel getArmorModel(ArmorItem armor)
	{
		ClientModels models = ClientModels.CLIENT;
		ClientModel model = this.armorModelMap.getOrDefault(armor, new Pair<>((m) -> null, null)).getFirst().apply(models);
		if (model == null)
		{
			switch (armor.getSlot())
			{
					default:
					case HEAD:
						model = models.ITEM_HELMET;
						break;
						
					case CHEST:
						model = models.ITEM_CHESTPLATE;
						break;
						
					case LEGS:
						model = models.ITEM_LEGGINS;
						break;
						
					case FEET:
						model = models.ITEM_BOOTS;
						break;
			}
		}
		return model;
	}
	
	public Set<Integer> getArmorJointMask(ArmorItem armor)
	{
		return this.armorModelMap.getOrDefault(armor, new Pair<>(null, new HashSet<>())).getSecond();
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
	
	public <E extends LivingEntity> void renderEntityArmatureModel(LivingCap<E> entityCap, EntityRenderer<E> renderer, MultiBufferSource buffer,
			PoseStack matStack, int packedLightIn, float partialTicks)
	{
		this.getArmatureRenderer(entityCap).render(entityCap, renderer, buffer, matStack, packedLightIn, partialTicks);
	}
	
	@SuppressWarnings("unchecked")
	public <E extends LivingEntity, T extends LivingCap<E>> ArmatureRenderer<E, T> getArmatureRenderer(T entityCap)
	{
		return (ArmatureRenderer<E, T>) this.entityRendererMap.get(entityCap.getOriginalEntity().getType());
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
	}

	public ModMatrix4f getCurrentProjectionMatrix()
	{
		return this.projectionMatrix;
	}

	@Mod.EventBusSubscriber(modid = DarkSouls.MOD_ID, value = Dist.CLIENT)
	public static class Events
	{
		private static RenderEngine renderEngine;
		private static final Minecraft minecraft = Minecraft.getInstance();
		
		@SubscribeEvent
		public static <E extends LivingEntity> void renderLivingEvent(
				RenderLivingEvent.Pre<E, ? extends EntityModel<E>>event)
		{
			LivingEntity livingentity = event.getEntity();
			if (renderEngine.isEntityContained(livingentity))
			{
				if (livingentity instanceof LocalPlayer && event.getPartialTick() == 1.0F)
				{
					LocalPlayerCap playerCap = (LocalPlayerCap)livingentity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
					if (playerCap != null)
					{
						renderEngine.playerGuiRenderer.render(playerCap, null, event.getMultiBufferSource(), event.getPoseStack(), event.getPackedLight(), event.getPartialTick());
						event.setCanceled(true);
					}
					return;
				}
				
				@SuppressWarnings("unchecked")
				LivingCap<E> entityCap = (LivingCap<E>)livingentity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				if (entityCap != null)
				{
					event.setCanceled(true);
					renderEngine.renderEntityArmatureModel(entityCap, event.getRenderer(), event.getMultiBufferSource(), event.getPoseStack(), event.getPackedLight(), event.getPartialTick());
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
			if (!ClientManager.INSTANCE.isCombatModeActive())
			{
				if (event.getItemStack().getItem() instanceof HasDarkSoulsUseAction)
				{
					HasDarkSoulsUseAction item = (HasDarkSoulsUseAction)event.getItemStack().getItem();
					FirstPersonRendererOverride.renderArmWithItem(item, event.getSwingProgress(), event.getPartialTicks(), event.getEquipProgress(), event.getHand(),
							event.getItemStack(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
					event.setCanceled(true);
				}
				return;
			}
			
			LocalPlayerCap playerCap = ClientManager.INSTANCE.getPlayerCap();
			if (event.getHand() == InteractionHand.MAIN_HAND)
			{
				renderEngine.firstPersonRenderer.render(playerCap, null, event.getMultiBufferSource(),
						event.getPoseStack(), event.getPackedLight(), event.getPartialTicks());
			}
			event.setCanceled(true);
		}
		
		@SubscribeEvent
		public static void renderWorldLast(RenderLevelStageEvent event)
		{
			if (minecraft.options.getCameraType() == CameraType.THIRD_PERSON_BACK && ClientManager.INSTANCE.getPlayerCap().getClientAnimator().isAiming())
			{
				renderEngine.aimHelper.doRender(event.getPoseStack(), event.getPartialTick());
			}
		}
	}
}