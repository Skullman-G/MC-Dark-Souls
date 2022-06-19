package com.skullmangames.darksouls.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;

public class QuestFlags
{
	private final Map<UUID, Byte> map;
	
	public static final EntityDataSerializer<QuestFlags> SERIALIZER = new EntityDataSerializer<QuestFlags>()
	{
		public void write(FriendlyByteBuf buf, QuestFlags questFlags)
		{
			buf.writeMap(questFlags.map, (b, uuid) -> b.writeUUID(uuid), (b, flags) -> b.writeByte(flags));
		}

		public QuestFlags read(FriendlyByteBuf buf)
		{
			return new QuestFlags(buf.readMap((b) -> b.readUUID(), (b) -> b.readByte()));
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
	
	public void save(CompoundTag nbt)
	{
		this.map.forEach((entity, flags) ->
		{
			nbt.putByte("QuestFlags"+entity, flags);
		});
	}
}
