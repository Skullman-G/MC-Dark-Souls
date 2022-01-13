package com.skullmangames.darksouls.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;

public class ModRenderTypes extends RenderType
{
	public ModRenderTypes(String p_173178_, VertexFormat p_173179_, Mode p_173180_, int p_173181_, boolean p_173182_,
			boolean p_173183_, Runnable p_173184_, Runnable p_173185_)
	{
		super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
	}

	private static final RenderType ARMOR_ENTITY_GLINT = create(DarkSouls.MOD_ID + ":armor_entity_glint",
			DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.TRIANGLES, 256, false, false,
			RenderType.CompositeState.builder()
					.setTextureState(
							new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false))
					.setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST)
					.setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(ENTITY_GLINT_TEXTURING)
					.setLayeringState(VIEW_OFFSET_Z_LAYERING).setShaderState(RENDERTYPE_ARMOR_GLINT_SHADER).createCompositeState(false));

	public static RenderType getAnimatedModel(ResourceLocation locationIn)
	{
		RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setDepthTestState(LEQUAL_DEPTH_TEST)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setShaderState(RENDERTYPE_ENTITY_CUTOUT_SHADER)
				.setOverlayState(OVERLAY).createCompositeState(true);

		return create(DarkSouls.MOD_ID + ":animated_model2", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256,
				true, false, state);
	}

	public static RenderType getItemEntityTranslucentCull(ResourceLocation locationIn)
	{
		RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET)
				.setDepthTestState(NO_DEPTH_TEST)
				.setShaderState(POSITION_COLOR_LIGHTMAP_SHADER)
				.setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
				.createCompositeState(true);

		return create(DarkSouls.MOD_ID + ":item_entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY,
				VertexFormat.Mode.TRIANGLES, 256, true, false, rendertype$state);
	}

	public static RenderType getAimHelper()
	{
		RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder().setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setDepthTestState(NO_DEPTH_TEST)
				.setLightmapState(NO_LIGHTMAP).setOverlayState(NO_OVERLAY)
				.setShaderState(POSITION_COLOR_SHADER)
				.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE).createCompositeState(true);

		return create(DarkSouls.MOD_ID + ":aim_helper", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINES, 256, true,
				false, rendertype$state);
	}

	public static RenderType getAnimatedArmorModel(ResourceLocation locationIn)
	{
		RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
				.setTransparencyState(NO_TRANSPARENCY)
				.setDepthTestState(LEQUAL_DEPTH_TEST).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY)
				.setShaderState(RENDERTYPE_ENTITY_CUTOUT_SHADER)
				.setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(true);

		return create(DarkSouls.MOD_ID + ":animated_armor_model", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES,
				256, true, false, state);
	}

	public static RenderType getEyes(ResourceLocation locationIn)
	{
		RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
				.setTransparencyState(ADDITIVE_TRANSPARENCY).setWriteMaskState(COLOR_WRITE)
				.setShaderState(RENDERTYPE_EYES_SHADER)
				.createCompositeState(false);

		return create(DarkSouls.MOD_ID + ":eyes", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, false, false,
				state);
	}

	public static RenderType getEntityCutoutNoCull(ResourceLocation locationIn)
	{
		RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
				.setTransparencyState(NO_TRANSPARENCY).setShaderState(RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER)
				.setDepthTestState(NO_DEPTH_TEST).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY)
				.createCompositeState(true);

		return create(DarkSouls.MOD_ID + ":entity_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES,
				256, true, false, state);
	}

	public static RenderType getEntityIndicator(ResourceLocation locationIn)
	{
		RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
				.setShaderState(POSITION_TEX_SHADER)
				.setTransparencyState(NO_TRANSPARENCY).setDepthTestState(NO_DEPTH_TEST).createCompositeState(false);

		return create(DarkSouls.MOD_ID + ":entity_indicator", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256,
				false, false, state);
	}

	public static RenderType getBoundingBox()
	{
		RenderType.CompositeState state = RenderType.CompositeState.builder().setTransparencyState(NO_TRANSPARENCY)
				.setShaderState(POSITION_COLOR_SHADER)
				.setDepthTestState(NO_DEPTH_TEST).createCompositeState(false);

		return create(DarkSouls.MOD_ID + ":bounding_box", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINE_STRIP, 256,
				false, false, state);
	}

	public static RenderType getEnchantedArmor()
	{
		return ARMOR_ENTITY_GLINT;
	}

	public static VertexConsumer getArmorVertexBuilder(MultiBufferSource buffer, RenderType renderType,
			boolean withGlint)
	{
		return withGlint
				? VertexMultiConsumer.create(buffer.getBuffer(getEnchantedArmor()), buffer.getBuffer(renderType))
				: buffer.getBuffer(renderType);
	}
}