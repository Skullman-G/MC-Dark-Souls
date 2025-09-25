package com.skullmangames.darksouls.core.data_provider;

import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.core.init.data.Colliders;
import com.skullmangames.darksouls.core.util.collider.Collider;
import com.skullmangames.darksouls.core.util.collider.ColliderType;
import com.skullmangames.darksouls.core.util.json.JsonBuilder;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.phys.Vec3;

public class ColliderProvider extends ConfigDataProvider<JsonBuilder<ColliderType<?>>>
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public ColliderProvider(DataGenerator generator)
	{
		super(generator);
	}
	
	@Override
	public String getName()
	{
		return "Colliders";
	}
	
	@Override
	protected Logger getLogger()
	{
		return LOGGER;
	}
	
	@Override
	protected String getSubFolder()
	{
		return "colliders";
	}
	
	@Override
	protected List<JsonBuilder<ColliderType<?>>> data()
	{
		return ImmutableList.of
		(
				Collider.capsuleBuilder(Colliders.FIST.getId(), 0.5D, 1.0D, new Vec3(0, 0, 0.5D)),
				Collider.capsuleBuilder(Colliders.SHORTSWORD.getId(), 0.5D, 1.5D, new Vec3(0, 0.05D, 0), -4.5F, 0),
				Collider.capsuleBuilder(Colliders.LONGSWORD.getId(), 0.5D, 1.75D, new Vec3(0, 0.05D, 0), -4.5F, 0),
				Collider.capsuleBuilder(Colliders.BROKEN_SWORD.getId(), 0.5D, 1.3D, new Vec3(0, 0.05D, 0), -4.5F, 0),
				Collider.multiBuilder(Colliders.GREAT_HAMMER.getId(),
							Collider.capsuleBuilder(Colliders.GREAT_HAMMER.getId(), 0.75D, 1.75D, new Vec3(0, 0.05D, -0.75D), -4.5F, 0F),
							Collider.capsuleBuilder(Colliders.GREAT_HAMMER.getId(), 0.25D, 1.5D, new Vec3(0, 0.05D, 0), -4.5F, 0F)
						),
				Collider.cubeBuilder(Colliders.DAGGER.getId(), -0.3F, -0.15F, -0.7F, 0.3F, 0.45F, -0.2F),
				Collider.multiBuilder(Colliders.SPEAR.getId(),
								Collider.capsuleBuilder(Colliders.SPEAR.getId(), 0.3D, 0.9D, new Vec3(0, 0.05D, -1.1D), -4.5F, 0),
								Collider.capsuleBuilder(Colliders.SPEAR.getId(), 0.2D, 1.5D, new Vec3(0, 0.05D, 0), -4.5F, 0)
							),
				Collider.multiBuilder(Colliders.WINGED_SPEAR.getId(),
								Collider.capsuleBuilder(Colliders.WINGED_SPEAR.getId(), 0.35D, 1D, new Vec3(0, 0.05D, -1.5D), -4.5F, 0),
								Collider.capsuleBuilder(Colliders.WINGED_SPEAR.getId(), 0.2D, 1.7D, new Vec3(0, 0.05D, 0), -4.5F, 0)
							),
				Collider.multiBuilder(Colliders.HALBERD.getId(),
							Collider.capsuleBuilder(Colliders.HALBERD.getId(), 0.35D, 1D, new Vec3(0, 0.05D, -1.5D), -4.5F, 0),
							Collider.capsuleBuilder(Colliders.HALBERD.getId(), 0.2D, 1.7D, new Vec3(0, 0.05D, 0), -4.5F, 0)
						),
				Collider.capsuleBuilder(Colliders.ULTRA_GREATSWORD.getId(), 0.5D, 2.7D, new Vec3(0, 0.125D, 0), -5.5F, 0),
				Collider.capsuleBuilder(Colliders.GREATSWORD.getId(), 0.5D, 2.1D, new Vec3(0, 0.1D, 0), -5.5F, 0),
				Collider.multiBuilder(Colliders.DEMONS_GREATAXE.getId(),
							Collider.capsuleBuilder(Colliders.DEMONS_GREATAXE.getId(), 0.8D, 1.75D, new Vec3(0, -0.1D, -0.5D), -4.5F, 0),
							Collider.capsuleBuilder(Colliders.DEMONS_GREATAXE.getId(), 0.25D, 1.5D, new Vec3(0, 0.2D, 0), -4.5F, 0)
						),
				Collider.capsuleBuilder(Colliders.GREATAXE.getId(), 0.8D, 1.75D, new Vec3(0, -0.1D, -0.25D), -4.5F, 0),
				Collider.capsuleBuilder(Colliders.SHIELD.getId(), 0.6D, 2D, new Vec3(0.15D, 0, 0.6D), -90F, 0),
				Collider.capsuleBuilder(Colliders.AXE.getId(), 0.3D, 0.75D, new Vec3(-0.025D, 0D, -0.1D), 3, 0),
				Collider.capsuleBuilder(Colliders.BATTLE_AXE.getId(), 0.5D, 1.1D, new Vec3(0, 0, -0.25D), -4.5F, 0),
				Collider.capsuleBuilder(Colliders.PICKAXE.getId(), 0.3D, 0.75D, new Vec3(-0.025D, 0D, -0.1D), 3, 0),
				Collider.multiBuilder(Colliders.MACE.getId(),
						Collider.capsuleBuilder(Colliders.MACE.getId(), 0.4D, 0.8D, new Vec3(0, 0.05D, -0.4D), -4.5F, 0),
						Collider.capsuleBuilder(Colliders.MACE.getId(), 0.15D, 1.3D, new Vec3(0, 0.05D, 0), -4.5F, 0)
					),
				
				//Stray Demon
				Collider.multiBuilder(Colliders.STRAY_DEMON_GREAT_HAMMER.getId(),
								Collider.capsuleBuilder(Colliders.STRAY_DEMON_GREAT_HAMMER.getId(), 2D, 4.5D, new Vec3(0, 0.05D, -2D), -4.5F, 0),
								Collider.capsuleBuilder(Colliders.STRAY_DEMON_GREAT_HAMMER.getId(), 0.75D, 3.5D, new Vec3(0, 0.05D, 0), -4.5F, 0)
						),
				Collider.capsuleBuilder(Colliders.STRAY_DEMON_BODY.getId(), 4D, 8D, new Vec3(0, 0, 0), -90F, 0),
				
				//Taurus Demon
				Collider.multiBuilder(Colliders.TAURUS_DEMON_GREATAXE.getId(),
								Collider.capsuleBuilder(Colliders.TAURUS_DEMON_GREATAXE.getId(), 2D, 4.5D, new Vec3(0, 0.05D, -2D), -4.5F, 0),
								Collider.capsuleBuilder(Colliders.TAURUS_DEMON_GREATAXE.getId(), 0.75D, 3.5D, new Vec3(0, 0.05D, 0), -4.5F, 0)
						),
				
				//Berenike Knight
				Collider.capsuleBuilder(Colliders.BERENIKE_KNIGHT_ULTRA_GREATSWORD.getId(), 0.5D, 3D, new Vec3(0, 0.125D, 0), -5.5F, 0),
				Collider.multiBuilder(Colliders.BERENIKE_KNIGHT_MACE.getId(),
								Collider.capsuleBuilder(Colliders.BERENIKE_KNIGHT_MACE.getId(), 0.5D, 1.0D, new Vec3(0, 0.1D, -0.4D), -4.5F, 0),
								Collider.capsuleBuilder(Colliders.BERENIKE_KNIGHT_MACE.getId(), 0.1D, 1.0D, new Vec3(0, 0.1D, 0), -4.5F, 0)
						),
				
				//Bell Gargoyle
				Collider.multiBuilder(Colliders.BELL_GARGOYLE_HALBERD.getId(),
							Collider.capsuleBuilder(Colliders.BELL_GARGOYLE_HALBERD.getId(), 0.35D, 1.5D, new Vec3(0, 0.05D, -2.0D), -4.5F, 0),
							Collider.capsuleBuilder(Colliders.BELL_GARGOYLE_HALBERD.getId(), 0.2D, 2.2D, new Vec3(0, 0.05D, 0), -4.5F, 0)
						)
		);
	}
}
