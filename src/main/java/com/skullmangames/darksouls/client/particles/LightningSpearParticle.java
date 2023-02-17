package com.skullmangames.darksouls.client.particles;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import com.skullmangames.darksouls.common.animation.Joint;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ModParticles;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LightningSpearParticle extends EntityboundParticle
{
	@Nullable private LivingCap<?> entityCap;
	@Nullable private Joint joint;
	private int spriteId;
	private Quaternion rot;
	private Quaternion rot2;
	private final float scale;
	
	protected LightningSpearParticle(ClientWorld level, int entityId, float scale, double xCoord, double yCoord, double zCoord)
	{
		super(level, entityId, xCoord, yCoord, zCoord, 0, 0, 0);
		this.quadSize = 1.0F;
	    this.xd = 0;
	    this.yd = 0;
	    this.zd = 0;
	    this.lifetime = 20;
	    this.alpha = 0;
	    this.scale = scale;
	    
	    if (this.entity instanceof LivingEntity)
	    {
	    	LivingCap<?> cap = (LivingCap<?>)this.entity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
	    	if (cap != null)
	    	{
	    		this.entityCap = cap;
	    		this.joint = cap.getEntityModel(ClientModels.CLIENT).getArmature().searchJointByName("Tool_R");
	    		
	    		PublicMatrix4f rotationTransform = this.entityCap.getModelMatrix(1.0F);
	    		PublicMatrix4f localTransform = this.joint.getAnimatedTransform();
	    		localTransform.mulFront(rotationTransform);
	    		Vector3f jpos = localTransform.toTranslationVector();
	    		jpos.mul(-1, 1, -1);
	    		
	    		this.setPos(jpos.x(), jpos.y(), jpos.z());
	    		this.rot = localTransform.transpose().rotate((float)Math.toRadians(180), Vector3f.YP).toQuaternion().vanilla();
				this.rot.mul(Vector3f.YP.rotationDegrees(90));
				this.rot2 = this.rot.copy();
				this.rot2.mul(Vector3f.ZN.rotationDegrees(45));
	    	}
	    }
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (this.age > 5 && this.age < 7)
		{
			for (int i = 0; i < 360; i++)
			{
				if (i % 80 == 0)
				{
					level.addParticle(ModParticles.LIGHTNING.get(), this.x, this.y - 0.25F, this.z, Math.cos(i) * 0.1D, Math.sin(i) * 0.1D, Math.sin(i) * 0.1D);
				}
			}
		}
		if (this.age > 8 && this.age <= 14)
		{
			this.alpha += 1.0F / 6.0F;
		}
		
		this.spriteId = this.random.nextInt(6);
		
		if (this.joint != null)
		{
			PublicMatrix4f rotationTransform = this.entityCap.getModelMatrix(1.0F);
			PublicMatrix4f localTransform = this.joint.getAnimatedTransform();
			localTransform.mulFront(rotationTransform);
			Vector3f jpos = localTransform.toTranslationVector();
			jpos.mul(-1, 1, -1);
			
			this.setPos(jpos.x(), jpos.y(), jpos.z());
			this.rot = localTransform.transpose().rotate((float)Math.toRadians(180), Vector3f.YP).toQuaternion().vanilla();
			this.rot.mul(Vector3f.YP.rotationDegrees(90));
			this.rot2 = this.rot.copy();
			this.rot2.mul(Vector3f.ZN.rotationDegrees(45));
		}
	}
	
	@Override
	public void render(IVertexBuilder vertexBuilder, ActiveRenderInfo camera, float partialTicks)
	{
		MatrixStack poseStack = new MatrixStack();
		Vector3d camPos = camera.getPosition();
		float posX = (float) (MathHelper.lerp((double) partialTicks, this.xo, this.x) - camPos.x());
		float posY = (float) (MathHelper.lerp((double) partialTicks, this.yo, this.y) - camPos.y());
		float posZ = (float) (MathHelper.lerp((double) partialTicks, this.zo, this.z) - camPos.z());
		int uv2 = this.getLightColor(partialTicks);
		
		poseStack.translate(posX, posY, posZ);
		poseStack.scale(this.scale, this.scale, this.scale);
		poseStack.pushPose();
		poseStack.mulPose(this.rot);
		this.drawTexturedPlane(vertexBuilder, poseStack.last().pose(), -1.0F, -0.15F, 1.0F, 0.15F, 0, 0, 32, 5, uv2);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		this.drawTexturedPlane(vertexBuilder, poseStack.last().pose(), -1.0F, -0.15F, 1.0F, 0.15F, 0, 0, 32, 5, uv2);
		poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
		this.drawTexturedPlane(vertexBuilder, poseStack.last().pose(), -1.0F, -0.15F, 1.0F, 0.15F, 0, 0, 32, 5, uv2);
		poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
		this.drawTexturedPlane(vertexBuilder, poseStack.last().pose(), -1.0F, -0.15F, 1.0F, 0.15F, 0, 0, 32, 5, uv2);
		poseStack.popPose();
		
		for (int i = 0; i < 7; i++)
		{
			Vector3f v = new Vector3f(-0.6F + 0.2F * i, -0.6F + 0.2F * i, 0.0F);
			v.transform(this.rot2);
			poseStack.pushPose();
			poseStack.translate(v.x(), v.y(), v.z());
			poseStack.mulPose(camera.rotation());
			poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
			poseStack.scale(0.15F, 0.15F, 0.15F);
			int r = this.spriteId + i;
			if (r > 5) r -= 5;
			this.drawTexturedPlane(vertexBuilder, poseStack.last().pose(), -1.0F, -1.25F, 1.0F, 1.25F, 1 + 5 * r, 6, 5 + 5 * r, 12, uv2);
			poseStack.popPose();
		}
	}
	
	private void drawTexturedPlane(IVertexBuilder vertexBuilder, Matrix4f matrix, float minX, float minY, float maxX, float maxY, float minU, float minV, float maxU, float maxV, int uv2)
	{
		float corU = 0.001953125F;
		float corV = 0.00390625F;
		minU = this.getU0() + minU * corU;
		minV = this.getV0() + minV * corV;
		maxU = this.getU0() + maxU * corU;
		maxV = this.getV0() + maxV * corV;
		
		vertexBuilder.vertex(matrix, minX, minY, 0.0F).uv(minU, maxV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(uv2).endVertex();
		vertexBuilder.vertex(matrix, maxX, minY, 0.0F).uv(maxU, maxV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(uv2).endVertex();
		vertexBuilder.vertex(matrix, maxX, maxY, 0.0F).uv(maxU, minV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(uv2).endVertex();
		vertexBuilder.vertex(matrix, minX, maxY, 0.0F).uv(minU, minV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(uv2).endVertex();
	}
	
	@Override
	public IParticleRenderType getRenderType()
	{
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	public static Factory lightningSpear(IAnimatedSprite sprite)
	{
		return new Factory(sprite, 1.0F);
	}
	
	public static Factory greatLightningSpear(IAnimatedSprite sprite)
	{
		return new Factory(sprite, 1.5F);
	}
	
	public static class Factory implements IParticleFactory<EntityboundParticleOptions>
	{
	    private final IAnimatedSprite sprite;
	    private final float scale;

	    public Factory(IAnimatedSprite sprite, float scale)
	    {
	    	this.sprite = sprite;
	    	this.scale = scale;
	    }

	    @Override
	    public Particle createParticle(EntityboundParticleOptions options, ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
	    {
	    	LightningSpearParticle particle = new LightningSpearParticle(level, options.getEntityId(), this.scale, x, y, z);
	    	particle.pickSprite(this.sprite);
	        return particle;
	    }
	}
}
