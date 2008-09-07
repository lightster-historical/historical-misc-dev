package com.lightdatasys.nascar.fantasy.gui.panel;

import java.awt.Color;
import java.awt.Graphics2D;

import com.lightdatasys.nascar.Driver;
import com.lightdatasys.nascar.Race;
import com.lightdatasys.nascar.fantasy.gui.FullScreenWindow;
import com.lightdatasys.nascar.fantasy.gui.LiveUpdater;
import com.lightdatasys.nascar.fantasy.gui.cell.CarNoCell;

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
}
