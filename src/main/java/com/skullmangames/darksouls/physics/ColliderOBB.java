package com.skullmangames.darksouls.physics;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.skullmangames.darksouls.client.renderer.ModRenderTypes;
import com.skullmangames.darksouls.util.math.MathUtils;
import com.skullmangames.darksouls.util.math.vector.PublicMatrix4f;
import com.skullmangames.darksouls.util.math.vector.Vector3fHelper;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ColliderOBB extends Collider
{
	private final Vector3f[] modelVertex;
	private final Vector3f[] modelNormal;
	
	private Vector3f[] rotatedVertex;
	private Vector3f[] rotatedNormal;
	
	/**
	 * make 3d obb
	 * @param pos1 left_back
	 * @param pos2 left_front
	 * @param pos3 right_front
	 * @param pos4 right_back
	 * @param modelCenter central position
	 */
	public ColliderOBB(float posX, float posY, float posZ, float center_x, float center_y, float center_z)
	{
		super(new Vector3f(center_x, center_y, center_z), null);
		
		float xLength = Math.abs(posX - center_x);
		float yLength = Math.abs(posY - center_y);
		float zLength = Math.abs(posZ - center_z);
		
		float maxLength = Math.max(xLength, Math.max(yLength, zLength));
		
		hitboxAABB = new AxisAlignedBB(maxLength, maxLength, maxLength, -maxLength, -maxLength, -maxLength);
		
		modelVertex = new Vector3f[4];
		modelNormal = new Vector3f[3];
		rotatedVertex = new Vector3f[4];
		rotatedNormal = new Vector3f[3];
		
		this.modelVertex[0] = new Vector3f(posX, posY, -posZ);
		this.modelVertex[1] = new Vector3f(posX, posY, posZ);
		this.modelVertex[2] = new Vector3f(-posX, posY, posZ);
		this.modelVertex[3] = new Vector3f(-posX, posY, -posZ);
		this.modelNormal[0] = new Vector3f(1,0,0);
		this.modelNormal[1] = new Vector3f(0,1,0);
		this.modelNormal[2] = new Vector3f(0,0,-1);
		
		this.rotatedVertex[0] = new Vector3f();
		this.rotatedVertex[1] = new Vector3f();
		this.rotatedVertex[2] = new Vector3f();
		this.rotatedVertex[3] = new Vector3f();
		this.rotatedNormal[0] = new Vector3f();
		this.rotatedNormal[1] = new Vector3f();
		this.rotatedNormal[2] = new Vector3f();
	}
	
	/**
	 * make 2d obb
	 * @param pos1 left
	 * @param pos2 right
	 * @param modelCenter central position
	 */
	public ColliderOBB(AxisAlignedBB entityCallAABB, float pos1_x, float pos1_y, float pos1_z, float pos2_x, float pos2_y, float pos2_z, 
			float norm1_x, float norm1_y, float norm1_z, float norm2_x, float norm2_y, float norm2_z, float center_x, float center_y, float center_z)
	{
		super(new Vector3f(center_x, center_y, center_z), entityCallAABB);
		
		modelVertex = new Vector3f[2];
		modelNormal = new Vector3f[2];
		rotatedVertex = new Vector3f[2];
		rotatedNormal = new Vector3f[2];
		
		this.modelVertex[0] = new Vector3f(pos1_x, pos1_y, pos1_z);
		this.modelVertex[1] = new Vector3f(pos2_x, pos2_y, pos2_z);
		this.modelNormal[0] = new Vector3f(norm1_x,norm1_y,norm1_z);
		this.modelNormal[1] = new Vector3f(norm2_x,norm2_y,norm2_z);
		
		this.rotatedVertex[0] = new Vector3f();
		this.rotatedVertex[1] = new Vector3f();
		this.rotatedNormal[0] = new Vector3f();
		this.rotatedNormal[1] = new Vector3f();
	}
	
	/**
	 * make obb from aabb
	 * @param aabbCopy
	 */
	public ColliderOBB(AxisAlignedBB aabbCopy)
	{
		super(null, null);
		modelVertex = null;
		modelNormal = null;
		
		float xSize = (float) (aabbCopy.maxX - aabbCopy.minX) / 2;
		float ySize = (float) (aabbCopy.maxY - aabbCopy.minY) / 2;
		float zSize = (float) (aabbCopy.maxZ - aabbCopy.minZ) / 2;
		
		worldCenter = new Vector3f(-((float)aabbCopy.minX + xSize), (float)aabbCopy.minY + ySize, -((float)aabbCopy.minZ + zSize));
		rotatedVertex = new Vector3f[4];
		rotatedNormal = new Vector3f[3];
		
		this.rotatedVertex[0] = new Vector3f(-xSize, ySize, -zSize);
		this.rotatedVertex[1] = new Vector3f(-xSize, ySize, zSize);
		this.rotatedVertex[2] = new Vector3f(xSize, ySize, zSize);
		this.rotatedVertex[3] = new Vector3f(xSize, ySize, -zSize);
		this.rotatedNormal[0] = new Vector3f(1,0,0);
		this.rotatedNormal[1] = new Vector3f(0,1,0);
		this.rotatedNormal[2] = new Vector3f(0,0,1);
	}
	
	/**
	 * Transform every elements of this Bounding Box
	 **/
	@Override
	public void transform(PublicMatrix4f mat)
	{
		Vector4f tempVector = new Vector4f(0,0,0,1);
		PublicMatrix4f rotationMatrix = new PublicMatrix4f(mat);
		rotationMatrix.m30 = 0;
		rotationMatrix.m31 = 0;
		rotationMatrix.m32 = 0;
		
		for(int i = 0; i < modelVertex.length; i ++)
		{
			tempVector.setX(modelVertex[i].x());
			tempVector.setY(modelVertex[i].y());
			tempVector.setZ(modelVertex[i].z());
			PublicMatrix4f.transform(rotationMatrix, tempVector, tempVector);
			rotatedVertex[i].setX(tempVector.x());
			rotatedVertex[i].setY(tempVector.y());
			rotatedVertex[i].setZ(tempVector.z());
		}
		
		for(int i = 0; i < modelNormal.length; i ++)
		{
			tempVector.setX(modelNormal[i].x());
			tempVector.setY(modelNormal[i].y());
			tempVector.setZ(modelNormal[i].z());
			PublicMatrix4f.transform(rotationMatrix, tempVector, tempVector);
			rotatedNormal[i].setX(tempVector.x());
			rotatedNormal[i].setY(tempVector.y());
			rotatedNormal[i].setZ(tempVector.z());
		}
		
		super.transform(mat);
	}
	
	public boolean isCollideWith(ColliderOBB opponent)
	{
		Vector3f toOpponent = Vector3fHelper.sub(opponent.worldCenter, this.worldCenter, null);
		
		for(Vector3f seperateAxis : this.rotatedNormal)
		{
			if(!collisionDetection(seperateAxis, toOpponent, this, opponent))
			{
				return false;
			}
		}
		
		for(Vector3f seperateAxis : opponent.rotatedNormal)
		{
			if(!collisionDetection(seperateAxis, toOpponent, this, opponent))
			{
				return false;
			}
		}
		
		/** Below code detects whether the each line of obb is collide but it is disabled for better performance
		for(Vector3f norm1 : this.rotatedNormal)
		{
			for(Vector3f norm2 : opponent.rotatedNormal)
			{
				Vector3f seperateAxis = Vector3f.cross(norm1, norm2, null);
				
				if(seperateAxis.x + seperateAxis.y + seperateAxis.z == 0)
				{
					continue;
				}
				
				if(!collisionLogic(seperateAxis, toOpponent, this, opponent))
				{
					return false;
				}
			}
		}
		 **/
		
		return true;
	}
	
	@Override
	public boolean isCollideWith(Entity entity)
	{
		ColliderOBB obb = new ColliderOBB(entity.getBoundingBox());
		
		return isCollideWith(obb);
	}
	
	private static boolean collisionDetection(Vector3f seperateAxis, Vector3f toOpponent, ColliderOBB box1, ColliderOBB box2)
	{
		Vector3f maxProj1 = null, maxProj2 = null, distance;
		float maxDot1 = -1, maxDot2 = -1;
		distance = Vector3fHelper.dot(seperateAxis, toOpponent) > 0 ? toOpponent : new Vector3f(-toOpponent.x(), -toOpponent.y(), -toOpponent.z());
		
		for(Vector3f vertexVector : box1.rotatedVertex)
		{
			Vector3f temp = Vector3fHelper.dot(seperateAxis, vertexVector) > 0 ? vertexVector : new Vector3f(-vertexVector.x(), -vertexVector.y(), -vertexVector.z());
			float dot = Vector3fHelper.dot(seperateAxis, temp);
			if(dot > maxDot1)
			{
				maxDot1 = dot;
				maxProj1 = temp;
			}
		}
		
		for(Vector3f vertexVector : box2.rotatedVertex)
		{
			Vector3f temp = Vector3fHelper.dot(seperateAxis, vertexVector) > 0 ? vertexVector : new Vector3f(-vertexVector.x(), -vertexVector.y(), -vertexVector.z());
			float dot = Vector3fHelper.dot(seperateAxis, temp);
			if(dot > maxDot2)
			{
				maxDot2 = dot;
				maxProj2 = temp;
			}
		}
		
		if(getProjectedScale(seperateAxis, distance) >= getProjectedScale(seperateAxis, maxProj1) + getProjectedScale(seperateAxis, maxProj2))
		{
			return false;
		}
		return true;
	}
	
	private static float getProjectedScale(Vector3f normal, Vector3f projecting)
	{
		float dot = Vector3fHelper.dot(normal, projecting);
		float normalScale = 1 / ((normal.x() * normal.x()) + (normal.y() * normal.y()) + (normal.z() * normal.z()));
		Vector3f projVec = new Vector3f(dot * normal.x() * normalScale, dot * normal.y() * normalScale, dot * normal.z() * normalScale);
		
		return (float) Math.sqrt((projVec.x() * projVec.x()) + (projVec.y() * projVec.y()) + (projVec.z() * projVec.z())); 
	}
	
	@OnlyIn(Dist.CLIENT) @Override
	public void draw(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, PublicMatrix4f pose, float partialTicks, boolean red)
	{
		IVertexBuilder vertexBuilder = buffer.getBuffer(ModRenderTypes.getBoundingBox());
		PublicMatrix4f transpose = new PublicMatrix4f();
		PublicMatrix4f.transpose(pose, transpose);
		MathUtils.translateStack(matrixStackIn, pose);
		PublicMatrix4f.rotateStack(matrixStackIn, transpose);
        Matrix4f matrix = matrixStackIn.last().pose();
        Vector3f vec = this.modelVertex[1];
        float maxX = this.modelCenter.x() + vec.x();
        float maxY = this.modelCenter.y() + vec.y();
        float maxZ = this.modelCenter.z() + vec.z();
        float minX = this.modelCenter.x() - vec.x();
        float minY = this.modelCenter.y() - vec.y();
        float minZ = this.modelCenter.z() - vec.z();
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
	}
}