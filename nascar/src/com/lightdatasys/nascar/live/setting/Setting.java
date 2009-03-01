package com.lightdatasys.nascar.live.setting;

import java.util.AbstractMap;
import java.util.HashMap;


public class Setting<ValueType>
{
	protected String keyname;
	protected String title;
	
	protected ValueType value;
	
	protected AbstractMap<String,Option<ValueType>> options;
	
	protected boolean strict;
	
	
	public Setting(String keyname, String title, ValueType defaultValue)
	{
		this(keyname, title, defaultValue, false);
	}
	
	public Setting(String keyname, String title, ValueType defaultValue, boolean strict)
	{
		this.keyname = keyname;
		this.title = title;
		
		value = defaultValue;
		
		options = new HashMap<String,Option<ValueType>>();
		this.strict = strict;
	}
	
	
	public String getKeyname()
	{
		return keyname;
	}
	
	public String getTitle()
	{
		return title;
	}	
	
	public ValueType getValue()
	{
		return value;
	}	
	
	@SuppressWarnings("unchecked")
	public Option<ValueType>[] getValueSet()
	{
		Option<?>[] valueSets = new Option<?>[options.values().size()];
		options.values().toArray(valueSets);
		return (Option<ValueType>[])valueSets;
	}
	
	
	public boolean setValue(ValueType value)
	{
		this.value = value;
		
		return true;
	}	
	
	public boolean setValueUsingKey(String key)
	{
		if(options.containsKey(key))
		{
			return setValue(options.get(key).value);
		}
		else
		{
			return false;
		}
	}
	
	
	public void addOption(String key, ValueType value)
	{
		options.put(key, new Option<ValueType>(key, value));
	}
	
	
	public String toString()
	{
		return title;
	}
	
	
	public static class Option<ValueType>
	{
		public String key;
		public ValueType value;
		
		
		public Option()
		{
			this.key = null;
			this.value = null;
		}
		
		public Option(String key, ValueType value)
		{
			this.key = key;
			this.value = value;
		}
	}
}
