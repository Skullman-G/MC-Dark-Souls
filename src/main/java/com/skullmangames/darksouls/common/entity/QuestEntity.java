package com.skullmangames.darksouls.common.entity;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.core.util.timer.ChatRenderTimer;

import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.common.MinecraftForge;

public class QuestEntity extends PathfinderMob
{
	private String questPath = this.getQuestPaths().get(0);
	protected final ChatRenderTimer chatTimer = new ChatRenderTimer();

	protected QuestEntity(EntityType<? extends PathfinderMob> type, Level level)
	{
		super(type, level);
		MinecraftForge.EVENT_BUS.register(this.chatTimer);
	}

	public List<String> getQuestPaths()
	{
		List<String> list = new ArrayList<String>();
		list.add("0");
		return list;
	}

	public String getCurrentQuestPath()
	{
		return this.questPath;
	}

	public void setCurrentQuestPath(String value)
	{
		if (this.getQuestPaths().contains(value))
		{
			this.questPath = value;
		}
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
				}
			}
		}
	}
	
	protected Item getEquipmentForSlot(EquipmentSlot slot)
	{
		return Items.AIR;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt)
	{
		super.addAdditionalSaveData(nbt);
		nbt.putString("QuestPath", this.getCurrentQuestPath());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt)
	{
		super.readAdditionalSaveData(nbt);
		this.setCurrentQuestPath(nbt.getString("QuestPath"));
	}
}
