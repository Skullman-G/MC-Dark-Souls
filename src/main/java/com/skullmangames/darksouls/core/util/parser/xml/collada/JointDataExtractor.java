package com.skullmangames.darksouls.core.util.parser.xml.collada;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.common.animation.Joint;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;
import com.skullmangames.darksouls.core.util.parser.xml.XmlNode;

public class JointDataExtractor
{
	private static final ModMatrix4f CORRECTION = new ModMatrix4f().rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0));
	
	private int jointNumber = 1;
	
	private XmlNode skeleton;
	private Map<String, Integer> rawJointMap;
	private Map<String, Joint> joints = new HashMap<>();
	
	public JointDataExtractor(XmlNode skeleton, Map<String, Integer> rawJointMap)
	{
		this.skeleton = skeleton;
		this.rawJointMap = rawJointMap;
	}
	
	public Joint extractSkeletonData()
	{
		XmlNode rootNode = skeleton.getChild("node");
		Joint root = getRootJoint(rootNode);
		bindJointData(root, rootNode.getChildren("node"));
		
		return root;
	}
	
	private void bindJointData(Joint root, List<XmlNode> nodes)
	{
		for(XmlNode node : nodes)
		{
			Joint joint = getJoint(node);
			root.addSubJoint(joint);
			bindJointData(joint, node.getChildren("node"));
		}
	}
	
	private Joint getRootJoint(XmlNode node)
	{
		String name = node.getAttributeValue("sid");
		String[] matrixData = node.getChild("matrix").getData().split(" ");
		ModMatrix4f jointTransform = convertStringToMatrix(matrixData);
		ModMatrix4f.mul(CORRECTION, jointTransform, jointTransform);
		Joint joint = new Joint(name, rawJointMap.get(name), jointTransform);
		joints.put(joint.getName(), joint);
		
		return joint;
	}
	
	private Joint getJoint(XmlNode node)
	{
		jointNumber++;
		
		String name = node.getAttributeValue("sid");
		String[] matrixData = node.getChild("matrix").getData().split(" ");
		ModMatrix4f jointTransform = convertStringToMatrix(matrixData);
		Joint joint = new Joint(name, rawJointMap.get(name), jointTransform);
		joints.put(joint.getName(), joint);
		
		return joint;
	}
	
	private ModMatrix4f convertStringToMatrix(String[] data)
	{
		float[] mat4 = new float[16];
		for(int i = 0; i < 16; i++)
		{
			mat4[i] = Float.parseFloat(data[i]);
		}
		FloatBuffer floatbuffer = FloatBuffer.allocate(16);
		floatbuffer.put(mat4);
		floatbuffer.flip();
		ModMatrix4f transform = new ModMatrix4f();
		transform.load(floatbuffer);
		transform.transpose();
		return transform;
	}

	public Map<String, Joint> getJointTable()
	{
		return this.joints;
	}
	
	public int getJointNumber()
	{
		return jointNumber;
	}
}