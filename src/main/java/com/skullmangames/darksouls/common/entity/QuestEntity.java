package com.skullmangames.darksouls.common.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.skullmangames.darksouls.core.util.QuestFlags;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class QuestEntity extends PathfinderMob
{
	protected static final EntityDataAccessor<QuestFlags> DATA_QUEST_FLAGS = SynchedEntityData.defineId(QuestEntity.class, QuestFlags.SERIALIZER);

	public QuestEntity(EntityType<? extends PathfinderMob> type, Level level)
	{
		super(type, level);
	}
	
	public void onFinishChat(ServerPlayer player, String location) {}
	
	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.ATTACK_DAMAGE, 1.0D);
	}
	
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type, SpawnGroupData data, CompoundTag nbt)
	{
		data = super.finalizeSpawn(level, difficulty, type, data, nbt);
		this.populateDefaultEquipmentSlots(difficulty);
		return data;
	}

	@Override
	protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty)
	{
		for (EquipmentSlot slot : EquipmentSlot.values())
		{
			ItemStack itemstack = this.getItemBySlot(slot);
			if (itemstack.isEmpty())
			{
				Item item = getEquipmentForSlot(slot);
				if (item != null)
				{
					this.setItemSlot(slot, new ItemStack(item));
					this.setDropChance(slot, 0);
				}
			}
		}
	}
	
	protected Item getEquipmentForSlot(EquipmentSlot slot)
	{
		return Items.AIR;
	}
	
	@Override
	protected void defineSynchedData()
	{
		super.defineSynchedData();
		this.entityData.define(DATA_QUEST_FLAGS, new QuestFlags());
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt)
	{
		super.addAdditionalSaveData(nbt);
		this.entityData.get(DATA_QUEST_FLAGS).save(nbt);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt)
	{
		super.readAdditionalSaveData(nbt);
		Map<UUID, Byte> questFlags = new HashMap<>();
		for (String s : nbt.getAllKeys())
		{
			if (s.contains("QuestFlags")) questFlags.put(UUID.fromString(s.substring(10)), nbt.getByte(s));
		}
		this.entityData.set(DATA_QUEST_FLAGS, new QuestFlags(questFlags));
	}
	
	protected void setQuestFlag(UUID entity, int index, boolean value)
	{
		QuestFlags q = this.entityData.get(DATA_QUEST_FLAGS);
		this.entityData.set(DATA_QUEST_FLAGS, q.setFlag(entity, index, value));
	}
	
	protected boolean getQuestFlag(UUID entity, int index)
	{
		return this.entityData.get(DATA_QUEST_FLAGS).getFlag(entity, index);
	}
	
	@Override
	public boolean removeWhenFarAway(double p_21542_)
	{
		return false;
	}
}
