package com.skullmangames.darksouls.network.play;

import com.skullmangames.darksouls.client.gui.screens.BonfireNameScreen;
import com.skullmangames.darksouls.client.gui.screens.BonfireScreen;
import com.skullmangames.darksouls.client.gui.screens.FireKeeperScreen;
import com.skullmangames.darksouls.common.tileentity.BonfireTileEntity;
import net.minecraft.client.Minecraft;
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

	@Override
	public void openFireKeeperScreen(int firekeeperid)
	{
		Minecraft.getInstance().setScreen(new FireKeeperScreen(firekeeperid));
	}
}
