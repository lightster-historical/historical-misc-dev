package com.lightdatasys.nascar.live.table.gui;

import java.awt.Dimension;
import java.util.ArrayList;

import com.lightdatasys.nascar.Race;
import com.lightdatasys.nascar.live.gui.cell.Cell;
import com.lightdatasys.nascar.live.gui.cell.PlayerCellSet;
import com.lightdatasys.nascar.live.gui.panel.LivePanel;

public class PlayerHeaderRow extends TableRow
{
	protected Cell[] cells;
	
	protected Race race;
	
	
	public PlayerHeaderRow(LivePanel panel, Dimension[] cellDimensions, int cellMargin
		, Race race, ArrayList<PlayerCellSet> playerCellSets)
	{
		super(panel, cellDimensions, cellMargin);

		this.race = race;
		
		initCells(playerCellSets);
		initCellProperties();
	}
	
	
	public Cell[] getCells()
	{
		return cells;
	}
	
	
	protected void initCells(ArrayList<PlayerCellSet> playerCellSets)
	{
		cells = new Cell[cellDimensions.length];

		int playerCount = 0;
		int startCount = cellDimensions.length - playerCellSets.size();
		for(int i = 0; i < cellDimensions.length; i++)
		{
			if(i >= startCount)
			{
				cells[i] = playerCellSets.get(playerCount).getPlayerHeaderCell();
				playerCount++;
			}
			else
			{
				cells[i] = null;
			}
		}
	}
}