package com.skullmangames.darksouls.core.util.physics;

import java.util.Iterator;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import com.skullmangames.darksouls.client.renderer.ModRenderTypes;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;
import com.skullmangames.darksouls.core.util.math.vector.Vector3fHelper;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Collider
{
	private final Vector3f modelCenter;
	private AABB hitboxAABB;
	
	private Vector3f worldCenter;
	
	private final Vector3f[] modelVertex;
	private final Vector3f[] modelNormal;
	
	private Vector3f[] rotatedVertex;
	private Vector3f[] rotatedNormal;
	
	/**
	 * Constructs a Collider
	 * @param pos The upper right front corner of the collider
	 * @param center Center of the collider
	 */
	public Collider(float posX, float posY, float posZ, float centerX, float centerY, float centerZ)
	{
		this.modelCenter = new Vector3f(centerX, centerY, centerZ);
		this.hitboxAABB = null;
		this.worldCenter = new Vector3f();
		
		float xLength = Math.abs(posX - centerX);
		float yLength = Math.abs(posY - centerY);
		float zLength = Math.abs(posZ - centerZ);
		
		float maxLength = Math.max(xLength, Math.max(yLength, zLength));
		
		this.hitboxAABB = new AABB(maxLength, maxLength, maxLength, -maxLength, -maxLength, -maxLength);
		
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
	 * Constructs a Collider
	 * @param pos The upper right front corner of the collider
	 * @param center Center of the collider
	 */
	public Collider(AABB entityCallAABB, float pos1_x, float pos1_y, float pos1_z, float pos2_x, float pos2_y, float pos2_z, 
			float norm1_x, float norm1_y, float norm1_z, float norm2_x, float norm2_y, float norm2_z, float center_x, float center_y, float center_z)
	{
		this.modelCenter = new Vector3f(center_x, center_y, center_z);
		this.hitboxAABB = entityCallAABB;
		this.worldCenter = new Vector3f();
		
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
	 * Copies a Collider from an AABB
	 */
	public Collider(AABB aabb)
	{
		this.modelCenter = null;
		this.hitboxAABB = null;
		this.worldCenter = new Vector3f();
		
		modelVertex = null;
		modelNormal = null;
		
		float xSize = (float) (aabb.maxX - aabb.minX) / 2;
		float ySize = (float) (aabb.maxY - aabb.minY) / 2;
		float zSize = (float) (aabb.maxZ - aabb.minZ) / 2;
		
		worldCenter = new Vector3f(-((float)aabb.minX + xSize), (float)aabb.minY + ySize, -((float)aabb.minZ + zSize));
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
	
	public Vector3d getCenter()
	{
		return new Vector3d(worldCenter.x(), worldCenter.y(), worldCenter.z());
	}

	public AABB getHitboxAABB()
	{
		return hitboxAABB.move(-worldCenter.x(), worldCenter.y(), -worldCenter.z());
	}
	
	public void extractHitEntities(List<Entity> entities)
	{
		Iterator<Entity> iterator = entities.iterator();
		while (iterator.hasNext())
		{
			Entity entity = iterator.next();
			if (!isCollideWith(entity))
			{
				iterator.remove();
			}
		}
	}
	
	public Collider getScaledCollider(float scale)
	{
		Vector3f pos = this.modelVertex[1];
		Vector3f center = this.modelCenter;
		return new Collider(pos.x() * scale, pos.y() * scale, pos.z() * scale, center.x() * scale, center.y() * scale, center.z() * scale);
	}
	
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
		
		Vector4f temp = new Vector4f(0,0,0,1);
		
		temp.setX(this.modelCenter.x());
		temp.setY(this.modelCenter.y());
		temp.setZ(this.modelCenter.z());
		PublicMatrix4f.transform(mat, temp, temp);
		this.worldCenter.setX(temp.x());
		this.worldCenter.setY(temp.y());
		this.worldCenter.setZ(temp.z());
	}
	
	public boolean isCollideWith(Collider opponent)
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
		
		return true;
	}
	
	public boolean isCollideWith(Entity entity)
	{
		Collider obb = new Collider(entity.getBoundingBox());
		
		return isCollideWith(obb);
	}
	
	private static boolean collisionDetection(Vector3f seperateAxis, Vector3f toOpponent, Collider box1, Collider box2)
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
	
	@OnlyIn(Dist.CLIENT)
	public void draw(PoseStack matrixStackIn, MultiBufferSource buffer, PublicMatrix4f pose, float partialTicks, boolean red)
	{
		VertexConsumer vertexBuilder = buffer.getBuffer(ModRenderTypes.getBoundingBox());
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