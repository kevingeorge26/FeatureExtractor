package edu.commons.aider.sql;

import java.util.LinkedHashMap;

public class DataRow extends LinkedHashMap<String,String>
{	
	private static final long serialVersionUID = 1L;
	
	public DataRow()
	{
	}
	
	@Override
	public String put(String key, String value)
	{
		int i = 0;
		String nkey = key;
		while(this.containsKey(nkey))
			nkey = key + "_" + i++; 
		return super.put(nkey, value);
	}
	
	protected void setValue(int index,String column,String value)
	{
		this.put(column, value);
	}
	
	protected String getAt(int index)
	{
		if(this.values().size() > index)
			return (String) this.values().toArray()[index];
		return null;
	}
}
