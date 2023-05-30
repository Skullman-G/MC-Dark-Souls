package com.skullmangames.darksouls.common.capability.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.input.ModKeys;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.init.WeaponMovesets;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.physics.Collider;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSPlayAnimation;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class MeleeWeaponCap extends WeaponCap implements IShield, ReloadableCap
{
	private final ResourceLocation movesetId;
	private WeaponMoveset moveset;
	private final Collider collider;
	private final boolean holdOnShoulder;
	
	private final ShieldType shieldType;
	private final WeaponMaterial weaponMaterial;
	private final ImmutableMap<CoreDamageType, Float> defense;
	private final float stability;
	
	public MeleeWeaponCap(Item item, ResourceLocation moveset, Collider collider, ImmutableMap<CoreDamageType, Integer> damage,
			float critical, boolean holdOnShoulder, float weight,
			ShieldType shieldType, WeaponMaterial weaponMaterial, ImmutableMap<CoreDamageType, Float> defense, float stability,
			ImmutableMap<Stat, Integer> statRequirements, ImmutableMap<Stat, Scaling> statScaling)
	{
		super(item, WeaponCategory.MELEE_WEAPON, damage, critical, weight, statRequirements, statScaling);
		this.movesetId = moveset;
		this.moveset = WeaponMovesets.getByLocation(this.movesetId).orElse(WeaponMoveset.EMPTY);
		this.collider = collider;
		this.holdOnShoulder = holdOnShoulder;
		this.weaponMaterial = weaponMaterial;
		this.shieldType = shieldType;
		this.defense = defense;
		this.stability = stability;
	}
	
	public void reload()
	{
		this.moveset = WeaponMovesets.getByLocation(this.movesetId).orElse(WeaponMoveset.EMPTY);
	}
	
	@Override
	public boolean hasHoldingAnimation()
	{
		return this.holdOnShoulder;
	}
	
	@Override
	public void modifyItemTooltip(List<Component> itemTooltip, PlayerCap<?> playerCap, ItemStack stack)
	{
		if (!(this.orgItem instanceof IForgeRegistryEntry)) return;
		super.modifyItemTooltip(itemTooltip, playerCap, stack);
		if (!ClientManager.INSTANCE.inputManager.isKeyDown(ModKeys.SHOW_ITEM_INFO))
		{
			itemTooltip.add(2, new TextComponent("\u00A72Physical Defense: " + (int)(this.getDefense(DamageType.REGULAR) * 100) + "%"));
		}
	}
	
	@Override
	public void onHeld(PlayerCap<?> playerCap)
	{
		super.onHeld(playerCap);
		if (playerCap.isClientSide())
		{
			AttributeInstance instance = playerCap.getOriginalEntity().getAttribute(Attributes.ATTACK_DAMAGE);
			instance.removeModifier(ModAttributes.EQUIPMENT_MODIFIER_UUIDS[EquipmentSlot.MAINHAND.ordinal()]);
			instance.addTransientModifier(ModAttributes.getAttributeModifierForSlot(EquipmentSlot.MAINHAND, this.getDamage(CoreDamageType.PHYSICAL)));
		}
	}
	
	public InteractionResult onUse(PlayerCap<?> playerCap, InteractionHand hand)
	{
		if (!playerCap.canBlock()) return InteractionResult.PASS;
		playerCap.getOriginalEntity().startUsingItem(hand);
		return InteractionResult.CONSUME;
	}
	
	public int getDamage(CoreDamageType type)
	{
		int value = super.getDamage(type);
		if (type != CoreDamageType.PHYSICAL || value != 0) return value;
		float defaultValue = this.orgItem instanceof SwordItem ? ((SwordItem) this.orgItem).getDamage()
				: this.orgItem instanceof DiggerItem ? ((DiggerItem) this.orgItem).getAttackDamage()
				: 0.0F;
		return (int)(defaultValue * 65);
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
			List<AttackAnimation> animations = new ArrayList<AttackAnimation>(Arrays.asList(Animations.HORSEBACK_LIGHT_ATTACK));
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
	
	public SoundEvent getSwingSound()
	{
		return null;
	}

	public SoundEvent getHitSound()
	{
		return null;
	}

	public Collider getWeaponCollider()
	{
		return this.collider;
	}
	
	public ResourceLocation getWeaponMovesetId()
	{
		return this.movesetId;
	}
	
	@Override
	public SoundEvent getBlockSound()
	{
		return this.weaponMaterial.getBlockSound();
	}
	
	@Override
	public float getDefense(DamageType damageType)
	{
		return this.defense.get(damageType.getCoreType());
	}

	@Override
	public ShieldType getShieldType()
	{
		return this.shieldType;
	}
	
	public static Builder builder(Item item, ResourceLocation movesetId, ResourceLocation colliderId, float weight)
	{
		return new Builder(item, movesetId, colliderId, weight);
	}
	
	public enum AttackType
	{
		LIGHT("light"), HEAVY("heavy"), DASH("dash"), BACKSTAB("backstab");
		
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
	}
	
	public enum WeaponMaterial
	{
		METAL_WEAPON("metal_weapon", ModSoundEvents.WEAPON_BLOCK), STONE_WEAPON("stone_weapon", ModSoundEvents.WEAPON_BLOCK),
		WOODEN_WEAPON("wooden_weapon", ModSoundEvents.WOODEN_SHIELD_BLOCK),
		WOODEN_SHIELD("wooden_shield", ModSoundEvents.WOODEN_SHIELD_BLOCK),
		METAL_SHIELD("metal_shield", ModSoundEvents.IRON_SHIELD_BLOCK);
		
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
	
	public static class Builder
	{
		private Item item;
		private ResourceLocation movesetId;
		private ResourceLocation colliderId;
		private ImmutableMap.Builder<CoreDamageType, Integer> damage = ImmutableMap.builder();
		private float critical = 1.00F;
		private boolean holdOnShoulder;
		private float weight;
		private float stability = 0.25F;
		private ImmutableMap.Builder<Stat, Integer> statRequirements = ImmutableMap.builder();
		private ImmutableMap.Builder<Stat, Scaling> statScaling = ImmutableMap.builder();
		
		private ShieldType shieldType = ShieldType.NONE;
		private WeaponMaterial weaponMaterial = WeaponMaterial.METAL_WEAPON;
		private ImmutableMap.Builder<CoreDamageType, Float> defense = ImmutableMap.builder();
		
		private Builder(Item item, ResourceLocation movesetId, ResourceLocation colliderId, float weight)
		{
			this.item = item;
			this.movesetId = movesetId;
			this.colliderId = colliderId;
			this.weight = weight;
		}
		
		public ResourceLocation getLocation()
		{
			return this.item.getRegistryName();
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
		
		public Builder setShieldType(ShieldType value)
		{
			this.shieldType = value;
			return this;
		}
		
		public Builder putStatInfo(Stat stat, int requirement, Scaling scaling)
		{
			this.statRequirements.put(stat, requirement);
			this.statScaling.put(stat, scaling);
			return this;
		}
		
		public Builder shouldHoldOnShoulder()
		{
			this.holdOnShoulder = true;
			return this;
		}
		
		public JsonObject toJson()
		{
			JsonObject root = new JsonObject();
			root.addProperty("registry_name", this.item.getRegistryName().toString());
			root.addProperty("moveset", this.movesetId.toString());
			root.addProperty("collider", this.colliderId.toString());
			root.addProperty("weight", this.weight);
			root.addProperty("hold_on_shoulder", this.holdOnShoulder);
			root.addProperty("stability", this.stability);
			root.addProperty("critical", this.critical);
			
			JsonObject damage = new JsonObject();
			root.add("damage", damage);
			this.damage.build().forEach((type, dam) ->
			{
				damage.addProperty(type.toString(), dam);
			});
			
			JsonObject statRequirements = new JsonObject();
			root.add("stat_requirements", statRequirements);
			this.statRequirements.build().forEach((stat, req) ->
			{
				statRequirements.addProperty(stat.getName(), req);
			});
			
			JsonObject statScaling = new JsonObject();
			root.add("stat_scaling", statScaling);
			this.statScaling.build().forEach((stat, scaling) ->
			{
				statScaling.addProperty(stat.getName(), scaling.toString());
			});
			
			root.addProperty("shield_type", this.shieldType.toString());
			root.addProperty("weapon_material", this.weaponMaterial.toString());
			
			JsonObject defense = new JsonObject();
			root.add("defense", defense);
			this.defense.build().forEach((type, def) ->
			{
				defense.addProperty(type.toString(), def);
			});
			return root;
		}
		
		public static Builder fromJson(ResourceLocation location, JsonObject json)
		{
			ResourceLocation itemId = ResourceLocation.tryParse(json.get("registry_name").getAsString());
			Item item = ForgeRegistries.ITEMS.getValue(itemId);
			
			ResourceLocation movesetId = ResourceLocation.tryParse(json.get("moveset").getAsString());
			
			ResourceLocation colliderId = ResourceLocation.tryParse(json.get("collider").getAsString());
			
			float weight = json.get("weight").getAsFloat();
			
			Builder builder = new Builder(item, movesetId, colliderId, weight);
			
			JsonElement holdOnShoulder = json.get("hold_on_shoulder");
			if (holdOnShoulder != null && holdOnShoulder.getAsBoolean()) builder.shouldHoldOnShoulder();
			
			JsonElement criticalJson = json.get("critical");
			if (criticalJson != null) builder.setCritical(criticalJson.getAsFloat());
			
			JsonElement stabilityJson = json.get("stability");
			if (stabilityJson != null) builder.setStability(stabilityJson.getAsFloat());
			
			JsonObject statRequirements = json.get("stat_requirements").getAsJsonObject();
			JsonObject statScaling = json.get("stat_scaling").getAsJsonObject();
			for (Stat stat : Stats.SCALING_STATS)
			{
				int requirement = statRequirements.get(stat.getName()).getAsInt();
				Scaling scaling = Scaling.fromString(statScaling.get(stat.getName()).getAsString());
				builder.putStatInfo(stat, requirement, scaling);
			}
			
			JsonElement shieldTypeJson = json.get("shield_type");
			if (shieldTypeJson != null)
			{
				ShieldType shieldType = ShieldType.valueOf(shieldTypeJson.getAsString());
				if (shieldType != null) builder.setShieldType(shieldType);
			}
			
			JsonElement weaponMaterialJson = json.get("weapon_material");
			if (weaponMaterialJson != null)
			{
				WeaponMaterial weaponMaterial = WeaponMaterial.fromString(weaponMaterialJson.getAsString());
				if (weaponMaterial != null) builder.setWeaponMaterial(weaponMaterial);
			}
			
			JsonObject damage = json.get("damage").getAsJsonObject();
			JsonObject defense = json.get("defense").getAsJsonObject();
			for (CoreDamageType type : CoreDamageType.values())
			{
				int dam = damage.get(type.toString()).getAsInt();
				float def = defense.get(type.toString()).getAsFloat();
				builder.putDamageInfo(type, dam, def);
			}
			
			return builder;
		}
		
		public MeleeWeaponCap build()
		{
			Collider collider = Colliders.COLLIDERS.get(this.colliderId);
			return new MeleeWeaponCap(this.item, this.movesetId, collider, this.damage.build(), this.critical, this.holdOnShoulder, this.weight,
					this.shieldType, this.weaponMaterial, this.defense.build(), this.stability, this.statRequirements.build(), this.statScaling.build());
		}
	}
}
