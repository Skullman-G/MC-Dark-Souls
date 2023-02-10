package com.skullmangames.darksouls.client.renderer;

import java.util.OptionalDouble;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

public class ModRenderTypes extends RenderType
{
	public ModRenderTypes(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn,
			boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn)
	{
		super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
	}

	private static final RenderType ARMOR_ENTITY_GLINT = create(DarkSouls.MOD_ID + ":armor_entity_glint",
			DefaultVertexFormats.POSITION_TEX, GL11.GL_TRIANGLES, 256, false, false,
			RenderType.State.builder()
					.setTextureState(new RenderState.TextureState(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false))
					.setWriteMaskState(COLOR_WRITE)
					.setCullState(NO_CULL)
					.setDepthTestState(EQUAL_DEPTH_TEST)
					.setTransparencyState(GLINT_TRANSPARENCY)
					.setTexturingState(ENTITY_GLINT_TEXTURING)
					.setLayeringState(VIEW_OFFSET_Z_LAYERING)
					.createCompositeState(false));
	
	private static final RenderType DEBUG_COLLIDER = create(DarkSouls.MOD_ID + ":debug_collider", DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINE_STRIP, 256, false, false,
			RenderType.State.builder()
				.setLineState(new RenderState.LineState(OptionalDouble.empty()))
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
				.setTransparencyState(NO_TRANSPARENCY)
				.setWriteMaskState(COLOR_DEPTH_WRITE)
				.setCullState(NO_CULL)
				.createCompositeState(false)
	);
	
	public static RenderType debugCollider()
	{
		return DEBUG_COLLIDER;
	}

	public static RenderType getAnimatedModel(ResourceLocation locationIn)
	{
		RenderType.State state = RenderType.State.builder()
				.setTextureState(new RenderState.TextureState(locationIn, false, false))
				.setDiffuseLightingState(NO_DIFFUSE_LIGHTING)
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setDepthTestState(LEQUAL_DEPTH_TEST)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.setAlphaState(DEFAULT_ALPHA)
				.createCompositeState(true);

		return create(DarkSouls.MOD_ID + ":animated_model2", DefaultVertexFormats.NEW_ENTITY, GL11.GL_TRIANGLES, 256, true, false, state);
	}

	public static RenderType getItemEntityTranslucentCull(ResourceLocation locationIn)
	{
		RenderType.State rendertype$state = RenderType.State.builder()
				.setTextureState(new RenderState.TextureState(locationIn, false, false))
				.setDiffuseLightingState(DIFFUSE_LIGHTING)
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setOutputState(ITEM_ENTITY_TARGET)
				.setDepthTestState(NO_DEPTH_TEST)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.setWriteMaskState(RenderState.COLOR_DEPTH_WRITE)
				.setAlphaState(DEFAULT_ALPHA)
				.setLightmapState(LIGHTMAP)
				.createCompositeState(true);

		return create(DarkSouls.MOD_ID + ":item_entity_translucent_cull", DefaultVertexFormats.NEW_ENTITY,
				GL11.GL_TRIANGLES, 256, true, false, rendertype$state);
	}

	public static RenderType getAnimatedArmorModel(ResourceLocation locationIn)
	{
		RenderType.State state = RenderType.State.builder()
				.setTextureState(new RenderState.TextureState(locationIn, false, false))
				.setDiffuseLightingState(NO_DIFFUSE_LIGHTING)
				.setAlphaState(DEFAULT_ALPHA)
				.setTransparencyState(NO_TRANSPARENCY)
				.setDepthTestState(LEQUAL_DEPTH_TEST)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
				.createCompositeState(true);

		return create(DarkSouls.MOD_ID + ":animated_armor_model", DefaultVertexFormats.NEW_ENTITY, GL11.GL_TRIANGLES,
				256, true, false, state);
	}

	public static RenderType getEntityIndicator(ResourceLocation locationIn)
	{
		RenderType.State state = RenderType.State.builder()
				.setTextureState(new RenderState.TextureState(locationIn, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setOutputState(OUTLINE_TARGET)
				.setAlphaState(DEFAULT_ALPHA)
				.createCompositeState(false);
		
		return create(DarkSouls.MOD_ID + ":entity_indicator", DefaultVertexFormats.POSITION_TEX, GL11.GL_QUADS, 256, false, false, state);
	}
	
	public static RenderType getEffectEntity(ResourceLocation locationIn)
	{
		RenderType.State state = RenderType.State.builder()
				.setTextureState(new RenderState.TextureState(locationIn, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setDepthTestState(LEQUAL_DEPTH_TEST)
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
				.setAlphaState(DEFAULT_ALPHA)
				.createCompositeState(false);
		
		return create(DarkSouls.MOD_ID + ":mod_effect_entity", DefaultVertexFormats.POSITION_TEX, GL11.GL_QUADS, 256, false, false, state);
	}

	public static RenderType getBoundingBox()
	{
		RenderType.State state = RenderType.State.builder()
				.setTransparencyState(NO_TRANSPARENCY)
				.setDepthTestState(NO_DEPTH_TEST)
				.setAlphaState(DEFAULT_ALPHA)
				.createCompositeState(false);

		return create(DarkSouls.MOD_ID + ":bounding_box", DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINE_STRIP, 256,
				false, false, state);
	}

	public static RenderType getEnchantedArmor()
	{
		return ARMOR_ENTITY_GLINT;
	}

	public static IVertexBuilder getArmorVertexBuilder(IRenderTypeBuffer buffer, RenderType renderType, boolean withGlint)
	{
		return withGlint ? VertexBuilderUtils.create(buffer.getBuffer(getEnchantedArmor()), buffer.getBuffer(renderType))
				: buffer.getBuffer(renderType);
	}
}