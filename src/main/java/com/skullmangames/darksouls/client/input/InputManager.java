package com.skullmangames.darksouls.client.input;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.ClientPlayerData;
import com.skullmangames.darksouls.common.capability.entity.LivingData.EntityState;
import com.skullmangames.darksouls.common.capability.item.CapabilityItem;
import com.skullmangames.darksouls.common.capability.item.CapabilityItem.WeaponCategory;
import com.skullmangames.darksouls.common.skill.SkillContainer;
import com.skullmangames.darksouls.common.skill.SkillSlot;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ProviderItem;
import com.skullmangames.darksouls.client.ClientEngine;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSPlayAnimation;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.InputMappings.Input;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.ClickInputEvent;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
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
	private GLFWCursorPosCallbackI callback = (handle, x, y) -> {tracingMouseX = x; tracingMouseY = y;};
	private ClientPlayerEntity player;
	private ClientPlayerData playerdata;
	private KeyBindingMap keyHash;
	private double tracingMouseX;
	private double tracingMouseY;
	private int comboHoldCounter;
	private int comboCounter;
	private int mouseLeftPressCounter = 0;
	private int reservedSkill;
	private int skillReserveCounter;
	private boolean mouseLeftPressToggle = false;
	private boolean lightPress;
	
	public GameSettings options;
	
	@SuppressWarnings("resource")
	public InputManager()
	{
		Events.inputManager = this;
		this.options = Minecraft.getInstance().options;
		this.keyFunctionMap = new HashMap<KeyBinding, BiConsumer<Integer, Integer>>();
		
		this.keyFunctionMap.put(options.keyAttack, this::onAttackKeyPressed);
		this.keyFunctionMap.put(options.keySwapOffhand, this::onSwapHandKeyPressed);
		this.keyFunctionMap.put(ModKeys.SWAP_ACTION_MODE, this::onSwapActionModeKeyPressed);
		this.keyFunctionMap.put(options.keySprint, this::onSprintKeyPressed);
		
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
		this.comboCounter = 0;
		this.mouseLeftPressCounter = 0;
		this.mouseLeftPressToggle = false;
		this.lightPress = false;
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
		if (action == 0)
		{
			if (ClientEngine.INSTANCE.isBattleMode() && (this.player.sprintTime <= 2 || !this.player.isSprinting()))
			{
				SkillContainer skill = this.playerdata.getSkill(SkillSlot.DODGE);
				if (skill.canExecute(this.playerdata) && skill.getContaining().isExecutableState(this.playerdata))
				{
					skill.execute(this.playerdata);
				}
			}
			
			this.player.setSprinting(false);
		}
	}
	
	private void onAttackKeyPressed(int key, int action)
	{
		if (action == 1)
		{
			if (ClientEngine.INSTANCE.isBattleMode())
			{
				this.setKeyBind(options.keyAttack, false);
				while(options.keyAttack.isDown())
				{
				}

				if (player.getUseItemRemainingTicks() == 0)
				{
					if (!mouseLeftPressToggle)
					{
						mouseLeftPressToggle = true;
					}
				}
			}
		}

		if (player.getAttackStrengthScale(0) < 0.9F)
		{
			while(options.keyAttack.isDown())
			{
			}
		}
	}
	
	private void onSwapHandKeyPressed(int key, int action)
	{
		CapabilityItem cap = this.playerdata.getHeldItemCapability(Hand.MAIN_HAND);

		if (this.playerdata.isInaction() || (cap != null && !cap.canUsedInOffhand()))
		{
			while (options.keySwapOffhand.isDown())
			{
			}
			this.setKeyBind(options.keySwapOffhand, false);
		}
	}
	
	private void onSwapActionModeKeyPressed(int key, int action)
	{
		if (action == 1)
		{
			Minecraft minecraft = Minecraft.getInstance();
			GameSettings options = minecraft.options;
			Item item = this.player.inventory.getSelected().getItem();
			
			if (options.getCameraType() == PointOfView.THIRD_PERSON_BACK)
			{
				if (ProviderItem.CAPABILITY_BY_INSTANCE.containsKey(item) && ProviderItem.CAPABILITY_BY_INSTANCE.get(item).getWeaponCategory() != WeaponCategory.NONE_WEAON)
				{
					return;
				}
				options.setCameraType(PointOfView.FIRST_PERSON);
				ClientEngine.INSTANCE.switchToMiningMode();
			}
			else
			{
				options.setCameraType(PointOfView.THIRD_PERSON_BACK);
				ClientEngine.INSTANCE.switchToBattleMode();
			}
		}
	}
	
	@SuppressWarnings("resource")
	public void tick()
	{
		if (this.playerdata == null) return;

		EntityState playerState = this.playerdata.getEntityState();

		if (this.mouseLeftPressToggle)
		{
			if (!this.isKeyDown(options.keyAttack))
			{
				this.lightPress = true;
				this.mouseLeftPressToggle = false;
				this.mouseLeftPressCounter = 0;
			}
			else
			{
				if (this.mouseLeftPressCounter > DarkSouls.CLIENT_INGAME_CONFIG.longPressCount.getValue())
				{
					if (this.playerCanExecuteSkill(playerState))
					{
						CapabilityItem itemCap = playerdata.getHeldItemCapability(Hand.MAIN_HAND);
						if(itemCap != null)
						{
							this.playerdata.getSkill(SkillSlot.WEAPON_SPECIAL_ATTACK).execute(this.playerdata);
						}
					}
					else
					{
						if (!this.player.isSpectator())
						{
							this.reserveSkill(SkillSlot.WEAPON_SPECIAL_ATTACK);
						}
					}
					
					this.mouseLeftPressToggle = false;
					this.mouseLeftPressCounter = 0;
					this.resetAttackCounter();
				}
				else
				{
					this.setKeyBind(this.options.keyAttack, false);
					this.mouseLeftPressCounter++;
				}
			}
		}
		
		if (this.lightPress)
		{
			if (this.playerCanAct(playerState))
			{
				playAttackMotion(this.player.getMainHandItem(), this.player.isSprinting());
				this.player.resetAttackStrengthTicker();
				this.lightPress = false;
			}
			else
			{
				if (this.player.isSpectator() || playerState.getLevel() < 2)
				{
					lightPress = false;
				}
			}
			
			this.mouseLeftPressToggle = false;
			this.mouseLeftPressCounter = 0;
		}
		
		if (this.reservedSkill >= 0)
		{
			if (skillReserveCounter > 0)
			{
				SkillContainer skill = playerdata.getSkill(reservedSkill);
				skillReserveCounter--;
				if(skill.getContaining() != null && skill.canExecute(playerdata) && skill.getContaining().isExecutableState(this.playerdata))
				{
					skill.execute(playerdata);
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
		
		if (this.comboHoldCounter > 0)
		{
			float f = player.getAttackStrengthScale(0);
			
			if (!playerState.isMovementLocked() && !playerState.isCameraRotationLocked() && f >= 1.0F)
			{
				--this.comboHoldCounter;
				
				if (comboHoldCounter == 0)
				{
					this.resetAttackCounter();
				}
			}
		}
		
		for (int i = 0; i < 9; ++i)
		{
			if (isKeyDown(options.keyHotbarSlots[i]))
			{
				if (playerdata.isInaction())
					options.keyHotbarSlots[i].isDown();
			}
		}
		
		if (Minecraft.getInstance().isPaused())
		{
			Minecraft.getInstance().mouseHandler.setup(Minecraft.getInstance().getWindow().getWindow());
		}
	}
	
	private void playAttackMotion(ItemStack holdItem, boolean dashAttack)
	{
		CapabilityItem cap = holdItem.getCapability(ModCapabilities.CAPABILITY_ITEM, null).orElse(null);
		StaticAnimation attackMotion = null;
		
		if (player.getControllingPassenger() != null)
		{
			if (player.isRidingJumpable() && cap != null && cap.canUseOnMount())
			{
				attackMotion = cap.getMountAttackMotion().get(comboCounter);
				comboCounter += 1;
				comboCounter %= cap.getMountAttackMotion().size();
			}
		}
		else
		{
			List<StaticAnimation> combo = null;
			
			if(combo == null)
			{
				combo = (cap != null) ? combo = cap.getAutoAttckMotion(this.playerdata) : CapabilityItem.getBasicAutoAttackMotion();
			}
			int comboSize = combo.size();
			if(dashAttack)
			{
				comboCounter = comboSize - 1;
			}
			else
			{
				comboCounter %= comboSize - 1;
			}
			
			attackMotion = combo.get(comboCounter);
			comboCounter = dashAttack ? 0 : comboCounter+1;
		}
		
		comboHoldCounter = 10;
		
		if(attackMotion != null)
		{
			this.playerdata.getAnimator().playAnimation(attackMotion, 0);
			ModNetworkManager.sendToServer(new CTSPlayAnimation(attackMotion, 0, false, false));
		}
	}
	
	private void reserveSkill(SkillSlot slot)
	{
		this.reservedSkill = slot.getIndex();
		this.skillReserveCounter = 8;
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
	
	public void resetAttackCounter()
	{
		comboCounter = 0;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Mod.EventBusSubscriber(modid = DarkSouls.MOD_ID, value = Dist.CLIENT)
	public static class Events
	{
		static InputManager inputManager;
		
		@SuppressWarnings("resource")
		@SubscribeEvent
		public static void onMouseInput(MouseInputEvent event)
		{
			if (Minecraft.getInstance().player != null && Minecraft.getInstance().screen == null)
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
		public static void onVanillaMouseClick(ClickInputEvent event)
		{
			if (event.isAttack() && ClientEngine.INSTANCE.isBattleMode() && Minecraft.getInstance().hitResult.getType() == RayTraceResult.Type.BLOCK)
			{
				event.setCanceled(true);
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
				event.getMovementInput().forwardImpulse = 0F;
				event.getMovementInput().leftImpulse = 0F;
				event.getMovementInput().up = false;
				event.getMovementInput().down = false;
				event.getMovementInput().left = false;
				event.getMovementInput().right = false;
				event.getMovementInput().jumping = false;
				event.getMovementInput().shiftKeyDown = false;
				((ClientPlayerEntity)event.getPlayer()).sprintTime = -1;
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