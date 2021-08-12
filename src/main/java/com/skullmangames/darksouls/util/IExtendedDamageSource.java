package com.skullmangames.darksouls.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;

public interface IExtendedDamageSource
{
	public static DamageSourceExtended causePlayerDamage(PlayerEntity player, StunType stunType, DamageType damageType, int id)
	{
        return new DamageSourceExtended("player", player, stunType, damageType, id);
    }
	
	public static DamageSourceExtended causeMobDamage(LivingEntity mob, StunType stunType, DamageType damageType, int id)
	{
        return new DamageSourceExtended("mob", mob, stunType, damageType, id);
    }
	
	public static DamageSourceExtended getFrom(IExtendedDamageSource original)
	{
		return new DamageSourceExtended(original.getType(), original.getOwner(), original.getStunType(), original.getExtDamageType(), original.getSkillId());
	}
	
	public void setImpact(float amount);
	public void setArmorNegation(float amount);
	public void setStunType(StunType stunType);
	public float getImpact();
	public float getArmorNegation();
	public int getSkillId();
	public StunType getStunType();
	public DamageType getExtDamageType();
	public Entity getOwner();
	public String getType();
	
	public static enum StunType
	{
		SHORT(TextFormatting.GREEN + "SHORT" + TextFormatting.DARK_GRAY + " stun"),
		LONG(TextFormatting.GOLD + "LONG" + TextFormatting.DARK_GRAY + " stun"),
		HOLD(TextFormatting.RED + "HOLDING");
		
		private String tooltip;
		
		StunType(String tooltip)
		{
			this.tooltip = tooltip;
		}
		
		@Override
		public String toString()
		{
			return tooltip;
		}
	}
	
	public static enum DamageType
	{
		PHYSICAL, MAGIC
	}
}