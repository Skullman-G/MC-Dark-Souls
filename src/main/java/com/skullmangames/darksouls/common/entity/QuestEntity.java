package com.skullmangames.darksouls.common.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.skullmangames.darksouls.core.util.QuestFlags;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public abstract class QuestEntity extends CreatureEntity
{
	protected static final DataParameter<QuestFlags> DATA_QUEST_FLAGS = EntityDataManager.defineId(QuestEntity.class, QuestFlags.SERIALIZER);

	public QuestEntity(EntityType<? extends QuestEntity> type, World level)
	{
		super(type, level);
	}
	
	public void onFinishChat(ServerPlayerEntity player, String location) {}
	
	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}
	
	public static AttributeModifierMap.MutableAttribute createAttributes()
	{
		return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.ATTACK_DAMAGE, 1.0D);
	}
	
	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld level, DifficultyInstance difficulty, SpawnReason type, ILivingEntityData data, CompoundNBT nbt)
	{
		data = super.finalizeSpawn(level, difficulty, type, data, nbt);
		this.populateDefaultEquipmentSlots(difficulty);
		return data;
	}

	@Override
	protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty)
	{
		for (EquipmentSlotType slot : EquipmentSlotType.values())
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
	
	protected Item getEquipmentForSlot(EquipmentSlotType slot)
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
	public void addAdditionalSaveData(CompoundNBT nbt)
	{
		super.addAdditionalSaveData(nbt);
		this.entityData.get(DATA_QUEST_FLAGS).save(nbt);
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT nbt)
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
