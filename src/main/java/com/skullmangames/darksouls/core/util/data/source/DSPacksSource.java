package com.skullmangames.darksouls.core.util.data.source;

import java.util.function.Consumer;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.core.util.data.pack_resources.DSDefaultPackResources;

import net.minecraft.SharedConstants;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;

public class DSPacksSource implements RepositorySource
{
	public static final PackMetadataSection BUILT_IN_METADATA = new PackMetadataSection(
			new TranslatableComponent("dataPack.vanilla.description"), PackType.SERVER_DATA.getVersion(SharedConstants.getCurrentVersion()));
	private final DSDefaultPackResources vanillaPack = new DSDefaultPackResources(BUILT_IN_METADATA, "minecraft", DarkSouls.MOD_ID);

	public void loadPacks(Consumer<Pack> packSupplier, Pack.PackConstructor packConstructor)
	{
		Pack pack = Pack.create("vanilla", false, () -> this.vanillaPack, packConstructor, Pack.Position.BOTTOM, PackSource.BUILT_IN);
		if (pack != null) packSupplier.accept(pack);
	}
}
