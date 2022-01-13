package com.skullmangames.darksouls.client.input;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;

public class FakeKeyBinding extends KeyMapping
{
	public FakeKeyBinding(String name)
	{
		super("fake."+name, InputConstants.Type.KEYSYM, 0, "key.categories.fake");
		
		ALL.remove("fake."+name, this);
	    CATEGORIES.remove("key.categories.fake");
	}
	
	@Override
	public boolean isDown()
	{
		return false;
	}
	
	@Override
	public boolean consumeClick()
	{
		return false;
	}
}
