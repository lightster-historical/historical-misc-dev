package com.lightdatasys.nascar.live.table.gui;

import java.awt.Dimension;
import java.awt.Graphics2D;

import com.lightdatasys.nascar.live.gui.GraphicsPanel;
import com.lightdatasys.nascar.live.gui.cell.Cell;
import com.lightdatasys.nascar.live.gui.panel.LivePanel;


public abstract class TableRow extends GraphicsPanel
{
	protected LivePanel panel;
	
	protected boolean updated;
	
	protected int width;
	protected int height;
	
	protected int cellMargin;
	protected Dimension[] cellDimensions;
	
	
	public TableRow(LivePanel panel, Dimension[] cellDimensions, int cellMargin)
	{
		super(panel.getWindow().getDevice());
		
		this.panel = panel;
		
		this.updated = true;
		
		this.width = 0;
		this.height = 0;
		
		this.cellMargin = cellMargin;
		this.cellDimensions = cellDimensions;
	}
	
	
	public abstract Cell[] getCells();
	

	@Override
	public void render(Graphics2D g)
	{
		Cell[] cells = getCells();
		
		for(int i = 0; i < cells.length; i++)
		{
			Cell cell = cells[i];
			
			if(cell != null)
			{
				cell.renderTo(g, cell.getXOffset(), 0);
				cell = null;
			}
		}
	}
	
	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public int getHeight()
	{
		return height;
	}
	
	
	public boolean isUpdated()
	{
		return true; /*
		boolean cellsUpdated = super.isUpdated();
		
		Cell[] cells = getCells();
		for(Cell cell : cells)
		{
			if(cellsUpdated)
				break;
			
			if(cell != null)
			{
				cellsUpdated |= cell.isUpdated();
			}
		}
		
		return cellsUpdated;
		//*/
	}

	
	protected void initCellProperties()
	{
		Cell[] cells = getCells();
		int[] cellPositions = new int[cellDimensions.length];
		for(int i = 0; i < cellDimensions.length; i++)
		{
			Cell cell = cells[i];
			
			width += cellDimensions[i].width + cellMargin;
			height = Math.max(cellDimensions[i].height, height);
			
			if(i == 0)
				cellPositions[i] = 0;
			else
			{
				cellPositions[i] = cellPositions[i - 1]
                   + cellDimensions[i - 1].width + cellMargin; 
			}
			
			if(cell != null)
				cell.setXOffset(cellPositions[i]);
		}
	}
}