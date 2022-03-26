package com.skullmangames.darksouls.client.renderer.entity;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.client.renderer.ModRenderTypes;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.client.renderer.entity.model.ClientModel;
import com.skullmangames.darksouls.client.renderer.layer.Layer;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.Joint;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;
import com.skullmangames.darksouls.core.util.physics.Collider;

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
	protected List<Layer<E, T>> layers;
	
	public ArmatureRenderer()
	{
		this.layers = Lists.newArrayList();
	}
	
	public void render(E livingEntity, T entitydata, EntityRenderer<E> renderer, MultiBufferSource buffer, PoseStack matStack, int packedLightIn, float partialTicks)
	{
		if(this.shouldRenderNameTag(entitydata, livingEntity))
		{
			RenderNameplateEvent renderNameplateEvent = new RenderNameplateEvent(livingEntity, livingEntity.getDisplayName(), renderer, matStack, buffer, packedLightIn, partialTicks);
		    MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
			this.renderNameTag(entitydata, livingEntity, renderNameplateEvent.getContent(), matStack, buffer, packedLightIn);
		}
		
		boolean visible = this.isVisible(livingEntity);
		boolean visibleToPlayer = !visible && !livingEntity.isInvisibleTo(this.minecraft.player);
		boolean glowing = this.minecraft.shouldEntityAppearGlowing(livingEntity);
		RenderType renderType = this.getRenderType(livingEntity, entitydata, visible, visibleToPlayer, glowing);
		
		if(renderType != null)
		{
			VertexConsumer builder = buffer.getBuffer(renderType);
			ClientModel model = entitydata.getEntityModel(ClientModels.CLIENT);
			Armature armature = model.getArmature();
			armature.initializeTransform();
			matStack.pushPose();
			this.applyRotations(matStack, armature, livingEntity, entitydata, partialTicks);
			entitydata.getClientAnimator().setPoseToModel(partialTicks);
			PublicMatrix4f[] poses = armature.getJointTransforms();
			model.draw(matStack, builder, packedLightIn, 1.0F, 1.0F, 1.0F, visibleToPlayer ? 0.15F : 1.0F, poses);
			
			if(!livingEntity.isSpectator()) renderLayer(entitydata, livingEntity, poses, buffer, matStack, packedLightIn, partialTicks);
			
			if(this.minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes())
			{
				AnimatorClient animator = entitydata.getClientAnimator();
				AnimationPlayer player = animator.getPlayer();
				if(player.getPlay() instanceof AttackAnimation)
				{
					AttackAnimation attackAnimation = (AttackAnimation) player.getPlay();
					boolean flag3 = entitydata.getEntityState().shouldDetectCollision();
					float elapsedTime = player.getElapsedTime();
					int index = attackAnimation.getIndexer(elapsedTime);
					Collider collider = attackAnimation.getCollider((LivingCap<?>) entitydata, elapsedTime);
					PublicMatrix4f mat = null;
					
					if (index > 0)
					{
						Joint joint = armature.getJointHierarcy();
						while(index >> 5 != 0)
						{
							index = index >> 5;
							joint = joint.getSubJoints().get((index & 31) - 1);
						}
						mat = joint.getAnimatedTransform();
					}
					
					if (mat == null) mat = new PublicMatrix4f();
					collider.draw(matStack, buffer, mat, partialTicks, flag3);
				}
			}
			
			matStack.popPose();;
		}
	}
	
	public RenderType getRenderType(E entityIn, T entitydata, boolean isVisible, boolean isVisibleToPlayer, boolean isGlowing)
	{
		ResourceLocation resourcelocation = this.getEntityTexture(entityIn);
		if (isVisibleToPlayer) return ModRenderTypes.getItemEntityTranslucentCull(resourcelocation);
		else if (isVisible) return this.getCommonRenderType(resourcelocation);
		else return isGlowing ? RenderType.outline(resourcelocation) : null;
	}
	
	protected abstract ResourceLocation getEntityTexture(E entityIn);
	
	protected void renderLayer(T entitydata, E entityIn, PublicMatrix4f[] poses, MultiBufferSource buffer, PoseStack matrixStackIn, int packedLightIn, float partialTicks)
	{
		for(Layer<E, T> layer : this.layers) layer.renderLayer(entitydata, entityIn, matrixStackIn, buffer, packedLightIn, poses, partialTicks);
	}
	
	protected boolean isVisible(E entityIn)
	{
		return !entityIn.isInvisible();
	}

	protected RenderType getCommonRenderType(ResourceLocation resourcelocation)
	{
		return ModRenderTypes.getAnimatedModel(resourcelocation);
	}
	
	protected void transformJoint(int jointId, Armature modelArmature, PublicMatrix4f mat)
	{
		Joint joint = modelArmature.findJointById(jointId);
        PublicMatrix4f.mul(joint.getAnimatedTransform(), mat, joint.getAnimatedTransform());
	}
	
	protected void applyRotations(PoseStack matStack, Armature armature, E entityIn, T entitydata, float partialTicks)
	{
        PublicMatrix4f transpose = entitydata.getModelMatrix(partialTicks).transpose();
        matStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        PublicMatrix4f.rotateStack(matStack, transpose);
        PublicMatrix4f.scaleStack(matStack, transpose);
	}
	
	protected boolean shouldRenderNameTag(T entitydata, E entity)
	{
		boolean flag1;
		double d0 = this.minecraft.cameraEntity.distanceToSqr(entity);
		float f = entity.isDiscrete() ? 32.0F : 64.0F;
		if (d0 >= (double) (f * f)) flag1 = false;
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
							flag1 = (team1 == null) ? flag : team.isAlliedTo(team1) && (team.canSeeFriendlyInvisibles() || flag);
						case HIDE_FOR_OWN_TEAM:
							flag1 = (team1 == null) ? flag : !team.isAlliedTo(team1) && flag;
						default:
							flag1 = true;
					}
				}
			}
			flag1 = Minecraft.renderNames() && entity != this.minecraft.cameraEntity && flag
					&& !entity.isVehicle();
		}

		return flag1 && (entity.shouldShowName()
				|| entity.hasCustomName() && (entity == this.minecraft.getEntityRenderDispatcher().crosshairPickEntity
						|| entity instanceof Player));
	}
	
	protected void renderNameTag(T entitydata, E entityIn, Component displayNameIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn)
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
			if (flag) {
				fontrenderer.drawInBatch(displayNameIn, f2, (float) i, -1, false, matrix4f, bufferIn, false, 0,
						packedLightIn);
			}

			matrixStackIn.popPose();
		}
	}
}