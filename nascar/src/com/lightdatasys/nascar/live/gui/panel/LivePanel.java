package com.lightdatasys.nascar.live.gui.panel;

import java.awt.Color;
import java.awt.Graphics2D;

import com.lightdatasys.nascar.Driver;
import com.lightdatasys.nascar.Race;
import com.lightdatasys.nascar.Result;
import com.lightdatasys.nascar.fantasy.FantasyResult;
import com.lightdatasys.nascar.live.gui.FullScreenWindow;
import com.lightdatasys.nascar.live.gui.LiveUpdater;
import com.lightdatasys.nascar.live.gui.cell.CarNoCell;
import com.lightdatasys.nascar.live.gui.cell.FantasyPlayerCell;
import com.lightdatasys.nascar.live.gui.cell.FantasyResultCell;
import com.lightdatasys.nascar.live.gui.cell.RaceStatusCell;
import com.lightdatasys.nascar.live.gui.cell.ResultCell;

public abstract class LivePanel
{
	private FullScreenWindow window;
	
	
	protected LivePanel(FullScreenWindow window)
	{
		super();
		
		this.window = window;
	}
	

	public FullScreenWindow getWindow()
	{
		return window;
	}
	
	public int getWidth()
	{
		return getWindow().getDevice().getDisplayMode().getWidth();
	}
	
	public int getHeight()
	{
		return getWindow().getDevice().getDisplayMode().getHeight();
	}

	
	public LiveUpdater getLiveUpdater()
	{
		return getWindow().getLiveUpdater();
	}
	
	public Race getRace()
	{
		return getLiveUpdater().getRace();
	}
	
	public Driver[] getDrivers()
	{
		return getLiveUpdater().getDrivers();
	}
	
	
	public abstract void update();	
	public abstract void render(Graphics2D g);	


	
	public CarNoCell createCarNoCell(int w, int h, String carNo, Color text, Color bg, Color border)
	{
		return new CarNoCell(getWindow().getDevice(), w, h, carNo, text, bg, border);
	}
	
	public FantasyPlayerCell createFantasyPlayerCell(int w, int h, String label, Color text, Color bg, Color border)
	{
		return new FantasyPlayerCell(getWindow().getDevice(), w, h, label, text,bg, border);
	}
	
	public FantasyPlayerCell createFantasyPlayerCell(int w, int h, String label, Color text, Color bg, Color border, boolean roundRectangles)
	{
		return new FantasyPlayerCell(getWindow().getDevice(), w, h, label, text,bg, border, roundRectangles);
	}
	
	public FantasyResultCell createFantasyResultCell(int w, int h, FantasyResult result, Color text, Color bg, Color border)
	{
		return new FantasyResultCell(getWindow().getDevice(), w, h, result, text, bg, border);
	}
	
	public RaceStatusCell createRaceStatusCell(int w, int h, Race race)
	{
		return new RaceStatusCell(getWindow().getDevice(), w, h, race);
	}
	
	public ResultCell createResultCell(int w, int h, Result result, Color text, Color bg, Color border)
	{
		return new ResultCell(getWindow().getDevice(), w, h, result, text, bg, border);
	}
}
