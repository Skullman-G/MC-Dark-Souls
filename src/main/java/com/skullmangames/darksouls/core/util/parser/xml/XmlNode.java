package com.skullmangames.darksouls.core.util.parser.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlNode
{
	private String name;
	private Map<String, String> attributes;
	private String data;
	private Map<String, List<XmlNode>> childNodes;

	public XmlNode(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}

	public String getData()
	{
		return this.data;
	}
	
	public boolean hasAttributes()
	{
		return this.attributes != null;
	}

	public String getAttributeValue(String attribute)
	{
		if (this.hasAttributes())
		{
			return this.attributes.get(attribute);
		}
		else return null;
	}
	
	public boolean hasChildNodes()
	{
		return this.childNodes != null;
	}

	public List<XmlNode> getChildren(String childname)
	{
		if (this.hasChildNodes())
		{
			List<XmlNode> children = childNodes.get(childname);
			if (children != null) return children;
		}
		
		return new ArrayList<XmlNode>();
	}
	
	public XmlNode getChild(String childName)
	{
		List<XmlNode> children = this.getChildren(childName);
		if (!children.isEmpty()) return children.get(0);
		
		return null;
	}
	
	public XmlNode getChildWithAttributeValue(String childname, String attribute, String attributevalue)
	{
		List<XmlNode> children = this.getChildren(childname);
		if (!children.isEmpty())
		{
			for (XmlNode child : children)
			{
				String childattributevalue = child.getAttributeValue(attribute);
				if (attributevalue.equals(childattributevalue)) return child;
			}
		}
		
		return null;
	}
	
	protected void addAttribute(String attribute, String value)
	{
		if (this.attributes == null)
		{
			this.attributes = new HashMap<String, String>();
		}
		
		this.attributes.put(attribute, value);
	}
	
	protected void addChild(XmlNode child)
	{
		if (this.childNodes == null)
		{
			this.childNodes = new HashMap<String, List<XmlNode>>();
		}
		
		List<XmlNode> list = this.childNodes.get(child.name);
		if (list == null)
		{
			list = new ArrayList<XmlNode>();
			this.childNodes.put(child.name, list);
		}
		
		list.add(child);
	}

	protected void setData(String content)
	{
		this.data = content;
	}
}