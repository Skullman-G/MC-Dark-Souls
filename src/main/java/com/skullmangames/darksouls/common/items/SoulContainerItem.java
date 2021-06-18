package com.skullmangames.darksouls.common.items;

import com.skullmangames.darksouls.common.entities.HumanityData;
import com.skullmangames.darksouls.core.init.SoundEventInit;

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
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
	{
		return ItemUser.startUsing(this, world, player, hand);
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, World world, LivingEntity livingentity)
	{
		livingentity.playSound(SoundEventInit.SOUL_CONTAINER_FINISH.get(), 0.5F, 1.0F);
		if (!world.isClientSide && this.getHumanity() != 0)
		{
			HumanityData.get(livingentity).raiseValue(this.getHumanity());
			livingentity.heal(livingentity.getMaxHealth() - livingentity.getHealth());
		}
		if (!(livingentity instanceof PlayerEntity) || !((PlayerEntity)livingentity).abilities.instabuild)
		{
			itemstack.shrink(1);
        }
		return itemstack;
	}
	
	@Override
	public void onUseTick(World world, LivingEntity livingentity, ItemStack itemstack, int durationremaining)
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
		return SoundEventInit.SOUL_CONTAINER_USE.get();
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
