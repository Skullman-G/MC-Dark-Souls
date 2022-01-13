package com.skullmangames.darksouls.common.animation;

import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;
import com.skullmangames.darksouls.core.util.math.vector.ModQuaternion;

public class JointTransform
{
	public static final JointTransform DEFAULT = new JointTransform(new Vector3f(0.0F,0.0F,0.0F), new ModQuaternion(0.0F,0.0F,0.0F,1.0F), new Vector3f(1.0F,1.0F,1.0F));
	
	private Vector3f position;
	private Vector3f scale;
	private ModQuaternion rotation;
	private ModQuaternion customRotation;

	public JointTransform(Vector3f position, ModQuaternion rotation, Vector3f scale)
	{
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}
	
	public JointTransform(Vector3f position, ModQuaternion rotation, ModQuaternion customRotation, Vector3f scale)
	{
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.customRotation = customRotation;
	}
	
	public Vector3f getPosition()
	{
		return position;
	}

	public ModQuaternion getRotation()
	{
		return rotation;
	}
	
	public ModQuaternion getCustomRotation()
	{
		return customRotation;
	}
	
	public Vector3f getScale()
	{
		return scale;
	}

	public void setRotation(ModQuaternion quat)
	{
		this.rotation = quat;
	}
	
	public void setCustomRotation(ModQuaternion quat)
	{
		this.customRotation = quat;
	}
	
	public PublicMatrix4f toTransformMatrix()
	{
		PublicMatrix4f matrix = new PublicMatrix4f();
		matrix.translate(position);
		PublicMatrix4f.mul(matrix, rotation.toRotationMatrix(), matrix);
		matrix.scale(this.scale);
		return matrix;
	}
	
	public static JointTransform interpolate(JointTransform prev, JointTransform next, float progression)
	{
		if (prev == null || next == null)
		{
			return JointTransform.DEFAULT;
		}
		
		Vector3f vertex = interpolate(prev.position, next.position, progression);
		ModQuaternion rot = ModQuaternion.interpolate(prev.rotation, next.rotation, progression);
		Vector3f scale = interpolate(prev.scale, next.scale, progression);
		
		if (prev.customRotation != null || next.customRotation != null)
		{
			if (prev.customRotation == null)
			{
				prev.customRotation = new ModQuaternion(0, 0, 0, 1);
			}
			if (next.customRotation == null)
			{
				next.customRotation = new ModQuaternion(0, 0, 0, 1);
			}
			
			return new JointTransform(vertex, rot, ModQuaternion.interpolate(prev.customRotation, next.customRotation, progression), scale);
			
		}
		else
		{
			return new JointTransform(vertex, rot, scale);
		}
	}
	
	private static Vector3f interpolate(Vector3f start, Vector3f end, float progression)
	{
		float x = start.x() + (end.x() - start.x()) * progression;
		float y = start.y() + (end.y() - start.y()) * progression;
		float z = start.z() + (end.z() - start.z()) * progression;
		return new Vector3f(x, y, z);
	}
}