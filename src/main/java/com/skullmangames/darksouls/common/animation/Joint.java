package com.skullmangames.darksouls.common.animation;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

public class Joint
{
	private final List<Joint> subJoints = new ArrayList<Joint>();
	private final int jointId;
	private final String jointName;
	private final ModMatrix4f localTransform;
	private ModMatrix4f inversedTransform = new ModMatrix4f();
	private ModMatrix4f animatedTransform = new ModMatrix4f();

	public Joint(String name, int jointID, ModMatrix4f localTransform)
	{
		this.jointId = jointID;
		this.jointName = name;
		this.localTransform = localTransform;
	}

	public void addSubJoint(Joint... joints)
	{
		for (Joint joint : joints)
		{
			this.subJoints.add(joint);
		}
	}

	public void setAnimatedTransform(ModMatrix4f animatedTransform)
	{
		this.animatedTransform.load(animatedTransform);
	}

	public void initializeAnimationTransform()
	{
		this.animatedTransform.setIdentity();
		for (Joint joint : this.subJoints)
		{
			joint.initializeAnimationTransform();
		}
	}

	public void setInversedModelTransform(ModMatrix4f parentTransform)
	{
		ModMatrix4f modelTransform = ModMatrix4f.mul(parentTransform, this.localTransform, null);
		ModMatrix4f.invert(modelTransform, this.inversedTransform);

		for (Joint joint : this.subJoints)
		{
			joint.setInversedModelTransform(modelTransform);
		}
	}

	public ModMatrix4f getLocalTransform()
	{
		return this.localTransform;
	}

	public ModMatrix4f getAnimatedTransform()
	{
		return this.animatedTransform;
	}

	public ModMatrix4f getInversedModelTransform()
	{
		return this.inversedTransform;
	}

	public List<Joint> getSubJoints()
	{
		return this.subJoints;
	}

	public String getName()
	{
		return this.jointName;
	}

	public int getId()
	{
		return this.jointId;
	}

	public String searchPath(String path, String joint)
	{
		if (joint.equals(this.getName()))
		{
			return path;
		} else
		{
			int i = 1;
			for (Joint subJoint : this.subJoints)
			{
				String str = subJoint.searchPath(String.valueOf(i) + path, joint);
				i++;
				if (str != null)
				{
					return str;
				}
			}
			return null;
		}
	}
}
