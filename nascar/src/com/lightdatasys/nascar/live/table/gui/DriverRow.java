package com.lightdatasys.nascar.live.table.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
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
import com.lightdatasys.nascar.live.setting.Settings;


public class DriverRow extends TableRow
{
	protected Result result;
	
	protected Cell[] cells;
	protected ResultCell[] resultCells;
	
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
	
	
	public void setResultMode(int i, ResultCell.Mode mode)
	{
		if(0 <= i && i < cells.length && cells[i] instanceof ResultCell)
		{
			((ResultCell)cells[i]).setMode(mode);
		}
	}
	
	public void setResultModes(ResultCell.Mode[] modes)
	{
		int count = Math.min(modes.length, resultCells.length);
		
		for(int i = 0; i < count; i++)
		{
			setResultMode(i, modes[i]);
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
		resultCells = new ResultCell[resultModes.length];
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
				resultCells[i] = cell;
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
	
	
 
	public static class ResultNumericComparator extends DefaultComparator
	{
		protected ResultNumericRetriever retriever;
		protected Settings setting;
		
		public ResultNumericComparator(ResultNumericRetriever obj, Settings setting)
		{
			retriever = obj;
			this.setting = setting;
		}
		
		public int compare(DriverRow o1, DriverRow o2)
		{
			int comp = super.compare(o1, o2);
				
			if(setting.getBooleanValue("activesOnTop") && comp != 0)
				return comp;
			else
			{
				Result r1 = o1.getResult();
				Result r2 = o2.getResult();
				
				if(retriever.isAscending())
				{
					if(retriever.getValue(r1) < retriever.getValue(r2))
						return -1;
					else if(retriever.getValue(r1) > retriever.getValue(r2))
						return 1;
				}
				else
				{
					if(retriever.getValue(r1) < retriever.getValue(r2))
						return 1;
					else if(retriever.getValue(r1) > retriever.getValue(r2))
						return -1;
				}
			}
			
			return 0;
		}
		
		public String toString()
		{
			return retriever.toString();
		}
	}
	
	public static interface ResultNumericRetriever
	{
		public double getValue(Result result);
		public boolean isAscending();
		public String toString();
	}
 
	public static class FinishRetriever implements ResultNumericRetriever
	{		
		public double getValue(Result result)
			{return result.getFinish();}
		public boolean isAscending()
			{return true;}
		public String toString()
			{return "Finish";}
	}
 
	public static class DriverStandingsRetriever implements ResultNumericRetriever
	{		
		public double getValue(Result result)
			{return result.getSeasonRank();}
		public boolean isAscending()
			{return true;}
		public String toString()
			{return "Driver Standings";}
	}
 
	public static class LapsLedRetriever implements ResultNumericRetriever
	{		
		public double getValue(Result result)
			{return result.getLapsLed();}
		public boolean isAscending()
			{return false;}
		public String toString()
			{return "Laps Led";}
	}
 
	public static class RacePointsRetriever implements ResultNumericRetriever
	{		
		public double getValue(Result result)
			{return result.getRacePoints();}
		public boolean isAscending()
			{return false;}
		public String toString()
			{return "Race Points";}
	}
 
	public static class SpeedRetriever implements ResultNumericRetriever
	{		
		public double getValue(Result result)
			{return result.getSpeed();}
		public boolean isAscending()
			{return false;}
		public String toString()
			{return "Speed";}
	}
 
	public static class LastLapSpeedRetriever implements ResultNumericRetriever
	{		
		public double getValue(Result result)
			{return result.getLastLapSpeed();}
		public boolean isAscending()
			{return false;}
		public String toString()
			{return "Last Lap Speed";}
	}

	/*
	LOCAL_INTERVAL("Local Interval"), 
	LOCAL_POINTS_DIFF("Local Points Diff")
	*/
}