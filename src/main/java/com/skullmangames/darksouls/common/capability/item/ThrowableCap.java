package com.skullmangames.darksouls.common.capability.item;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Animations;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ThrowableCap extends ItemCapability
{
	private final BiFunction<Level, LivingEntity, ThrowableItemProjectile> projectile;
	private final Supplier<SoundEvent> throwSound;
	
	public ThrowableCap(Item item, BiFunction<Level, LivingEntity, ThrowableItemProjectile> projectile, Supplier<SoundEvent> throwSound)
	{
		super(item);
		this.projectile = projectile;
		this.throwSound = throwSound;
		
	}
	
	public void use(LivingCap<?> cap)
	{
		cap.playAnimationSynchronized(Animations.BIPED_THROW.get(), 0.0F);
	}
	
	public void spawnProjectile(LivingCap<?> cap)
	{
		cap.playSound(this.throwSound.get());
		LivingEntity entity = cap.getOriginalEntity();
		ThrowableItemProjectile projectile = this.projectile.apply(entity.level, entity);
		ItemStack itemStack = entity.getMainHandItem();
		projectile.setItem(itemStack);
		projectile.shootFromRotation(entity, cap.getXRot(), cap.getYRot(), 0.0F, 1.5F, 1.0F);
		entity.level.addFreshEntity(projectile);
		
		if (entity instanceof Player player)
		{
			player.awardStat(Stats.ITEM_USED.get(this.orgItem));
			if (!player.getAbilities().instabuild)
			{
				itemStack.shrink(1);
			}
		}
	}
}
