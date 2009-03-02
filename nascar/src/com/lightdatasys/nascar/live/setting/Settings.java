package com.lightdatasys.nascar.live.setting;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;

import com.lightdatasys.nascar.Result;
import com.lightdatasys.nascar.live.table.gui.DriverRow;
import com.lightdatasys.nascar.live.table.gui.DriverRow.ResultNumericRetriever;

public class Settings 
{
	protected AbstractMap<String,Setting<?>> settings;
	
	
	public Settings()
	{
		settings = new HashMap<String,Setting<?>>();
		
		{
			RangeSetting setting;		
			setting = new RangeSetting("targetFPS", "Target FPS", 75, .1, 120);
			setting.addOption("45", 45.0);
			setting.addOption("55", 55.0);
			setting.addOption("65", 65.0);
			setting.addOption("75", 75.0);
			setting.addOption("85", 85.0);
			
			add(setting);
		}
		
		{
			RangeSetting setting;		
			setting = new RangeSetting("targetUPS", "Target UPS", 2, .1, 120);
			setting.addOption(".25", .25);
			setting.addOption(".5", .5);
			setting.addOption("1", 1.0);
			setting.addOption("2", 2.0);
			setting.addOption("5", 5.0);
			setting.addOption("10", 10.0);
			
			add(setting);
		}
		
		{
			RangeSetting setting;		
			setting = new RangeSetting("swapPeriod", "Swap Period", 500, 0, 5000);
			setting.addOption(".25", 250.0);
			setting.addOption(".5", 500.0);
			setting.addOption("1", 1000.0);
			setting.addOption("2", 2000.0);
			
			add(setting);
		}
		
		for(int i = 1; i <= 5; i++)
		{
			add(new ResultModeSetting(i));
		}
		
		for(int i = 1; i <= 2; i++)
		{	
			add(new FantasyResultModeSetting(i));
		}

		{
			add(new BooleanSetting("highlightActives", "Highlight Actives", false, "Yes", "No"));
		}
		
		{
			DriverRow.DefaultComparator comparator = new DriverRow.ResultNumericComparator(new DriverRow.FinishRetriever());
			Setting<DriverRow.DefaultComparator> setting;
			setting = new Setting<DriverRow.DefaultComparator>("rowOrder", "Row Order", comparator);
			setting.addOption("finish", comparator);
			//setting.addOption("lastLapSpeed", new DriverRow.ResultNumericComparator(new DriverRow.LastLapSpeedComparator()));
			setting.addOption("driverStandings", new DriverRow.ResultNumericComparator(new DriverRow.DriverStandingsRetriever()));
			setting.addOption("lapsLed", new DriverRow.ResultNumericComparator(new DriverRow.LapsLedRetriever()));
			setting.addOption("racePoints", new DriverRow.ResultNumericComparator(new DriverRow.RacePointsRetriever()));
			setting.addOption("speed", new DriverRow.ResultNumericComparator(new DriverRow.SpeedRetriever()));
			setting.addOption("lastLapSpeed", new DriverRow.ResultNumericComparator(new DriverRow.LastLapSpeedRetriever()));

			add(setting);
		}
	}
	
	
	public void add(Setting<?> setting)
	{
		settings.put(setting.getKeyname(), setting);
	}
	
	public Setting<?> get(String key)
	{
		return settings.get(key);
	}
	
	
	public Object getValue(String key)
	{
		Setting<?> setting = settings.get(key);
		if(setting != null)
		{
			return setting.getValue();
		}
		
		return null;
	}
	
	public Boolean getBooleanValue(String key)
	{
		Object value = getValue(key);
		
		if(value instanceof Boolean)
			return (Boolean)value;
		
		return null;
	}
	
	public Double getDoubleValue(String keyname)
	{
		Object value = getValue(keyname);
		
		if(value instanceof Double)
			return (Double)value;
		
		return null;
	}
	
	public Long getLongValue(String keyname)
	{
		Object value = getValue(keyname);
		
		if(value instanceof Long)
			return (Long)value;
		else if(value instanceof Double)
			return ((Double)value).longValue();
		
		return null;
	}
	
	
	public boolean setValueUsingKey(String settingKey, String valueKey)
	{
		Setting<?> setting = settings.get(settingKey);
		if(setting != null)
		{
			setting.setValueUsingKey(valueKey);
		}
		
		return false;
	}
	
	
	public ArrayList<Setting<?>> getSettingsList()
	{
		return new ArrayList<Setting<?>>(settings.values());
	}
}
