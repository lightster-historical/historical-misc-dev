package com.lightdatasys.nascar.live.setting;


public class RangeSetting extends Setting<Double>
{
	protected double minValue;
	protected double maxValue;
	
	
	public RangeSetting(String keyname, String title, double defaultValue, double min, double max)
	{
		super(keyname, title, defaultValue, false);
		
		minValue = min;
		maxValue = max;
	}
	
	
	public boolean setValue(double value)
	{
		if(minValue <= value && value <= maxValue)
		{
			super.setValue(value);
			return true;
		}
		else
		{
			return false;
		}
	}
}
