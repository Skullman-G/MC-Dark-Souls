package com.skullmangames.darksouls.common.skill;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.property.Property.DamageProperty;
import com.skullmangames.darksouls.common.capability.entity.ClientPlayerData;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.common.capability.entity.LivingData.EntityState;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSExecuteSkill;

import net.minecraft.network.PacketBuffer;

public abstract class AttackSkill extends Skill
{
	protected List<Map<DamageProperty<?>, Object>> properties;
	
	public AttackSkill(int duration, boolean isActiveSkill, String skillName)
	{
		super(duration, isActiveSkill, skillName);
		this.properties = Lists.<Map<DamageProperty<?>, Object>>newArrayList();
	}
	
	@Override
	public void executeOnClient(ClientPlayerData executer, PacketBuffer args)
	{
		ModNetworkManager.sendToServer(new CTSExecuteSkill(true, args));
	}
	
	@Override
	public boolean canExecute(PlayerData<?> executer)
	{
		return executer.getOriginalEntity().getControllingPassenger() == null;
	}
	
	@Override
	public boolean isExecutableState(PlayerData<?> executer)
	{
		EntityState playerState = executer.getEntityState();
		return !(executer.getOriginalEntity().isFallFlying() || executer.currentMotion == LivingMotion.FALL || !playerState.canAct());
	}
	
	@SuppressWarnings("unchecked")
	protected <V> Optional<V> getProperty(DamageProperty<V> propertyType, Map<DamageProperty<?>, Object> map)
	{
		return (Optional<V>) Optional.ofNullable(map.get(propertyType));
	}
	
	public AttackSkill newPropertyLine()
	{
		this.properties.add(Maps.<DamageProperty<?>, Object>newHashMap());
		return this;
	}
	
	public <T> AttackSkill addProperty(DamageProperty<T> attribute, T object)
	{
		this.properties.get(properties.size()-1).put(attribute, object);
		return this;
	}
	
	public abstract AttackSkill registerPropertiesToAnimation();
}
