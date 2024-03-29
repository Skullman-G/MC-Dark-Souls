package com.skullmangames.darksouls.core.util.parser.xml.collada;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.common.animation.Joint;
import com.skullmangames.darksouls.common.animation.Keyframe;
import com.skullmangames.darksouls.common.animation.JointTransform;
import com.skullmangames.darksouls.common.animation.TransformSheet;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;
import com.skullmangames.darksouls.core.util.parser.xml.XmlNode;
import com.skullmangames.darksouls.core.util.parser.xml.XmlParser;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.phys.Vec3;

import com.skullmangames.darksouls.client.renderer.entity.model.Armature;

public class AnimationDataExtractor
{
	private static final PublicMatrix4f CORRECTION = new PublicMatrix4f().rotate((float) Math.toRadians(-90),
			new Vector3f(1, 0, 0));

	private static TransformSheet getTransformSheet(String[] times, String[] trasnformMatrix,
			PublicMatrix4f invLocalTransform, boolean correct)
	{
		List<Keyframe> keyframeList = new ArrayList<Keyframe>();

		for (int i = 0; i < times.length; i++)
		{
			float timeStamp = Float.parseFloat(times[i]);

			if (timeStamp < 0) continue;

			float[] matrixValue = new float[16];
			for (int j = 0; j < 16; j++)
			{
				matrixValue[j] = Float.parseFloat(trasnformMatrix[i * 16 + j]);
			}

			FloatBuffer buffer = FloatBuffer.allocate(16);
			buffer.put(matrixValue);
			buffer.flip();

			PublicMatrix4f matrix = new PublicMatrix4f();
			matrix.load(buffer);
			matrix.transpose();

			if (correct)
			{
				PublicMatrix4f.mul(CORRECTION, matrix, matrix);
			}

			PublicMatrix4f.mul(invLocalTransform, matrix, matrix);

			JointTransform transform = new JointTransform(new Vector3f(matrix.m30, matrix.m31, matrix.m32),
					matrix.toQuaternion(),
					new Vector3f((float) new Vec3(matrix.m00, matrix.m01, matrix.m02).length(),
							(float) new Vec3(matrix.m10, matrix.m11, matrix.m12).length(),
							(float) new Vec3(matrix.m20, matrix.m21, matrix.m22).length()));
			keyframeList.add(new Keyframe(timeStamp, transform));
		}

		TransformSheet sheet = new TransformSheet(keyframeList);

		return sheet;
	}

	public static void extractAnimation(ResourceManager resourceManager, ResourceLocation location, StaticAnimation data, Armature armature)
	{
		BufferedReader bufreader = null;
		try
		{
			BufferedInputStream inputStream = new BufferedInputStream(resourceManager.getResource(location).getInputStream());
			bufreader = new BufferedReader(new InputStreamReader(inputStream));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		XmlNode rootNode = XmlParser.loadXmlFile(bufreader);
		List<XmlNode> jointAnimations = rootNode.getChild("library_animations").getChildren("animation");
		boolean root = true;

		for (XmlNode jointAnimation : jointAnimations)
		{
			String jointName = jointAnimation.getAttributeValue("id");
			String input = jointAnimation.getChild("sampler").getChildWithAttributeValue("input", "semantic", "INPUT")
					.getAttributeValue("source").substring(1);
			String output = jointAnimation.getChild("sampler").getChildWithAttributeValue("input", "semantic", "OUTPUT")
					.getAttributeValue("source").substring(1);

			String[] timeValue = jointAnimation.getChildWithAttributeValue("source", "id", input)
					.getChild("float_array").getData().split(" ");
			String[] matrixArray = jointAnimation.getChildWithAttributeValue("source", "id", output)
					.getChild("float_array").getData().split(" ");

			String fir = jointName.substring(9);
			if (fir.length() - 12 <= 0)
			{
				if (jointName.contains("Armature_"))
					continue;
				System.err.println("Joint " + jointName + " not correctly titled.");
			}
			String sec = fir.substring(0, fir.length() - 12);

			Joint joint = armature.searchJointByName(sec);

			if (joint == null)
			{
				IllegalArgumentException exception = new IllegalArgumentException();
				System.err.println("Can't find joint " + sec + ". Did you use the wrong armature?");
				exception.printStackTrace();
				throw exception;
			}

			TransformSheet sheet = getTransformSheet(timeValue, matrixArray,
					PublicMatrix4f.invert(joint.getLocalTransform(), null), root);
			data.addSheet(sec, sheet);
			data.setTotalTime(Float.parseFloat(timeValue[timeValue.length - 1]));
			root = false;
		}
	}
}