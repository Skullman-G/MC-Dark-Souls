package com.skullmangames.darksouls.skill;

import java.util.UUID;

import com.skullmangames.darksouls.animation.LivingMotion;
import com.skullmangames.darksouls.common.entities.PlayerData;
import com.skullmangames.darksouls.common.entities.ServerPlayerData;
import com.skullmangames.darksouls.core.event.EntityEventListener.EventType;
import com.skullmangames.darksouls.core.event.PlayerEvent;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.play.server.STCLivingMotionChange;
import com.skullmangames.darksouls.network.play.server.STCModifySkillVariable;
import com.skullmangames.darksouls.network.play.server.STCModifySkillVariable.VariableType;
import com.skullmangames.darksouls.network.play.server.STCPlayAnimation;

import net.minecraft.entity.player.ServerPlayerEntity;

public class KatanaPassive extends Skill {
	private static final String NBT_KEY = "sheath";
	private static final UUID EVENT_UUID = UUID.fromString("a416c93a-42cb-11eb-b378-0242ac130002");

	public KatanaPassive() {
		super(SkillSlot.WEAPON_GIMMICK, 5.0F, "katana_passive");
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getVariableNBT().putBoolean(NBT_KEY, false);
		container.executer.getEventListener().addEventListener(EventType.ON_ACTION_EVENT, PlayerEvent.makeEvent(EVENT_UUID, (player, args)->{
			container.executer.getSkill(SkillSlot.WEAPON_GIMMICK).getContaining().setCooldownSynchronize((ServerPlayerData)player, 0);
			return false;
		}));
	}
	
	@Override
	public void onDeleted(SkillContainer container) {
		container.executer.getEventListener().removeListener(EventType.ON_ACTION_EVENT, EVENT_UUID);
	}
	
	@Override
	public void onReset(SkillContainer container) {
		PlayerData<?> executer = container.executer;

		if (!executer.isRemote()) {
			ServerPlayerEntity executePlayer = (ServerPlayerEntity) executer.getOriginalEntity();
			container.getVariableNBT().putBoolean(NBT_KEY, false);
			
			STCLivingMotionChange msg = new STCLivingMotionChange(executePlayer.getId(), 3);
			msg.setMotions(LivingMotion.IDLE, LivingMotion.WALKING, LivingMotion.RUNNING);
			msg.setAnimations(Animations.BIPED_IDLE_UNSHEATHING, Animations.BIPED_WALK_UNSHEATHING, Animations.BIPED_RUN_UNSHEATHING);
			((ServerPlayerData)executer).modifiLivingMotionToAll(msg);
			
			STCModifySkillVariable msg2 = new STCModifySkillVariable(VariableType.BOOLEAN, SkillSlot.WEAPON_GIMMICK, NBT_KEY, false);
			ModNetworkManager.sendToPlayer(msg2, executePlayer);
		}
	}
	
	@Override
	public void setCooldown(SkillContainer container, float value) {
		PlayerData<?> executer = container.executer;
		
		if (!executer.isRemote()) {
			if (this.cooldown < value) {
				ServerPlayerEntity executePlayer = (ServerPlayerEntity) executer.getOriginalEntity();
				container.getVariableNBT().putBoolean(NBT_KEY, true);
				
				STCLivingMotionChange msg = new STCLivingMotionChange(executePlayer.getId(), 6);
				msg.setMotions(LivingMotion.IDLE, LivingMotion.WALKING, LivingMotion.RUNNING, LivingMotion.JUMPING, LivingMotion.KNEELING, LivingMotion.SNEAKING);
				msg.setAnimations(Animations.BIPED_IDLE_SHEATHING, Animations.BIPED_WALK_SHEATHING, Animations.BIPED_RUN_SHEATHING,
						Animations.BIPED_JUMP_SHEATHING, Animations.BIPED_KNEEL_SHEATHING, Animations.BIPED_SNEAK_SHEATHING);
				((ServerPlayerData)executer).modifiLivingMotionToAll(msg);
				
				STCModifySkillVariable msg2 = new STCModifySkillVariable(VariableType.BOOLEAN, SkillSlot.WEAPON_GIMMICK, NBT_KEY, true);
				ModNetworkManager.sendToPlayer(msg2, executePlayer);
				
				STCPlayAnimation msg3 = new STCPlayAnimation(Animations.BIPED_KATANA_SCRAP.getId(), executePlayer.getId(), 0.0F, true);
				ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(msg3, executePlayer);
			}
		}
		
		super.setCooldown(container, value);
	}
	
	@Override
	public float getRegenTimePerTick(PlayerData<?> player) {
		return 1.0F;
	}
}