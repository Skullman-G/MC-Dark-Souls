package com.skullmangames.darksouls.core.util;

public class StringHelper
{
	public static String trySubstring(String value, int first, int last)
	{
		return value.length() < last ? value.substring(first) : value.substring(first, last);
	}
}
