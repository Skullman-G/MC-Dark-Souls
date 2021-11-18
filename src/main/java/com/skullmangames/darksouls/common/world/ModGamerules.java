package com.skullmangames.darksouls.common.world;

import com.skullmangames.darksouls.common.capability.entity.ServerPlayerData;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.Formulars;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCGameruleChange;
import com.skullmangames.darksouls.network.server.STCGameruleChange.Gamerules;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.RuleKey;

public class ModGamerules
{
	public static RuleKey<GameRules.BooleanValue> DO_VANILLA_ATTACK;
	public static RuleKey<GameRules.BooleanValue> HAS_FALL_ANIMATION;
	public static RuleKey<GameRules.IntegerValue> SPEED_PENALTY_PERCENT;
	
	public static void registerRules()
	{
		DO_VANILLA_ATTACK = GameRules.register("doVanillaAttack", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
		HAS_FALL_ANIMATION = GameRules.register("hasFallAnimation", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true, (server, value) ->
		{
			ModNetworkManager.sendToAll(new STCGameruleChange(Gamerules.HAS_FALL_ANIMATION, value.get()));
		}));
		SPEED_PENALTY_PERCENT = GameRules.register("speedPenaltyPercent", GameRules.Category.PLAYER, GameRules.IntegerValue.create(100, (server, value) ->
		{
			
			for (ServerPlayerEntity player : server.getPlayerList().getPlayers())
			{
				ServerPlayerData playerdata = (ServerPlayerData)player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
				if (playerdata != null)
				{
					ModifiableAttributeInstance mainhandAttackSpeed = playerdata.getOriginalEntity().getAttribute(Attributes.ATTACK_SPEED);
					ModifiableAttributeInstance offhandAttackSpeed = playerdata.getOriginalEntity().getAttribute(ModAttributes.OFFHAND_ATTACK_SPEED.get());
					
					mainhandAttackSpeed.removeModifier(ServerPlayerData.WEIGHT_PENALTY_MODIFIIER);
					float mainWeaponSpeed = (float) mainhandAttackSpeed.getBaseValue();
					for(AttributeModifier attributeModifier : playerdata.getOriginalEntity().getMainHandItem().getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_SPEED))
					{
						mainWeaponSpeed += (float)attributeModifier.getAmount();
					}
					
					mainhandAttackSpeed.addTransientModifier(new AttributeModifier(ServerPlayerData.WEIGHT_PENALTY_MODIFIIER, "weight penalty modifier",
							Formulars.getAttackSpeedPenalty(playerdata.getWeight(), mainWeaponSpeed, playerdata), Operation.ADDITION));
					
					offhandAttackSpeed.removeModifier(ServerPlayerData.WEIGHT_PENALTY_MODIFIIER);
					float offWeaponSpeed = (float) offhandAttackSpeed.getBaseValue();
					for(AttributeModifier attributeModifier : playerdata.getOriginalEntity().getMainHandItem().getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_SPEED))
					{
						offWeaponSpeed += (float)attributeModifier.getAmount();
					}
					
					offhandAttackSpeed.addTransientModifier(new AttributeModifier(ServerPlayerData.WEIGHT_PENALTY_MODIFIIER, "weight penalty modifier",
							Formulars.getAttackSpeedPenalty(playerdata.getWeight(), offWeaponSpeed, playerdata), Operation.ADDITION));
				}
			}
			
			ModNetworkManager.sendToAll(new STCGameruleChange(Gamerules.SPEED_PENALTY_PERCENT, value.get()));
		}));
	}
}