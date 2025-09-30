package com.skullmangames.darksouls.core.util.json;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.core.util.ResourceBuilder;

public interface JsonBuilder<T> extends ResourceBuilder<T>
{
	public JsonObject toJson();
}
