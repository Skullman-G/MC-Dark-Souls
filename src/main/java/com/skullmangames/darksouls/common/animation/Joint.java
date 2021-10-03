package com.skullmangames.darksouls.common.animation;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

public class Joint
{
	private final List<Joint> subJoints = new ArrayList<Joint>();
	private final int jointId;
	private final String jointName;
	private final PublicMatrix4f localTransform;
	private PublicMatrix4f inversedTransform = new PublicMatrix4f();
	private PublicMatrix4f animatedTransform = new PublicMatrix4f();
	
	public Joint(String name, int jointID, PublicMatrix4f localTransform)
	{
		this.jointId = jointID;
		this.jointName = name;
		this.localTransform = localTransform;
	}

	public void addSubJoint(Joint... joints)
	{
		for (Joint joint : joints) this.subJoints.add(joint);
	}
	
	public void setAnimatedTransform(PublicMatrix4f animatedTransform)
	{
		this.animatedTransform = animatedTransform;
	}

	public void initializeAnimationTransform()
	{
		this.animatedTransform.setIdentity();

		for (Joint joint : this.subJoints)
		{
			joint.initializeAnimationTransform();
		}
	}
	
	public void setInversedModelTransform(PublicMatrix4f superTransform)
	{
		PublicMatrix4f modelTransform = PublicMatrix4f.mul(superTransform, this.localTransform, null);
		PublicMatrix4f.invert(modelTransform, this.inversedTransform);
		
		for (Joint joint : this.subJoints)
		{
			joint.setInversedModelTransform(modelTransform);
		}
	}
	
	public PublicMatrix4f getLocalTrasnform()
	{
		return this.localTransform;
	}

	public PublicMatrix4f getAnimatedTransform()
	{
		return this.animatedTransform;
	}

	public PublicMatrix4f getInversedModelTransform()
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
}
