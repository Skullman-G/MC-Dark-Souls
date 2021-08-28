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
import com.skullmangames.darksouls.common.capability.item.IShield;
import com.skullmangames.darksouls.common.capability.item.WeaponCapability;
import com.skullmangames.darksouls.common.skill.Skill;
import com.skullmangames.darksouls.common.skill.SkillExecutionHelper;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.Skills;
import com.skullmangames.darksouls.client.ClientEngine;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.InputMappings.Input;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.client.event.InputEvent.RawMouseEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

@OnlyIn(Dist.CLIENT)
public class InputManager
{
	private final Map<KeyBinding, BiConsumer<Integer, Integer>> keyFunctionMap;
	private GLFWCursorPosCallbackI callback = (handle, x, y) -> {tracingMouseX = x; tracingMouseY = y;};
	private ClientPlayerEntity player;
	private ClientPlayerData playerdata;
	private KeyBindingMap keyHash;
	private double tracingMouseX;
	private double tracingMouseY;
	private int rightHandPressCounter;
	private int reservedSkill;
	private int skillReserveCounter;
	private boolean rightHandToggle;
	private boolean sprintToggle;
	private int sprintPressCounter;
	private Minecraft minecraft;
	public GameSettings options;
	
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
		
		try
		{
			this.keyHash = (KeyBindingMap) ObfuscationReflectionHelper.findField(KeyBinding.class, "field_74514_b").get(null);
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

	public boolean playerCanRotate(EntityState playerState)
	{
		return !playerState.isCameraRotationLocked() || this.player.isRidingJumpable();
	}

	public boolean playerCanAct(EntityState playerState)
	{
		return !this.player.isSpectator() && !(this.player.isFallFlying() || playerdata.currentMotion == LivingMotion.FALL || playerState.isMovementLocked());
	}

	public boolean playerCanDodging(EntityState playerState)
	{
		return !this.player.isSpectator() && !(this.player.isFallFlying() || playerdata.currentMotion == LivingMotion.FALL || !playerState.canAct());
	}

	public boolean playerCanExecuteSkill(EntityState playerState)
	{
		return !this.player.isSpectator() && !(this.player.isFallFlying() || playerdata.currentMotion == LivingMotion.FALL || !playerState.canAct());
	}
	
	private void onSprintKeyPressed(int key, int action)
	{
		if (action == 1)
		{
			if (!this.sprintToggle)
			{
				this.sprintToggle = true;
			}
		}
		else if (action == 0)
		{
			this.player.setSprinting(false);
		}
	}
	
	private void onAttackKeyPressed(int key, int action)
	{
		if (action == 1 && ClientEngine.INSTANCE.isBattleMode())
		{
			this.setKeyBind(options.keyAttack, false);
			while(options.keyAttack.consumeClick()) {}

			if (player.getTicksUsingItem() == 0)
			{
				if (!rightHandToggle)
				{
					rightHandToggle = true;
				}
			}
		}

		if (player.getAttackStrengthScale(0) < 0.9F)
		{
			while(options.keyAttack.consumeClick()) { }
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
				for (Hand hand : Hand.values())
				{
					WeaponCapability item = this.playerdata.getHeldWeaponCapability(hand);
					if (item == null || (hand == Hand.OFF_HAND && item.equals(ModCapabilities.FIST))) continue;
				}
				ClientEngine.INSTANCE.switchToMiningMode();
			}
			else
			{
				ClientEngine.INSTANCE.switchToBattleMode();
			}
		}
	}
	
	public void tick()
	{
		if (this.playerdata == null) return;

		EntityState playerState = this.playerdata.getEntityState();

		this.handleRightHandAction(playerState);
		this.handleSprintAction(playerState);
		
		if (this.reservedSkill >= 0)
		{
			if (skillReserveCounter > 0)
			{
				skillReserveCounter--;
				if(SkillExecutionHelper.getActiveSkill() != null && SkillExecutionHelper.canExecute(playerdata) && SkillExecutionHelper.getActiveSkill().isExecutableState(this.playerdata))
				{
					SkillExecutionHelper.execute(playerdata, SkillExecutionHelper.getActiveSkill());
					this.reservedSkill = -1;
					this.skillReserveCounter = -1;
				}
			}
			else
			{
				this.reservedSkill = -1;
				this.skillReserveCounter = -1;
			}
		}
		
		if (this.minecraft.isPaused())
		{
			this.minecraft.mouseHandler.setup(Minecraft.getInstance().getWindow().getWindow());
		}
	}
	
	private void handleSprintAction(EntityState playerState)
	{
		if (!this.sprintToggle) return;
		if (this.isKeyDown(options.keySprint))
		{
			this.sprintPressCounter++;
			return;
		}
		
		this.sprintToggle = false;
		
		if (ClientEngine.INSTANCE.isBattleMode() && this.sprintPressCounter < 5)
		{
			Skill skill = Skills.ROLL;
			if (SkillExecutionHelper.canExecute(this.playerdata, skill) && Skills.ROLL.isExecutableState(this.playerdata))
			{
				SkillExecutionHelper.execute(this.playerdata, skill);
			}
		}
		
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
				if (this.playerCanExecuteSkill(playerState))
				{
					WeaponCapability itemCap = playerdata.getHeldWeaponCapability(Hand.MAIN_HAND);
					if(itemCap != null)
					{
						SkillExecutionHelper.execute(this.playerdata, itemCap.getHeavyAttack());
					}
				}
				
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
			WeaponCapability itemCap = playerdata.getHeldWeaponCapability(Hand.MAIN_HAND);
			if(itemCap != null)
			{
				SkillExecutionHelper.execute(this.playerdata, itemCap.getLightAttack());
				this.player.resetAttackStrengthTicker();
			}
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
		else
		{
			return false;
		}
	}
	
	public void setKeyBind(KeyBinding key, boolean setter)
	{
		KeyBinding.set(key.getKey(), setter);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Mod.EventBusSubscriber(modid = DarkSouls.MOD_ID, value = Dist.CLIENT)
	public static class Events
	{
		static InputManager inputManager;
		
		@SubscribeEvent
		public static void onItemRightClick(RightClickItem event)
		{
			if (event.getHand() == Hand.OFF_HAND) return;
			CapabilityItem cap = inputManager.playerdata.getHeldItemCapability(event.getHand());
            if (!(cap instanceof IShield)) return;
            
            event.setCanceled(true);
		}
		
		@SubscribeEvent
		public static void onMouseInput(RawMouseEvent event)
		{
			Minecraft minecraft = Minecraft.getInstance();
			if (minecraft.player != null && minecraft.overlay == null && minecraft.screen == null)
			{
				Input input = InputMappings.Type.MOUSE.getOrCreate(event.getButton());
				for (KeyBinding keybinding : inputManager.keyHash.lookupAll(input))
				{
					if(inputManager.keyFunctionMap.containsKey(keybinding))
					{
						inputManager.keyFunctionMap.get(keybinding).accept(event.getButton(), event.getAction());
						event.setCanceled(true);
					}
				}
			}
		}
		
		@SuppressWarnings("resource")
		@SubscribeEvent
		public static void onMouseScroll(MouseScrollEvent event)
		{
			if (Minecraft.getInstance().player != null && inputManager.playerdata != null && inputManager.playerdata.isInaction())
			{
				if(Minecraft.getInstance().screen == null)
				{
					event.setCanceled(true);
				}
			}
		}
		
		@SuppressWarnings("resource")
		@SubscribeEvent
		public static void onKeyboardInput(KeyInputEvent event)
		{
			if (Minecraft.getInstance().player != null && Minecraft.getInstance().screen == null)
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
			if(inputManager.playerdata == null)
			{
				return;
			}
			
			Minecraft minecraft = Minecraft.getInstance();
			EntityState playerState = inputManager.playerdata.getEntityState();
			
			if(!inputManager.playerCanRotate(playerState) && inputManager.player.isAlive())
			{
				GLFW.glfwSetCursorPosCallback(minecraft.getWindow().getWindow(), inputManager.callback);
				minecraft.mouseHandler.xpos = inputManager.tracingMouseX;
				minecraft.mouseHandler.ypos = inputManager.tracingMouseY;
			}
			else
			{
				inputManager.tracingMouseX = minecraft.mouseHandler.xpos();
				inputManager.tracingMouseY = minecraft.mouseHandler.ypos();
				minecraft.mouseHandler.setup(Minecraft.getInstance().getWindow().getWindow());
			}
			
			if (!inputManager.playerCanMove(playerState))
			{
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
				if (inputManager.minecraft.options.getCameraType() != PointOfView.FIRST_PERSON)
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
					
					double x = inputManager.player.xo;
					double y = inputManager.player.yo;
					double z = inputManager.player.zo;
					inputManager.player.lerpTo(x, y, z, rot, 0.0F, 2, false);
					event.getMovementInput().forwardImpulse = forward;
					event.getMovementInput().leftImpulse = left;
				}
			}
		}
		
		@SuppressWarnings("resource")
		@SubscribeEvent
		public static void preProcessKeyBindings(TickEvent.ClientTickEvent event)
		{
			if (event.phase == TickEvent.Phase.START)
			{
				if (Minecraft.getInstance().player != null)
				{
					inputManager.tick();
				}
			}
		}
	}
}