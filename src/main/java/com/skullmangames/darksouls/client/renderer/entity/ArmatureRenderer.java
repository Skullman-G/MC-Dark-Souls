package com.skullmangames.darksouls.client.renderer.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.client.animation.AnimationLayer;
import com.skullmangames.darksouls.client.renderer.ModRenderTypes;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.client.renderer.entity.model.ClientModel;
import com.skullmangames.darksouls.client.renderer.layer.Layer;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.Joint;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.common.MinecraftForge;

@OnlyIn(Dist.CLIENT)
public abstract class ArmatureRenderer<E extends LivingEntity, T extends LivingCap<E>>
{
	protected Minecraft minecraft = Minecraft.getInstance();
	protected List<Layer<E, T>> layers = new ArrayList<>();

	public void render(T entityCap, EntityRenderer<E> renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks)
	{
		E entity = entityCap.getOriginalEntity();
		if (this.shouldRenderNameTag(entityCap))
		{
			RenderNameplateEvent renderNameplateEvent = new RenderNameplateEvent(entity,
					entity.getDisplayName(), renderer, poseStack, buffer, packedLight, partialTicks);
			MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
			this.renderNameTag(entityCap, entity, renderNameplateEvent.getContent(), poseStack, buffer,
					packedLight);
		}

		boolean visible = this.isVisible(entity);
		boolean visibleToPlayer = !visible && !entity.isInvisibleTo(this.minecraft.player);
		boolean glowing = this.minecraft.shouldEntityAppearGlowing(entity);
		RenderType renderType = this.getRenderType(entityCap, visible, visibleToPlayer, glowing);

		if (renderType != null)
		{
			VertexConsumer builder = buffer.getBuffer(renderType);
			ClientModel model = entityCap.getEntityModel(ClientModels.CLIENT);
			Armature armature = model.getArmature();
			armature.initializeTransform();
			poseStack.pushPose();
			this.applyRotations(poseStack, armature, entityCap, partialTicks);
			entityCap.getClientAnimator().setPoseToModel(partialTicks);
			ModMatrix4f[] poses = armature.getJointTransforms();
			
			Set<Integer> jointMask = new HashSet<>();
			for (Layer<?, ?> layer : this.layers) jointMask.addAll(layer.getJointMask(entityCap));
			
			model.draw(poseStack, builder, packedLight, 1.0F, 1.0F, 1.0F, visibleToPlayer ? 0.15F : entityCap.getAlpha(), poses, jointMask);

			if (!entity.isSpectator())
				this.renderLayer(entityCap, poses, buffer, poseStack, packedLight, partialTicks);

			if (this.minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes())
			{
				for (AnimationLayer.LayerPart priority : AnimationLayer.LayerPart.values())
				{
					AnimationPlayer animPlayer = entityCap.getClientAnimator().getMixLayer(priority).animationPlayer;
					animPlayer.getPlay().renderDebugging(poseStack, buffer, entityCap, partialTicks);
				}
			}
		}

		poseStack.popPose();
	}

	public RenderType getRenderType(T entityCap, boolean isVisible, boolean isVisibleToPlayer, boolean isGlowing)
	{
		E entityIn = entityCap.getOriginalEntity();
		ResourceLocation resourcelocation = this.getEntityTexture(entityIn);
		if (isVisibleToPlayer)
			return ModRenderTypes.getItemEntityTranslucentCull(resourcelocation);
		else if (isVisible)
			return this.getCommonRenderType(resourcelocation);
		else
			return isGlowing ? RenderType.outline(resourcelocation) : null;
	}

	protected abstract ResourceLocation getEntityTexture(E entityIn);

	protected void renderLayer(T entityCap, ModMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks)
	{
		for (Layer<E, T> layer : this.layers) 
		{
			layer.renderLayer(entityCap, poseStack, buffer, packedLight, poses, partialTicks);
		}
	}

	protected boolean isVisible(E entityIn)
	{
		return !entityIn.isInvisible();
	}

	protected RenderType getCommonRenderType(ResourceLocation resourcelocation)
	{
		return ModRenderTypes.getAnimatedModel(resourcelocation);
	}

	protected void transformJoint(int jointId, Armature modelArmature, ModMatrix4f mat)
	{
		Joint joint = modelArmature.searchJointById(jointId);
		ModMatrix4f.mul(joint.getAnimatedTransform(), mat, joint.getAnimatedTransform());
	}

	protected void applyRotations(PoseStack poseStack, Armature armature, T entityCap, float partialTicks)
	{
		ModMatrix4f transpose = entityCap.getModelMatrix(partialTicks).transpose();
		poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		ModMatrix4f.rotateStack(poseStack, transpose);
		ModMatrix4f.scaleStack(poseStack, transpose);
	}

	protected boolean shouldRenderNameTag(T entityCap)
	{
		E entity = entityCap.getOriginalEntity();
		boolean flag1;
		double d0 = this.minecraft.cameraEntity.distanceToSqr(entity);
		float f = entity.isDiscrete() ? 32.0F : 64.0F;
		if (d0 >= (double) (f * f))
			flag1 = false;
		else
		{
			LocalPlayer clientplayerentity = this.minecraft.player;
			boolean flag = !entity.isInvisibleTo(clientplayerentity);
			if (entity != clientplayerentity)
			{
				Team team = entity.getTeam();
				Team team1 = clientplayerentity.getTeam();
				if (team != null)
				{
					Team.Visibility team$visible = team.getNameTagVisibility();
					switch (team$visible)
					{
					case ALWAYS:
						flag1 = flag;
					case NEVER:
						flag1 = false;
					case HIDE_FOR_OTHER_TEAMS:
						flag1 = (team1 == null) ? flag
								: team.isAlliedTo(team1) && (team.canSeeFriendlyInvisibles() || flag);
					case HIDE_FOR_OWN_TEAM:
						flag1 = (team1 == null) ? flag : !team.isAlliedTo(team1) && flag;
					default:
						flag1 = true;
					}
				}
			}
			flag1 = Minecraft.renderNames() && entity != this.minecraft.cameraEntity && flag && !entity.isVehicle();
		}

		return flag1 && (entity.shouldShowName()
				|| entity.hasCustomName() && (entity == this.minecraft.getEntityRenderDispatcher().crosshairPickEntity
						|| entity instanceof Player));
	}

	protected void renderNameTag(T entityCap, E entityIn, Component displayNameIn, PoseStack matrixStackIn,
			MultiBufferSource bufferIn, int packedLightIn)
	{
		EntityRenderDispatcher renderManager = this.minecraft.getEntityRenderDispatcher();

		double d0 = renderManager.distanceToSqr(entityIn);
		if (net.minecraftforge.client.ForgeHooksClient.isNameplateInRenderDistance(entityIn, d0))
		{
			boolean flag = !entityIn.isDiscrete();
			float f = entityIn.getBbHeight() + 0.5F;
			int i = "deadmau5".equals(displayNameIn.getString()) ? -10 : 0;
			matrixStackIn.pushPose();
			matrixStackIn.translate(0.0D, (double) f, 0.0D);
			matrixStackIn.mulPose(renderManager.cameraOrientation());
			matrixStackIn.scale(-0.025F, -0.025F, 0.025F);
			Matrix4f matrix4f = matrixStackIn.last().pose();
			float f1 = this.minecraft.options.getBackgroundOpacity(0.25F);
			int j = (int) (f1 * 255.0F) << 24;
			Font fontrenderer = this.minecraft.font;
			float f2 = (float) (-fontrenderer.width(displayNameIn) / 2);
			fontrenderer.drawInBatch(displayNameIn, f2, (float) i, 553648127, false, matrix4f, bufferIn, flag, j,
					packedLightIn);
			if (flag)
			{
				fontrenderer.drawInBatch(displayNameIn, f2, (float) i, -1, false, matrix4f, bufferIn, false, 0,
						packedLightIn);
			}

			matrixStackIn.popPose();
		}
	}
}