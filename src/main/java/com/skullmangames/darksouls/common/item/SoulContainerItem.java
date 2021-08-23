package com.skullmangames.darksouls.common.item;

import com.skullmangames.darksouls.common.entity.ModEntityDataManager;
import com.skullmangames.darksouls.core.init.SoundEvents;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class SoulContainerItem extends DescriptionItem implements IHaveDarkSoulsUseAction
{
	public SoulContainerItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand)
	{
		return ItemUser.startUsing(this, level, player, hand);
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, World level, LivingEntity livingentity)
	{
		livingentity.playSound(SoundEvents.SOUL_CONTAINER_FINISH, 0.5F, 1.0F);
		if (!level.isClientSide && this.getHumanity() != 0)
		{
			ModEntityDataManager.raiseHumanity(livingentity, this.getHumanity());
			livingentity.heal(livingentity.getMaxHealth() - livingentity.getHealth());
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
		return SoundEvents.SOUL_CONTAINER_USE;
	}
	
	@Override
	public int getUseDuration(ItemStack itemstack)
	{
		return 32;
	}
	
	public int getHumanity()
	{
		return 0;
	}
}
