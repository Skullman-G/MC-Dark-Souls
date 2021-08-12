package com.skullmangames.darksouls.physics;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.util.math.vector.PublicMatrix4f;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Collider
{
	protected final Vector3f modelCenter;
	protected AxisAlignedBB hitboxAABB;
	
	protected Vector3f worldCenter;

	public Collider(Vector3f vertex, @Nullable AxisAlignedBB entityCallAABB)
	{
		this.modelCenter = vertex;
		this.hitboxAABB = entityCallAABB;
		this.worldCenter = new Vector3f();
	}

	public void transform(PublicMatrix4f mat)
	{
		Vector4f temp = new Vector4f(0,0,0,1);
		
		temp.setX(this.modelCenter.x());
		temp.setY(this.modelCenter.y());
		temp.setZ(this.modelCenter.z());
		PublicMatrix4f.transform(mat, temp, temp);
		this.worldCenter.setX(temp.x());
		this.worldCenter.setY(temp.y());
		this.worldCenter.setZ(temp.z());
	}
	
	@OnlyIn(Dist.CLIENT)
	public abstract void draw(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, PublicMatrix4f pose, float partialTicks, boolean red);
	
	public abstract boolean isCollideWith(Entity opponent);
	
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

	public Vector3d getCenter()
	{
		return new Vector3d(worldCenter.x(), worldCenter.y(), worldCenter.z());
	}

	public AxisAlignedBB getHitboxAABB()
	{
		return hitboxAABB.move(-worldCenter.x(), worldCenter.y(), -worldCenter.z());
	}
}