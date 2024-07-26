package com.skullmangames.darksouls.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.util.math.ModMath;

import net.minecraft.Util;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class YouDiedScreen extends Screen
{
	private int currentTick;
	private int lastTick;
	private float renderTick;
	private boolean fadeout;
	
	public YouDiedScreen()
	{
		super(NarratorChatListener.NO_TITLE);
	}
	
	@Override
	protected void init()
	{
		super.init();
		this.currentTick = 0;
		this.lastTick = 0;
	}
	
	@Override
	public boolean isPauseScreen()
	{
		return false;
	}
	
	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialticks)
	{
		int x = this.width / 2;
		int y = this.height / 2;
		
		int alpha = (int)Math.round(this.animatedValue(50D, 50D, 0, 200D));
		int textAlpha = (int)Math.round(this.animatedValue(60D, 50D, 10, 245D));
		int blockerAlpha = (int)Math.round(this.animatedValue(150D, 20D, 10, 245D))
				- (int)Math.round(this.animatedValue(this.fadeout ? 250D : Double.MAX_VALUE, 20D, 0, 245D));
		float scale = (float)this.animatedValue(50D, 50D, 1D, 1.5D);
		if (this.renderTick < 200D)
		{
			fill(poseStack, 0, y - 15, this.width, y + 15, 0x0d0d0d | alpha << 24);
			poseStack.pushPose();
			poseStack.scale(scale, scale, 1.0F);
			this.font.draw(poseStack, "YOU DIED", (x - this.font.width("YOU DIED") / 2F * scale) / scale,
					(y + 3F - this.font.lineHeight / 2F * scale) / scale, 0xb30000 | textAlpha << 24);
			poseStack.popPose();
		}
		fill(poseStack, 0, 0, this.width, this.height, 0x000000 | blockerAlpha << 24);
		
		if (Util.getMillis() % 10 == 0) this.tick();
		
		this.renderTick += partialticks;
		
		if (this.fadeout && blockerAlpha <= 10)
		{
			this.minecraft.setScreen((Screen)null);
		}
	}
	
	private double animatedValue(double animStart, double animLength, double valueStart, double valueStop)
	{
		return valueStart + ModMath.saturate((this.renderTick - animStart) / animLength) * valueStop;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (this.ticksPassed(67))
		{
			this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(ModSoundEvents.GENERIC_YOU_DIED.get(), 1.0F));
		}
		if (this.ticksPassed(300))
		{
			this.minecraft.player.respawn();
		}
		if (this.currentTick > 300 && !this.minecraft.player.isDeadOrDying())
		{
			this.fadeout = true;
		}
		
		this.lastTick = this.currentTick;
		this.currentTick++;
	}
	
	private boolean ticksPassed(int ticks)
	{
		return this.currentTick >= ticks && this.lastTick < ticks;
	}
	
	@Override
	public boolean shouldCloseOnEsc()
	{
		return false;
	}
}
