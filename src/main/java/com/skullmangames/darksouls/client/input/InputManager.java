package com.skullmangames.darksouls.client.input;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.entity.ClientPlayerData;
import com.skullmangames.darksouls.common.capability.entity.LivingData.EntityState;
import com.skullmangames.darksouls.common.capability.item.CapabilityItem;
import com.skullmangames.darksouls.common.capability.item.WeaponCapability.AttackType;
import com.skullmangames.darksouls.client.ClientEngine;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.InputMappings.Input;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.client.event.InputEvent.RawMouseEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

@OnlyIn(Dist.CLIENT)
public class InputManager
{
	private final Map<KeyBinding, BiConsumer<Integer, Integer>> keyFunctionMap;
	private ClientPlayerEntity player;
	private ClientPlayerData playerdata;
	private KeyBindingMap keyHash;
	private int rightHandPressCounter;
	private boolean rightHandToggle;
	private boolean sprintToggle;
	private int sprintPressCounter;
	private Minecraft minecraft;
	public GameSettings options;
	private GLFWCursorPosCallbackI callback = (handle, x, y) -> {tracingMouseX = x; tracingMouseY = y;};
	private double tracingMouseX;
	private double tracingMouseY;
	
	
	public InputManager()
	{
		Events.inputManager = this;
		this.minecraft = Minecraft.getInstance();
		this.options = this.minecraft.options;
		this.keyFunctionMap = new HashMap<KeyBinding, BiConsumer<Integer, Integer>>();
		
		this.keyFunctionMap.put(this.options.keyAttack, this::onAttackKeyPressed);
		this.keyFunctionMap.put(this.options.keySwapOffhand, this::onSwapHandKeyPressed);
		this.keyFunctionMap.put(ModKeys.SWAP_ACTION_MODE, this::onSwapActionModeKeyPressed);
		this.keyFunctionMap.put(this.options.keySprint, this::onSprintKeyPressed);
		this.keyFunctionMap.put(ModKeys.VISIBLE_HITBOXES, this::toggleRenderCollision);
		
		try
		{
			this.keyHash = (KeyBindingMap)ObfuscationReflectionHelper.findField(KeyBinding.class, "field_74514_b").get(null);
		}
		catch (IllegalArgumentException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setGamePlayer(ClientPlayerData playerdata)
	{
		this.rightHandPressCounter = 0;
		this.rightHandToggle = false;
		this.sprintToggle = false;
		this.sprintPressCounter = 0;
		this.player = playerdata.getOriginalEntity();
		this.playerdata = playerdata;
	}
	
	public boolean playerCanMove(EntityState playerState)
	{
		return !playerState.isMovementLocked() || this.player.isRidingJumpable();
	}

	public boolean playerCanAct(EntityState playerState)
	{
		return !this.player.isSpectator() && !(this.player.isFallFlying() || playerdata.currentMotion == LivingMotion.FALL || playerState.isMovementLocked());
	}

	public boolean playerCanExecuteSkill(EntityState playerState)
	{
		return !this.player.isSpectator()
				&& !(this.player.isFallFlying() || this.playerdata.currentMotion == LivingMotion.FALL || !playerState.canAct())
				&& (this.playerdata.getStamina() >= 3.0F || this.player.isCreative())
				&& !this.player.isInWater()
				&& this.player.isOnGround()
				&& !this.player.isUsingItem()
				&& this.minecraft.screen == null;
	}
	
	private void toggleRenderCollision(int key, int action)
	{
		if (action != 1) return;
		this.minecraft.getEntityRenderDispatcher().setRenderHitBoxes(!this.minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes());
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
		if (action == 1 && !this.minecraft.options.getCameraType().isFirstPerson())
		{
			this.setKeyBind(options.keyAttack, false);
			while(options.keyAttack.consumeClick()) {}

			if (player.getTicksUsingItem() == 0 && !rightHandToggle) this.rightHandToggle = true;
		}

		if (player.getAttackStrengthScale(0) < 0.9F)
		{
			while(options.keyAttack.consumeClick()) {}
		}
	}
	
	private void onSwapHandKeyPressed(int key, int action)
	{
		CapabilityItem cap = this.playerdata.getHeldItemCapability(Hand.MAIN_HAND);

		if (this.playerdata.isInaction() || (cap != null && !cap.canUsedInOffhand()))
		{
			while (options.keySwapOffhand.consumeClick()) {}
			this.setKeyBind(options.keySwapOffhand, false);
		}
	}
	
	private void onSwapActionModeKeyPressed(int key, int action)
	{
		if (action == 1)
		{
			GameSettings options = this.minecraft.options;
			
			if (options.getCameraType() == PointOfView.THIRD_PERSON_BACK)
			{
				ClientEngine.INSTANCE.switchToFirstPerson();
			}
			else
			{
				ClientEngine.INSTANCE.switchToThirdPerson();
			}
		}
	}
	
	public void tick()
	{
		if (this.playerdata == null) return;

		EntityState playerState = this.playerdata.getEntityState();
		
		if (this.sprintToggle)
		{
			if (!this.player.isCreative() && this.playerdata.getStamina() <= 0.0F)
			{
				this.player.setSprinting(false);
				this.sprintToggle = false;
			}
		}
		else if (this.isKeyDown(this.options.keySprint) && (this.playerdata.getStamina() / this.playerdata.getMaxStamina()) >= 0.7F)
		{
			this.sprintToggle = true;
		}

		this.handleRightHandAction(playerState);
		this.handleSprintAction(playerState);
		
		if (this.minecraft.isPaused()) this.minecraft.mouseHandler.setup(this.minecraft.getWindow().getWindow());
	}
	
	private boolean playerCanSprint()
	{
		Vector2f vector2f = this.player.input.getMoveVector();
		return (this.player.isOnGround() || this.player.isUnderWater() || this.player.abilities.mayfly)
				&& (this.player.isUnderWater() ? this.player.input.hasForwardImpulse() : (double)this.player.input.forwardImpulse >= 0.8D)
				&& !this.player.isSprinting()
				&& (float)this.player.getFoodData().getFoodLevel() > 6.0F
				&& !this.player.isUsingItem()
				&& !this.player.hasEffect(Effects.BLINDNESS)
				&& (vector2f.x != 0.0F || vector2f.y != 0.0F);
	}
	
	private void handleSprintAction(EntityState playerState)
	{
		if (!this.sprintToggle) return;
		if (this.isKeyDown(this.options.keySprint))
		{
			this.sprintPressCounter++;
			
			if (this.playerCanSprint()) this.player.setSprinting(true);
			return;
		}
		
		this.sprintToggle = false;
		if (this.sprintPressCounter < 5 && this.playerCanExecuteSkill(playerState)) this.playerdata.performDodge();
		this.sprintPressCounter = 0;
	}
	
	private void handleRightHandAction(EntityState playerState)
	{
		if (!this.rightHandToggle) return;
		
		if (!this.isKeyDown(options.keyAttack))
		{
			this.rightHandToggle = false;
			this.rightHandPressCounter = 0;
			this.rightHandLightPress(playerState);
		}
		else
		{
			if (this.rightHandPressCounter > DarkSouls.CLIENT_INGAME_CONFIG.longPressCount.getValue())
			{
				if (this.playerCanExecuteSkill(playerState)) this.playerdata.performAttack(AttackType.HEAVY);
				
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
		if (this.playerCanExecuteSkill(playerState))
		{
			if (this.player.isSprinting()) this.playerdata.performAttack(AttackType.DASH);
			else this.playerdata.performAttack(AttackType.LIGHT);
		}
		
		this.rightHandToggle = false;
		this.rightHandPressCounter = 0;
	}
	
	public boolean isKeyDown(KeyBinding key)
	{
		if(key.getKey().getType() == InputMappings.Type.KEYSYM)
		{
			return GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), key.getKey().getValue()) > 0;
		}
		else if(key.getKey().getType() == InputMappings.Type.MOUSE)
		{
			return GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), key.getKey().getValue()) > 0;
		}
		else return false;
	}
	
	public void setKeyBind(KeyBinding key, boolean setter)
	{
		KeyBinding.set(key.getKey(), setter);
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
			if (!event.isAttack()) return;
			
			if (!minecraft.options.getCameraType().isFirstPerson())
			{
				event.setSwingHand(false);
			}
			
			if (minecraft.hitResult.getType() == RayTraceResult.Type.ENTITY
					|| (minecraft.hitResult.getType() == RayTraceResult.Type.BLOCK
					&& !minecraft.options.getCameraType().isFirstPerson()))
			{
				event.setCanceled(true);
			}
		}
		
		@SubscribeEvent
		public static void onMouseInput(RawMouseEvent event)
		{
			if (minecraft.player != null && minecraft.overlay == null && minecraft.screen == null)
			{
				Input input = InputMappings.Type.MOUSE.getOrCreate(event.getButton());
				for (KeyBinding keybinding : inputManager.keyHash.lookupAll(input))
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
			if (minecraft.player != null && inputManager.playerdata != null && inputManager.playerdata.isInaction())
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
				Input input = InputMappings.Type.KEYSYM.getOrCreate(event.getKey());
				for (KeyBinding keybinding : inputManager.keyHash.lookupAll(input))
				{
					if(inputManager.keyFunctionMap.containsKey(keybinding))
					{
						inputManager.keyFunctionMap.get(keybinding).accept(event.getKey(), event.getAction());
					}
				}
			}
		}
		
		@SubscribeEvent
		public static void onMoveInput(InputUpdateEvent event)
		{
			if(inputManager.playerdata == null) return;
			EntityState playerState = inputManager.playerdata.getEntityState();
			
			if (!inputManager.playerCanMove(playerState))
			{
				if (minecraft.options.getCameraType() == PointOfView.FIRST_PERSON)
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
				
				event.getMovementInput().forwardImpulse = 0.0F;
				event.getMovementInput().leftImpulse = 0.0F;
				event.getMovementInput().up = false;
				event.getMovementInput().down = false;
				event.getMovementInput().left = false;
				event.getMovementInput().right = false;
				event.getMovementInput().jumping = false;
				event.getMovementInput().shiftKeyDown = false;
				((ClientPlayerEntity)event.getPlayer()).sprintTime = -1;
			}
			else
			{
				inputManager.tracingMouseX = minecraft.mouseHandler.xpos();
				inputManager.tracingMouseY = minecraft.mouseHandler.ypos();
				minecraft.mouseHandler.setup(minecraft.getWindow().getWindow());
				
				if (minecraft.options.getCameraType() != PointOfView.FIRST_PERSON
						&& !ClientEngine.INSTANCE.getPlayerData().getClientAnimator().prevAiming())
				{
					float forward = 0.0F;
					float left = 0.0F;
					float rot = inputManager.player.yRot;
					boolean back = false;
					
					if (event.getMovementInput().forwardImpulse > 0.0F)
					{
						rot = ClientEngine.INSTANCE.mainCamera.getPivotXRot(1.0F);
						forward = event.getMovementInput().forwardImpulse;
					}
					else if (event.getMovementInput().forwardImpulse < 0.0F)
					{
						rot = ClientEngine.INSTANCE.mainCamera.getPivotXRot(1.0F) - 180.0F;
						forward = -event.getMovementInput().forwardImpulse;
						back = true;
					}
					if (event.getMovementInput().leftImpulse > 0.0F)
					{
						rot = ClientEngine.INSTANCE.mainCamera.getPivotXRot(1.0F) - 90.0F;
						
						if (forward == 0.0F) forward = event.getMovementInput().leftImpulse;
						else if (!back) left = -event.getMovementInput().leftImpulse;
						else left = event.getMovementInput().leftImpulse;
					}
					else if (event.getMovementInput().leftImpulse < 0.0F)
					{
						rot = ClientEngine.INSTANCE.mainCamera.getPivotXRot(1.0F) + 90.0F;
						
						if (forward == 0.0F) forward = -event.getMovementInput().leftImpulse;
						else if (!back) left = -event.getMovementInput().leftImpulse;
						else left = event.getMovementInput().leftImpulse;
					}
					
					inputManager.player.yRot = rot;
					event.getMovementInput().forwardImpulse = forward;
					event.getMovementInput().leftImpulse = left;
				}
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