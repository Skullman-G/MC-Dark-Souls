package com.skullmangames.darksouls.core.init.data;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import com.skullmangames.darksouls.core.util.data.pack_resources.DSDefaultPackResources;
import com.skullmangames.darksouls.core.util.data.source.DSFolderRepositorySource;
import com.skullmangames.darksouls.core.util.data.source.DSPacksSource;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraftforge.fml.loading.FMLPaths;

public class DSDataManager
{
	private final List<AbstractDSDataConfig> configs = new LinkedList<>();
	
	public void loadDSData()
	{
		PackRepository packRepository = new PackRepository(PackType.SERVER_DATA, new DSPacksSource(),
				new DSFolderRepositorySource(new File(FMLPaths.GAMEDIR.get().toFile(), DSDefaultPackResources.ROOT_DIR_NAME+"/"), PackSource.DEFAULT));
		packRepository.reload();
		packRepository.setSelected(packRepository.getAvailableIds());
		CloseableResourceManager resourceManager = new MultiPackResourceManager(PackType.SERVER_DATA, packRepository.openAllSelected());
		
		this.configs.forEach(config -> config.init(resourceManager));

		resourceManager.close();
		packRepository.close();
	}
	
	public <T extends AbstractDSDataConfig> T register(T config)
	{
		this.configs.add(config);
		return config;
	}
}
