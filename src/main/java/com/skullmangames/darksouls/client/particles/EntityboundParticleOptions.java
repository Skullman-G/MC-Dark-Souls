package com.skullmangames.darksouls.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

public class EntityboundParticleOptions implements ParticleOptions
{
	@SuppressWarnings("deprecation")
	public static final ParticleOptions.Deserializer<EntityboundParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<EntityboundParticleOptions>()
	{
		public EntityboundParticleOptions fromCommand(ParticleType<EntityboundParticleOptions> type, StringReader stringReader) throws CommandSyntaxException
		{
			String[] strings = stringReader.getString().split(" ");
			return new EntityboundParticleOptions(type, Integer.valueOf(strings[strings.length - 1]));
		}

		public EntityboundParticleOptions fromNetwork(ParticleType<EntityboundParticleOptions> type, FriendlyByteBuf buf)
		{
			return new EntityboundParticleOptions(type, buf.readInt());
		}
	};

	private final ParticleType<EntityboundParticleOptions> type;
	private final int entityId;

	public static Codec<EntityboundParticleOptions> codec(ParticleType<EntityboundParticleOptions> type)
	{
		return Codec.INT.xmap((i) ->
		{
			return new EntityboundParticleOptions(type, i);
		}, (options) ->
		{
			return options.entityId;
		});
	}
	
	public EntityboundParticleOptions(ParticleType<EntityboundParticleOptions> type, int entityId)
	{
		this.type = type;
		this.entityId = entityId;
	}
	
	public int getEntityId()
	{
		return this.entityId;
	}
	
	@Override
	public ParticleType<?> getType()
	{
		return this.type;
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buf)
	{
		buf.writeInt(this.entityId);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String writeToString()
	{
		return Registry.PARTICLE_TYPE.getKey(this.getType()).toString() + this.entityId;
	}
}
