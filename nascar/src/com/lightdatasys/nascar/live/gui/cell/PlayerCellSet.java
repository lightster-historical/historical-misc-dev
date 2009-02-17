package com.lightdatasys.nascar.live.gui.cell;

import com.lightdatasys.nascar.fantasy.FantasyPlayer;
import com.lightdatasys.nascar.fantasy.FantasyResult;

public class PlayerCellSet
{
	protected FantasyResult result;
	
	protected FantasyResultCell[] resultCells;
	protected FantasyPlayerCell playerHeaderCell;
	
	protected FantasyPlayerCell playerCell;
	
	
	public PlayerCellSet(FantasyResult result, FantasyResultCell[] resultCells
		, FantasyPlayerCell playerHeaderCell, FantasyPlayerCell playerCell)
	{
		this.result = result;			

		this.resultCells = new FantasyResultCell[resultCells.length];
		for(int i = 0; i < resultCells.length; i++)
			this.resultCells[i] = resultCells[i];
		
		this.playerHeaderCell = playerHeaderCell;
		this.playerCell = playerCell;
	}

	
	public FantasyResult getResult()
	{
		return result;
	}
	
	public FantasyPlayer getPlayer()
	{
		return result.getPlayer();
	}
	
	public int getResultCellCount()
	{
		return resultCells.length;
	}
	
	public FantasyResultCell getResultCell(int i)
	{
		if(0 <= i && i < resultCells.length)
			return resultCells[i];
		
		return null;
	}
	
	public FantasyPlayerCell getPlayerHeaderCell()
	{
		return playerHeaderCell;
	}
	
	public FantasyPlayerCell getPlayerCell()
	{
		return playerCell;
	}
}