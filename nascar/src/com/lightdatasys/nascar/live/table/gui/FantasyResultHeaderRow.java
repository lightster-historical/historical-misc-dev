package com.lightdatasys.nascar.live.table.gui;

import java.awt.Dimension;
import java.util.ArrayList;

import com.lightdatasys.nascar.Race;
import com.lightdatasys.nascar.live.gui.cell.Cell;
import com.lightdatasys.nascar.live.gui.cell.FantasyResultCell;
import com.lightdatasys.nascar.live.gui.cell.PlayerCellSet;
import com.lightdatasys.nascar.live.gui.panel.LivePanel;



public class FantasyResultHeaderRow extends TableRow
{
	protected Cell[] cells;
	
	protected Race race;
	
	
	public FantasyResultHeaderRow(LivePanel panel, Dimension[] cellDimensions, int cellMargin
		, Race race, FantasyResultCell.Mode resultMode, int rowNum
		, ArrayList<PlayerCellSet> playerCellSets)
	{
		super(panel, cellDimensions, cellMargin);
		
		this.race = race;

		initCells(resultMode, rowNum, playerCellSets);
	}
	
	
	public Cell[] getCells()
	{
		return cells;
	}
	
	
	protected void initCells(FantasyResultCell.Mode resultMode, int rowNum
		, ArrayList<PlayerCellSet> playerCellSets)
	{
		cells = new Cell[cellDimensions.length];

		int playerCount = 0;
		int startCount = cellDimensions.length - playerCellSets.size();
		for(int i = 0; i < cellDimensions.length; i++)
		{
			if(i >= startCount)
			{
				FantasyResultCell cell = playerCellSets.get(playerCount).getResultCell(rowNum);
				cell.setMode(resultMode);
				cells[i] = cell;
				
				playerCount++;
			}
			else
			{
				cells[i] = null;
			}
		}

		initCellProperties();
	}
	
	
	public void setFantasyResultMode(FantasyResultCell.Mode mode)
	{
		for(Cell cell : cells)
		{
			if(cell instanceof FantasyResultCell)
			{
				FantasyResultCell resultCell = (FantasyResultCell)cell;
				resultCell.setMode(mode);
			}
		}
	}
}