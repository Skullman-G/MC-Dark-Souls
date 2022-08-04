package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.common.item.SpellItem;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCPlayAnimation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class CTSCastSpell
{
	private SpellItem spell;
	
	public CTSCastSpell(SpellItem item)
	{
		this.spell = item;
	}
	
	public static CTSCastSpell fromBytes(FriendlyByteBuf buf)
	{
		return new CTSCastSpell((SpellItem)buf.readItem().getItem());
	}
	
	public static void toBytes(CTSCastSpell msg, FriendlyByteBuf buf)
	{
		buf.writeItem(new ItemStack(msg.spell));
	}
	
	public static void handle(CTSCastSpell msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayer serverPlayer = ctx.get().getSender();
			ServerPlayerCap playerCap = (ServerPlayerCap) serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerCap == null) return;
			
			playerCap.raiseFP(-msg.spell.getFPConsumption());
			StaticAnimation animation = msg.spell.getCastingAnimation();
			playerCap.getAnimator().playAnimation(animation, 0.0F);
			ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(new STCPlayAnimation(animation, 0.0F, playerCap), serverPlayer);
		});
		
		ctx.get().setPacketHandled(true);
	}
}
