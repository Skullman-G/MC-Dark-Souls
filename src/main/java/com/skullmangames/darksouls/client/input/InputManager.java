package com.skullmangames.darksouls.client.input;

import java.util.HashMap;
import java.util.Map;
import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.platform.InputConstants;
import com.skullmangames.darksouls.client.input.detector.AdvancedKeyActionDetector;
import com.skullmangames.darksouls.client.input.detector.KeyActionDetector;
import com.skullmangames.darksouls.client.input.detector.KeyActionDetector.Action;
import com.skullmangames.darksouls.client.input.key.ModKeys;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

@OnlyIn(Dist.CLIENT)
public class InputManager
{	
	private final Map<KeyMapping, KeyActionDetector> keyFunctionMap;
	private final Map<KeyMapping, KeyActionDetector> guiKeyFunctionMap;
	
	protected LocalPlayer player;
	protected LocalPlayerCap playerCap;
	private KeyBindingMap keyHash;
	protected final Minecraft minecraft;
	public final Options options;
	
	private final MouseInputHandler mouseHandler;
	
	public final ActionInputHandler actionHandler;
	private final MovementInputHandler movementHandler;
	private final UIInputHandler uiHandler;
	
	public InputManager()
	{
		this.minecraft = Minecraft.getInstance();
		this.options = this.minecraft.options;
		
		this.keyFunctionMap = new HashMap<KeyMapping, KeyActionDetector>();
		this.guiKeyFunctionMap = new HashMap<KeyMapping, KeyActionDetector>();
		
		this.addKeyAction(ModKeys.VISIBLE_HITBOXES, this::toggleRenderCollision);
		
		try
		{
			this.keyHash = (KeyBindingMap)ObfuscationReflectionHelper.findField(KeyMapping.class, "f_90810_").get(null);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		
		this.actionHandler = new ActionInputHandler(this);
		this.movementHandler = new MovementInputHandler(this);
		this.uiHandler = new UIInputHandler(this);
		
		this.mouseHandler = new MouseInputHandler(this.minecraft);
		this.mouseHandler.setup(this.minecraft.getWindow().getWindow());
		this.minecraft.mouseHandler = this.mouseHandler;
	}
	
	public void setGamePlayer(LocalPlayerCap playerCap)
	{
		this.player = playerCap.getOriginalEntity();
		this.playerCap = playerCap;
	}
	
	public AdvancedKeyActionDetector addAdvancedKeyAction(KeyMapping key, int longPress, KeyActionDetector.Action action)
	{
		AdvancedKeyActionDetector detector = new AdvancedKeyActionDetector(key, longPress, action);
		this.keyFunctionMap.put(key, detector);
		return detector;
	}
	
	public KeyActionDetector addKeyAction(KeyMapping key, KeyActionDetector.Action action)
	{
		KeyActionDetector detector = new KeyActionDetector(key, action);
		this.keyFunctionMap.put(key, detector);
		return detector;
	}
	
	public KeyActionDetector addGuiKeyAction(KeyMapping key, KeyActionDetector.Action action)
	{
		KeyActionDetector detector = new KeyActionDetector(key, action);
		this.guiKeyFunctionMap.put(key, detector);
		return detector;
	}
	
	/***
	 * Cancel the scrolling action during player actions. Making the player unable to swap items during attacks by scrolling.
	 * @return whether scrolling should be cancelled or not.
	 */
	public boolean shouldCancelScrolling()
	{
		return this.playerCap != null && this.playerCap.isInaction() && this.minecraft.screen == null;
	}
	
	public boolean shouldShowItemInfo()
	{
		return this.uiHandler.shouldShowItemInfo();
	}
	
	public void doActionForKey(InputConstants.Type type, int key, int action)
	{
		if (action == GLFW.GLFW_REPEAT) return;
		
		InputConstants.Key input = type.getOrCreate(key);
		for (KeyMapping keybinding : this.keyHash.lookupAll(input))
		{
			if(this.minecraft.screen == null && this.minecraft.getOverlay() == null
					&& this.keyFunctionMap.containsKey(keybinding))
			{
				this.keyFunctionMap.get(keybinding).glfwKeyAction(action);
			}
			else if (this.guiKeyFunctionMap.containsKey(keybinding))
			{
				this.guiKeyFunctionMap.get(keybinding).glfwKeyAction(action);
			}
		}
	}
	
	private void toggleRenderCollision(Action.Context ctx)
	{
		if (ctx.isDown()) return;
		this.minecraft.getEntityRenderDispatcher().setRenderHitBoxes(!this.minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes());
	}
	
	public void tick()
	{
		this.keyFunctionMap.values().forEach((d) -> d.tick());
		this.guiKeyFunctionMap.values().forEach((d) -> d.tick());
		
		this.actionHandler.tick();
		
		if (this.minecraft.isPaused()) this.minecraft.mouseHandler.setup(this.minecraft.getWindow().getWindow());
	}
	
	public void handleMovement(Input in)
	{
		this.movementHandler.handleMovement(in);
	}
	
	public void setKeyBind(KeyMapping key, boolean setter)
	{
		KeyMapping.set(key.getKey(), setter);
	}
}