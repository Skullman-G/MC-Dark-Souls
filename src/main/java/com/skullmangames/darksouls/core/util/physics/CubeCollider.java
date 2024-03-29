package com.skullmangames.darksouls.core.util.physics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.skullmangames.darksouls.client.renderer.ModRenderTypes;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CubeCollider extends Collider
{
	protected final Vec3[] modelVertex;
	protected final Vec3[] modelNormal;
	protected Vec3[] rotatedVertex;
	protected Vec3[] rotatedNormal;

	/**
	 * make 3d obb
	 * 
	 * @param pos1        left_back
	 * @param pos2        left_front
	 * @param pos3        right_front
	 * @param pos4        right_back
	 * @param modelCenter central position
	 */
	public CubeCollider(double posX, double posY, double posZ, double center_x, double center_y, double center_z)
	{
		this(getInitialAABB(posX, posY, posZ, center_x, center_y, center_z), posX, posY, posZ, center_x, center_y,
				center_z);
	}

	public CubeCollider(AABB outerAABB, double posX, double posY, double posZ, double center_x, double center_y,
			double center_z)
	{
		super(new Vec3(center_x, center_y, center_z), outerAABB);
		this.modelVertex = new Vec3[4];
		this.modelNormal = new Vec3[3];
		this.rotatedVertex = new Vec3[4];
		this.rotatedNormal = new Vec3[3];
		this.modelVertex[0] = new Vec3(posX, posY, -posZ);
		this.modelVertex[1] = new Vec3(posX, posY, posZ);
		this.modelVertex[2] = new Vec3(-posX, posY, posZ);
		this.modelVertex[3] = new Vec3(-posX, posY, -posZ);
		this.modelNormal[0] = new Vec3(1, 0, 0);
		this.modelNormal[1] = new Vec3(0, 1, 0);
		this.modelNormal[2] = new Vec3(0, 0, -1);
		this.rotatedVertex[0] = new Vec3(0.0D, 0.0D, 0.0D);
		this.rotatedVertex[1] = new Vec3(0.0D, 0.0D, 0.0D);
		this.rotatedVertex[2] = new Vec3(0.0D, 0.0D, 0.0D);
		this.rotatedVertex[3] = new Vec3(0.0D, 0.0D, 0.0D);
		this.rotatedNormal[0] = new Vec3(0.0D, 0.0D, 0.0D);
		this.rotatedNormal[1] = new Vec3(0.0D, 0.0D, 0.0D);
		this.rotatedNormal[2] = new Vec3(0.0D, 0.0D, 0.0D);
	}

	static AABB getInitialAABB(double posX, double posY, double posZ, double center_x, double center_y, double center_z)
	{
		double xLength = Math.abs(posX) + Math.abs(center_x);
		double yLength = Math.abs(posY) + Math.abs(center_y);
		double zLength = Math.abs(posZ) + Math.abs(center_z);
		double maxLength = Math.max(xLength, Math.max(yLength, zLength));
		return new AABB(maxLength, maxLength, maxLength, -maxLength, -maxLength, -maxLength);
	}

	/**
	 * make 2d obb
	 * 
	 * @param pos1        left
	 * @param pos2        right
	 * @param modelCenter central position
	 */
	public CubeCollider(AABB entityCallAABB, double pos1_x, double pos1_y, double pos1_z, double pos2_x, double pos2_y,
			double pos2_z, double norm1_x, double norm1_y, double norm1_z, double norm2_x, double norm2_y,
			double norm2_z, double center_x, double center_y, double center_z)
	{
		super(new Vec3(center_x, center_y, center_z), entityCallAABB);
		this.modelVertex = new Vec3[2];
		this.modelNormal = new Vec3[2];
		this.rotatedVertex = new Vec3[2];
		this.rotatedNormal = new Vec3[2];
		this.modelVertex[0] = new Vec3(pos1_x, pos1_y, pos1_z);
		this.modelVertex[1] = new Vec3(pos2_x, pos2_y, pos2_z);
		this.modelNormal[0] = new Vec3(norm1_x, norm1_y, norm1_z);
		this.modelNormal[1] = new Vec3(norm2_x, norm2_y, norm2_z);
		this.rotatedVertex[0] = new Vec3(0.0D, 0.0D, 0.0D);
		this.rotatedVertex[1] = new Vec3(0.0D, 0.0D, 0.0D);
		this.rotatedNormal[0] = new Vec3(0.0D, 0.0D, 0.0D);
		this.rotatedNormal[1] = new Vec3(0.0D, 0.0D, 0.0D);
	}

	/**
	 * make obb from aabb
	 * 
	 * @param aabbCopy
	 */
	public CubeCollider(AABB aabbCopy)
	{
		super(null, null);
		this.modelVertex = null;
		this.modelNormal = null;
		double xSize = (aabbCopy.maxX - aabbCopy.minX) / 2;
		double ySize = (aabbCopy.maxY - aabbCopy.minY) / 2;
		double zSize = (aabbCopy.maxZ - aabbCopy.minZ) / 2;
		this.worldCenter = new Vec3(-((float) aabbCopy.minX + xSize), (float) aabbCopy.minY + ySize,
				-((float) aabbCopy.minZ + zSize));
		this.rotatedVertex = new Vec3[4];
		this.rotatedNormal = new Vec3[3];
		this.rotatedVertex[0] = new Vec3(-xSize, ySize, -zSize);
		this.rotatedVertex[1] = new Vec3(-xSize, ySize, zSize);
		this.rotatedVertex[2] = new Vec3(xSize, ySize, zSize);
		this.rotatedVertex[3] = new Vec3(xSize, ySize, -zSize);
		this.rotatedNormal[0] = new Vec3(1, 0, 0);
		this.rotatedNormal[1] = new Vec3(0, 1, 0);
		this.rotatedNormal[2] = new Vec3(0, 0, 1);
	}

	/**
	 * Transform every elements of this Bounding Box
	 **/
	@Override
	public void transform(PublicMatrix4f mat)
	{
		PublicMatrix4f rotationMatrix = mat.removeTranslation();

		for (int i = 0; i < this.modelVertex.length; i++)
		{
			this.rotatedVertex[i] = PublicMatrix4f.transform(rotationMatrix, this.modelVertex[i]);
		}

		for (int i = 0; i < this.modelNormal.length; i++)
		{
			this.rotatedNormal[i] = PublicMatrix4f.transform(rotationMatrix, this.modelNormal[i]);
		}

		super.transform(mat);
	}

	public boolean isCollideWith(CubeCollider opponent)
	{
		Vec3 toOpponent = opponent.worldCenter.subtract(this.worldCenter);

		for (Vec3 seperateAxis : this.rotatedNormal)
		{
			if (!collisionDetection(seperateAxis, toOpponent, this, opponent))
			{
				return false;
			}
		}

		for (Vec3 seperateAxis : opponent.rotatedNormal)
		{
			if (!collisionDetection(seperateAxis, toOpponent, this, opponent))
			{
				return false;
			}
		}

		return true;
	}
	
	@Override
	public Collider getScaledCollider(float scale)
	{
		Vec3 pos = this.modelVertex[1];
		Vec3 center = this.modelCenter;
		return new CubeCollider(pos.x() * scale, pos.y() * scale, pos.z() * scale, center.x() * scale, center.y() * scale,
				center.z() * scale);
	}

	@Override
	public boolean collide(Entity entity)
	{
		CubeCollider obb = new CubeCollider(entity.getBoundingBox());
		return this.isCollideWith(obb);
	}

	private static boolean collisionDetection(Vec3 seperateAxis, Vec3 toOpponent, CubeCollider box1, CubeCollider box2)
	{
		Vec3 maxProj1 = null, maxProj2 = null, distance;
		double maxDot1 = -1, maxDot2 = -1;
		distance = seperateAxis.dot(toOpponent) > 0.0F ? toOpponent : toOpponent.scale(-1.0D);

		for (Vec3 vertexVector : box1.rotatedVertex)
		{
			Vec3 temp = seperateAxis.dot(vertexVector) > 0.0F ? vertexVector : vertexVector.scale(-1.0D);
			double dot = seperateAxis.dot(temp);

			if (dot > maxDot1)
			{
				maxDot1 = dot;
				maxProj1 = temp;
			}
		}

		for (Vec3 vertexVector : box2.rotatedVertex)
		{
			Vec3 temp = seperateAxis.dot(vertexVector) > 0.0F ? vertexVector : vertexVector.scale(-1.0D);
			double dot = seperateAxis.dot(temp);

			if (dot > maxDot2)
			{
				maxDot2 = dot;
				maxProj2 = temp;
			}
		}

		return !(MathUtils.projectVector(distance, seperateAxis).length() > MathUtils.projectVector(maxProj1, seperateAxis).length() + MathUtils.projectVector(maxProj2, seperateAxis).length());
	}

	@Override
	public String toString()
	{
		return String.format("Center : [%f, %f, %f],  Direction : [%f, %f, %f]", this.worldCenter.x, this.worldCenter.y,
				this.worldCenter.z, this.rotatedVertex[1].x, this.rotatedVertex[1].y, this.rotatedVertex[1].z);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawInternal(PoseStack matrixStackIn, MultiBufferSource buffer, PublicMatrix4f pose, boolean red)
	{
		VertexConsumer vertexBuilder = buffer.getBuffer(ModRenderTypes.debugCollider());
		PublicMatrix4f transpose = new PublicMatrix4f();
		PublicMatrix4f.transpose(pose, transpose);
		matrixStackIn.pushPose();
		MathUtils.translateStack(matrixStackIn, pose);
		MathUtils.rotateStack(matrixStackIn, transpose);
		Matrix4f matrix = matrixStackIn.last().pose();
		Vec3 vec = this.modelVertex[1];
		float maxX = (float) (this.modelCenter.x + vec.x);
		float maxY = (float) (this.modelCenter.y + vec.y);
		float maxZ = (float) (this.modelCenter.z + vec.z);
		float minX = (float) (this.modelCenter.x - vec.x);
		float minY = (float) (this.modelCenter.y - vec.y);
		float minZ = (float) (this.modelCenter.z - vec.z);
		float color = red ? 0.0F : 1.0F;
		vertexBuilder.vertex(matrix, maxX, maxY, maxZ).color(1.0F, color, color, 1.0F).endVertex();
		vertexBuilder.vertex(matrix, maxX, maxY, minZ).color(1.0F, color, color, 1.0F).endVertex();
		vertexBuilder.vertex(matrix, minX, maxY, minZ).color(1.0F, color, color, 1.0F).endVertex();
		vertexBuilder.vertex(matrix, minX, maxY, maxZ).color(1.0F, color, color, 1.0F).endVertex();
		vertexBuilder.vertex(matrix, maxX, maxY, maxZ).color(1.0F, color, color, 1.0F).endVertex();
		vertexBuilder.vertex(matrix, maxX, minY, maxZ).color(1.0F, color, color, 1.0F).endVertex();
		vertexBuilder.vertex(matrix, maxX, minY, minZ).color(1.0F, color, color, 1.0F).endVertex();
		vertexBuilder.vertex(matrix, maxX, maxY, minZ).color(1.0F, color, color, 1.0F).endVertex();
		vertexBuilder.vertex(matrix, maxX, minY, minZ).color(1.0F, color, color, 1.0F).endVertex();
		vertexBuilder.vertex(matrix, minX, minY, minZ).color(1.0F, color, color, 1.0F).endVertex();
		vertexBuilder.vertex(matrix, minX, maxY, minZ).color(1.0F, color, color, 1.0F).endVertex();
		vertexBuilder.vertex(matrix, minX, minY, minZ).color(1.0F, color, color, 1.0F).endVertex();
		vertexBuilder.vertex(matrix, minX, minY, maxZ).color(1.0F, color, color, 1.0F).endVertex();
		vertexBuilder.vertex(matrix, minX, maxY, maxZ).color(1.0F, color, color, 1.0F).endVertex();
		vertexBuilder.vertex(matrix, minX, minY, maxZ).color(1.0F, color, color, 1.0F).endVertex();
		vertexBuilder.vertex(matrix, maxX, minY, maxZ).color(1.0F, color, color, 1.0F).endVertex();
		matrixStackIn.popPose();
	}
}
