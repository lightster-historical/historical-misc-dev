package com.lightdatasys.nascar.live.setting;

public class Setting
{
	protected String keyname;
	protected String title;
	
	protected Object value;
	
	
	public Setting(String keyname, String title, Object defaultValue)
	{
		keyname = keyname;
		title = title;
		
		value = defaultValue;
	}
	
	
	public Object[] getValues()
	{
		return null;
	}
	
	public Object getValue()
	{
		return value;
	}
}
