package com.skullmangames.darksouls.client.input;

import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.input.detector.AdvancedKeyActionDetector;
import com.skullmangames.darksouls.client.input.detector.KeyActionDetector.Action;
import com.skullmangames.darksouls.client.input.key.ModKeys;
import com.skullmangames.darksouls.common.capability.entity.EquipLoaded.EquipLoadLevel;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap.PlayerAction;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.config.ConfigManager;
import com.skullmangames.darksouls.core.util.timer.TickTimer;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.packets.client.CTSTwoHanding;
import net.minecraft.client.CameraType;
import net.minecraft.world.InteractionHand;

public class ActionInputHandler
{
	private static final float STAMINA_RECOVERY = 0.7F;
	private static final int MAX_SPRINT_PRESS_COUNT = 5;
	private static final int RESERVE_TIME = 50;
	
	private final InputManager im;
	
	public final AdvancedKeyActionDetector sprintDetector;
	private boolean recoveringStamina;
	
	private PlayerAction reservedAction;
	private final TickTimer reserveActionTimer;
	
	public ActionInputHandler(InputManager im)
	{
		this.im = im;
		
		this.reservedAction = null;
		this.reserveActionTimer = TickTimer.timer()
				.withOnUpdate(this::onReserveTimerUpdate)
				.withOnFinish(this::onReserveTimerFinish);
		
		this.recoveringStamina = false;
		
		this.sprintDetector = this.im.addAdvancedKeyAction(this.im.options.keySprint,
				MAX_SPRINT_PRESS_COUNT, this::onSprintKeyPressed);
		
		this.im.addAdvancedKeyAction(this.im.options.keyAttack,
				ConfigManager.CLIENT_CONFIG.longPressCount.getValue(),
				this::onAttackKeyPressed);
		
		for (int i = 0; i < 9; ++i)
		{
			this.im.addKeyAction(this.im.options.keyHotbarSlots[i], this::hotbarSlotKeyAction);
		}
		
		this.im.addKeyAction(this.im.options.keySwapOffhand, this::onSwapHandKeyPressed);
		this.im.addKeyAction(this.im.options.keyTogglePerspective, this::onTogglePerspectiveKeyPressed);
		this.im.addKeyAction(ModKeys.TOGGLE_COMBAT_MODE, this::onToggleCombatModeKeyPressed);
		this.im.addKeyAction(ModKeys.ATTUNEMENT_SLOT_UP, this::onAttunementSlotUp);
		this.im.addKeyAction(ModKeys.ATTUNEMENT_SLOT_DOWN, this::onAttunementSlotDown);
		this.im.addKeyAction(ModKeys.TARGET_LOCK_ON, this::onTrySelectTarget);
		this.im.addKeyAction(ModKeys.TWO_HANDING, this::onTwoHanding);
		this.im.addKeyAction(ModKeys.PERFORM_SKILL, this::onPerformSkill);
	}
	
	private void onReserveTimerFinish()
	{
		this.reservedAction = null;
	}
	
	private void onReserveTimerUpdate()
	{
		if (this.im.playerCap.canAct())
		{
			this.im.playerCap.performAction(this.reservedAction);
			this.reserveActionTimer.stop();
		}
	}
	
	public void tick()
	{
		if (this.im.player.isSprinting())
		{
			if (this.im.playerCap.getEquipLoadLevel() == EquipLoadLevel.OVERENCUMBERED)
			{
				this.im.player.setSprinting(false);
			}
			else if (!this.im.player.isCreative() && this.im.playerCap.getStamina() <= 0.0F)
			{
				this.im.player.setSprinting(false);
				this.recoveringStamina = true;
			}
		}
		
		this.reserveActionTimer.tick();
	}
	
	private void performOrReserveAction(PlayerAction action)
	{
		if (this.im.playerCap.canAct())
		{
			this.im.playerCap.performAction(action);
			if (this.reserveActionTimer.isTicking())
			{
				this.reserveActionTimer.stop();
			}
		}
		else
		{
			this.reservedAction = action;
			this.reserveActionTimer.start(RESERVE_TIME);
		}
	}
	
	private void onSprintKeyPressed(Action.Context ctx)
	{
		ctx.setOverride(true);

		switch (ctx.getAction())
		{
			case SHORT_PRESS:
				this.performOrReserveAction(PlayerAction.DODGE);
				break;
			
			case LONG_PRESS:
				if (this.im.playerCap.canStartSprinting())
				{
					this.im.player.setSprinting(true);
				}
				break;
				
			case LONG_HOLD:
				if (this.recoveringStamina
						&& (this.im.playerCap.getStamina() / this.im.playerCap.getMaxStamina()) >= STAMINA_RECOVERY)
				{
					if (this.im.playerCap.canStartSprinting())
					{
						this.im.player.setSprinting(true);
					}
					this.recoveringStamina = false;
				}
				break;
				
			case RELEASE:
				this.im.player.setSprinting(false);
				break;
				
			default:
				break;
		}
	}
	
	private void onPerformSkill(Action.Context ctx)
	{
		if (ctx.isDown() && ClientManager.INSTANCE.isCombatModeActive())
		{
			this.performOrReserveAction(PlayerAction.SKILL);
		}
	}
	
	private void onTwoHanding(Action.Context ctx)
	{
		if (ctx.isDown() && ClientManager.INSTANCE.isCombatModeActive())
		{
			boolean value = !this.im.playerCap.isTwohanding();
			ModNetworkManager.sendToServer(new CTSTwoHanding(value));
			this.im.playerCap.setTwoHanding(value);
		}
	}
	
	private void onTrySelectTarget(Action.Context ctx)
	{
		if (ctx.isDown() && ClientManager.INSTANCE.isCombatModeActive()
				&& !this.im.options.getCameraType().isFirstPerson())
		{
			this.im.playerCap.updateTarget();
		}
	}
	
	private void onAttunementSlotUp(Action.Context ctx)
	{
		if (ctx.isDown() && this.im.playerCap.getAttunements().selected > 0)
		{
			this.im.playerCap.getAttunements().selected--;
		}
	}
	
	private void onAttunementSlotDown(Action.Context ctx)
	{
		if (ctx.isDown()
				&& this.im.playerCap.getAttunements().selected < this.im.playerCap.getAttunements().getContainerSize() - 1)
		{
			this.im.playerCap.getAttunements().selected++;
		}
	}
	
	private void hotbarSlotKeyAction(Action.Context ctx)
	{
		ctx.setOverride(this.im.playerCap.isInaction());
	}
	
	private void onSwapHandKeyPressed(Action.Context ctx)
	{
		ItemCapability cap = this.im.playerCap.getHeldItemCapability(InteractionHand.MAIN_HAND);
		ctx.setOverride(this.im.playerCap.isInaction() || (cap != null && !cap.canUsedInOffhand()));
	}
	
	private void onTogglePerspectiveKeyPressed(Action.Context ctx)
	{
		if (ctx.isDown())
		{
			if (this.im.options.getCameraType() == CameraType.THIRD_PERSON_BACK)
			{
				ClientManager.INSTANCE.switchToFirstPerson();
			}
			else
			{
				ClientManager.INSTANCE.switchToThirdPerson();
			}
		}
	}
	
	private void onToggleCombatModeKeyPressed(Action.Context ctx)
	{
		if (ctx.isDown())
		{
			ClientManager.INSTANCE.toggleCombatMode();
		}
	}
	
	private void onAttackKeyPressed(Action.Context ctx)
	{
		if (ClientManager.INSTANCE.isCombatModeActive())
		{
			ctx.setOverride(true);
			
			switch (ctx.getAction())
			{
				case SHORT_PRESS:
					this.performOrReserveAction(PlayerAction.LIGHT_ATTACK);
					break;
				
				case LONG_PRESS:
					this.im.playerCap.performAction(PlayerAction.HEAVY_ATTACK);
					if (this.reserveActionTimer.isTicking())
					{
						this.reserveActionTimer.stop();
					}
					break;
					
				default:
					if (ctx.isDown() && this.reserveActionTimer.isTicking())
					{
						this.reserveActionTimer.stop();
					}
					break;
			}
		}
	}
}
