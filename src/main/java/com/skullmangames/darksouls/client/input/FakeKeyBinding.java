package com.skullmangames.darksouls.client.input;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;

public class FakeKeyBinding extends KeyBinding
{
	public FakeKeyBinding(String name)
	{
		super("fake."+name, InputMappings.Type.KEYSYM, 0, "key.categories.fake");
		
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
