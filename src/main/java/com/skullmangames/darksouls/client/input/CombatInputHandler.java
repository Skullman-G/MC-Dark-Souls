package com.skullmangames.darksouls.client.input;

import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.input.detector.KeyActionDetector;
import com.skullmangames.darksouls.client.input.detector.KeyActionDetector.Action;
import com.skullmangames.darksouls.client.input.key.ModKeys;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.config.ConfigManager;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSTwoHanding;

import net.minecraft.client.CameraType;
import net.minecraft.world.InteractionHand;

public class CombatInputHandler
{
	private final InputManager im;
	private final KeyActionDetector attackDetector;
	
	private AttackType reservedAttack;
	
	public CombatInputHandler(InputManager im)
	{
		this.im = im;
		
		this.attackDetector = this.im.addAdvancedKeyAction(this.im.options.keyAttack,
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
	
	public void tick()
	{
		if (!this.attackDetector.isDown() && this.reservedAttack != null && this.im.playerCap.canAttack())
		{
			this.im.playerCap.performAttack(this.reservedAttack);
			this.reservedAttack = null;
		}
	}
	
	private void onPerformSkill(Action.Context ctx)
	{
		if (ctx.isDown() && ClientManager.INSTANCE.isCombatModeActive())
		{
			this.im.playerCap.performSkill();
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
					if (this.im.playerCap.canAttack())
					{
						if (this.im.player.isSprinting()) this.im.playerCap.performAttack(AttackType.DASH);
						else this.im.playerCap.performAttack(AttackType.LIGHT);
					}
					else if (this.im.playerCap.enoughStaminaToAct() && this.im.player.getVehicle() == null)
					{
						this.reservedAttack = AttackType.LIGHT;
					}
					break;
				
				case LONG_PRESS:
					if (this.im.playerCap.canAttack())
					{
						this.im.playerCap.performAttack(AttackType.HEAVY);
					}
					else if (this.im.playerCap.enoughStaminaToAct())
					{
						this.reservedAttack = AttackType.HEAVY;
					}
					break;
					
				default:
					break;
			}
		}
	}
}
