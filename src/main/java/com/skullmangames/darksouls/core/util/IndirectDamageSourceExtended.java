package com.skullmangames.darksouls.core.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.IndirectEntityDamageSource;

public class IndirectDamageSourceExtended extends IndirectEntityDamageSource implements IExtendedDamageSource {
	private float impact;
	private float armorIgnore;
	private StunType stunType;
	private DamageType damageType;

	public IndirectDamageSourceExtended(String damageTypeIn, Entity source, Entity owner, StunType stunType)
	{
		super(damageTypeIn, source, owner);
		
		//EntitydataLiving entityCap = (EntitydataLiving) source.getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
		//CapabilityItem capItem = entityCap.getHeldItemCapability(EnumHand.MAIN_HAND);
		/**
		if(capItem != null)
		{
			this.impact = (float) capItem.getDamageAttributesInCondition(entityCap.getOriginalEntity().getHeldItemOffhand().isEmpty()).getImpact();
			this.armorIgnore = (float) capItem.getDamageAttributesInCondition(entityCap.getOriginalEntity().getHeldItemOffhand().isEmpty()).getArmorIgnore();
		}**/
		
		this.stunType = stunType;
	}
	
	@Override
	public void setImpact(float amount) {
		this.impact = amount;
	}

	@Override
	public void setArmorNegation(float amount) {
		this.armorIgnore = amount;
	}

	@Override
	public void setStunType(StunType stunType) {
		this.stunType = stunType;
	}

	@Override
	public float getImpact() {
		return impact;
	}

	@Override
	public float getArmorNegation() {
		return armorIgnore;
	}

	@Override
	public StunType getStunType() {
		return stunType;
	}

	@Override
	public DamageType getExtDamageType() {
		return damageType;
	}

	@Override
	public Entity getOwner() {
		return super.getDirectEntity();
	}

	@Override
	public String getType() {
		return super.getMsgId();
	}

	@Override
	public int getSkillId() {
		return -1;
	}
}