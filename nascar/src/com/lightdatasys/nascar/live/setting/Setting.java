package com.lightdatasys.nascar.live.setting;

import com.lightdatasys.nascar.live.gui.cell.ResultCell;


public class Setting
{
	protected String keyname;
	protected String title;
	
	protected Object value;
	
	
	public Setting(String keyname, String title, Object defaultValue)
	{
		this.keyname = keyname;
		this.title = title;
		
		value = defaultValue;
	}
	
	
	public void setValue(Object value)
	{
		this.value = value;
	}
	
	
	public Option[] getValueSet()
	{
		return null;
	}
	
	public Object getValue()
	{
		return value;
	}
	
	
	public static class Option
	{
		public Object value;
		public Object displayValue;
		
		
		public Option()
		{
			this.value = null;
			this.displayValue = null;
		}
		
		public Option(Object value, Object displayValue)
		{
			this.value = value;
			this.displayValue = displayValue;
		}
	}
}
