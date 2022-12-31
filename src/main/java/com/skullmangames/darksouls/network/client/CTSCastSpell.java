package com.skullmangames.darksouls.network.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.common.item.SpellItem;
import com.skullmangames.darksouls.core.init.ModCapabilities;
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
			
			if (((playerCap.getFP() >= msg.spell.getFPConsumption() && playerCap.getStats().getStatValue(Stats.FAITH) >= msg.spell.getRequiredFaith())
					|| serverPlayer.isCreative())
					&& (!playerCap.isMounted() || msg.spell.getHorsebackAnimation() != null))
			{
				playerCap.raiseFP(-msg.spell.getFPConsumption());
				StaticAnimation animation = playerCap.isMounted() ? msg.spell.getHorsebackAnimation() : msg.spell.getCastingAnimation();
				playerCap.playAnimationSynchronized(animation, 0.0F);
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
}
