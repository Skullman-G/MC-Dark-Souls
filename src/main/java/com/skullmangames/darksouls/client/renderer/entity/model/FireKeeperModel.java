package com.skullmangames.darksouls.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.skullmangames.darksouls.common.entities.FireKeeperEntity;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class FireKeeperModel extends EntityModel<FireKeeperEntity>
{
	private final ModelRenderer head;
	private final ModelRenderer headwear;
	private final ModelRenderer headwear2;
	private final ModelRenderer nose;
	private final ModelRenderer body;
	private final ModelRenderer bodywear;
	private final ModelRenderer arms;
	private final ModelRenderer arms_rotation;
	private final ModelRenderer arms_flipped;
	private final ModelRenderer skirt;

	public FireKeeperModel()
	{
		this.texWidth = 64;
		this.texHeight = 81;

		head = new ModelRenderer(this);
		head.setPos(0.0F, 0.0F, 0.0F);
		head.texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, 0.0F, false);

		headwear = new ModelRenderer(this);
		headwear.setPos(0.0F, 0.0F, 0.0F);
		headwear.texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, 0.25F, false);

		headwear2 = new ModelRenderer(this);
		headwear2.setPos(0.0F, 0.0F, 0.0F);
		

		nose = new ModelRenderer(this);
		nose.setPos(0.0F, -2.0F, 0.0F);
		nose.texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

		body = new ModelRenderer(this);
		body.setPos(0.0F, 0.0F, 0.0F);
		body.texOffs(0, 29).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 9.0F, 6.0F, 0.0F, false);

		bodywear = new ModelRenderer(this);
		bodywear.setPos(0.0F, 0.0F, 0.0F);
		bodywear.texOffs(0, 47).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 9.0F, 6.0F, 0.5F, false);

		arms = new ModelRenderer(this);
		arms.setPos(0.0F, 3.5F, 0.3F);
		

		arms_rotation = new ModelRenderer(this);
		arms_rotation.setPos(0.0F, -2.0F, 0.05F);
		arms.addChild(arms_rotation);
		setRotationAngle(arms_rotation, -0.7505F, 0.0F, 0.0F);
		arms_rotation.texOffs(44, 22).addBox(-8.0F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);
		arms_rotation.texOffs(40, 38).addBox(-4.0F, 4.0F, -2.0F, 8.0F, 4.0F, 4.0F, 0.0F, false);

		arms_flipped = new ModelRenderer(this);
		arms_flipped.setPos(0.0F, 24.0F, 0.0F);
		arms_rotation.addChild(arms_flipped);
		arms_flipped.texOffs(44, 22).addBox(4.0F, -24.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, true);

		skirt = new ModelRenderer(this);
		skirt.setPos(0.0F, 0.0F, 0.0F);
		skirt.texOffs(0, 62).addBox(-4.5F, 10.0F, -3.0F, 9.0F, 13.0F, 6.0F, 0.5F, false);
	}

	@Override
	public void setupAnim(FireKeeperEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
	{
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
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

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z)
	{
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}