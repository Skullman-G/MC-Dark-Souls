package com.skullmangames.darksouls.core.util.parser.xml;

import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.skullmangames.darksouls.DarkSouls;

public class XmlParser
{
	private static final Pattern DATA = Pattern.compile(">(.+?)<");
	private static final Pattern START_TAG = Pattern.compile("<(.+?)>");
	private static final Pattern ATTRIBUTE_NAME = Pattern.compile("(.+?)=");
	private static final Pattern ATTRIBUTE_VALUE = Pattern.compile("\"(.+?)\"");
	private static final Pattern CLOSED = Pattern.compile("(</|/>)");
	
	public static XmlNode loadXmlFile(BufferedReader bufferedReader)
	{
		try
		{
			bufferedReader.readLine();
			XmlNode node = loadXmlNode(bufferedReader);
			bufferedReader.close();
			return node;
		}
		catch (Exception e)
		{
			DarkSouls.LOGGER.error("Error with XML file format: " + e);
			return null;
		}
	}

	private static XmlNode loadXmlNode(BufferedReader reader) throws Exception
	{
		String line = reader.readLine().trim();
		
		if (line.startsWith("</")) return null;
		
		String[] startTagParts = getStartTag(line).split(" ");
		XmlNode node = new XmlNode(startTagParts[0].replace("/", ""));
		
		addAttributes(startTagParts, node);
		addData(line, node);
		
		if (CLOSED.matcher(line).find()) return node;
		
		XmlNode child = null;
		
		while ((child = loadXmlNode(reader)) != null) node.addChild(child);
		return node;
	}

	private static void addData(String line, XmlNode node)
	{
		Matcher matcher = DATA.matcher(line);
		if (matcher.find())
		{
			node.setData(matcher.group(1));
		}
	}

	private static void addAttributes(String[] titleParts, XmlNode node)
	{
		for (int i = 1; i < titleParts.length; i++)
		{
			if (titleParts[i].contains("="))
			{
				addAttribute(titleParts[i], node);
			}
		}
	}

	private static void addAttribute(String attributeLine, XmlNode node)
	{
		Matcher nameMatch = ATTRIBUTE_NAME.matcher(attributeLine);
		nameMatch.find();
		
		Matcher valMatch = ATTRIBUTE_VALUE.matcher(attributeLine);
		valMatch.find();
		
		node.addAttribute(nameMatch.group(1), valMatch.group(1));
	}

	private static String getStartTag(String line)
	{
		Matcher match = START_TAG.matcher(line);
		match.find();
		return match.group(1);
	}

}
