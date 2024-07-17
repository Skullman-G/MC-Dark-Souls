package com.skullmangames.darksouls.network.play;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.gui.screens.BonfireNameScreen;
import com.skullmangames.darksouls.client.gui.screens.BonfireScreen;
import com.skullmangames.darksouls.client.gui.screens.BonfireTeleportScreen;
import com.skullmangames.darksouls.client.gui.screens.CovenantScreen;
import com.skullmangames.darksouls.client.gui.screens.FireKeeperScreen;
import com.skullmangames.darksouls.client.gui.screens.JoinCovenantScreen;
import com.skullmangames.darksouls.client.sound.BonfireAmbientSoundInstance;
import com.skullmangames.darksouls.common.block.BonfireBlock;
import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import com.skullmangames.darksouls.common.entity.covenant.Covenant;
import com.skullmangames.darksouls.core.init.ModBlockEntities;
import com.skullmangames.darksouls.core.init.ModParticles;
import com.skullmangames.darksouls.core.util.math.ModMath;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegistryObject;

@OnlyIn(Dist.CLIENT)
public class ModClientPlayNetHandler extends ModPlayNetHandler
{
	private final Minecraft minecraft;
	
	public ModClientPlayNetHandler()
	{
		this.minecraft = Minecraft.getInstance();
	}

	@Override
	public void setTitle(Component text, int fadein, int stay, int fadeout)
	{
		this.minecraft.gui.setTimes(fadein, stay, fadeout);
		this.minecraft.gui.setTitle(text);
	}
	
	@Override
	public void setOverlayMessage(Component text)
	{
		this.minecraft.gui.setOverlayMessage(text, false);
	}

	@Override
	public void openBonfireNameScreen(BlockPos blockPos)
	{
		BonfireBlockEntity bonfire = this.minecraft.level.getBlockEntity(blockPos, ModBlockEntities.BONFIRE.get()).orElse(null);
		if (bonfire != null) this.minecraft.setScreen(new BonfireNameScreen(bonfire));
	}

	@Override
	public void openBonfireScreen(BlockPos blockPos)
	{
		BonfireBlockEntity bonfire = this.minecraft.level.getBlockEntity(blockPos, ModBlockEntities.BONFIRE.get()).orElse(null);
		if (bonfire != null) this.minecraft.setScreen(new BonfireScreen(bonfire));
	}

	
	Set<BlockPos> sounds = new HashSet<>();
	@Override
	public void tryPlayBonfireAmbientSound(BlockPos blockPos)
	{
		float dist = (float)this.minecraft.player.distanceToSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		if (!this.sounds.contains(blockPos) && (1.0F - (dist * 0.005F)) >= 0.0F)
		{
			BonfireBlockEntity bonfire = this.minecraft.player.level.getBlockEntity(blockPos, ModBlockEntities.BONFIRE.get()).orElse(null);
			if (bonfire == null) return;
			BonfireAmbientSoundInstance soundInstance = new BonfireAmbientSoundInstance(bonfire);
			this.minecraft.getSoundManager().queueTickingSound(soundInstance);
			this.sounds.add(blockPos.immutable());
		}
	}

	@Override
	public void removeBonfireAmbientSound(BlockPos blockPos)
	{
		this.sounds.remove(blockPos);
	}

	@Override
	public void openFireKeeperScreen(int entityId)
	{
		this.minecraft.setScreen(new FireKeeperScreen(entityId));
	}
	
	@Override
	public void openJoinCovenantScreen(Covenant covenant)
	{
		this.minecraft.setScreen(new JoinCovenantScreen(covenant));
	}

	@Override
	public void openCovenantScreen(Covenant covenant)
	{
		this.minecraft.setScreen(new CovenantScreen(covenant));
	}

	@Override
	public void openBonfireTeleportScreen(BlockPos blockPos, List<Pair<String, BlockPos>> teleports)
	{
		BonfireBlockEntity bonfire = this.minecraft.level.getBlockEntity(blockPos, ModBlockEntities.BONFIRE.get()).orElse(null);
		if (bonfire != null) this.minecraft.setScreen(new BonfireTeleportScreen(teleports));
	}

	@Override
	public void shakeCam(Vec3 source, int duration, float magnitude)
	{
		if (this.minecraft.player.isOnGround() && this.minecraft.player.distanceToSqr(source) < 400)
		{
			ClientManager.INSTANCE.mainCamera.shake(duration, magnitude);
		}
	}
	
	@Override
	public void shakeCamForEntity(Entity entity, int duration, float magnitude)
	{
		if (this.minecraft.player == entity)
		{
			ClientManager.INSTANCE.mainCamera.shake(duration, magnitude);
		}
	}

	@Override
	public void playEntitySound(Entity entity, SoundEvent sound, float volume)
	{
		entity.level.playSound(this.minecraft.player, entity, sound, entity.getSoundSource(), volume, 1.0F);
	}

	@Override
	public void playSound(Entity entity, SoundEvent sound, float volume)
	{
		entity.level.playSound(this.minecraft.player, entity.getX(), entity.getY(), entity.getZ(), sound, entity.getSoundSource(), volume, 1.0F);
	}

	@Override
	public void bonfireKindleEffect(BlockPos pos)
	{
		BonfireBlock.kindleEffect(this.minecraft.level, pos);
	}

	@Override
	public void makeImpactParticles(Entity entity, Vec3 impactPos, boolean blocked)
	{
		Random random = this.minecraft.level.random;
		AABB bb = entity.getBoundingBox();
		double x = entity.getX() + ModMath.clamp(impactPos.x - entity.getX(), bb.getXsize() / 3);
		double y = entity.getY() + ModMath.clamp(impactPos.y - entity.getY(), bb.getYsize() / 3);
		double z = entity.getZ() + ModMath.clamp(impactPos.z - entity.getZ(), bb.getZsize() / 3);
		
		if (blocked)
		{
			for (int i = 0; i < 10; i++)
			{
				double xd = ModMath.dir(impactPos.x - entity.getX()) * 0.25F * random.nextDouble();
				double zd = ModMath.dir(impactPos.z - entity.getZ()) * 0.25F * random.nextDouble();
				entity.level.addParticle(ModParticles.SPARK.get(), x, y, z, xd, 0.2D * random.nextDouble(), zd);
			}
		}
		else
		{
			for (int i = 0; i < 20; i++)
			{
				double xd = ModMath.dir(impactPos.x - entity.getX()) * 0.5F * random.nextDouble();
				double zd = ModMath.dir(impactPos.z - entity.getZ()) * 0.5F * random.nextDouble();
				entity.level.addParticle(ModParticles.BLOOD.get(), x, y, z, xd, 0.2D, zd);
			}
		}
	}
	
	@Override
	public void spawnParticlesCircle(RegistryObject<SimpleParticleType> particle, Vec3 pos, float radius)
	{
		SimpleParticleType p = particle.get();
		
		for (int i = 0; i < 360; i++)
		{
			if (i % 40 == 0)
			{
				double a = Math.toRadians(i);
				this.minecraft.level.addParticle(p, pos.x, pos.y, pos.z, Math.sin(a) * radius, 0, Math.cos(a) * radius);
			}
		}
	}
}
