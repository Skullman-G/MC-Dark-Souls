package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.entity.ClientPlayerData;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSRotatePlayerYaw;

public class ImmovableAnimation extends StaticAnimation
{
	public ImmovableAnimation(int id, float convertTime, String path, String armature, boolean clientOnly)
	{
		super(id, convertTime, false, path, armature, clientOnly);
	}
	
	public ImmovableAnimation(int id, float convertTime, boolean isRepeat, String path, String armature, boolean clientOnly)
	{
		super(id, convertTime, isRepeat, path, armature, clientOnly);
	}
	
	@Override
	public void onActivate(LivingData<?> entitydata)
	{
		super.onActivate(entitydata);
		
		if(entitydata.isClientSide())
		{
			entitydata.getClientAnimator().resetMotion();
			entitydata.getClientAnimator().resetMixMotion();
			entitydata.getClientAnimator().offMixLayer(entitydata.getClientAnimator().mixLayerLeft, true);
			entitydata.getClientAnimator().offMixLayer(entitydata.getClientAnimator().mixLayerRight, true);
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