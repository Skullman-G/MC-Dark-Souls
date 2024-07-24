package com.skullmangames.darksouls.common.capability.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.input.ModKeys;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.init.data.WeaponMovesets;
import com.skullmangames.darksouls.core.init.data.WeaponSkills;
import com.skullmangames.darksouls.core.util.WeaponMoveset;
import com.skullmangames.darksouls.core.util.WeaponSkill;
import com.skullmangames.darksouls.core.util.AuxEffect;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.core.util.collider.Collider;
import com.skullmangames.darksouls.core.util.WeaponCategory;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSPlayAnimation;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MeleeWeaponCap extends WeaponCap implements Shield
{
	private WeaponMoveset moveset;
	private final Collider collider;
	
	private final Deflection deflection;
	private final WeaponMaterial weaponMaterial;
	private final ImmutableMap<CoreDamageType, Float> defense;
	private final float stability;
	
	public MeleeWeaponCap(Item item, WeaponCategory category, WeaponMoveset moveset, WeaponSkill skill,
			Collider collider, ImmutableMap<CoreDamageType, Integer> damage, ImmutableSet<AuxEffect> auxEffects,
			float critical, float weight, Deflection deflection, WeaponMaterial weaponMaterial,
			ImmutableMap<CoreDamageType, Float> defense, float stability, ImmutableMap<Stat, Integer> statRequirements,
			ImmutableMap<Stat, Scaling> statScaling)
	{
		super(item, category, skill, damage, auxEffects, critical, weight, statRequirements, statScaling);
		this.moveset = moveset;
		this.collider = collider;
		this.weaponMaterial = weaponMaterial;
		this.deflection = deflection;
		this.defense = defense;
		this.stability = stability;
		
		if (this.getWeaponCategory().isShield())
		{
			this.twoHandingOverrides.put(LivingMotion.IDLE, Animations.BIPED_IDLE_TH_SHIELD.get());
			this.twoHandingOverrides.put(LivingMotion.WALKING, Animations.BIPED_IDLE_TH_SHIELD.get());
			this.twoHandingOverrides.put(LivingMotion.RUNNING, Animations.BIPED_IDLE_TH_SHIELD.get());
			this.twoHandingOverrides.put(LivingMotion.SNEAKING, Animations.BIPED_IDLE_TH_SHIELD.get());
			this.twoHandingOverrides.put(LivingMotion.KNEELING, Animations.BIPED_IDLE_TH_SHIELD.get());
			this.twoHandingOverrides.put(LivingMotion.MOUNTED, Animations.BIPED_IDLE_TH_SHIELD.get());
		}
		if (this.getWeaponCategory().isHeavy())
		{
			this.animationOverrides.put(LivingMotion.IDLE, Animations.BIPED_HOLDING_BIG_WEAPON.get());
			this.animationOverrides.put(LivingMotion.WALKING, Animations.BIPED_HOLDING_BIG_WEAPON.get());
			this.animationOverrides.put(LivingMotion.RUNNING, Animations.BIPED_HOLDING_BIG_WEAPON.get());
			this.animationOverrides.put(LivingMotion.SNEAKING, Animations.BIPED_HOLDING_BIG_WEAPON.get());
			this.animationOverrides.put(LivingMotion.KNEELING, Animations.BIPED_HOLDING_BIG_WEAPON.get());
			this.animationOverrides.put(LivingMotion.MOUNTED, Animations.BIPED_HOLDING_BIG_WEAPON.get());
			this.animationOverrides.put(LivingMotion.BLOCKING, Animations.BIPED_HOLDING_BIG_WEAPON.get());
			
			this.twoHandingOverrides.put(LivingMotion.IDLE, Animations.BIPED_IDLE_TH_BIG_WEAPON.get());
			this.twoHandingOverrides.put(LivingMotion.WALKING, Animations.BIPED_IDLE_TH_BIG_WEAPON.get());
			this.twoHandingOverrides.put(LivingMotion.RUNNING, Animations.BIPED_IDLE_TH_BIG_WEAPON.get());
			this.twoHandingOverrides.put(LivingMotion.SNEAKING, Animations.BIPED_IDLE_TH_BIG_WEAPON.get());
			this.twoHandingOverrides.put(LivingMotion.KNEELING, Animations.BIPED_IDLE_TH_BIG_WEAPON.get());
			this.twoHandingOverrides.put(LivingMotion.MOUNTED, Animations.BIPED_IDLE_TH_BIG_WEAPON.get());
		}
		else if (this.getWeaponCategory().isLong())
		{
			this.twoHandingOverrides.put(LivingMotion.IDLE, Animations.BIPED_IDLE_TH_SPEAR.get());
			this.twoHandingOverrides.put(LivingMotion.WALKING, Animations.BIPED_IDLE_TH_SPEAR.get());
			this.twoHandingOverrides.put(LivingMotion.RUNNING, Animations.BIPED_IDLE_TH_SPEAR.get());
			this.twoHandingOverrides.put(LivingMotion.SNEAKING, Animations.BIPED_IDLE_TH_SPEAR.get());
			this.twoHandingOverrides.put(LivingMotion.KNEELING, Animations.BIPED_IDLE_TH_SPEAR.get());
			this.twoHandingOverrides.put(LivingMotion.MOUNTED, Animations.BIPED_IDLE_TH_SPEAR.get());
		}
	}
	
	@Override
	public float getStability()
	{
		return this.stability;
	}
	
	@Override
	public void modifyItemTooltip(List<Component> itemTooltip, PlayerCap<?> playerCap, ItemStack stack)
	{
		super.modifyItemTooltip(itemTooltip, playerCap, stack);
		if (!ClientManager.INSTANCE.inputManager.isKeyDown(ModKeys.SHOW_ITEM_INFO))
		{
			int i = 1;
			itemTooltip.set(i, new TextComponent(itemTooltip.get(i++).getString()
					+ "  |  \u00A77Physical Defense: " + (int)(this.getDefense(CoreDamageType.PHYSICAL) * 100) + "%"));
			itemTooltip.set(i, new TextComponent(itemTooltip.get(i++).getString()
					+ "  |  \u00A73Magic Defense: " + (int)(this.getDefense(CoreDamageType.MAGIC) * 100) + "%"));
			itemTooltip.set(i, new TextComponent(itemTooltip.get(i++).getString()
					+ "  |  \u00A7cFire Defense: " + (int)(this.getDefense(CoreDamageType.FIRE) * 100) + "%"));
			itemTooltip.set(i, new TextComponent(itemTooltip.get(i++).getString()
					+ "  |  \u00A7eLightning Defense: " + (int)(this.getDefense(CoreDamageType.LIGHTNING) * 100) + "%"));
			itemTooltip.set(i, new TextComponent(itemTooltip.get(i++).getString()
					+ "  |  \u00A76Holy Defense: " + (int)(this.getDefense(CoreDamageType.HOLY) * 100) + "%"));
			itemTooltip.set(i, new TextComponent(itemTooltip.get(i++).getString()
					+ "  |  \u00A75Dark Defense: " + (int)(this.getDefense(CoreDamageType.DARK) * 100) + "%"));
		}
	}
	
	public InteractionResult onUse(PlayerCap<?> playerCap, InteractionHand hand)
	{
		if (!playerCap.canBlock()) return InteractionResult.PASS;
		playerCap.getOriginalEntity().startUsingItem(hand);
		return InteractionResult.CONSUME;
	}
	
	public AttackAnimation[] getAttacks(AttackType type)
	{
		return this.moveset.getAttacks(type);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void performAttack(AttackType type, LocalPlayerCap playerCap)
	{
		AttackAnimation animation = this.getAttack(type, playerCap);
		if (animation == null) return;
		playerCap.getAnimator().playAnimation(animation, 0.0F);
		ModNetworkManager.sendToServer(new CTSPlayAnimation(animation, 0.0F, false, false));
	};

	@OnlyIn(Dist.CLIENT)
	public AttackAnimation getAttack(AttackType type, LocalPlayerCap playerCap)
	{
		if (playerCap.isMounted())
		{
			List<AttackAnimation> animations = Animations.HORSEBACK_LIGHT_ATTACK.getAsList();
			int combo = animations.indexOf(playerCap.getClientAnimator().baseLayer.animationPlayer.getPlay());
			if (combo + 1 < animations.size()) combo += 1;
			else combo = 0;
			return animations.get(combo);
		}
		
		Pair<Boolean, AttackAnimation[]> move = this.moveset.get(type);
		if (move == null) return null;
		AttackAnimation[] animations = move.getSecond();
		if (animations == null) return null;
		List<AttackAnimation> animationList = new ArrayList<AttackAnimation>(Arrays.asList(animations));
		int combo = animationList.indexOf(playerCap.getClientAnimator().baseLayer.animationPlayer.getPlay());
		if (combo + 1 < animations.length) combo += 1;
		else if (move.getFirst()) combo = 0;
		return animations[combo];
	}
	
	@Override
	public Deflection getDeflection()
	{
		return this.deflection;
	}

	public Collider getWeaponCollider()
	{
		return this.collider;
	}
	
	@Override
	public SoundEvent getBlockSound()
	{
		return this.weaponMaterial.getBlockSound();
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canBeRenderedBoth(ItemStack item)
	{
		return true;
	}
	
	@Override
	public boolean canBeRenderedOnBack()
	{
		return true;
	}

	@Override
	public float getDefense(CoreDamageType damageType)
	{
		return this.defense.get(damageType);
	}
	
	public static Builder builder(Item item, WeaponCategory category, WeaponMovesets.Getter moveset, ResourceLocation colliderId, float weight)
	{
		return new Builder(item, category, moveset.getId(), colliderId, weight);
	}
	
	public enum AttackType
	{
		LIGHT("light"), HEAVY("heavy"), DASH("dash"), BACKSTAB("backstab"),
		TWO_HANDED_LIGHT("two_handed_light"), TWO_HANDED_HEAVY("two_handed_heavy"),
		TWO_HANDED_DASH("two_handed_dash"), PUNISH("punish");
		
		private String id;
		
		private AttackType(String id)
		{
			this.id = id;
		}
		
		public String toString()
		{
			return this.id;
		}
		
		public static AttackType fromString(String id)
		{
			for (AttackType type : AttackType.values())
			{
				if (type.id.equals(id)) return type;
			}
			return null;
		}
		
		public AttackType getTwoHanded()
		{
			switch(this)
			{
				default: return this;
				case LIGHT: return TWO_HANDED_LIGHT;
				case HEAVY: return TWO_HANDED_HEAVY;
				case DASH: return TWO_HANDED_DASH;
			}
		}
	}
	
	public enum WeaponMaterial
	{
		METAL_WEAPON("metal_weapon", ModSoundEvents.WEAPON_BLOCK), STONE_WEAPON("stone_weapon", ModSoundEvents.WEAPON_BLOCK),
		WOODEN_WEAPON("wooden_weapon", ModSoundEvents.WOODEN_SHIELD_BLOCK),
		WOODEN_SHIELD("wooden_shield", ModSoundEvents.WOODEN_SHIELD_BLOCK),
		METAL_SHIELD("metal_shield", ModSoundEvents.IRON_SHIELD_BLOCK),
		LARGE_IRON_SHIELD("large_iron_shield", ModSoundEvents.LARGE_IRON_SHIELD_BLOCK);
		
		private final String id;
		private final Supplier<SoundEvent> blockSound;
		
		private WeaponMaterial(String id, Supplier<SoundEvent> blockSound)
		{
			this.id = id;
			this.blockSound = blockSound;
		}
		
		public SoundEvent getBlockSound()
		{
			return this.blockSound.get();
		}
		
		@Override
		public String toString()
		{
			return this.id;
		}
		
		public static WeaponMaterial fromString(String id)
		{
			for (WeaponMaterial mat : WeaponMaterial.values())
			{
				if (mat.id == id) return mat;
			}
			return null;
		}
	}
	
	public static class Builder extends WeaponCap.Builder<MeleeWeaponCap>
	{
		private ResourceLocation movesetId;
		private ResourceLocation colliderId;
		private float stability = 0.25F;
		
		private Deflection deflection;
		private WeaponMaterial weaponMaterial = WeaponMaterial.METAL_WEAPON;
		private ImmutableMap.Builder<CoreDamageType, Float> defense = ImmutableMap.builder();
		
		private Builder() {}
		
		private Builder(Item item, WeaponCategory category, ResourceLocation movesetId, ResourceLocation colliderId, float weight)
		{
			super(item, category, weight);
			this.movesetId = movesetId;
			this.colliderId = colliderId;
			this.deflection = category.getDeflection();
		}
		
		public Builder setSkill(ResourceLocation id)
		{
			this.skillId = id;
			return this;
		}
		
		public Builder addAuxEffect(AuxEffect auxEffect)
		{
			this.auxEffects.add(auxEffect);
			return this;
		}
		
		public Builder setCritical(float critical)
		{
			this.critical = critical;
			return this;
		}
		
		public Builder setStability(float value)
		{
			this.stability = value;
			return this;
		}
		
		public Builder putDamageInfo(CoreDamageType damageType, int damage, float defense)
		{
			this.damage.put(damageType, damage);
			this.defense.put(damageType, defense);
			return this;
		}
		
		public Builder setWeaponMaterial(WeaponMaterial value)
		{
			this.weaponMaterial = value;
			return this;
		}
		
		public Builder setDeflection(Deflection value)
		{
			this.deflection = value;
			return this;
		}
		
		public Builder putStatInfo(Stat stat, int requirement, Scaling scaling)
		{
			this.statRequirements.put(stat, requirement);
			this.statScaling.put(stat, scaling);
			return this;
		}
		
		public JsonObject toJson()
		{
			JsonObject json = super.toJson();
			json.addProperty("moveset", this.movesetId.toString());
			json.addProperty("collider", this.colliderId.toString());
			json.addProperty("stability", this.stability);
			
			json.addProperty("deflection", this.deflection.toString());
			json.addProperty("weapon_material", this.weaponMaterial.toString());
			
			JsonObject defense = new JsonObject();
			json.add("defense", defense);
			this.defense.build().forEach((type, def) ->
			{
				defense.addProperty(type.toString(), def);
			});
			return json;
		}
		
		public void initFromJson(ResourceLocation location, JsonObject json)
		{
			super.initFromJson(location, json);
			
			this.movesetId = ResourceLocation.tryParse(json.get("moveset").getAsString());
			
			this.colliderId = ResourceLocation.tryParse(json.get("collider").getAsString());
			
			JsonElement stabilityJson = json.get("stability");
			if (stabilityJson != null) this.stability = stabilityJson.getAsFloat();
			
			JsonElement deflectionJson = json.get("deflection");
			if (deflectionJson != null)
			{
				this.deflection = Deflection.valueOf(deflectionJson.getAsString());
			}
			
			JsonElement weaponMaterialJson = json.get("weapon_material");
			if (weaponMaterialJson != null)
			{
				WeaponMaterial weaponMaterial = WeaponMaterial.fromString(weaponMaterialJson.getAsString());
				if (weaponMaterial != null) this.weaponMaterial = weaponMaterial;
			}
			
			JsonObject defense = json.get("defense").getAsJsonObject();
			for (CoreDamageType type : CoreDamageType.values())
			{
				float def = Optional.ofNullable(defense.get(type.toString())).orElse(new JsonPrimitive(0)).getAsFloat();
				this.defense.put(type, def);
			}
		}
		
		public static Builder fromJson(ResourceLocation location, JsonObject json)
		{
			Builder builder = new Builder();
			builder.initFromJson(location, json);
			return builder;
		}
		
		public MeleeWeaponCap build()
		{
			Collider collider = Colliders.COLLIDERS.get(this.colliderId);
			return new MeleeWeaponCap(this.item, this.category, WeaponMovesets.getMoveset(this.movesetId), WeaponSkills.getFromLocation(this.skillId),
					collider, this.damage.build(), this.auxEffects.build(),
					this.critical, this.weight,
					this.deflection, this.weaponMaterial, this.defense.build(), this.stability,
					this.statRequirements.build(), this.statScaling.build());
		}
	}
}
