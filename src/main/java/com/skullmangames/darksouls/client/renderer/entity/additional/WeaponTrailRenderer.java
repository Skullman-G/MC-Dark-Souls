package com.skullmangames.darksouls.client.renderer.entity.additional;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.renderer.ModRenderTypes;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class WeaponTrailRenderer extends AdditionalEntityRenderer
{
	private static final ResourceLocation TEXTURE_LOCATION = DarkSouls.rl("textures/entities/additional/weapon_trail.png");
	private static final RenderType RENDER_TYPE = ModRenderTypes.getWeaponTrail(TEXTURE_LOCATION);
	
	private final Map<Integer, List<LastPos>> lastPosMap = new HashMap<>();
	
	@Override
	public boolean shouldDraw(LivingEntity entity)
	{
		return entity instanceof LivingEntity;
	}

	@Override
	public void draw(LivingEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks)
	{
		LivingCap<?> cap = (LivingCap<?>)entity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		if (cap == null) return;
		
		List<LastPos> list = this.lastPosMap.get(entity.getId());
		if (list == null)
		{
			list = new LinkedList<>();
			this.lastPosMap.put(entity.getId(), list);
		}
		
		boolean canAdd = cap.weaponCollider != null && cap.getEntityState() == EntityState.CONTACT;
		if (canAdd || !list.isEmpty())
		{
			if (canAdd) cap.weaponCollider.update(cap, "Tool_R", partialTicks);
			LastPos lastPos = canAdd ? new LastPos(cap.weaponCollider.top(), cap.weaponCollider.bottom())
					: list.get(0);
			
			VertexConsumer builder = bufferSource.getBuffer(RENDER_TYPE);
			
			LastPos lastPos1 = lastPos;
			for (LastPos lastPos2 : list)
			{
				this.drawPlane(builder, lastPos1, lastPos2);
				lastPos2.tick(partialTicks);
				lastPos1 = lastPos2;
			}
			if (!list.contains(lastPos)) list.add(0, lastPos);
			if (lastPos1.expired()) list.remove(lastPos1);
		}
	}
	
	private void drawPlane(VertexConsumer builder, LastPos newPos, LastPos oldPos)
	{
		Vec3 up = newPos.up;
		Vec3 down = newPos.down;
		Vec3 up2 = oldPos.up;
		Vec3 down2 = oldPos.down;
		
		this.drawTriangle(builder, down, up, up2, new Vec2(0, 256), new Vec2(0, 0), new Vec2(256, 0));
		this.drawTriangle(builder, up2, down, down2, new Vec2(256, 0), new Vec2(0, 256), new Vec2(256, 256));
	}
	
	private void drawTriangle(VertexConsumer builder, Vec3 v00, Vec3 v01, Vec3 v02, Vec2 uv00, Vec2 uv01, Vec2 uv02)
	{
		Vec3 center = new Vec3((v00.x + v01.x + v02.x) / 3, (v00.y + v01.y + v02.y) / 3, (v00.z + v01.z + v02.z) / 3);
		
		Camera cam = ClientManager.INSTANCE.mainCamera;
		Vec3 camPos = cam.getPosition();
		float camXRot = cam.getXRot();
		float camYRot = cam.getYRot();
		double camYRotRad = Math.toRadians(camYRot);
		
		PoseStack poseStack = new PoseStack();
		
		Vec3 projCenter = new Vec3(camPos.x - center.x, -(camPos.y - center.y), camPos.z - center.z);
		
		poseStack.mulPose(Vector3f.YP.rotationDegrees(camYRot));
		poseStack.mulPose(new Vector3f((float)Math.cos(camYRotRad), 0, (float)Math.sin(camYRotRad)).rotationDegrees(camXRot));
		poseStack.translate(projCenter.x, projCenter.y, projCenter.z);
		
		Matrix4f mat = poseStack.last().pose();
		
		v00 = center.subtract(v00);
		v00 = new Vec3(v00.x, -v00.y, v00.z);
		v01 = center.subtract(v01);
		v01 = new Vec3(v01.x, -v01.y, v01.z);
		v02 = center.subtract(v02);
		v02 = new Vec3(v02.x, -v02.y, v02.z);
		
		float cor = 0.00390625F;
		// Front Side
		builder.vertex(mat, (float)v00.x, (float)v00.y, (float)v00.z).uv(uv00.x * cor, uv00.y * cor).endVertex();
		builder.vertex(mat, (float)v01.x, (float)v01.y, (float)v01.z).uv(uv01.x * cor, uv01.y * cor).endVertex();
		builder.vertex(mat, (float)v02.x, (float)v02.y, (float)v02.z).uv(uv02.x * cor, uv02.y * cor).endVertex();
		
		// Back Side
		builder.vertex(mat, (float)v00.x, (float)v00.y, (float)v00.z).uv(uv00.x * cor, uv00.y * cor).endVertex();
		builder.vertex(mat, (float)v02.x, (float)v02.y, (float)v02.z).uv(uv02.x * cor, uv02.y * cor).endVertex();
		builder.vertex(mat, (float)v01.x, (float)v01.y, (float)v01.z).uv(uv01.x * cor, uv01.y * cor).endVertex();
	}
	
	private class LastPos
	{
		private final Vec3 up;
		private final Vec3 down;
		
		private float duration;
		
		private LastPos(Vec3 up, Vec3 down)
		{
			this.up = up;
			this.down = down;
			this.duration = 3.0F;
		}
		
		private void tick(float deltaTime)
		{
			this.duration -= deltaTime;
		}
		
		private boolean expired()
		{
			return this.duration < 0;
		}
	}
}
