package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.entity.ClientPlayerCap;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSRotatePlayerYaw;

public class ImmovableAnimation extends StaticAnimation
{
	public ImmovableAnimation(float convertTime, String path, String armature, boolean clientOnly)
	{
		super(true, convertTime, false, path, armature, clientOnly);
	}
	
	public ImmovableAnimation(float convertTime, boolean isRepeat, String path, String armature, boolean clientOnly)
	{
		super(true, convertTime, isRepeat, path, armature, clientOnly);
	}
	
	@Override
	public void onActivate(LivingCap<?> entitydata)
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
	public void onFinish(LivingCap<?> entitydata, boolean isEnd)
	{
		super.onFinish(entitydata, isEnd);
		
		if (entitydata.isClientSide() && entitydata instanceof ClientPlayerCap)
	    {
			((ClientPlayerCap)entitydata).changeYaw(0);
			ModNetworkManager.sendToServer(new CTSRotatePlayerYaw(0));
	    }
	}
	
	@Override
	public LivingCap.EntityState getState(float time)
	{
		return LivingCap.EntityState.PRE_DELAY;
	}
}