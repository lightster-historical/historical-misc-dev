package com.lightdatasys.nascar.live.table.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.lightdatasys.nascar.Driver;
import com.lightdatasys.nascar.Race;
import com.lightdatasys.nascar.Result;
import com.lightdatasys.nascar.fantasy.FantasyPlayer;
import com.lightdatasys.nascar.fantasy.FantasyResult;
import com.lightdatasys.nascar.live.gui.Swap;
import com.lightdatasys.nascar.live.gui.cell.CarNoCell;
import com.lightdatasys.nascar.live.gui.cell.Cell;
import com.lightdatasys.nascar.live.gui.cell.PlayerCellSet;
import com.lightdatasys.nascar.live.gui.cell.ResultCell;
import com.lightdatasys.nascar.live.gui.panel.LivePanel;


public class DriverRow extends TableRow
{
	protected Result result;
	
	protected Cell[] cells;
	
	protected int yOffset;
	protected Swap swap;
	
	
	public DriverRow(LivePanel panel, Dimension[] cellDimensions, int cellMargin
		, Result result, ResultCell.Mode[] resultModes
		, ArrayList<PlayerCellSet> playerCellSets)
	{
		super(panel, cellDimensions, cellMargin);
		
		this.result = result;
		this.yOffset = 0;
		this.swap = null;

		initCells(resultModes, playerCellSets);
	}
	
	
	@Override
	public void render(Graphics2D g)
	{
		super.render(g);
	}
	
	
	public void setResultMode(int i, ResultCell.Mode mode)
	{
		if(0 <= i && i < cells.length && cells[i] instanceof ResultCell)
		{
			((ResultCell)cells[i]).setMode(mode);
			triggerUpdate();
		}
	}
	
	
	public Result getResult()
	{
		return result;
	}
	
	public Cell[] getCells()
	{
		return cells;
	}
	
	public int getYOffset()
	{
		return super.getYOffset();
	}
	
	
	protected void initCells(ResultCell.Mode[] resultModes
		, ArrayList<PlayerCellSet> playerCellSets)
	{
		cells = new Cell[cellDimensions.length];
		Race race = result.getRace();
		
		int playerCount = 0;
		for(int i = 0; i < cellDimensions.length; i++)
		{
			Dimension dim = cellDimensions[i];
		
			if(i < resultModes.length)
			{
				ResultCell cell = panel.createResultCell(dim.width, dim.height, result
					, Color.WHITE, Color.BLACK, Color.BLACK);
				cell.setMode(resultModes[i]);
				cells[i] = cell;
			}
			else if(i == resultModes.length)
			{
				Driver driver = result.getDriver();
				CarNoCell cell = panel.createCarNoCell(dim.width, dim.height
					, result.getCar(), driver.getFontColor()
					, driver.getBackgroundColor(), driver.getBorderColor());
				cells[i] = cell;
			}
			else
			{
				FantasyPlayer player = playerCellSets.get(playerCount).getPlayer();
				FantasyResult fantasyResult = race.getFantasyResultByPlayer(player);
				
				if(fantasyResult != null
					&& fantasyResult.getPicks().contains(result.getCar()))
				{
					cells[i] = playerCellSets.get(playerCount).getPlayerCell();
				}
				
				playerCount++;
			}
		}
		
		initCellProperties();
	}
	
	
	public static class DefaultComparator implements Comparator<DriverRow>
	{
		public int compare(DriverRow o1, DriverRow o2)
		{
			Result r1 = o1.getResult();
			Result r2 = o2.getResult();
			
			if(r1.isCurrent() == r2.isCurrent())
				return 0;
			else if(r1.isCurrent())
				return -1;
			else
				return 1;
		}
	}
 
	public static class FinishComparator extends DefaultComparator
	{
		public int compare(DriverRow o1, DriverRow o2)
		{
			Result r1 = o1.getResult();
			Result r2 = o2.getResult();
			
			if(r1.getFinish() < r2.getFinish())
				return -1;
			else if(r1.getFinish() > r2.getFinish())
				return 1;
			
			return 0;
		}
	}
 
	public static class LastLapSpeedComparator extends DefaultComparator
	{
		public int compare(DriverRow o1, DriverRow o2)
		{
			int compareSuper = super.compare(o1, o2);
			
			if(compareSuper == 0)
			{
				Result r1 = o1.getResult();
				Result r2 = o2.getResult();
				
				if(r1.getLastLapSpeed() < r2.getLastLapSpeed())
					return 1;
				else if(r1.getLastLapSpeed() > r2.getLastLapSpeed())
					return -1;
				
				return 0;
			}
			else
				return compareSuper;
		}
	}
}