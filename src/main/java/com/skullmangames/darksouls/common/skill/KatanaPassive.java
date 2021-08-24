package com.skullmangames.darksouls.common.skill;

import java.util.UUID;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerData;
import com.skullmangames.darksouls.core.event.EntityEventListener.EventType;
import com.skullmangames.darksouls.core.event.PlayerEvent;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCLivingMotionChange;
import com.skullmangames.darksouls.network.server.STCModifySkillVariable;
import com.skullmangames.darksouls.network.server.STCModifySkillVariable.VariableType;

import net.minecraft.entity.player.ServerPlayerEntity;

public class KatanaPassive extends Skill
{
	private static final String NBT_KEY = "sheath";
	private static final UUID EVENT_UUID = UUID.fromString("a416c93a-42cb-11eb-b378-0242ac130002");

	public KatanaPassive()
	{
		super("katana_passive");
	}
	
	@Override
	public void onInitiate()
	{
		SkillExecutionHelper.getVariableNBT().putBoolean(NBT_KEY, false);
		SkillExecutionHelper.getExecuter().getEventListener().addEventListener(EventType.ON_ACTION_EVENT, PlayerEvent.makeEvent(EVENT_UUID, (player, args)->
		{
			return false;
		}));
	}
	
	@Override
	public void onDeleted()
	{
		SkillExecutionHelper.getExecuter().getEventListener().removeListener(EventType.ON_ACTION_EVENT, EVENT_UUID);
	}
	
	@Override
	public void onReset()
	{
		PlayerData<?> executer = SkillExecutionHelper.getExecuter();

		if (!executer.isClientSide())
		{
			ServerPlayerEntity executePlayer = (ServerPlayerEntity) executer.getOriginalEntity();
			SkillExecutionHelper.getVariableNBT().putBoolean(NBT_KEY, false);
			
			STCLivingMotionChange msg = new STCLivingMotionChange(executePlayer.getId(), 3);
			msg.setMotions(LivingMotion.IDLE, LivingMotion.WALKING, LivingMotion.RUNNING);
			msg.setAnimations(Animations.BIPED_IDLE_UNSHEATHING, Animations.BIPED_WALK_UNSHEATHING, Animations.BIPED_RUN_UNSHEATHING);
			((ServerPlayerData)executer).modifiLivingMotionToAll(msg);
			
			STCModifySkillVariable msg2 = new STCModifySkillVariable(VariableType.BOOLEAN, NBT_KEY, false);
			ModNetworkManager.sendToPlayer(msg2, executePlayer);
		}
	}
	
	@Override
	public float getRegenTimePerTick(PlayerData<?> player) {
		return 1.0F;
	}
}