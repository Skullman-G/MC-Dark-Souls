package com.skullmangames.darksouls.client.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;

import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.gui.screens.DSEquipmentScreen;
import com.skullmangames.darksouls.client.gui.screens.DSSelectMenuScreen;
import com.skullmangames.darksouls.client.input.detector.AdvancedKeyActionDetector;
import com.skullmangames.darksouls.client.input.detector.KeyActionDetector.Action;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.common.capability.entity.EquipLoaded.EquipLoadLevel;
import com.skullmangames.darksouls.network.client.CTSPerformDodge.DodgeType;

import net.minecraft.client.CameraType;
import net.minecraft.client.player.Input;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.settings.IKeyConflictContext;

public class MovementInputHandler
{
	private static final float STAMINA_RECOVERY = 0.7F;
	private static final int MAX_SPRINT_PRESS_COUNT = 5;
	
	private final InputManager im;
	private final AdvancedKeyActionDetector sprintDetector;
	private boolean recoveringStamina;
	
	private final GLFWCursorPosCallbackI callback;
	private double tracingMouseX;
	private double tracingMouseY;
	
	public MovementInputHandler(InputManager im)
	{
		this.im = im;
		
		this.callback = (handle, x, y) -> {tracingMouseX = x; tracingMouseY = y;};
		this.recoveringStamina = false;
		
		this.sprintDetector = this.im.addAdvancedKeyAction(this.im.options.keySprint,
				MAX_SPRINT_PRESS_COUNT, this::onSprintKeyPressed);
		
		this.im.addGuiKeyAction(this.im.options.keyUp, this::walkWhileUIOpen);
		this.im.addGuiKeyAction(this.im.options.keyDown, this::walkWhileUIOpen);
		this.im.addGuiKeyAction(this.im.options.keyLeft, this::walkWhileUIOpen);
		this.im.addGuiKeyAction(this.im.options.keyRight, this::walkWhileUIOpen);
		
		IKeyConflictContext walkConflict = new IKeyConflictContext()
		{
			@Override
			public boolean isActive()
			{
				return true;
			}
			
			@Override
			public boolean conflicts(IKeyConflictContext other)
			{
				return true;
			}
		};
		
		this.im.options.keyUp.setKeyConflictContext(walkConflict);
		this.im.options.keyDown.setKeyConflictContext(walkConflict);
		this.im.options.keyLeft.setKeyConflictContext(walkConflict);
		this.im.options.keyRight.setKeyConflictContext(walkConflict);
	}
	
	public void tick()
	{
		if (this.im.playerCap == null) return;
		
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
	}
	
	private void walkWhileUIOpen(Action.Context ctx)
	{
		if (this.im.minecraft.screen instanceof DSEquipmentScreen || this.im.minecraft.screen instanceof DSSelectMenuScreen)
		{
			ctx.getMapping().setDown(ctx.isDown());
		}
	}
	
	private void onSprintKeyPressed(Action.Context ctx)
	{
		ctx.setOverride(true);

		switch (ctx.getAction())
		{
			case SHORT_PRESS:
				if (this.im.playerCap.canDodge())
				{
					DodgeType dodgeType = DodgeType.JUMP_BACK;
					if (this.im.playerCap.getTarget() != null || this.im.playerCap.shouldShoulderSurf())
					{
						if (this.im.isKeyDown(this.im.options.keyUp)) dodgeType = DodgeType.FORWARD;
						else if (this.im.isKeyDown(this.im.options.keyDown)) dodgeType = DodgeType.BACK;
						else if (this.im.isKeyDown(this.im.options.keyLeft)) dodgeType = DodgeType.LEFT;
						else if (this.im.isKeyDown(this.im.options.keyRight)) dodgeType = DodgeType.RIGHT;
					}
					else if (this.im.isKeyDown(this.im.options.keyUp)
							|| this.im.isKeyDown(this.im.options.keyDown)
							|| this.im.isKeyDown(this.im.options.keyLeft)
							|| this.im.isKeyDown(this.im.options.keyRight)) dodgeType = DodgeType.FORWARD;
					this.im.playerCap.performDodge(dodgeType);
				}
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
	
	public void handleMovement(Input in)
	{
		if(this.im.playerCap == null) return;
		
		EntityState playerState = this.im.playerCap.getEntityState();
		
		// Mouse Movement
		if (this.im.options.getCameraType() == CameraType.FIRST_PERSON && playerState.isRotationLocked() && this.im.player.isAlive())
		{
			GLFW.glfwSetCursorPosCallback(this.im.minecraft.getWindow().getWindow(), this.callback);
			this.im.minecraft.mouseHandler.xpos = this.tracingMouseX;
			this.im.minecraft.mouseHandler.ypos = this.tracingMouseY;
		}
		else
		{
			this.tracingMouseX = this.im.minecraft.mouseHandler.xpos();
			this.tracingMouseY = this.im.minecraft.mouseHandler.ypos();
			this.im.minecraft.mouseHandler.setup(this.im.minecraft.getWindow().getWindow());
		}
		
		// Keyboard Movement
		if (this.im.playerCap.getTarget() != null)
		{
			float forward = in.forwardImpulse;
			float left = in.leftImpulse;
			float rot = 0.0F;
			
			boolean w = in.up;
			boolean s = in.down;
			boolean a = in.left;
			boolean d = in.right;
			
			if (!this.im.playerCap.shouldShoulderSurf()
				&& (this.sprintDetector.isLongPress() || this.im.player.getVehicle() != null))
			{
				rot = w && !s && a && !d ? 45 : !w && !s && a && !d ? 90
						: !w && s && a && !d ? 135 : !w && s && !a && !d ? 180
						: !w && s && !a && d ? 225 : !w && !s && !a && d ? 270
						: w && !s && !a && d ? 315 : 0;
				
				forward = rot == 0.0F ? in.forwardImpulse
						: rot == 180.0F ? -in.forwardImpulse
						: rot == 90.0F ? in.leftImpulse
						: rot == 270.0F ? -in.leftImpulse
						: rot == 45.0F ? in.forwardImpulse * 10
						: rot == 135.0F ? -in.forwardImpulse * 10
						: rot == 225.0F ? -in.forwardImpulse * 10
						: rot == 315.0F ? in.forwardImpulse * 10
						: 0.0F;
				
				left = rot == 45.0F ? in.leftImpulse
						: rot == 135.0F ? -in.leftImpulse
						: rot == 225.0F ? -in.leftImpulse
						: rot == 315.0F ? in.leftImpulse
						: 0.0F;
			}
			
			Entity target = this.im.playerCap.getTarget();
			double dx = target.getX() - this.im.player.getX();
			double dz = target.getZ() - this.im.player.getZ();
			double dy = target.getY() + 0.6D * target.getBbHeight() - this.im.player.getY() - this.im.player.getEyeHeight();
			float degree = (float) (Math.atan2(dz, dx) * (180D / Math.PI)) - rot - 90.0F;
			float xDegree = (float) (Math.atan2(Math.sqrt(dx * dx + dz * dz), dy) * (180D / Math.PI)) - 90.0F;
			if (!playerState.isRotationLocked() || this.im.player.getVehicle() != null)
			{
				this.im.playerCap.rotateTo(degree, 60, false);
				this.im.player.xRot = xDegree;
			}
			in.forwardImpulse = forward;
			in.leftImpulse = left;
			
		}
		else if (!this.im.playerCap.shouldShoulderSurf() && this.im.options.getCameraType() != CameraType.FIRST_PERSON)
		{
			if (this.im.player.getVehicle() != null)
			{
				float forward = in.forwardImpulse;
				float left = in.leftImpulse;
				float rot = this.im.player.yRot;
				
				boolean w = in.up;
				boolean s = in.down;
				boolean a = in.left;
				boolean d = in.right;
				
				float pivot = ClientManager.INSTANCE.mainCamera.getPivotXRot(1.0F);
				
				if (w || a || s || d)
				{
					rot = pivot;
					rot -= w && !s && a && !d ? 45 : !w && !s && a && !d ? 90
							: !w && s && a && !d ? 135 : !w && s && !a && !d ? 180
							: !w && s && !a && d ? 225 : !w && !s && !a && d ? 270
							: w && !s && !a && d ? 315 : 0;
				}
				
				forward = rot == pivot ? in.forwardImpulse
						: rot == pivot - 180.0F ? -in.forwardImpulse
						: rot == pivot - 90.0F ? in.leftImpulse
						: rot == pivot - 270.0F ? -in.leftImpulse
						: rot == pivot - 45.0F ? in.forwardImpulse * 10
						: rot == pivot - 135.0F ? -in.forwardImpulse * 10
						: rot == pivot - 225.0F ? -in.forwardImpulse * 10
						: rot == pivot - 315.0F ? in.forwardImpulse * 10
						: 0.0F;
				
				left = rot == pivot - 45.0F ? in.leftImpulse
						: rot == pivot - 135.0F ? -in.leftImpulse
						: rot == pivot - 225.0F ? -in.leftImpulse
						: rot == pivot - 315.0F ? in.leftImpulse
						: 0.0F;
				
				if (!playerState.isRotationLocked() || this.im.player.getVehicle() != null) this.im.playerCap.rotateTo(rot, 60, false);
				in.forwardImpulse = forward;
				in.leftImpulse = left;
			}
			else
			{
				boolean w = in.up;
				boolean s = in.down;
				boolean a = in.left;
				boolean d = in.right;
				float rot = this.im.player.yRot;
				
				if (w || a || s || d)
				{
					rot = ClientManager.INSTANCE.mainCamera.getPivotXRot(1.0F);
					rot -= w && !s && a && !d ? 45 : !w && !s && a && !d ? 90
							: !w && s && a && !d ? 135 : !w && s && !a && !d ? 180
							: !w && s && !a && d ? 225 : !w && !s && !a && d ? 270
							: w && !s && !a && d ? 315 : 0;
				}
				
				float forward = w ? in.forwardImpulse
						: s ? -in.forwardImpulse
						: !w && !s && a ? in.leftImpulse
						: !w && !s && d ? -in.leftImpulse
						: 0;
				
				float r = Mth.rotLerp(0.5F, this.im.player.yHeadRot, rot);
				
				if (!playerState.isRotationLocked())
				{
					this.im.player.yRot = r;
					this.im.player.yBodyRot = r;
					this.im.player.yHeadRot = r;
				}
				if (this.im.playerCap.canMove())
				{
					in.forwardImpulse = forward;
				}
				else in.forwardImpulse = 0.0F;
				in.leftImpulse = 0.0F;
			}
		}
		
		if (this.im.playerCap.isBlocking())
		{
			float mul = this.im.player.isCrouching() ? 5F : 20F;
			in.leftImpulse *= mul;
			in.forwardImpulse *= mul;
		}
		
		if (!this.im.playerCap.canMove() && this.im.player.isAlive())
		{
			in.forwardImpulse = 0.0F;
			in.leftImpulse = 0.0F;
			in.up = false;
			in.down = false;
			in.left = false;
			in.right = false;
			in.jumping = false;
			in.shiftKeyDown = false;
			this.im.player.sprintTime = -1;
		}
	}
}
