package com.skullmangames.darksouls.network.play;

import com.skullmangames.darksouls.client.gui.screens.BonfireNameScreen;
import com.skullmangames.darksouls.client.gui.screens.BonfireScreen;
import com.skullmangames.darksouls.common.entity.SoulEntity;
import com.skullmangames.darksouls.common.tileentity.BonfireTileEntity;
import com.skullmangames.darksouls.network.server.SSpawnSoulPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModClientPlayNetHandler implements IModClientPlayNetHandler
{
	@Override
	public void handleAddSoulEntity(SSpawnSoulPacket packet)
	{
		Minecraft minecraft = Minecraft.getInstance();
		ClientPlayNetHandler handler = minecraft.getConnection();
		PacketThreadUtil.ensureRunningOnSameThread(packet, handler, minecraft);
		double x = packet.getX();
		double y = packet.getY();
		double z = packet.getZ();
		Entity entity = new SoulEntity(handler.getLevel(), x, y, z, packet.getValue());
		entity.setPacketCoordinates(x, y, z);
		entity.yRot = 0.0F;
		entity.xRot = 0.0F;
		entity.setId(packet.getId());
		handler.getLevel().putNonPlayerEntity(packet.getId(), entity);
	}

	@Override
	public void openBonfireNameScreen(PlayerEntity player, BonfireTileEntity tileentity)
	{
		Minecraft.getInstance().setScreen(new BonfireNameScreen(player, tileentity));
	}

	@Override
	public void openBonfireScreen(BonfireTileEntity tileentity)
	{
		Minecraft.getInstance().setScreen(new BonfireScreen(tileentity));
	}

	@Override
	public void handleSetTitles(STitlePacket packet)
	{
		Minecraft minecraft = Minecraft.getInstance();
		PacketThreadUtil.ensureRunningOnSameThread(packet, minecraft.getConnection(), minecraft);
		STitlePacket.Type stitlepacket$type = packet.getType();
		ITextComponent title = null;
		ITextComponent subtitle = null;
		ITextComponent text = packet.getText() != null ? packet.getText() : StringTextComponent.EMPTY;
		switch (stitlepacket$type)
		{
		case TITLE:
			title = text;
			break;
		case SUBTITLE:
			subtitle = text;
			break;
		case ACTIONBAR:
			minecraft.gui.setOverlayMessage(text, false);
			return;
		case RESET:
			minecraft.gui.setTitles((ITextComponent) null, (ITextComponent) null, -1, -1, -1);
			minecraft.gui.resetTitleTimes();
			return;
		default:
			break;
		}

		minecraft.gui.setTitles(title, subtitle, packet.getFadeInTime(), packet.getStayTime(), packet.getFadeOutTime());
	}
}
