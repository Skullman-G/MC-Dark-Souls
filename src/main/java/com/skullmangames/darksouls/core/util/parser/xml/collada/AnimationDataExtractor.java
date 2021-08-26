package com.skullmangames.darksouls.core.util.parser.xml.collada;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.animation.Joint;
import com.skullmangames.darksouls.common.animation.JointKeyFrame;
import com.skullmangames.darksouls.common.animation.JointTransform;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.animation.TransformSheet;
import com.skullmangames.darksouls.common.animation.types.MixLinkAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;
import com.skullmangames.darksouls.core.util.math.vector.Quaternion;
import com.skullmangames.darksouls.core.util.math.vector.Vector3fHelper;
import com.skullmangames.darksouls.core.util.parser.xml.XmlNode;
import com.skullmangames.darksouls.core.util.parser.xml.XmlParser;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class AnimationDataExtractor
{
	private static final PublicMatrix4f CORRECTION = new PublicMatrix4f().rotate((float) Math.toRadians(-90), new Vector3f(1, 0,0));
	
	private static TransformSheet getTransformSheet(String[] times, String[] trasnformMatrix, PublicMatrix4f invLocalTransform, boolean correct)
	{
		List<JointKeyFrame> keyframeList = new ArrayList<JointKeyFrame>();
		
		for(int i = 0; i < times.length; i++)
		{
			float timeStamp = Float.parseFloat(times[i]);
			
			if(timeStamp < 0)
			{
				continue;
			}
			
			float[] matrixValue = new float[16];
			for(int j = 0; j < 16; j++)
			{
				matrixValue[j] = Float.parseFloat(trasnformMatrix[i*16 + j]);
			}
			
			FloatBuffer buffer = FloatBuffer.allocate(16);
			buffer.put(matrixValue);
			buffer.flip();
			
			PublicMatrix4f matrix = new PublicMatrix4f();
			matrix.load(buffer);
			matrix.transpose();
			
			if(correct)
			{
				PublicMatrix4f.mul(CORRECTION, matrix, matrix);
			}
			
			PublicMatrix4f.mul(invLocalTransform, matrix, matrix);
			
			JointTransform transform = new JointTransform(new Vector3f(matrix.m30, matrix.m31, matrix.m32), Quaternion.fromMatrix(matrix),
					new Vector3f(Vector3fHelper.length(new Vector3f(matrix.m00, matrix.m01, matrix.m02)),
							Vector3fHelper.length(new Vector3f(matrix.m10, matrix.m11, matrix.m12)),
							Vector3fHelper.length(new Vector3f(matrix.m20, matrix.m21, matrix.m22))));
			keyframeList.add(new JointKeyFrame(timeStamp, transform));
		}
		
		TransformSheet sheet = new TransformSheet(keyframeList);
		
		return sheet;
	}
	
	private static BufferedInputStream getInputStream(ResourceLocation resourceLocation) throws FileNotFoundException
	{
		BufferedInputStream inputStream = new BufferedInputStream(DarkSouls.class.getResourceAsStream("/assets/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath()));
		return inputStream;
	}
	
	public static void extractAnimation(ResourceLocation location, StaticAnimation data, Armature armature)
	{
		BufferedReader bufreader = null;
		
		try
		{
			bufreader = new BufferedReader(new InputStreamReader(getInputStream(location)));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		XmlNode rootNode = XmlParser.loadXmlFile(bufreader);
		List<XmlNode> jointAnimations = rootNode.getChild("library_animations").getChildren("animation");
		boolean root = true;
		
		for(XmlNode jointAnimation : jointAnimations)
		{
			String jointName = jointAnimation.getAttributeValue("id");
			String input = jointAnimation.getChild("sampler").getChildWithAttributeValue("input", "semantic", "INPUT").getAttributeValue("source").substring(1);
			String output = jointAnimation.getChild("sampler").getChildWithAttributeValue("input", "semantic", "OUTPUT").getAttributeValue("source").substring(1);
			
			String[] timeValue = jointAnimation.getChildWithAttributeValue("source", "id", input).getChild("float_array").getData().split(" ");
			String[] matrixArray = jointAnimation.getChildWithAttributeValue("source", "id", output).getChild("float_array").getData().split(" ");
			
			String fir = jointName.substring(9);
			if (fir.length() - 12 <= 0)
			{
				System.err.println("Joint " + jointName + " not correctly titled.");
				continue;
			}
			String sec = fir.substring(0, fir.length() - 12);
			
			Joint joint = armature.findJointByName(sec);
			
			if(joint == null)
			{
				IllegalArgumentException exception = new IllegalArgumentException();
				System.err.println("Cant find joint " + sec + ". Did use wrong armature?");
				exception.printStackTrace();
				throw exception;
			}
			
			TransformSheet sheet = getTransformSheet(timeValue, matrixArray, PublicMatrix4f.invert(joint.getLocalTrasnform(), null), root);
			data.addSheet(sec, sheet);
			data.setTotalTime(Float.parseFloat(timeValue[timeValue.length - 1]));
			root = false;
		}
	}
	
	public static void getMixLinkAnimation(float convertTime, Pose currentPose, MixLinkAnimation player)
	{
		player.setLastPose(currentPose);
		player.setTotalTime(convertTime);
	}
}