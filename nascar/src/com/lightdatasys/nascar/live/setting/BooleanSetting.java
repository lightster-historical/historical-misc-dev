package com.lightdatasys.nascar.live.setting;

public class BooleanSetting extends Setting<Boolean>
{
	public BooleanSetting(String keyname, String title, boolean defaultValue, String trueValue, String falseValue)
	{
		super(keyname, title, defaultValue, true);
		
		addOption("1", Boolean.TRUE);
		addOption("0", Boolean.FALSE);
	}
}
