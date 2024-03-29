package com.skullmangames.darksouls.client.input;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;

import com.mojang.blaze3d.platform.InputConstants;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.common.capability.entity.EquipLoaded.EquipLoadLevel;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.config.ConfigManager;
import com.skullmangames.darksouls.network.client.CTSPerformDodge.DodgeType;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.gui.screens.PlayerStatsScreen;

import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.client.event.InputEvent.RawMouseEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

@OnlyIn(Dist.CLIENT)
public class InputManager
{
	private final Map<KeyMapping, BiConsumer<Integer, Integer>> keyFunctionMap;
	private LocalPlayer player;
	private LocalPlayerCap playerCap;
	private KeyBindingMap keyHash;
	private int rightHandPressCounter;
	private boolean rightHandToggle;
	private boolean sprintToggle;
	private int sprintPressCounter;
	private Minecraft minecraft;
	public Options options;
	private GLFWCursorPosCallbackI callback = (handle, x, y) -> {tracingMouseX = x; tracingMouseY = y;};
	private double tracingMouseX;
	private double tracingMouseY;
	private AttackType reservedAttack;
	
	
	public InputManager()
	{
		Events.inputManager = this;
		this.minecraft = Minecraft.getInstance();
		this.options = this.minecraft.options;
		this.keyFunctionMap = new HashMap<KeyMapping, BiConsumer<Integer, Integer>>();
		
		this.keyFunctionMap.put(this.options.keyAttack, this::onAttackKeyPressed);
		this.keyFunctionMap.put(this.options.keySwapOffhand, this::onSwapHandKeyPressed);
		this.keyFunctionMap.put(ModKeys.TOGGLE_COMBAT_MODE, this::onToggleCombatModeKeyPressed);
		this.keyFunctionMap.put(this.options.keySprint, this::onSprintKeyPressed);
		this.keyFunctionMap.put(ModKeys.VISIBLE_HITBOXES, this::toggleRenderCollision);
		this.keyFunctionMap.put(ModKeys.OPEN_STAT_SCREEN, this::openPlayerStatScreen);
		this.keyFunctionMap.put(this.options.keyTogglePerspective, this::onTogglePerspectiveKeyPressed);
		this.keyFunctionMap.put(ModKeys.ATTUNEMENT_SLOT_UP, this::onAttunementSlotUp);
		this.keyFunctionMap.put(ModKeys.ATTUNEMENT_SLOT_DOWN, this::onAttunementSlotDown);
		this.keyFunctionMap.put(this.options.keyPickItem, this::onTrySelectTarget);
		
		try
		{
			this.keyHash = (KeyBindingMap)ObfuscationReflectionHelper.findField(KeyMapping.class, "f_90810_").get(null);
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setGamePlayer(LocalPlayerCap playerCap)
	{
		this.rightHandPressCounter = 0;
		this.rightHandToggle = false;
		this.sprintToggle = false;
		this.sprintPressCounter = 0;
		this.player = playerCap.getOriginalEntity();
		this.playerCap = playerCap;
	}
	
	public boolean playerCanMove(EntityState playerState)
	{
		return this.player.isAlive() && (!playerState.isMovementLocked() || this.player.isRidingJumpable());
	}

	public boolean playerCanAct(EntityState playerState)
	{
		return !this.player.isSpectator() && !(this.player.isFallFlying() || playerCap.currentMotion == LivingMotion.FALL || playerState.isMovementLocked());
	}

	public boolean playerCanAttack(EntityState playerState)
	{
		return !this.player.isSpectator()
				&& !(this.player.isFallFlying() || this.playerCap.currentMotion == LivingMotion.FALL || !playerState.canAct())
				&& (this.playerCap.getStamina() >= 3.0F || this.player.isCreative())
				&& (!this.player.isUnderWater() || this.player.isOnGround())
				&& (this.player.isOnGround() || this.playerCap.isMounted())
				&& (!this.player.isUsingItem() || this.playerCap.isBlocking())
				&& this.minecraft.screen == null;
	}
	
	public boolean playerCanDodge(EntityState playerState)
	{
		return ClientManager.INSTANCE.isCombatModeActive() 
				&&!this.player.isSpectator()
				&& !(this.player.isFallFlying() || this.playerCap.currentMotion == LivingMotion.FALL || !playerState.canAct())
				&& (this.playerCap.getStamina() >= 3.0F || this.player.isCreative())
				&& !this.player.isUnderWater()
				&& this.player.isOnGround()
				&& this.player.getVehicle() == null
				&& (!this.player.isUsingItem() || this.playerCap.isBlocking())
				&& this.minecraft.screen == null;
	}
	
	private void onTrySelectTarget(int key, int action)
	{
		if (action == 1 && ClientManager.INSTANCE.isCombatModeActive())
		{
			this.playerCap.updateTarget();
		}
	}
	
	private void onAttunementSlotUp(int key, int action)
	{
		if (action == 1 && this.playerCap.getAttunements().selected > 0)
		{
			this.playerCap.getAttunements().selected--;
		}
	}
	
	private void onAttunementSlotDown(int key, int action)
	{
		if (action == 1 && this.playerCap.getAttunements().selected < this.playerCap.getAttunements().getContainerSize() - 1)
		{
			this.playerCap.getAttunements().selected++;
		}
	}
	
	private void toggleRenderCollision(int key, int action)
	{
		if (action != 1) return;
		this.minecraft.getEntityRenderDispatcher().setRenderHitBoxes(!this.minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes());
	}
	
	private void openPlayerStatScreen(int key, int action)
	{
		if (action != 1 || this.minecraft.screen != null) return;
		this.minecraft.setScreen(new PlayerStatsScreen());
	}
	
	private void onSprintKeyPressed(int key, int action)
	{
		this.setKeyBind(options.keySprint, false);
		while(options.keySprint.consumeClick()) {}
		
		if (action == 0) this.player.setSprinting(false);
		else if (action == 1 && !this.sprintToggle) this.sprintToggle = true;
	}
	
	private void onAttackKeyPressed(int key, int action)
	{
		if (action == 1 && ClientManager.INSTANCE.isCombatModeActive())
		{
			this.setKeyBind(options.keyAttack, false);
			while(options.keyAttack.consumeClick()) {}

			if (!rightHandToggle) this.rightHandToggle = true;
		}

		if (player.getAttackStrengthScale(0) < 0.9F)
		{
			while(options.keyAttack.consumeClick()) {}
		}
	}
	
	private void onSwapHandKeyPressed(int key, int action)
	{
		ItemCapability cap = this.playerCap.getHeldItemCapability(InteractionHand.MAIN_HAND);

		if (this.playerCap.isInaction() || (cap != null && !cap.canUsedInOffhand()))
		{
			while (options.keySwapOffhand.consumeClick()) {}
			this.setKeyBind(options.keySwapOffhand, false);
		}
	}
	
	private void onTogglePerspectiveKeyPressed(int key, int action)
	{
		if (action == 1)
		{
			Options options = this.minecraft.options;
			
			if (options.getCameraType() == CameraType.THIRD_PERSON_BACK)
			{
				ClientManager.INSTANCE.switchToFirstPerson();
			}
			else
			{
				ClientManager.INSTANCE.switchToThirdPerson();
			}
		}
	}
	
	private void onToggleCombatModeKeyPressed(int key, int action)
	{
		if (action == 1)
		{
			ClientManager.INSTANCE.toggleCombatMode();
		}
	}
	
	public void tick()
	{
		if (this.playerCap == null) return;

		EntityState playerState = this.playerCap.getEntityState();
		
		if (this.sprintToggle)
		{
			boolean overencumbered = this.playerCap.getEquipLoadLevel() == EquipLoadLevel.OVERENCUMBERED;
			if ((!this.player.isCreative() && this.playerCap.getStamina() <= 0.0F) || overencumbered)
			{
				this.player.setSprinting(false);
				if (!overencumbered) this.sprintToggle = false;
			}
		}
		else if (this.isKeyDown(this.options.keySprint) && (this.playerCap.getStamina() / this.playerCap.getMaxStamina()) >= 0.7F)
		{
			this.sprintToggle = true;
		}
		this.handleRightHandAction(playerState);
		this.handleSprintAction(playerState);
		
		for (int i = 0; i < 9; ++i)
		{
			if (isKeyDown(this.options.keyHotbarSlots[i]))
			{
				if (this.playerCap.isInaction())
				{
					this.options.keyHotbarSlots[i].consumeClick();
				}
			}
		}
		
		if (this.minecraft.isPaused()) this.minecraft.mouseHandler.setup(this.minecraft.getWindow().getWindow());
	}
	
	private boolean playerCanSprint()
	{
		Vec2 vector2f = this.player.input.getMoveVector();
		return (this.player.isOnGround() || this.player.isUnderWater() || this.player.getAbilities().mayfly)
				&& (this.player.isUnderWater() ? this.player.input.hasForwardImpulse() : (double)this.player.input.forwardImpulse >= 0.8D)
				&& !this.player.isSprinting()
				&& (float)this.player.getFoodData().getFoodLevel() > 6.0F
				&& (!this.player.isUsingItem() || this.playerCap.isBlocking())
				&& !this.player.hasEffect(MobEffects.BLINDNESS)
				&& (vector2f.x != 0.0F || vector2f.y != 0.0F)
				&& this.playerCap.getEquipLoadLevel() != EquipLoadLevel.OVERENCUMBERED;
	}
	
	private void handleSprintAction(EntityState playerState)
	{
		if (!this.sprintToggle) return;
		if (this.isKeyDown(this.options.keySprint))
		{
			this.sprintPressCounter++;
			if (this.playerCanSprint() && this.sprintPressCounter >= 5) this.player.setSprinting(true);
		}
		else
		{
			this.sprintToggle = false;
			if (this.sprintPressCounter < 5 && this.playerCanDodge(playerState))
			{
				DodgeType dodgeType = DodgeType.JUMP_BACK;
				if (this.playerCap.getTarget() != null || this.playerCap.shouldShoulderSurf())
				{
					if (this.isKeyDown(this.options.keyUp)) dodgeType = DodgeType.FORWARD;
					else if (this.isKeyDown(this.options.keyDown)) dodgeType = DodgeType.BACK;
					else if (this.isKeyDown(this.options.keyLeft)) dodgeType = DodgeType.LEFT;
					else if (this.isKeyDown(this.options.keyRight)) dodgeType = DodgeType.RIGHT;
				}
				else if (this.isKeyDown(this.options.keyUp)
						|| this.isKeyDown(this.options.keyDown)
						|| this.isKeyDown(this.options.keyLeft)
						|| this.isKeyDown(this.options.keyRight)) dodgeType = DodgeType.FORWARD;
				this.playerCap.performDodge(dodgeType);
			}
			this.sprintPressCounter = 0;
		}
	}
	
	private void handleRightHandAction(EntityState playerState)
	{
		if (!this.rightHandToggle)
		{
			if (this.reservedAttack != null && this.playerCanAttack(playerState))
			{
				this.playerCap.performAttack(this.reservedAttack);
				this.reservedAttack = null;
			}
			return;
		}
		
		if (!this.isKeyDown(options.keyAttack))
		{
			this.rightHandToggle = false;
			this.rightHandPressCounter = 0;
			this.rightHandLightPress(playerState);
		}
		else
		{
			if (this.rightHandPressCounter > ConfigManager.INGAME_CONFIG.longPressCount.getValue())
			{
				if (this.playerCanAttack(playerState)) this.playerCap.performAttack(AttackType.HEAVY);
				else if (this.playerCap.getStamina() >= 3.0F || this.player.isCreative()) this.reservedAttack = AttackType.HEAVY;
				
				this.rightHandToggle = false;
				this.rightHandPressCounter = 0;
			}
			else
			{
				this.setKeyBind(this.options.keyAttack, false);
				this.rightHandPressCounter++;
			}
		}
	}
	
	private void rightHandLightPress(EntityState playerState)
	{
		if (this.playerCanAttack(playerState))
		{
			if (this.player.isSprinting()) this.playerCap.performAttack(AttackType.DASH);
			else this.playerCap.performAttack(AttackType.LIGHT);
		}
		else if ((this.playerCap.getStamina() >= 3.0F || this.player.isCreative()) && this.player.getVehicle() == null) this.reservedAttack = AttackType.LIGHT;
		
		this.rightHandToggle = false;
		this.rightHandPressCounter = 0;
	}
	
	public boolean isKeyDown(KeyMapping key)
	{
		if(key.getKey().getType() == InputConstants.Type.KEYSYM)
		{
			return GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), key.getKey().getValue()) > 0;
		}
		else if(key.getKey().getType() == InputConstants.Type.MOUSE)
		{
			return GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), key.getKey().getValue()) > 0;
		}
		else return false;
	}
	
	public void setKeyBind(KeyMapping key, boolean setter)
	{
		KeyMapping.set(key.getKey(), setter);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Mod.EventBusSubscriber(modid = DarkSouls.MOD_ID, value = Dist.CLIENT)
	public static class Events
	{
		private static InputManager inputManager;
		private static Minecraft minecraft = Minecraft.getInstance();
		
		// I'm using this only to cancel vanilla attacks
		@SubscribeEvent
		public static void onClickInputCancelable(InputEvent.ClickInputEvent event)
		{
			if (event.isAttack())
			{
				if (ClientManager.INSTANCE.isCombatModeActive())
				{
					event.setSwingHand(false);
				}
				
				if (minecraft.hitResult.getType() == HitResult.Type.ENTITY
						|| (minecraft.hitResult.getType() == HitResult.Type.BLOCK && ClientManager.INSTANCE.isCombatModeActive()))
				{
					event.setCanceled(true);
				}
			}
			else if (event.isPickBlock())
			{
				if (ClientManager.INSTANCE.isCombatModeActive())
				{
					event.setCanceled(true);
				}
			}
		}
		
		@SubscribeEvent
		public static void onMouseInput(RawMouseEvent event)
		{
			if (minecraft.player != null && minecraft.getOverlay() == null && minecraft.screen == null)
			{
				InputConstants.Key input = InputConstants.Type.MOUSE.getOrCreate(event.getButton());
				for (KeyMapping keybinding : inputManager.keyHash.lookupAll(input))
				{
					if(inputManager.keyFunctionMap.containsKey(keybinding))
					{
						inputManager.keyFunctionMap.get(keybinding).accept(event.getButton(), event.getAction());
					}
				}
			}
		}
		
		@SubscribeEvent
		public static void onMouseScroll(MouseScrollEvent event)
		{
			if (minecraft.player != null && inputManager.playerCap != null && inputManager.playerCap.isInaction())
			{
				if(minecraft.screen == null)
				{
					event.setCanceled(true);
				}
			}
		}
		
		@SubscribeEvent
		public static void onKeyboardInput(KeyInputEvent event)
		{
			if (minecraft.player != null && minecraft.screen == null)
			{
				InputConstants.Key input = InputConstants.Type.KEYSYM.getOrCreate(event.getKey());
				for (KeyMapping keybinding : inputManager.keyHash.lookupAll(input))
				{
					if(inputManager.keyFunctionMap.containsKey(keybinding))
					{
						inputManager.keyFunctionMap.get(keybinding).accept(event.getKey(), event.getAction());
					}
				}
			}
		}
		
		@SubscribeEvent
		public static void onMoveInput(MovementInputUpdateEvent event)
		{
			if(inputManager.playerCap == null) return;
			EntityState playerState = inputManager.playerCap.getEntityState();
			
			// Mouse Movement
			if (minecraft.options.getCameraType() == CameraType.FIRST_PERSON && playerState.isRotationLocked() && inputManager.player.isAlive())
			{
				GLFW.glfwSetCursorPosCallback(minecraft.getWindow().getWindow(), inputManager.callback);
				minecraft.mouseHandler.xpos = inputManager.tracingMouseX;
				minecraft.mouseHandler.ypos = inputManager.tracingMouseY;
			}
			else
			{
				inputManager.tracingMouseX = minecraft.mouseHandler.xpos();
				inputManager.tracingMouseY = minecraft.mouseHandler.ypos();
				minecraft.mouseHandler.setup(minecraft.getWindow().getWindow());
			}
			
			// Keyboard Movement
			if (inputManager.playerCap.getTarget() != null)
			{
				float forward = event.getInput().forwardImpulse;
				float left = event.getInput().leftImpulse;
				float rot = 0.0F;
				
				if (!inputManager.playerCap.shouldShoulderSurf()
					&& (inputManager.sprintPressCounter >= 5 || inputManager.player.getVehicle() != null))
				{
					boolean w = event.getInput().up;
					boolean s = event.getInput().down;
					boolean a = event.getInput().left;
					boolean d = event.getInput().right;
					
					rot = w && !s && a && !d ? 45 : !w && !s && a && !d ? 90
							: !w && s && a && !d ? 135 : !w && s && !a && !d ? 180
							: !w && s && !a && d ? 225 : !w && !s && !a && d ? 270
							: w && !s && !a && d ? 315 : 0;
					
					forward = rot == 0.0F ? event.getInput().forwardImpulse
							: rot == 180.0F ? -event.getInput().forwardImpulse
							: rot == 90.0F ? event.getInput().leftImpulse
							: rot == 270.0F ? -event.getInput().leftImpulse
							: rot == 45.0F ? event.getInput().forwardImpulse * 10
							: rot == 135.0F ? -event.getInput().forwardImpulse * 10
							: rot == 225.0F ? -event.getInput().forwardImpulse * 10
							: rot == 315.0F ? event.getInput().forwardImpulse * 10
							: 0.0F;
					
					left = rot == 45.0F ? event.getInput().leftImpulse
							: rot == 135.0F ? -event.getInput().leftImpulse
							: rot == 225.0F ? -event.getInput().leftImpulse
							: rot == 315.0F ? event.getInput().leftImpulse
							: 0.0F;
				}
				
				Entity target = inputManager.playerCap.getTarget();
				double dx = target.getX() - inputManager.player.getX();
				double dz = target.getZ() - inputManager.player.getZ();
				double dy = target.getY() + 0.6D * target.getBbHeight() - inputManager.player.getY() - inputManager.player.getEyeHeight();
				float degree = (float) (Math.atan2(dz, dx) * (180D / Math.PI)) - rot - 90.0F;
				float xDegree = (float) (Math.atan2(Math.sqrt(dx * dx + dz * dz), dy) * (180D / Math.PI)) - 90.0F;
				if (!playerState.isRotationLocked() || inputManager.player.getVehicle() != null)
				{
					inputManager.playerCap.rotateTo(degree, 60, false);
					inputManager.player.xRot = xDegree;
				}
				event.getInput().forwardImpulse = forward;
				event.getInput().leftImpulse = left;
				
			}
			else if (!inputManager.playerCap.shouldShoulderSurf() && minecraft.options.getCameraType() != CameraType.FIRST_PERSON)
			{
				if (inputManager.player.getVehicle() != null)
				{
					float forward = event.getInput().forwardImpulse;
					float left = event.getInput().leftImpulse;
					float rot = inputManager.player.yRot;
					
					boolean w = event.getInput().up;
					boolean s = event.getInput().down;
					boolean a = event.getInput().left;
					boolean d = event.getInput().right;
					
					float pivot = ClientManager.INSTANCE.mainCamera.getPivotXRot(1.0F);
					
					if (w || a || s || d)
					{
						rot = pivot;
						rot -= w && !s && a && !d ? 45 : !w && !s && a && !d ? 90
								: !w && s && a && !d ? 135 : !w && s && !a && !d ? 180
								: !w && s && !a && d ? 225 : !w && !s && !a && d ? 270
								: w && !s && !a && d ? 315 : 0;
					}
					
					forward = rot == pivot ? event.getInput().forwardImpulse
							: rot == pivot - 180.0F ? -event.getInput().forwardImpulse
							: rot == pivot - 90.0F ? event.getInput().leftImpulse
							: rot == pivot - 270.0F ? -event.getInput().leftImpulse
							: rot == pivot - 45.0F ? event.getInput().forwardImpulse * 10
							: rot == pivot - 135.0F ? -event.getInput().forwardImpulse * 10
							: rot == pivot - 225.0F ? -event.getInput().forwardImpulse * 10
							: rot == pivot - 315.0F ? event.getInput().forwardImpulse * 10
							: 0.0F;
					
					left = rot == pivot - 45.0F ? event.getInput().leftImpulse
							: rot == pivot - 135.0F ? -event.getInput().leftImpulse
							: rot == pivot - 225.0F ? -event.getInput().leftImpulse
							: rot == pivot - 315.0F ? event.getInput().leftImpulse
							: 0.0F;
					
					if (!playerState.isRotationLocked() || inputManager.player.getVehicle() != null) inputManager.playerCap.rotateTo(rot, 60, false);
					event.getInput().forwardImpulse = forward;
					event.getInput().leftImpulse = left;
				}
				else if (!inputManager.playerCap.shouldShoulderSurf())
				{
					float forward = 0.0F;
					float left = 0.0F;
					float rot = inputManager.player.yRot;
					boolean back = false;
					
					if (event.getInput().forwardImpulse > 0.0F)
					{
						rot = ClientManager.INSTANCE.mainCamera.getPivotXRot(1.0F);
						forward = event.getInput().forwardImpulse;
					}
					else if (event.getInput().forwardImpulse < 0.0F)
					{
						rot = ClientManager.INSTANCE.mainCamera.getPivotXRot(1.0F) - 180.0F;
						forward = -event.getInput().forwardImpulse;
						back = true;
					}
					if (event.getInput().leftImpulse > 0.0F)
					{
						rot = ClientManager.INSTANCE.mainCamera.getPivotXRot(1.0F) - 90.0F;
						
						if (forward == 0.0F) forward = event.getInput().leftImpulse;
						else if (!back) left = -event.getInput().leftImpulse;
						else left = event.getInput().leftImpulse;
					}
					else if (event.getInput().leftImpulse < 0.0F)
					{
						rot = ClientManager.INSTANCE.mainCamera.getPivotXRot(1.0F) + 90.0F;
						
						if (forward == 0.0F) forward = -event.getInput().leftImpulse;
						else if (!back) left = -event.getInput().leftImpulse;
						else left = event.getInput().leftImpulse;
					}
					
					if (forward > 0 && left > 0)
					{
						forward *= 2.0F;
						left *= 2.0F;
					}
					
					if (!playerState.isRotationLocked()) inputManager.player.yRot = rot;
					if (inputManager.playerCanMove(playerState))
					{
						event.getInput().forwardImpulse = forward;
						event.getInput().leftImpulse = left;
					}
				}
			}
			
			if (inputManager.playerCap.isBlocking())
			{
				event.getInput().leftImpulse *= 20F;
				event.getInput().forwardImpulse *= 20F;
			}
			
			if (!inputManager.playerCanMove(playerState) && inputManager.player.isAlive())
			{
				event.getInput().forwardImpulse = 0.0F;
				event.getInput().leftImpulse = 0.0F;
				event.getInput().up = false;
				event.getInput().down = false;
				event.getInput().left = false;
				event.getInput().right = false;
				event.getInput().jumping = false;
				event.getInput().shiftKeyDown = false;
				((LocalPlayer)event.getPlayer()).sprintTime = -1;
			}
		}
		
		@SubscribeEvent
		public static void preProcessKeyBindings(TickEvent.ClientTickEvent event)
		{
			if (event.phase != TickEvent.Phase.START || minecraft.player == null) return;
			inputManager.tick();
		}
	}
}