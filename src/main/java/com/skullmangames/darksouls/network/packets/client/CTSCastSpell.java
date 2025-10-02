package com.skullmangames.darksouls.network.packets.client;

import java.util.function.Supplier;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.common.capability.item.SpellCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.packets.NetworkPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class CTSCastSpell implements NetworkPacket
{
	private SpellCap spell;
	
	public CTSCastSpell(SpellCap item)
	{
		this.spell = item;
	}
	
	public CTSCastSpell(FriendlyByteBuf buf)
	{
		this.spell = (SpellCap)buf.readItem().getCapability(ModCapabilities.CAPABILITY_ITEM).orElse(null);
	}
	
	@Override
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeItem(new ItemStack(this.spell.getOriginalItem()));
	}
	
	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->
		{
			ServerPlayer serverPlayer = ctx.get().getSender();
			ServerPlayerCap playerCap = (ServerPlayerCap) serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerCap == null) return;
			
			if (((playerCap.getFP() >= this.spell.getFPConsumption() && this.spell.meetsRequirements(playerCap.getStats()))
					|| serverPlayer.isCreative())
					&& (!playerCap.isMounted() || this.spell.getHorsebackAnimation() != null))
			{
				playerCap.raiseFP(-this.spell.getFPConsumption());
				StaticAnimation animation = playerCap.isMounted() ? this.spell.getHorsebackAnimation() : this.spell.getCastingAnimation();
				playerCap.playAnimationSynchronized(animation, 0.0F);
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
}
