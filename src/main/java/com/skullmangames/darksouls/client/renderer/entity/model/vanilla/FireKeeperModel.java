package com.skullmangames.darksouls.client.renderer.entity.model.vanilla;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.skullmangames.darksouls.common.entity.AbstractFireKeeper;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class FireKeeperModel extends EntityModel<AbstractFireKeeper>
{
	private final ModelPart head;
	private final ModelPart headwear;
	private final ModelPart headwear2;
	private final ModelPart nose;
	private final ModelPart body;
	private final ModelPart bodywear;
	private final ModelPart arms;
	private final ModelPart skirt;

	public FireKeeperModel(ModelPart model)
	{
		this.head = model.getChild("head");
		this.headwear = model.getChild("headwear");
		this.headwear2 = model.getChild("headwear2");
		this.nose = model.getChild("nose");
		this.body = model.getChild("body");
		this.bodywear = model.getChild("bodywear");
		this.arms = model.getChild("arms");
		this.skirt = model.getChild("skirt");
	}

	public static LayerDefinition createBodyModel()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		
		partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, false), PartPose.ZERO);
		partdefinition.addOrReplaceChild("headwear", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, false), PartPose.ZERO);
		partdefinition.addOrReplaceChild("headwear2", CubeListBuilder.create(), PartPose.ZERO);
		partdefinition.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, false), PartPose.offset(0F, -2F, 0F));
		partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 29).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 9.0F, 6.0F, false), PartPose.ZERO);
		partdefinition.addOrReplaceChild("bodywear", CubeListBuilder.create().texOffs(0, 47).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 9.0F, 6.0F, false), PartPose.ZERO);
		partdefinition.addOrReplaceChild("arms", CubeListBuilder.create().texOffs(44, 22).addBox(-8.0F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F, false), PartPose.offsetAndRotation(0.0F, 3.5F, 0.3F, 0.0F, -2.0F, 0.05F));
		partdefinition.addOrReplaceChild("skirt", CubeListBuilder.create().texOffs(0, 62).addBox(-4.5F, 10.0F, -3.0F, 9.0F, 13.0F, 6.0F, false), PartPose.ZERO);
		
		return LayerDefinition.create(meshdefinition, 64, 81);
	}

	@Override
	public void setupAnim(AbstractFireKeeper entity, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch)
	{
		// previously the render function, render code was moved to a method below
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha)
	{
		head.render(matrixStack, buffer, packedLight, packedOverlay);
		headwear.render(matrixStack, buffer, packedLight, packedOverlay);
		headwear2.render(matrixStack, buffer, packedLight, packedOverlay);
		nose.render(matrixStack, buffer, packedLight, packedOverlay);
		body.render(matrixStack, buffer, packedLight, packedOverlay);
		bodywear.render(matrixStack, buffer, packedLight, packedOverlay);
		arms.render(matrixStack, buffer, packedLight, packedOverlay);
		skirt.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z)
	{
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}