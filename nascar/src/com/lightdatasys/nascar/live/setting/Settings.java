package com.lightdatasys.nascar.live.setting;

import java.util.AbstractMap;

import com.lightdatasys.nascar.live.gui.cell.FantasyResultCell;
import com.lightdatasys.nascar.live.gui.cell.ResultCell;

public class Settings 
{
	protected AbstractMap<String,Setting> settings;
	
	private float fps;
	private float ups;
	
	private long swapPeriod;
	
	private float scrollSpeed;

	private ResultCell.Mode resultMode1;
	private ResultCell.Mode resultMode2;
	private FantasyResultCell.Mode fantasyMode1;
	private FantasyResultCell.Mode fantasyMode2;
	
	
	public Settings()
	{
		fps = 75.0f;
		ups = 2.0f;

		swapPeriod = 500;
		scrollSpeed = 0.0f;

		resultMode1 = ResultCell.Mode.LOCAL_POINTS_DIFF;
		resultMode2 = ResultCell.Mode.LOCAL_INTERVAL;
		fantasyMode1 = FantasyResultCell.Mode.SEASON_POINTS;
		fantasyMode2 = FantasyResultCell.Mode.LEADER_DRIVER_DIFF;
	}
	
	
	public synchronized float getFPS()
	{
		return fps;
	}
	
	public synchronized float getUPS()
	{
		return ups;
	}

	public synchronized long getSwapPeriod()
	{
		return swapPeriod;
	}
	
	public synchronized float getScrollSpeed()
	{
		return scrollSpeed;
	}
	
	public synchronized ResultCell.Mode getResultMode1()
	{
		return resultMode1;
	}
	
	public synchronized ResultCell.Mode getResultMode2()
	{
		return resultMode2;
	}
	
	public synchronized FantasyResultCell.Mode getFantasyMode1()
	{
		return fantasyMode1;
	}
	
	public synchronized FantasyResultCell.Mode getFantasyMode2()
	{
		return fantasyMode2;
	}
	
	
	public synchronized void setFPS(float fps)
	{
		this.fps = fps;
	}
	
	public synchronized void setUPS(float ups)
	{
		this.ups = ups;
	}
	
	public synchronized void setSwapPeriod(long swapPeriod)
	{
		this.swapPeriod = swapPeriod;
	}
	
	public synchronized void setScrollSpeed(float scrollSpeed)
	{
		this.scrollSpeed = scrollSpeed;
	}
	
	public synchronized void setResultMode1(ResultCell.Mode resultMode)
	{
		this.resultMode1 = resultMode;
	}
	
	public synchronized void setResultMode2(ResultCell.Mode resultMode)
	{
		this.resultMode2 = resultMode;
	}
	
	public synchronized void setFantasyMode1(FantasyResultCell.Mode fantasyMode1)
	{
		this.fantasyMode1 = fantasyMode1;
	}
	
	public synchronized void setFantasyMode2(FantasyResultCell.Mode fantasyMode2)
	{
		this.fantasyMode2 = fantasyMode2;
	}
}
