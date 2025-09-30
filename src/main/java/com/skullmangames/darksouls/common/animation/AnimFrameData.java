package com.skullmangames.darksouls.common.animation;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.util.ResourceBuilder;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;
import com.skullmangames.darksouls.core.util.parser.xml.XmlNode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class AnimFrameData
{
	private final ImmutableMap<String, TransformSheet> jointTransforms;
	private final float totalTime;
	
	public AnimFrameData()
	{
		this.jointTransforms = ImmutableMap.of();
		this.totalTime = 0.0F;
	}
	
	public AnimFrameData(ImmutableMap<String, TransformSheet> jointTransforms, float totalTime)
	{
		this.jointTransforms = jointTransforms;
		this.totalTime = totalTime;
	}
	
	public float getTotalTime()
	{
		return this.totalTime;
	}
	
	public boolean isJointEnabled(String joint)
	{
		return this.jointTransforms.containsKey(joint);
	}
	
	public Map<String, TransformSheet> getTransfroms()
	{
		return this.jointTransforms;
	}
	
	public Pose getPoseByTimeRaw(LivingCap<?> entityCap, float time, float partialTicks)
	{
		Pose pose = new Pose();
		this.jointTransforms.forEach((name, transform) ->
		{
			if (!entityCap.isClientSide() || this.isJointEnabled(name))
			{
				pose.putJointData(name, transform.getInterpolatedTransform(time));
			}
		});
		return pose;
	}
	
	public TransformSheet get(String name)
	{
		return this.jointTransforms.get(name);
	}
	
	public static Builder builder(ResourceLocation id)
	{
		return new Builder(id);
	}
	
	public static Builder read(ResourceLocation id, XmlNode collada) throws Exception
	{
		return builder(id).read(collada);
	}
	
	public static class Builder implements ResourceBuilder<AnimFrameData>
	{
		private static final ModMatrix4f CORRECTION = new ModMatrix4f().rotate((float) Math.toRadians(-90),
				new Vector3f(1, 0, 0));
		
		private final ResourceLocation id;
		private final ImmutableMap.Builder<String, TransformSheet> jointTransforms;
		private float totalTime;
		
		private Builder(ResourceLocation id)
		{
			this.id = id;
			this.jointTransforms = ImmutableMap.builder();
			this.totalTime = 0;
		}
		
		public Builder read(XmlNode collada) throws Exception
		{
			List<XmlNode> jointAnimations = collada.getDirectChild("library_animations").getChildren("animation");
			boolean root = true;

			for (XmlNode jointAnimation : jointAnimations)
			{
				String jointName = jointAnimation.getAttributeValue("id");
				String input = jointAnimation.getDirectChild("sampler").getChildWithAttributeValue("input", "semantic", "INPUT")
						.getAttributeValue("source").substring(1);
				String output = jointAnimation.getDirectChild("sampler").getChildWithAttributeValue("input", "semantic", "OUTPUT")
						.getAttributeValue("source").substring(1);

				String[] timeValue = jointAnimation.getChildWithAttributeValue("source", "id", input)
						.getDirectChild("float_array").getData().split(" ");
				String[] matrixArray = jointAnimation.getChildWithAttributeValue("source", "id", output)
						.getDirectChild("float_array").getData().split(" ");

				String fir = jointName.substring(9);
				if (fir.length() - 12 <= 0)
				{
					if (jointName.contains("Armature_")) continue;
					System.err.println("Joint " + jointName + " not correctly titled.");
				}
				String sec = fir.substring(0, fir.length() - 12);
				
				
				XmlNode rootJoint = collada.getDirectChild("library_visual_scenes")
						.getDirectChild("visual_scene")
						.getChildWithAttributeValue("node", "id", "Armature")
						.getChildWithAttributeValue("node", "type", "JOINT");
				
				XmlNode joint = rootJoint.getAttributeValue("sid").equals(sec) ? rootJoint :
					rootJoint.getChildWithAttributeValue("node", "sid", sec, false);
				
				if (joint == null)
				{
					IllegalArgumentException exception = new IllegalArgumentException("Can't find joint " + sec + ". Did you use the wrong armature?");
					throw exception;
				}
				
				ModMatrix4f localJointTransform = this.getLocalJointTransform(joint, root);

				TransformSheet sheet = this.getTransformSheet(timeValue, matrixArray,
						ModMatrix4f.invert(localJointTransform, null), root);
				this.addSheet(sec, sheet);
				this.withTotalTime(Float.parseFloat(timeValue[timeValue.length - 1]));
				root = false;
			}
			return this;
		}
		
		private ModMatrix4f getLocalJointTransform(XmlNode jointNode, boolean root)
		{
			String[] matrixData = jointNode.getDirectChild("matrix").getData().split(" ");
			ModMatrix4f jointTransform = this.convertStringToMatrix(matrixData);
			if (root) ModMatrix4f.mul(CORRECTION, jointTransform, jointTransform);
			return jointTransform;
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
		
		private TransformSheet getTransformSheet(String[] times, String[] trasnformMatrix,
				ModMatrix4f invLocalTransform, boolean correct)
		{
			List<Keyframe> keyframeList = new ArrayList<>();

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

				ModMatrix4f matrix = new ModMatrix4f();
				matrix.load(buffer);
				matrix.transpose();

				if (correct)
				{
					ModMatrix4f.mul(CORRECTION, matrix, matrix);
				}

				ModMatrix4f.mul(invLocalTransform, matrix, matrix);

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
		
		@Override
		public ResourceLocation getId()
		{
			return this.id;
		}
		
		public Builder addSheet(String jointName, TransformSheet sheet)
		{
			this.jointTransforms.put(jointName, sheet);
			return this;
		}
		
		public Builder withTotalTime(float totalTime)
		{
			this.totalTime = totalTime;
			return this;
		}
		
		@Override
		public AnimFrameData build()
		{
			return new AnimFrameData(this.jointTransforms.build(), this.totalTime);
		}
	}
}
