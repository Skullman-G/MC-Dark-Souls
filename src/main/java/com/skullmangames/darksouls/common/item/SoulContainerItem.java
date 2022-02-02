package com.skullmangames.darksouls.common.item;

import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ModSoundEvents;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SoulContainerItem extends Item implements IHaveDarkSoulsUseAction
{
	private final int amount;
	
	public SoulContainerItem(int amount, Properties properties)
	{
		super(properties);
		this.amount = amount;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		return ItemUser.startUsing(this, level, player, hand);
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level level, LivingEntity livingentity)
	{
		livingentity.playSound(ModSoundEvents.SOUL_CONTAINER_FINISH.get(), 0.5F, 1.0F);
		if (!level.isClientSide && this.getAmount() > 0)
		{
			PlayerData<?> playerdata = (PlayerData<?>)livingentity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerdata != null)
			{
				playerdata.raiseSouls(this.getAmount());
			}
		}
		if (!(livingentity instanceof Player) || !((Player)livingentity).getAbilities().instabuild)
		{
			itemstack.shrink(1);
        }
		return itemstack;
	}
	
	@Override
	public void onUseTick(Level level, LivingEntity livingentity, ItemStack itemstack, int durationremaining)
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
