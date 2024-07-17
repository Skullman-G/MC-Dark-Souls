package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.DeathAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.common.entity.covenant.Covenant;
import com.skullmangames.darksouls.common.entity.covenant.Covenants;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.StatHolder;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.common.inventory.SpellInventory;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.WeaponCategory;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.Damages;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.math.ModMath;
import com.skullmangames.darksouls.common.capability.item.Shield.Deflection;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public abstract class PlayerCap<T extends Player> extends LivingCap<T> implements EquipLoaded
{
	protected float yaw;
	protected int tickSinceLastAction;
	
	private StatHolder stats;
	
	private int humanity;
	private boolean human;
	private int souls;
	private float fp;
	
	private SpellInventory attunements;
	
	private Covenant covenant = Covenants.NONE;
	private int covenantProgress;
	private int[] covenantProgresses = new int[Covenants.COVENANTS.size()];
	
	private boolean twoHanding;
	
	@Override
	public void onEntityConstructed(T entityIn)
	{
		super.onEntityConstructed(entityIn);
		this.stats = new StatHolder(this);
		this.attunements = new SpellInventory(entityIn);
	}
	
	@Override
	public void onEntityJoinWorld(T entityIn)
	{
		super.onEntityJoinWorld(entityIn);
		
		this.tickSinceLastAction = 40;
		
		if (!this.orgEntity.getInventory().contains(new ItemStack(ModItems.DARKSIGN.get())))
		{
			this.orgEntity.getInventory().add(new ItemStack(ModItems.DARKSIGN.get()));
		}
	}
	
	@Override
	protected void initAttributes()
	{
		super.initAttributes();
		this.orgEntity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(400D);
		this.orgEntity.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ModAttributes.PLAYER_FIST_DAMAGE);
		this.orgEntity.getAttribute(ModAttributes.MAX_EQUIP_LOAD.get()).setBaseValue(25.0D);
		this.orgEntity.getAttribute(ModAttributes.MAX_STAMINA.get()).setBaseValue(80.0D);
	}
	
	public SpellInventory getAttunements()
	{
		return this.attunements;
	}
	
	public void onLoad(CompoundTag nbt)
	{
		this.humanity = nbt.getInt("Humanity");
		this.souls = nbt.getInt("Souls");
		this.human = nbt.getBoolean("IsHuman");
		if (nbt.contains("FocusPoints")) this.fp = Math.min(nbt.getFloat("FocusPoints"), this.getMaxFP());
		else this.fp = this.getMaxFP();
		this.attunements.load(nbt.getList("Attunements", 10));
		this.covenant = Covenants.COVENANTS.get(nbt.getInt("Covenant"));
		this.covenantProgress = nbt.getInt("CovenantProgress");
		
		for (int i = 0; i < Covenants.COVENANTS.size(); i++)
		{
			this.covenantProgresses[i] = nbt.getInt(Covenants.COVENANTS.get(i).toString());
		}
		
		this.stats.loadStats(nbt.getCompound("Stats"));
	}
	
	public final void save()
	{
		CompoundTag nbt = new CompoundTag();
		this.onSave(nbt);
		this.orgEntity.getPersistentData().put(DarkSouls.MOD_ID, nbt);
	}
	
	public void onSave(CompoundTag nbt)
	{
		nbt.putInt("Humanity", this.humanity);
		nbt.putInt("Souls", this.souls);
		nbt.putBoolean("IsHuman", this.human);
		nbt.putFloat("FocusPoints", this.fp);
		nbt.put("Attunements", this.attunements.save(new ListTag()));
		nbt.putInt("Covenant", Covenants.COVENANTS.indexOf(this.covenant));
		nbt.putInt("CovenantProgress", this.covenantProgress);
		
		for (int i = 0; i < Covenants.COVENANTS.size(); i++)
		{
			nbt.putInt(Covenants.COVENANTS.get(i).toString(), this.covenantProgresses[i]);
		}
		
		CompoundTag statsNbt = new CompoundTag();
		this.stats.saveStats(statsNbt);
		nbt.put("Stats", statsNbt);
	}
	
	@Override
	public boolean canBeParried()
	{
		return true;
	}
	
	@Override
	public boolean canBeBackstabbed()
	{
		return !this.getEntityState().isInvincible();
	}
	
	@Override
	public boolean canBePunished()
	{
		return this.getEntityState() == EntityState.PUNISHABLE;
	}
	
	@Override
	public ShieldHoldType getShieldHoldType()
	{
		MeleeWeaponCap weapon = this.getHeldMeleeWeaponCap(this.orgEntity.getUsedItemHand());
		return weapon != null && weapon.getWeaponCategory() == WeaponCategory.GREATSHIELD ? ShieldHoldType.VERTICAL_REVERSE
				: ShieldHoldType.VERTICAL;
	}
	
	@Override
	public boolean isTwohanding()
	{
		return this.twoHanding;
	}
	
	public void setTwoHanding(boolean value)
	{
		this.twoHanding = value;
		Stats.changeWeaponScalingAttributes(this);
	}
	
	public void addTeleport(BonfireBlockEntity bonfire) {}
	
	public Covenant getCovenant()
	{
		return this.covenant;
	}
	
	public void setCovenant(Covenant value)
	{
		if (this.covenant == value) return;
		this.covenant = value;
		this.covenantProgress = 0;
	}
	
	public int getCovenantProgress()
	{
		return this.covenantProgress;
	}
	
	public void setCovenantProgress(int value)
	{
		this.covenantProgress = value;
		int i = Covenants.COVENANTS.indexOf(this.covenant);
		if (this.covenantProgresses[i] < value)
		{
			this.covenantProgresses[i] = value;
		}
	}
	
	public void raiseCovenantProgress(int raise)
	{
		this.setCovenantProgress(this.covenantProgress + raise);
	}
	
	public int getLastProgressFor(Covenant covenant)
	{
		return this.covenantProgresses[Covenants.COVENANTS.indexOf(covenant)];
	}
	
	public StatHolder getStats()
	{
		return this.stats;
	}
	
	public int getSoulLevel()
	{
		return this.stats.getLevel();
	}
	
	public int getSouls()
	{
		return this.souls;
	}
	
	public void setSouls(int value)
	{
		this.souls = Math.max(0, value);
	}
	
	public void raiseSouls(int value)
	{
		this.setSouls(this.souls + value);
	}
	
	public float getFP()
	{
		return this.fp;
	}
	
	public void setFP(float value)
	{
		this.fp = ModMath.clamp(value, 0, this.getMaxFP());
	}
	
	public void raiseFP(float value)
	{
		this.setFP(this.fp + value);
	}
	
	public float getMaxFP()
	{
		return (float)this.orgEntity.getAttributeValue(ModAttributes.MAX_FOCUS_POINTS.get());
	}
	
	public boolean isHuman()
	{
		return this.human;
	}
	
	public void setHuman(boolean value)
	{
		this.human = value;
	}
	
	public boolean hasEnoughHumanity(int cost)
	{
		return this.isCreativeOrSpectator() ? true : this.humanity >= cost;
	}
	
	public boolean hasEnoughSouls(int cost)
	{
		return this.isCreativeOrSpectator() ? true : this.souls >= cost;
	}
	
	public int getHumanity()
	{
		return this.humanity;
	}
	
	public void setHumanity(int value)
	{
		this.humanity = ModMath.clamp(value, 0, 99);
	}
	
	public void raiseHumanity(int value)
	{
		this.setHumanity(this.humanity + value);
	}
	
	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.putLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
		animatorClient.putLivingAnimation(LivingMotion.WALKING, Animations.BIPED_WALK);
		animatorClient.putLivingAnimation(LivingMotion.RUNNING, Animations.BIPED_RUN);
		animatorClient.putLivingAnimation(LivingMotion.SNEAKING, Animations.BIPED_SNEAK);
		animatorClient.putLivingAnimation(LivingMotion.SWIMMING, Animations.BIPED_SWIM);
		animatorClient.putLivingAnimation(LivingMotion.FLOATING, Animations.BIPED_FLOAT);
		animatorClient.putLivingAnimation(LivingMotion.KNEELING, Animations.BIPED_KNEEL);
		animatorClient.putLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.putLivingAnimation(LivingMotion.MOUNTED, Animations.BIPED_HORSEBACK_IDLE);
		animatorClient.putLivingAnimation(LivingMotion.DRINKING, Animations.BIPED_DRINK);
		animatorClient.putLivingAnimation(LivingMotion.CONSUME_SOUL, Animations.BIPED_CONSUME_SOUL);
		animatorClient.putLivingAnimation(LivingMotion.EATING, Animations.BIPED_EAT);
		animatorClient.putLivingAnimation(LivingMotion.AIMING, Animations.BIPED_BOW_AIM);
		animatorClient.putLivingAnimation(LivingMotion.RELOADING, Animations.BIPED_CROSSBOW_RELOAD);
		animatorClient.putLivingAnimation(LivingMotion.SHOOTING, Animations.BIPED_BOW_REBOUND);
		animatorClient.putLivingAnimation(LivingMotion.DIGGING, Animations.BIPED_DIG);
		animatorClient.putLivingAnimation(LivingMotion.BLOCKING, Animations.createSupplier((cap, part) ->
		{
			if (this.isTwohanding()) return Animations.BIPED_BLOCK_TH_SWORD;
			MeleeWeaponCap weapon = cap.getHeldMeleeWeaponCap(cap.getOriginalEntity().getUsedItemHand());
			return weapon == null || !weapon.getWeaponCategory().isShield() ? Animations.BIPED_BLOCK_HORIZONTAL
					: weapon.getWeaponCategory() == WeaponCategory.GREATSHIELD ? Animations.BIPED_BLOCK_GREATSHIELD
					: Animations.BIPED_BLOCK_VERTICAL;
		}));
		animatorClient.setCurrentMotionsToDefault();
	}
	
	public void changeYaw(float amount)
	{
		this.yaw = amount;
	}
	
	public int getStatValue(Stat stat)
	{
		return this.stats.getStatValue(stat);
	}
	
	@Override
	public void updateOnServer()
	{
		super.updateOnServer();
		this.tickSinceLastAction++;
	}
	
	@Override
	public void update()
	{
		super.update();
		this.orgEntity.getFoodData().setFoodLevel(15);
	}
	
	@Override
	public StaticAnimation getDeflectAnimation()
	{
		return Animations.HOLLOW_DEFLECTED;
	}
	
	@Override
	public DeathAnimation getDeathAnimation(ExtendedDamageSource dmgSource)
	{
		return HumanoidCap.getHumanoidDeathAnimation(this, dmgSource);
	}
	
	@Override
	public boolean onHurt(DamageSource damageSource, float amount)
	{
		if(super.onHurt(damageSource, amount))
		{
			this.tickSinceLastAction = 0;
			return true;
		}
		else return false;
	}
	
	public boolean isCreativeOrSpectator()
	{
		return this.orgEntity.isCreative() || this.orgEntity.isSpectator();
	}
	
	@Override
	public ExtendedDamageSource getDamageSource(Vec3 attackPos, int staminaDmg,
			StunType stunType, Deflection reqDeflection, float poiseDamage, Damages damages)
	{
		return ExtendedDamageSource.causePlayerDamage(this.orgEntity, attackPos, stunType, reqDeflection, poiseDamage, staminaDmg, damages);
	}
	
	public void discard()
	{
		super.onDeath();
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB)
	{
		return modelDB.ENTITY_BIPED;
	}
	
	@Override
	public StaticAnimation getHitAnimation(ExtendedDamageSource dmgSource)
	{
		return HumanoidCap.getHumanoidHitAnimation(this, dmgSource);
	}
	
	@Override
	public float getEncumbrance()
	{
		return (float) (this.orgEntity.getAttributeValue(ModAttributes.EQUIP_LOAD.get())
				/ this.orgEntity.getAttributeValue(ModAttributes.MAX_EQUIP_LOAD.get()));
	}

	@Override
	public EquipLoadLevel getEquipLoadLevel()
	{
		float e = this.getEncumbrance();

		if (e <= 0.0F)
			return EquipLoadLevel.NONE;
		else if (e <= 0.25F)
			return EquipLoadLevel.LIGHT;
		else if (e <= 0.50F)
			return EquipLoadLevel.MEDIUM;
		else if (e <= 1.00F)
			return EquipLoadLevel.HEAVY;
		else
			return EquipLoadLevel.OVERENCUMBERED;
	}
}