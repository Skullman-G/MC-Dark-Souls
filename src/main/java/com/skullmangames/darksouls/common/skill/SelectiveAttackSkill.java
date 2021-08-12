package com.skullmangames.darksouls.common.skill;

import java.util.List;
import java.util.function.Function;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation.Phase;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerData;
import com.skullmangames.darksouls.common.capability.item.CapabilityItem;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCResetBasicAttackCool;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;

public class SelectiveAttackSkill extends SpecialAttackSkill {
	protected final StaticAnimation[] attackAnimations;
	protected final Function<ServerPlayerData, Integer> selector;
	
	public SelectiveAttackSkill(float restriction, String skillName, Function<ServerPlayerData, Integer> func, StaticAnimation... animations) {
		super(restriction, skillName);
		this.attackAnimations = animations;
		this.selector = func;
	}
	
	public SelectiveAttackSkill(float restriction, int duration, String skillName, Function<ServerPlayerData, Integer> func, StaticAnimation... animations) {
		super(restriction, duration, skillName);
		this.attackAnimations = animations;
		this.selector = func;
	}
	
	@Override
	public List<ITextComponent> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerData<?> playerCap) {
		List<ITextComponent> list = super.getTooltipOnItem(itemStack, cap, playerCap);
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(0), "Each Strikes:");
		
		return list;
	}
	
	@Override
	public SpecialAttackSkill registerPropertiesToAnimation() {
		for(StaticAnimation animation : this.attackAnimations) {
			AttackAnimation anim = ((AttackAnimation)animation);
			for(Phase phase : anim.phases) {
				phase.addProperties(this.properties.get(0).entrySet());
			}
		}
		
		return this;
	}
	
	@Override
	public void executeOnServer(ServerPlayerData executer, PacketBuffer args) {
		executer.playAnimationSynchronize(this.attackAnimations[this.getAnimationInCondition(executer)], 0);
		ModNetworkManager.sendToPlayer(new STCResetBasicAttackCool(), executer.getOriginalEntity());
	}
	
	public int getAnimationInCondition(ServerPlayerData executer) {
		return selector.apply(executer);
	}
}