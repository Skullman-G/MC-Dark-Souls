package com.skullmangames.darksouls.core.util.parser.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlNode
{
	private final String name;
	private final Map<String, String> attributes;
	private String data;
	private final List<XmlNode> childNodes;

	public XmlNode(String name)
	{
		this.name = name;
		this.data = "";
		this.attributes = new HashMap<>();
		this.childNodes = new ArrayList<>();
	}

	public String getName()
	{
		return this.name;
	}

	public String getData()
	{
		return this.data;
	}
	
	public void setData(String value)
	{
		this.data = value;
	}
	
	@Override
	public String toString()
	{
		return this.toString(0);
	}
	
	private String toString(int indent)
	{
	    StringBuilder sb = new StringBuilder();
	    String ind = "  ".repeat(indent);

	    sb.append(ind).append("<").append(name);

	    attributes.forEach((key, value) ->
	    {
	        sb.append(" ").append(key).append("=\"").append(value).append("\"");
	    });

	    if ((data == null || data.isEmpty()) && childNodes.isEmpty())
	    {
	        sb.append("/>");
	        return sb.toString();
	    }

	    sb.append(">");
	    if (data != null && !data.isEmpty())
	    {
	        sb.append(data);
	    }

	    if (!childNodes.isEmpty())
	    {
	        sb.append("\n");
	        for (XmlNode child : childNodes)
	        {
	            sb.append(child.toString(indent + 1)).append("\n");
	        }
	        sb.append(ind);
	    }

	    sb.append("</").append(name).append(">");
	    return sb.toString();
	}

	public String getAttributeValue(String attribute)
	{
		return this.attributes.getOrDefault(attribute, "");
	}
	
	public boolean hasChildNodes()
	{
		return !this.childNodes.isEmpty();
	}
	
	private void collectChildren(XmlNode node, String childName, List<XmlNode> result, boolean direct)
	{
	    for (XmlNode child : node.childNodes)
	    {
	        if (child.getName().equals(childName))
	        {
	            result.add(child);
	        }
	        if (!direct) this.collectChildren(child, childName, result, direct);
	    }
	}
	
	public List<XmlNode> getChildren(String childname)
	{
		return this.getChildren(childname, true);
	}

	public List<XmlNode> getChildren(String childname, boolean direct)
	{
		List<XmlNode> result = new ArrayList<>();
		this.collectChildren(this, childname, result, direct);
		return result;
	}
	
	public XmlNode getDirectChild(String childName) 
	{
		for (XmlNode child : this.childNodes)
		{
			if (child.getName().equals(childName))
			{
				return child;
			}
		}
		
		return null;
	}
	
	public XmlNode getChildWithAttributeValue(String childname, String attribute, String attributevalue)
	{
		return this.getChildWithAttributeValue(childname, attribute, attributevalue, true);
	}
	
	public XmlNode getChildWithAttributeValue(String childname, String attribute, String attributevalue, boolean direct)
	{
		for (XmlNode child : this.getChildren(childname, direct))
		{
			String childattributevalue = child.getAttributeValue(attribute);
			if (attributevalue.equals(childattributevalue)) return child;
		}
		
		return null;
	}
	
	protected void addAttribute(String attribute, String value)
	{
		this.attributes.put(attribute, value);
	}
	
	protected void addChild(XmlNode child)
	{
		this.childNodes.add(child);
	}
}