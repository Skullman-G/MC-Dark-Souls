package com.skullmangames.darksouls.common.item;

import com.skullmangames.darksouls.common.entity.TerracottaVase;
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

public class TerracottaVaseItem extends Item
{
	public TerracottaVaseItem(Properties properties)
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
			AABB aabb = ModEntities.TERRACOTTA_VASE.get().getDimensions().makeBoundingBox(vec3.x(), vec3.y(), vec3.z());
			if (level.noCollision((Entity) null, aabb) && level.getEntities((Entity) null, aabb).isEmpty())
			{
				if (level instanceof ServerLevel)
				{
					ServerLevel serverlevel = (ServerLevel) level;
					TerracottaVase vase = ModEntities.TERRACOTTA_VASE.get().create(serverlevel, itemstack.getTag(),
							(Component) null, ctx.getPlayer(), blockpos, MobSpawnType.SPAWN_EGG, true, true);
					if (vase == null)
					{
						return InteractionResult.FAIL;
					}

					float f = (float) Mth.floor((Mth.wrapDegrees(ctx.getRotation() - 180.0F) + 22.5F) / 45.0F)
							* 45.0F;
					vase.moveTo(vase.getX(), vase.getY(), vase.getZ(), f, 0.0F);
					serverlevel.addFreshEntityWithPassengers(vase);
					level.playSound((Player) null, vase.getX(), vase.getY(), vase.getZ(),
							SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
					level.gameEvent(ctx.getPlayer(), GameEvent.ENTITY_PLACE, vase);
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
