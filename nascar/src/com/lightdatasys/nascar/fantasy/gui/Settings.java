package com.lightdatasys.nascar.fantasy.gui;

public class Settings 
{
	private float fps;
	private float ups;
	
	private long swapPeriod;
	
	private float scrollSpeed;
	
	private ResultCell.Mode resultMode;
	private FantasyResultCell.Mode fantasyMode1;
	private FantasyResultCell.Mode fantasyMode2;
	
	
	public Settings()
	{
		fps = 75.0f;
		ups = 2.0f;

		swapPeriod = 500;
		scrollSpeed = 0.5f;
		
		resultMode = ResultCell.Mode.LOCAL_INTERVAL;
		fantasyMode1 = FantasyResultCell.Mode.POSITION;
		fantasyMode2 = FantasyResultCell.Mode.DRIVER_RACE_POINTS;
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
	
	public synchronized ResultCell.Mode getResultMode()
	{
		return resultMode;
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
	
	public synchronized void setResultMode(ResultCell.Mode resultMode)
	{
		this.resultMode = resultMode;
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
