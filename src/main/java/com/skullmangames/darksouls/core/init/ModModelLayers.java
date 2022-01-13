package com.skullmangames.darksouls.core.init;

import java.util.Set;
import java.util.function.Supplier;

import com.google.common.collect.Sets;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.vanilla.FireKeeperModel;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;

@OnlyIn(Dist.CLIENT)
public class ModModelLayers
{
	private static final Set<ModelLayerLocation> MODELS = Sets.newHashSet();

	public static final ModelLayerLocation FIRE_KEEPER = register("fire_keeper", () -> FireKeeperModel.createBodyModel());

	private static ModelLayerLocation register(String p_171294_, Supplier<LayerDefinition> method)
	{
		return register(p_171294_, "main", method);
	}

	private static ModelLayerLocation register(String p_171296_, String p_171297_, Supplier<LayerDefinition> method)
	{
		ModelLayerLocation modellayerlocation = createLocation(p_171296_, p_171297_);
		ForgeHooksClient.registerLayerDefinition(modellayerlocation, method);
		if (!MODELS.add(modellayerlocation))
		{
			throw new IllegalStateException("Duplicate registration for " + modellayerlocation);
		} else
		{
			return modellayerlocation;
		}
	}

	private static ModelLayerLocation createLocation(String p_171301_, String p_171302_)
	{
		return new ModelLayerLocation(new ResourceLocation(DarkSouls.MOD_ID, p_171301_), p_171302_);
	}
	
	public static void call() {}
}
