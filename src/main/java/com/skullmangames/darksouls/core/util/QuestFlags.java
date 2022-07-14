package com.skullmangames.darksouls.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;

public class QuestFlags
{
	private final Map<UUID, Byte> map;
	
	public static final IDataSerializer<QuestFlags> SERIALIZER = new IDataSerializer<QuestFlags>()
	{
		public void write(PacketBuffer buf, QuestFlags questFlags)
		{
			buf.writeInt(questFlags.map.size());
			for (Map.Entry<UUID, Byte> entry : questFlags.map.entrySet())
			{
				buf.writeUUID(entry.getKey());
				buf.writeByte(entry.getValue());
			}
		}

		public QuestFlags read(PacketBuffer buf)
		{
			Map<UUID, Byte> map = new HashMap<>();
			int size = buf.readInt();
			for (int i = 0; i < size; i++)
			{
				map.put(buf.readUUID(), buf.readByte());
			}
			return new QuestFlags(map);
		}

		public QuestFlags copy(QuestFlags questFlags)
		{
			Map<UUID, Byte> copy = new HashMap<>();
			copy.putAll(questFlags.map);
			return new QuestFlags(copy);
		}
	};
	
	public QuestFlags()
	{
		this.map = new HashMap<>();
	}
	
	public QuestFlags(Map<UUID, Byte> map)
	{
		this.map = map;
	}
	
	public QuestFlags setFlag(UUID entity, int index, boolean value)
	{
		Map<UUID, Byte> copy = new HashMap<>();
		copy.putAll(this.map);
		byte flags = copy.getOrDefault(entity, (byte)0);
		if (value) flags |= 1 << index;
		else flags &= ~(1 << index);
		copy.put(entity, flags);
		return new QuestFlags(copy);
	}
	
	public boolean getFlag(UUID entity, int index)
	{
		return (this.map.getOrDefault(entity, (byte)0) & 1 << index) != 0;
	}
	
	public void save(CompoundNBT nbt)
	{
		this.map.forEach((entity, flags) ->
		{
			nbt.putByte("QuestFlags"+entity, flags);
		});
	}
}
