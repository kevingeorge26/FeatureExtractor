package edu.commons.aider.sql;

import java.io.File;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class SettingsLoader
{
	private static Logger logger 	= Logger.getLogger(SettingsLoader.class.getName());
	private Hashtable<String,String> nodemap	= null;

	public SettingsLoader(String configPath)
	{
		if(configPath != null)
		{
			logger.info("Loading configuration file [ " + configPath + " ]");
			logger.debug("SettingsLoader, calling init method");
			init(configPath);
		}
		else
		{
			logger.info("Configuration file not found . . . Shutting down system");
			logger.error("SettingsLoader, Could not get the config path");
			System.exit(1);
		}
	}

	private void init(String file)
	{
		loadXml(file);
	}

	private String namespace(Node node)
	{
		String namespace 			= "";
		Node parent 				= node.getParentNode();
		Stack<String> collection 	= new Stack<String>();
		boolean wasIn 				= false;

		try
		{
			while(parent != null && !parent.getNodeName().equals("#document"))
			{
				collection.push(parent.getNodeName());
				parent = parent.getParentNode();
			}
			while(!collection.isEmpty())
			{
				namespace += collection.pop() + ".";
				wasIn = true;
			}
			if(wasIn)
				namespace = namespace.substring(0, namespace.length() - 1);
		}
		catch(Exception ex)
		{
			logger.error("namespace, ",ex);
		}
		logger.debug("namespace, the name space : " + namespace);
		return namespace;
	}

	private Hashtable<String,String> index(Document xml)
	{
		logger.debug("index, about to index the the leaf nodes of the xml");
		Hashtable<String,String> map = new Hashtable<String,String>();
		Node root 					= xml.getDocumentElement();
		Queue<Node> queue 			= new LinkedList<Node>();

		queue.add(root);

		while(!queue.isEmpty())
		{
			Node node = queue.remove();
			int nChilds = node.getChildNodes().getLength();

			for(int i = 0;i < nChilds;i++)
			{
				Node child = node.getChildNodes().item(i);
				if(!child.getNodeName().equals("#text"))
					queue.add(child);
				if(child.getParentNode() != null)
				{
					if(child.getChildNodes().getLength() == 0 && child.getParentNode().getChildNodes().getLength() == 1)
					{
						String namespc = namespace(child);
						String val = child.getNodeValue();

						logger.debug("index, Hash key : " + namespc + " Value : " + val);
						map.put(namespc, val);
					}
				}
			}
		}
		return map;
	}

	private void loadXml(String file)
	{
		try
		{
			logger.debug("loadXml, loading xml from file and indexing it");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document xml = builder.parse(new File(file));
			nodemap = index(xml);
		}
		catch (Exception e)
		{
			logger.error("loadXml, ",e);
		}
	}

	public String getProperty(String key)
	{
		String value = null;
		logger.debug("getProperty, getting the value for key : " + key);
		value = property(key);
		return value;
	}

	private String property(String key)
	{
		return nodemap.get(key);
	}
}