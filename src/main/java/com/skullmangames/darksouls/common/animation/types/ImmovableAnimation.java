package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.entity.ClientPlayerData;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSRotatePlayerYaw;

public class ImmovableAnimation extends StaticAnimation
{
	public ImmovableAnimation(int id, float convertTime, String path)
	{
		super(id, convertTime, false, path);
	}
	
	public ImmovableAnimation(int id, float convertTime, boolean isRepeat, String path)
	{
		super(id, convertTime, isRepeat, path);
	}
	
	@Override
	public void onActivate(LivingData<?> entitydata)
	{
		super.onActivate(entitydata);
		
		if(entitydata.isClientSide())
		{
			entitydata.getClientAnimator().resetMotion();
			entitydata.getClientAnimator().resetMixMotion();
			entitydata.getClientAnimator().offMixLayer(true);
			entitydata.currentMotion = LivingMotion.IDLE;
			entitydata.currentMixMotion = LivingMotion.NONE;
		}
		
		entitydata.cancelUsingItem();
	}
	
	@Override
	public void onFinish(LivingData<?> entitydata, boolean isEnd)
	{
		super.onFinish(entitydata, isEnd);
		
		if (entitydata.isClientSide() && entitydata instanceof ClientPlayerData)
	    {
			((ClientPlayerData)entitydata).changeYaw(0);
			ModNetworkManager.sendToServer(new CTSRotatePlayerYaw(0));
	    }
	}
	
	@Override
	public LivingData.EntityState getState(float time)
	{
		return LivingData.EntityState.PRE_DELAY;
	}
}