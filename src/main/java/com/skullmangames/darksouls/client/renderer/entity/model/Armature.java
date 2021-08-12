package com.skullmangames.darksouls.client.renderer.entity.model;

import java.util.Map;

import com.skullmangames.darksouls.common.animation.Joint;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

public class Armature
{
	private final Map<Integer, Joint> jointTable;
	private final Joint jointHierarcy;
	private final int jointNumber;

	public Armature(int jointNumber, Joint rootJoint, Map<Integer, Joint> jointTable)
	{
		this.jointNumber = jointNumber;
		this.jointHierarcy = rootJoint;
		this.jointTable = jointTable;
	}

	public PublicMatrix4f[] getJointTransforms()
	{
		PublicMatrix4f[] jointMatrices = new PublicMatrix4f[jointNumber];
		jointToTransformMatrixArray(jointHierarcy, jointMatrices);
		return jointMatrices;
	}

	public Joint findJointById(int id)
	{
		return this.jointTable.get(id);
	}

	public Joint findJointByName(String name)
	{
		for (int i : jointTable.keySet())
		{
			if (jointTable.get(i).getName().equals(name))
			{
				return jointTable.get(i);
			}
		}

		return null;
	}

	public void initializeTransform()
	{
		this.jointHierarcy.initializeAnimationTransform();
	}

	public int getJointNumber()
	{
		return jointNumber;
	}

	public Joint getJointHierarcy()
	{
		return jointHierarcy;
	}

	private void jointToTransformMatrixArray(Joint joint, PublicMatrix4f[] jointMatrices)
	{
		PublicMatrix4f result = new PublicMatrix4f();
		PublicMatrix4f.mul(joint.getAnimatedTransform(), joint.getInversedModelTransform(), result);
		jointMatrices[joint.getId()] = result;

		for (Joint childJoint : joint.getSubJoints())
		{
			jointToTransformMatrixArray(childJoint, jointMatrices);
		}
	}
}