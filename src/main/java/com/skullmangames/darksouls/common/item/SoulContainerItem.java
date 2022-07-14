package com.skullmangames.darksouls.common.item;

import com.skullmangames.darksouls.common.entity.SoulEntity;
import com.skullmangames.darksouls.core.init.ModSoundEvents;

import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SoulContainerItem extends Item implements IHaveDarkSoulsUseAction
{
	private final int amount;
	
	public SoulContainerItem(int amount, Properties properties)
	{
		super(properties);
		this.amount = amount;
	}
	
	@Override
	public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand)
	{
		return ItemUser.startUsing(this, level, player, hand);
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, World level, LivingEntity livingentity)
	{
		livingentity.playSound(ModSoundEvents.SOUL_CONTAINER_FINISH.get(), 0.5F, 1.0F);
		if (!level.isClientSide && this.getAmount() > 0)
		{
			float rot = Math.abs(livingentity.yRot);
			double x;
			double z;
			if (rot <= 90.0F)
			{
				x = 0.5D * ((45 - rot) / 45);
				if (rot <= 45.0F) z = 0.5D * (rot / 45);
				else z = 0.5D * ((90 - rot) / 45);
			}
			else
			{
				x = 0.5D * ((rot - 180) / 45);
				if (rot <= 180.0F) z = 0.5D * ((90 - rot) / 45);
				else z = 0.5D * ((360 - rot) / 45);
			}
			level.addFreshEntity(new SoulEntity(livingentity.level,
					livingentity.getX() + x,
					livingentity.getY() + livingentity.getEyeHeight(),
					livingentity.getZ() + z,
					this.getAmount()));
		}
		if (!(livingentity instanceof PlayerEntity) || !((PlayerEntity)livingentity).abilities.instabuild)
		{
			itemstack.shrink(1);
        }
		return itemstack;
	}
	
	@Override
	public void onUseTick(World level, LivingEntity livingentity, ItemStack itemstack, int durationremaining)
	{
		ItemUser.triggerItemUseEffects(livingentity, itemstack, this, durationremaining);
	}

	@Override
	public DarkSoulsUseAction getDarkSoulsUseAnimation()
	{
		return DarkSoulsUseAction.SOUL_CONTAINER;
	}

	@Override
	public SoundEvent getUseSound()
	{
		return ModSoundEvents.SOUL_CONTAINER_USE.get();
	}
	
	@Override
	public int getUseDuration(ItemStack itemstack)
	{
		return 32;
	}
	
	public int getAmount()
	{
		return this.amount;
	}
}
