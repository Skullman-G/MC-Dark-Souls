package com.skullmangames.darksouls.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModUtil
{
	@SuppressWarnings("unchecked")
	public static <T extends TileEntity> Optional<T> getBlockEntity(World level, BlockPos pos, TileEntityType<T> type)
	{
		TileEntity tileEntity = level.getBlockEntity(pos);
		return tileEntity != null && tileEntity.getType() == type ? Optional.of((T)tileEntity) : Optional.empty();
	}
	
	public static <T extends Object> List<T> readList(PacketBuffer buffer, Function<PacketBuffer, T> function)
	{
		List<T> list = new ArrayList<>();
		int size = buffer.readInt();
		
		for (int i = 0; i < size; i++)
		{
			list.add(function.apply(buffer));
		}
		
		return list;
	}
	
	public static <T extends Object> void writeList(PacketBuffer buffer, List<T> list, BiConsumer<PacketBuffer, T> consumer)
	{
		buffer.writeInt(list.size());
		
		for (T obj : list)
		{
			consumer.accept(buffer, obj);
		}
	}
}
