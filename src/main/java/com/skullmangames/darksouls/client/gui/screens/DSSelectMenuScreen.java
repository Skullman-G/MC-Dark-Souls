package com.skullmangames.darksouls.client.gui.screens;

import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.input.InputManager;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyConflictContext;

@OnlyIn(Dist.CLIENT)
public class DSSelectMenuScreen extends Screen
{
	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/ds_select_menu.png");
	
	private final EffectRenderingInventoryScreen<?> craftingScreen;
	
	protected final int textureWidth;
	protected final int textureHeight;
	
	protected final int imageWidth;
	protected final int imageHeight;
	
	public DSSelectMenuScreen(EffectRenderingInventoryScreen<?> craftingScreen)
	{
		super(new TextComponent("Select Menu"));
		
		Minecraft minecraft = Minecraft.getInstance();
		
		this.craftingScreen = minecraft.gameMode.hasInfiniteItems() ? new CreativeModeInventoryScreen(minecraft.player)
				: craftingScreen;
		
		this.textureWidth = 126;
		this.textureHeight = 103;
		
		this.imageWidth = 126;
		this.imageHeight = 62;
		
		List<InputConstants.Key> keyOverrides = ClientManager.INSTANCE.inputManager.pressedKeyOverride;
		if (minecraft.options.keyUp.isDown()) keyOverrides.add(minecraft.options.keyUp.getKey());
		if (minecraft.options.keyDown.isDown()) keyOverrides.add(minecraft.options.keyDown.getKey());
		if (minecraft.options.keyLeft.isDown()) keyOverrides.add(minecraft.options.keyLeft.getKey());
		if (minecraft.options.keyRight.isDown()) keyOverrides.add(minecraft.options.keyRight.getKey());
	}
	
	@Override
	protected void init()
	{
		super.init();
		
		int x = this.width / 2;
		int y = this.height / 2;
		int buttonsize = 40;
		
		this.addRenderableWidget(new ImageButton(x - 43, y - 20, buttonsize, buttonsize, 20, 11, 52, TEXTURE_LOCATION, this.textureWidth, this.textureHeight,
				(btn) -> this.minecraft.setScreen(new DSEquipmentScreen())));
		this.addRenderableWidget(new ImageButton(x + 3, y - 20, buttonsize, buttonsize, 66, 11, 52, TEXTURE_LOCATION, this.textureWidth, this.textureHeight,
				(btn) -> this.minecraft.setScreen(this.craftingScreen)));
		
		this.minecraft.options.keyUp.setKeyConflictContext(InputManager.WALK_CONFLICT);
		this.minecraft.options.keyLeft.setKeyConflictContext(InputManager.WALK_CONFLICT);
		this.minecraft.options.keyDown.setKeyConflictContext(InputManager.WALK_CONFLICT);
		this.minecraft.options.keyRight.setKeyConflictContext(InputManager.WALK_CONFLICT);
	}
	
	@Override
	public boolean isPauseScreen()
	{
		return false;
	}
	
	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialticks)
	{
		this.renderBg(poseStack, partialticks, mouseX, mouseY);
		super.render(poseStack, mouseX, mouseY, partialticks);
	}
	
	private void renderBg(PoseStack poseStack, float partialticks, int mouseX, int mouseY)
	{
		RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		GuiComponent.blit(poseStack, x, y, 0, 0, this.imageWidth, this.imageHeight, this.textureWidth, this.textureHeight);
	}
	
	@Override
	public void onClose()
	{
		super.onClose();
		this.minecraft.options.keyUp.setKeyConflictContext(KeyConflictContext.IN_GAME);
		this.minecraft.options.keyLeft.setKeyConflictContext(KeyConflictContext.IN_GAME);
		this.minecraft.options.keyDown.setKeyConflictContext(KeyConflictContext.IN_GAME);
		this.minecraft.options.keyRight.setKeyConflictContext(KeyConflictContext.IN_GAME);
	}
	
	@Override
	public boolean keyPressed(int p_96552_, int p_96553_, int p_96554_)
	{
		InputConstants.Key key = InputConstants.getKey(p_96552_, p_96553_);
		if (this.minecraft.options.keyInventory.isActiveAndMatches(key))
		{
			this.onClose();
			return true;
		}
		else if (this.minecraft.options.keyUp.getKey() == key
				|| this.minecraft.options.keyDown.getKey() == key
				|| this.minecraft.options.keyLeft.getKey() == key
				|| this.minecraft.options.keyRight.getKey() == key)
		{
			KeyMapping.set(key, true);
			return true;
		}
		else return super.keyPressed(p_96552_, p_96553_, p_96554_);
	}
	
	@Override
	public boolean keyReleased(int p_94715_, int p_94716_, int p_94717_)
	{
		InputConstants.Key key = InputConstants.getKey(p_94715_, p_94716_);
		if (this.minecraft.options.keyUp.getKey() == key
				|| this.minecraft.options.keyDown.getKey() == key
				|| this.minecraft.options.keyLeft.getKey() == key
				|| this.minecraft.options.keyRight.getKey() == key)
		{
			KeyMapping.set(key, false);
			return true;
		}
		else return super.keyReleased(p_94715_, p_94716_, p_94717_);
	}
}
