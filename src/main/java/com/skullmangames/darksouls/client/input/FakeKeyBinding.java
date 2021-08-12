package com.skullmangames.darksouls.client.input;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;

public class FakeKeyBinding extends KeyBinding
{
	public FakeKeyBinding(String name)
	{
		super("fake_" + name, InputMappings.Type.KEYSYM, 0, "key.categories.fake");
		
		ALL.remove("fake_" + name, this);
	    MAP.removeKey(this);
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
