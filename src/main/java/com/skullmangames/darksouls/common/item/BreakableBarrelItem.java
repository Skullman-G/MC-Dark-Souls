package com.skullmangames.darksouls.common.item;

import com.skullmangames.darksouls.common.entity.BreakableBarrel;
import com.skullmangames.darksouls.core.init.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BreakableBarrelItem extends Item
{
	public BreakableBarrelItem(Properties properties)
	{
		super(properties);
	}
	
	public InteractionResult useOn(UseOnContext ctx)
	{
		Direction direction = ctx.getClickedFace();
		if (direction == Direction.DOWN)
		{
			return InteractionResult.FAIL;
		}
		else
		{
			Level level = ctx.getLevel();
			BlockPlaceContext blockplacecontext = new BlockPlaceContext(ctx);
			BlockPos blockpos = blockplacecontext.getClickedPos();
			ItemStack itemstack = ctx.getItemInHand();
			Vec3 vec3 = Vec3.atBottomCenterOf(blockpos);
			AABB aabb = ModEntities.BREAKABLE_BARREL.get().getDimensions().makeBoundingBox(vec3.x(), vec3.y(), vec3.z());
			if (level.noCollision((Entity) null, aabb) && level.getEntities((Entity) null, aabb).isEmpty())
			{
				if (level instanceof ServerLevel)
				{
					ServerLevel serverlevel = (ServerLevel) level;
					BreakableBarrel barrel = ModEntities.BREAKABLE_BARREL.get().create(serverlevel, itemstack.getTag(),
							(Component) null, ctx.getPlayer(), blockpos, MobSpawnType.SPAWN_EGG, true, true);
					if (barrel == null)
					{
						return InteractionResult.FAIL;
					}

					float f = (float) Mth.floor((Mth.wrapDegrees(ctx.getRotation() - 180.0F) + 22.5F) / 45.0F)
							* 45.0F;
					barrel.moveTo(vec3.x(), vec3.y(), vec3.z(), f, 0.0F);
					serverlevel.addFreshEntityWithPassengers(barrel);
					level.playSound((Player) null, barrel.getX(), barrel.getY(), barrel.getZ(),
							SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
					level.gameEvent(ctx.getPlayer(), GameEvent.ENTITY_PLACE, barrel);
				}

				itemstack.shrink(1);
				return InteractionResult.sidedSuccess(level.isClientSide);
			}
			else
			{
				return InteractionResult.FAIL;
			}
		}
	}
}
