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
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.gui.screens.PlayerStatsScreen;

import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
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
	private LocalPlayerCap playerdata;
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
	
	
	public InputManager()
	{
		Events.inputManager = this;
		this.minecraft = Minecraft.getInstance();
		this.options = this.minecraft.options;
		this.keyFunctionMap = new HashMap<KeyMapping, BiConsumer<Integer, Integer>>();
		
		this.keyFunctionMap.put(this.options.keyAttack, this::onAttackKeyPressed);
		this.keyFunctionMap.put(this.options.keySwapOffhand, this::onSwapHandKeyPressed);
		this.keyFunctionMap.put(ModKeys.SWAP_ACTION_MODE, this::onSwapActionModeKeyPressed);
		this.keyFunctionMap.put(this.options.keySprint, this::onSprintKeyPressed);
		this.keyFunctionMap.put(ModKeys.VISIBLE_HITBOXES, this::toggleRenderCollision);
		this.keyFunctionMap.put(ModKeys.OPEN_STAT_SCREEN, this::openPlayerStatScreen);
		
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
	
	public void setGamePlayer(LocalPlayerCap playerdata)
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
		return this.player.isAlive() && (!playerState.isMovementLocked() || this.player.isRidingJumpable());
	}

	public boolean playerCanAct(EntityState playerState)
	{
		return !this.player.isSpectator() && !(this.player.isFallFlying() || playerdata.currentMotion == LivingMotion.FALL || playerState.isMovementLocked());
	}
	
	public boolean movingKeysDown()
	{
		return this.isKeyDown(this.options.keyUp) || this.isKeyDown(this.options.keyDown) || this.isKeyDown(this.options.keyLeft) || this.isKeyDown(this.options.keyRight);
	}

	public boolean playerCanAttack(EntityState playerState)
	{
		return !this.player.isSpectator()
				&& !(this.player.isFallFlying() || this.playerdata.currentMotion == LivingMotion.FALL || !playerState.canAct())
				&& (this.playerdata.getStamina() >= 3.0F || this.player.isCreative())
				&& !this.player.isUnderWater()
				&& this.player.isOnGround()
				&& (!this.player.isUsingItem() || this.playerdata.isBlocking())
				&& this.minecraft.screen == null;
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
		if (action == 1 && !this.minecraft.options.getCameraType().isFirstPerson())
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
		ItemCapability cap = this.playerdata.getHeldItemCapability(InteractionHand.MAIN_HAND);

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
		Vec2 vector2f = this.player.input.getMoveVector();
		return (this.player.isOnGround() || this.player.isUnderWater() || this.player.getAbilities().mayfly)
				&& (this.player.isUnderWater() ? this.player.input.hasForwardImpulse() : (double)this.player.input.forwardImpulse >= 0.8D)
				&& !this.player.isSprinting()
				&& (float)this.player.getFoodData().getFoodLevel() > 6.0F
				&& (!this.player.isUsingItem() || this.playerdata.isBlocking())
				&& !this.player.hasEffect(MobEffects.BLINDNESS)
				&& (vector2f.x != 0.0F || vector2f.y != 0.0F);
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
			if (this.sprintPressCounter < 5 && this.playerCanAttack(playerState)) this.playerdata.performDodge(this.movingKeysDown());
			this.sprintPressCounter = 0;
		}
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
				if (this.playerCanAttack(playerState)) this.playerdata.performAttack(AttackType.HEAVY);
				
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
			if (this.player.isSprinting()) this.playerdata.performAttack(AttackType.DASH);
			else this.playerdata.performAttack(AttackType.LIGHT);
		}
		
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
			if (!event.isAttack()) return;
			if (!minecraft.options.getCameraType().isFirstPerson())
			{
				event.setSwingHand(false);
			}
			
			if (minecraft.hitResult.getType() == HitResult.Type.ENTITY
					|| (minecraft.hitResult.getType() == HitResult.Type.BLOCK
					&& !minecraft.options.getCameraType().isFirstPerson()))
			{
				event.setCanceled(true);
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
			if(inputManager.playerdata == null) return;
			EntityState playerState = inputManager.playerdata.getEntityState();
			
			if (!inputManager.playerCanMove(playerState) && inputManager.player.isAlive())
			{
				if (minecraft.options.getCameraType() == CameraType.FIRST_PERSON)
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
			else
			{
				inputManager.tracingMouseX = minecraft.mouseHandler.xpos();
				inputManager.tracingMouseY = minecraft.mouseHandler.ypos();
				minecraft.mouseHandler.setup(minecraft.getWindow().getWindow());
				
				if (minecraft.options.getCameraType() != CameraType.FIRST_PERSON
						&& !ClientManager.INSTANCE.getPlayerData().getClientAnimator().isAiming())
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
					
					inputManager.player.yRot = rot;
					event.getInput().forwardImpulse = forward;
					event.getInput().leftImpulse = left;
					
					if (inputManager.playerdata.isBlocking())
					{
						event.getInput().leftImpulse *= 20F;
						event.getInput().forwardImpulse *= 20F;
					}
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