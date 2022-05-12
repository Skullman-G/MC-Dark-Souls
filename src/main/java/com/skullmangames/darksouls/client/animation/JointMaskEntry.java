package com.skullmangames.darksouls.client.animation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.skullmangames.darksouls.common.animation.LivingMotion;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JointMaskEntry
{
	public static final List<JointMask> BIPED_UPPER_JOINTS = new ArrayList<>(Arrays.asList(JointMask.of("Torso"),
			JointMask.of("Chest"), JointMask.of("Head"), JointMask.of("Shoulder_R"), JointMask.of("Arm_R"),
			JointMask.of("Hand_R"), JointMask.of("Elbow_R"), JointMask.of("Tool_R"), JointMask.of("Shoulder_L"),
			JointMask.of("Arm_L"), JointMask.of("Hand_L"), JointMask.of("Elbow_L"), JointMask.of("Tool_L")));

	public static final List<JointMask> BIPED_UPPER_JOINTS_WITH_ROOT = new ArrayList<>(
			Arrays.asList(JointMask.of("Root", JointMask.ROOT_COMBINE), JointMask.of("Torso"), JointMask.of("Chest"),
					JointMask.of("Head"), JointMask.of("Shoulder_R"), JointMask.of("Arm_R"), JointMask.of("Hand_R"),
					JointMask.of("Elbow_R"), JointMask.of("Tool_R"), JointMask.of("Shoulder_L"), JointMask.of("Arm_L"),
					JointMask.of("Hand_L"), JointMask.of("Elbow_L"), JointMask.of("Tool_L")));

	public static final List<JointMask> BIPED_LOWER_JOINTS_WITH_ROOT = new ArrayList<>(
			Arrays.asList(JointMask.of("Root", JointMask.ROOT_COMBINE), JointMask.of("Thigh_R"), JointMask.of("Leg_R"),
					JointMask.of("Knee_R"), JointMask.of("Thigh_L"), JointMask.of("Leg_L"), JointMask.of("Knee_L"),
					JointMask.of("Torso"), JointMask.of("Head")));

	public static final List<JointMask> BIPED_ARMS = new ArrayList<>(
			Arrays.asList(JointMask.of("Shoulder_R"), JointMask.of("Arm_R"), JointMask.of("Hand_R"),
					JointMask.of("Elbow_R"), JointMask.of("Tool_R"), JointMask.of("Shoulder_L"), JointMask.of("Arm_L"),
					JointMask.of("Hand_L"), JointMask.of("Elbow_L"), JointMask.of("Tool_L")));

	public static final List<JointMask> NONE = new ArrayList<>(
			Arrays.asList(JointMask.of("Root"), JointMask.of("Thigh_R"), JointMask.of("Leg_R"), JointMask.of("Knee_R"),
					JointMask.of("Thigh_L"), JointMask.of("Leg_L"), JointMask.of("Knee_L"), JointMask.of("Torso"),
					JointMask.of("Chest"), JointMask.of("Head"), JointMask.of("Shoulder_R"), JointMask.of("Arm_R"),
					JointMask.of("Hand_R"), JointMask.of("Elbow_R"), JointMask.of("Tool_R"), JointMask.of("Shoulder_L"),
					JointMask.of("Arm_L"), JointMask.of("Hand_L"), JointMask.of("Elbow_L"), JointMask.of("Tool_L")));

	private final Map<LivingMotion, List<JointMask>> masks = new HashMap<>();
	private final List<JointMask> defaultMask;

	public JointMaskEntry(List<JointMask> defaultMask, List<Pair<LivingMotion, List<JointMask>>> masks)
	{
		this.defaultMask = defaultMask;

		for (Pair<LivingMotion, List<JointMask>> mask : masks)
		{
			this.masks.put(mask.getLeft(), mask.getRight());
		}
	}

	public List<JointMask> getMask(LivingMotion livingmotion)
	{
		return this.masks.getOrDefault(livingmotion, this.defaultMask);
	}

	public boolean isMasked(LivingMotion livingmotion, String jointName)
	{
		List<JointMask> masks = this.masks.getOrDefault(livingmotion, this.defaultMask);

		for (JointMask mask : masks)
		{
			if (mask.equals(JointMask.of(jointName)))
			{
				return false;
			}
		}

		return true;
	}

	public boolean isValid()
	{
		return this.defaultMask != null;
	}

	public static JointMaskEntry.Builder builder()
	{
		return new JointMaskEntry.Builder();
	}

	public static class Builder
	{
		private List<Pair<LivingMotion, List<JointMask>>> masks = new ArrayList<>();
		private List<JointMask> defaultMask = null;

		public JointMaskEntry.Builder mask(LivingMotion motion, List<JointMask> masks)
		{
			this.masks.add(Pair.of(motion, masks));
			return this;
		}

		public JointMaskEntry.Builder defaultMask(List<JointMask> masks)
		{
			this.defaultMask = masks;
			return this;
		}

		public JointMaskEntry create()
		{
			return new JointMaskEntry(this.defaultMask, this.masks);
		}
	}
}
