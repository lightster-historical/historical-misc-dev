//http://www.nfl.com/liveupdate/scores/scoresPage.json

package com.lightdatasys.nascar.live.gui.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import com.lightdatasys.nascar.Driver;
import com.lightdatasys.nascar.Race;
import com.lightdatasys.nascar.Result;
import com.lightdatasys.nascar.fantasy.FantasyPlayer;
import com.lightdatasys.nascar.fantasy.FantasyResult;
import com.lightdatasys.nascar.live.gui.FullScreenWindow;
import com.lightdatasys.nascar.live.gui.Swap;
import com.lightdatasys.nascar.live.gui.cell.Cell;
import com.lightdatasys.nascar.live.gui.cell.FantasyPlayerCell;
import com.lightdatasys.nascar.live.gui.cell.FantasyResultCell;
import com.lightdatasys.nascar.live.gui.cell.PlayerCellSet;
import com.lightdatasys.nascar.live.gui.cell.ResultCell;
import com.lightdatasys.nascar.live.setting.FantasyResultModeSetting;
import com.lightdatasys.nascar.live.setting.ResultModeSetting;
import com.lightdatasys.nascar.live.setting.Setting;
import com.lightdatasys.nascar.live.setting.Settings;
import com.lightdatasys.nascar.live.table.gui.DriverRow;
import com.lightdatasys.nascar.live.table.gui.FantasyResultHeaderRow;
import com.lightdatasys.nascar.live.table.gui.PlayerHeaderRow;
import com.lightdatasys.nascar.live.table.gui.TableRow;

public class SortableTable extends LivePanel
{	
	private Color rowAltColor = new Color(0x12, 0x12, 0x12);
	
    protected float[] topHeaderWeights = {1, 1, .6f};
    protected float[] leftHeaderWeights = {.8f, 1.3f, 1.3f, 1.3f, .8f, .8f};

    protected FantasyResultCell.Mode[] topHeaderModes = 
    {
    	FantasyResultCell.Mode.LEADER_DRIVER_DIFF,
    	FantasyResultCell.Mode.SEASON_POINTS
    };
    protected ResultCell.Mode[] leftHeaderModes = 
    {
    	ResultCell.Mode.SEASON_RANK,
		ResultCell.Mode.LAST_LAP_TIME,
		ResultCell.Mode.SPEED,
		ResultCell.Mode.LOCAL_INTERVAL,
		ResultCell.Mode.POSITION
    };
    
    private Dimension cellDimension;
    private int[] topHeaderHeight;
    private int[] leftHeaderWidth;
    private int totalTopHeaderHeight;
    private int totalLeftHeaderWidth;

	private Cell raceStatusCell;
	
    private ArrayList<TableRow> topHeaderRows;
    private ArrayList<DriverRow> rows;
    
	private AbstractMap<String,DriverRow> rowsByCarNo;
	private AbstractMap<String,Integer> orderingByCarNo;
	
	private ArrayList<PlayerCellSet> playerCells;
	private AbstractMap<Integer,Integer> orderingByPlayerId;

	
	private int totalColHeaderSize;
	
	private Cell rowBackground;
	
	private int topRow;
	private int maxRows;
	
	private Cell bgCell;
	private String leaderCar;
	
	private Race.Flag flag;
	
	private int width;
	private int height;
	
	private int cellMargin;
	
	private int posMin;
	private int posMax;
	private boolean showHeader;
	
	
	public SortableTable(FullScreenWindow window, int posMin, int posMax, boolean showHeader)
	{
		super(window);
		
		this.posMin = posMin;
		this.posMax = posMax;
		this.showHeader = showHeader;
		
		initResultModes();
		initFantasyResultModes();
		
        cellMargin = 1;
        
        //resultMode = ResultCell.Mode.POSITION;

        width = getWidth();
        height = getHeight();
        
        calcDimensions();
        int w = (leftHeaderWidth.length - 1) * cellMargin, h = (topHeaderHeight.length - 1) * cellMargin;
        for(int i = 0; i < leftHeaderWidth.length; i++)
        	w += leftHeaderWidth[i];
        for(int i = 0; i < topHeaderHeight.length; i++)
        	h += topHeaderHeight[i];
		initGlobalCells(w, h);
		
		maxRows = (height - totalColHeaderSize) / cellDimension.height;
		
		topRow = 0;
        
        getWindow().setBackground(new Color(0x33, 0x33, 0x33));

        initPlayerCells();
		if(showHeader)
			initTopHeaderRows();
        initRows();
	}
	
	
	public void calcDimensions()
	{
        float topHeaderWeightTotal = 0;
        if(showHeader)
        {
	        for(int i = 0; i < topHeaderWeights.length; i++)
	        	topHeaderWeightTotal += topHeaderWeights[i];
        }
        float leftHeaderWeightTotal = 0;
        for(int i = 0; i < leftHeaderWeights.length; i++)
        	leftHeaderWeightTotal += leftHeaderWeights[i];
        
        float colWeightTotal = getPlayerCount() + leftHeaderWeightTotal;
        float rowWeightTotal = getDriverCount() + topHeaderWeightTotal;
        
        leftHeaderWidth = new int[leftHeaderWeights.length];
        for(int i = 0; i < leftHeaderWeights.length; i++)
        {
			leftHeaderWidth[i] = (int)Math.floor(leftHeaderWeights[i] * (getWidth() - getCellMargin() * (getColumnCount())) / colWeightTotal);
			totalLeftHeaderWidth += leftHeaderWidth[i] + getCellMargin();
		}
        
        topHeaderHeight = new int[topHeaderWeights.length];
        totalTopHeaderHeight = 0; 
		for(int i = 0; i < topHeaderWeights.length; i++)
		{
			topHeaderHeight[i] = (int)Math.floor(topHeaderWeights[i] * (getHeight() - getCellMargin() * (2 + getRowCount())) / rowWeightTotal);
	        if(showHeader)
	        	totalTopHeaderHeight += topHeaderHeight[i] + getCellMargin();
		}

        int cellWidth = (int)Math.floor((getWidth() - getCellMargin() * (getColumnCount() + 1)) / colWeightTotal);
		int cellHeight = (int)Math.floor((getHeight() - getCellMargin() * (getDriverCount() + 1)) / (rowWeightTotal));
		
		cellDimension = new Dimension(cellWidth, cellHeight);
	}
	

	public void initGlobalCells(int w, int h)
	{
		raceStatusCell = createRaceStatusCell(w, h, getRace());
		
		Result leaderResult = getRace().getResultByFinish(1);
		Driver leader = leaderResult.getDriver();
		leaderCar = leaderResult.getCar();
	}
	
	
	public void moveColumn(PlayerCellSet playerCellSet, int newPosition)
	{
		int playerCount = playerCells.size();
		int oldPosition = orderingByPlayerId.get(playerCellSet.getPlayer().getPlayerId());
		
		if(1 <= oldPosition && oldPosition <= playerCount &&
			1 <= newPosition && newPosition <= playerCount &&
			oldPosition != newPosition)
		{			
			orderingByPlayerId.put(playerCellSet.getPlayer().getPlayerId(), newPosition);

			int newX = getColumnPosition(newPosition);

			for(int i = 0; i < playerCellSet.getResultCellCount(); i++)
				playerCellSet.getResultCell(i).moveToX(newX, getSettings().getLongValue("swapPeriod"));
			playerCellSet.getPlayerHeaderCell().moveToX(newX, getSettings().getLongValue("swapPeriod"));
			playerCellSet.getPlayerCell().moveToX(newX, getSettings().getLongValue("swapPeriod"));
		}
	}

	public void moveRow(DriverRow row, int newPosition)
	{
		int driverCount = getRace().getResults().size();
		int oldPosition = orderingByCarNo.get(row.getResult().getCar());
		
		if(1 <= oldPosition && oldPosition <= driverCount &&
			1 <= newPosition && newPosition <= driverCount &&
			oldPosition != newPosition)
		{			
			orderingByCarNo.put(row.getResult().getCar(), newPosition);
			row.getResult().setRowNumber((short)newPosition);

			int newY = getRowPosition(newPosition);
			row.moveToY(newY, getSettings().getLongValue("swapPeriod"));
		}
	}
	
	
	public void update()
	{			
		ResultCell.Mode[] modes = getResultModes();
		for(int i = 0; i < rows.size(); i++)
		{
			rows.get(i).setResultModes(modes);
		}
		
		if(topHeaderRows != null)
		{
			FantasyResultCell.Mode[] fantasyModes = getFantasyResultModes();
			for(int i = 0; i < topHeaderRows.size(); i++)
			{
				TableRow row = topHeaderRows.get(i);
				if(row instanceof FantasyResultHeaderRow)
				{
					FantasyResultHeaderRow headerRow = (FantasyResultHeaderRow)row;
					headerRow.setFantasyResultMode(fantasyModes[i]);
				}
			}
		}
			
		if(flag != getRace().getFlag())
		{
			if(getRace().getFlag() == Race.Flag.YELLOW)
				rowAltColor = new Color(0x99, 0x99, 0x00);
			else if(getRace().getFlag() == Race.Flag.RED)
				rowAltColor = new Color(0x99, 0x00, 0x00);
			else
				rowAltColor = new Color(0x12, 0x12, 0x12);
			
			//rowBackground.triggerRender();
		}

		updateRowOrdering();
		updateColumnOrdering();

		Result leaderResult = getRace().getResultByFinish(1);
		if(!leaderCar.equals(leaderResult.getCar()))
		{
			Driver leader = leaderResult.getDriver();
			//bgCell = createCarNoCell(getWidth() - getLeftHeaderTotalWidth(), getHeight() - getTopHeaderTotalHeight(), leaderResult.getCar(), leader.getFontColor(),
			//	leader.getBackgroundColor(), leader.getBorderColor());
			leaderCar = leaderResult.getCar();
		}
	}

	
	public void render(Graphics2D g)
	{
		FullScreenWindow window = getWindow();
		Race race = getRace();
		Swap.incrementCacheIndex();
		
		if(race.getFlag() == com.lightdatasys.nascar.Race.Flag.YELLOW)
			window.setBackground(Color.YELLOW);
		else if(race.getFlag() == com.lightdatasys.nascar.Race.Flag.RED)
			window.setBackground(new Color(0xCC, 0x00, 0x00));
		else if(race.getFlag() == com.lightdatasys.nascar.Race.Flag.WHITE)
			window.setBackground(Color.WHITE);
		else
			window.setBackground(new Color(0x55, 0x55, 0x55));
		
		g.clearRect(0, 0, getWidth(), getHeight());

		renderRows(g);
		
		if(showHeader)
		{
			renderTopHeaderRows(g, 0, 0);
			raceStatusCell.render(g);
		}
		
		//renderFPS(g);
	}

	
	public int getTopHeaderTotalHeight()
	{
		return totalTopHeaderHeight;
	}
	
	public int getLeftHeaderTotalWidth()
	{
		return totalLeftHeaderWidth;
	}
	
	public int getLeftHeaderCount()
	{
		return leftHeaderWeights.length;
	}
	
	public int getTopHeaderCount()
	{
		return topHeaderWeights.length;
	}
	
	public int getPlayerCount()
	{
		return getRace().getFantasyResults().size();
	}
	
	public int getDriverCount()
	{
		return posMax - posMin + 1;
	}
	
	public int getColumnCount()
	{
		return getPlayerCount() + getLeftHeaderCount();
	}
	
	public int getRowCount()
	{
		return getDriverCount() + 3;
	}
	
	public Dimension[] getTopHeaderCellDimensions(int rowNum)
	{
		int colCount = getColumnCount();
		int leftHeaderCount = getLeftHeaderCount();
		
		Dimension[] dimensions = new Dimension[colCount];
		for(int i = 0; i < colCount; i++)
		{
			if(i < leftHeaderCount)
				dimensions[i] = new Dimension(leftHeaderWidth[i], topHeaderHeight[rowNum]);
			else
				dimensions[i] = new Dimension(cellDimension.width, topHeaderHeight[rowNum]);
		}
			
		return dimensions;
	}
	
	public Dimension[] getRowCellDimensions()
	{
		int colCount = getColumnCount();
		int leftHeaderCount = getLeftHeaderCount();
		
		Dimension[] dimensions = new Dimension[colCount];
		for(int i = 0; i < colCount; i++)
		{
			if(i < leftHeaderCount)
				dimensions[i] = new Dimension(leftHeaderWidth[i], cellDimension.height);
			else
				dimensions[i] = new Dimension(cellDimension.width, cellDimension.height);
		}
			
		return dimensions;
	}
	
	
	public ResultCell.Mode[] getDefaultResultModes()
	{
		return leftHeaderModes;
	}
	
	public ResultCell.Mode[] getResultModes()
	{
		int count = getDefaultResultModes().length;
		ResultCell.Mode[] modes = new ResultCell.Mode[count];
		
		for(int i = 1; i <= count; i++)
		{
			Object value;
			if(getRace().getFlag() == Race.Flag.YELLOW && i == 1)
			{
				value = getSettings().getValue("resultModeUnderCaution" + i);
			}
			else
			{
				value = getSettings().getValue("resultMode" + i);
			}
			
			if(value instanceof ResultCell.Mode)
				modes[i - 1] = (ResultCell.Mode)value;
			else
				modes[i - 1] = getDefaultResultModes()[i - 1];			
		}
		
		return modes;
	}
	
	
	public FantasyResultCell.Mode[] getDefaultFantasyResultModes()
	{
		return topHeaderModes;
	}
	
	public FantasyResultCell.Mode[] getFantasyResultModes()
	{
		int count = getDefaultFantasyResultModes().length;
		FantasyResultCell.Mode[] modes = new FantasyResultCell.Mode[count];
		
		for(int i = 1; i <= count; i++)
		{
			Object value;
			if(getRace().getFlag() == Race.Flag.YELLOW && i == 2)
			{
				value = getSettings().getValue("fantasyResultModeUnderCaution" + i);
			}
			else
			{
				value = getSettings().getValue("fantasyResultMode" + i);
			}
			
			if(value instanceof FantasyResultCell.Mode)
				modes[i - 1] = (FantasyResultCell.Mode)value;
			else
				modes[i - 1] = getDefaultFantasyResultModes()[i - 1];			
		}
		
		return modes;
	}
	
	
	public int getCellMargin()
	{
		return cellMargin;
	}
	
	public int getMinPosition()
	{
		return posMin;
	}
	
	public int getMaxPosition()
	{
		return posMax;
	}
	
	
	public void updateRowOrdering()
	{
		Object objComp = getSettings().getValue("rowOrder");
		if(objComp instanceof DriverRow.DefaultComparator)
		{
			DriverRow.DefaultComparator comparator 
				= (DriverRow.DefaultComparator)objComp;
			Collections.sort(rows, comparator);
		}
		else
			Collections.sort(rows, new DriverRow.ResultNumericComparator(new DriverRow.FinishRetriever(), getSettings()));
		
		for(int i = 0; i < rows.size(); i++)
		{
			DriverRow row = rows.get(i);
			moveRow(row, i + 1);
			
			Result result = row.getResult();			
			
			if(!result.isCurrent() || !getSettings().getBooleanValue("highlightActives").booleanValue())
				row.setBackground(getWindow().getBackground());
			else
				row.setBackground(getWindow().getBackground().darker().darker());
		}
	}
	
	public void updateColumnOrdering()
	{
		Collections.sort(playerCells, 
			new Comparator<PlayerCellSet>()
			{
				public int compare(PlayerCellSet o1, PlayerCellSet o2)
				{
					FantasyResult r1 = o1.getResult();
					FantasyResult r2 = o2.getResult();
					
					if(r1.getFinish() < r2.getFinish())
						return -1;
					else if(r1.getFinish() > r2.getFinish())
						return 1;
					
					return 0;
				}
			}
		);
		
		for(int i = 0; i < playerCells.size(); i++)
		{
			moveColumn(playerCells.get(i), i + 1);
		}
	}

	
	public void initResultModes()
	{
		int count = getDefaultResultModes().length;
		
		for(int i = 1; i <= count; i++)
		{
			Setting<?> setting = getSettings().get("resultMode" + i);
			
			if(setting instanceof ResultModeSetting)
			{
				ResultModeSetting modeSetting = (ResultModeSetting)setting;
				modeSetting.setValue(getDefaultResultModes()[i - 1]);
			}
		}
		
		Setting<?> setting = getSettings().get("resultModeUnderCaution1");
		if(setting instanceof ResultModeSetting)
		{
			ResultModeSetting modeSetting = (ResultModeSetting)setting;
			modeSetting.setValue(ResultCell.Mode.POSITION_CHANGE);
		}
	}
	
	public void initFantasyResultModes()
	{
		int count = getDefaultFantasyResultModes().length;
		
		for(int i = 1; i <= count; i++)
		{
			Setting<?> setting = getSettings().get("fantasyResultMode" + i);
			
			if(setting instanceof FantasyResultModeSetting)
			{
				FantasyResultModeSetting modeSetting = (FantasyResultModeSetting)setting;
				modeSetting.setValue(getDefaultFantasyResultModes()[i - 1]);
			}
		}
		
		Setting<?> setting = getSettings().get("fantasyResultModeUnderCaution2");
		if(setting instanceof FantasyResultModeSetting)
		{
			FantasyResultModeSetting modeSetting = (FantasyResultModeSetting)setting;
			modeSetting.setValue(FantasyResultCell.Mode.POSITION_CHANGE);
		}
	}
	
	
	public void initPlayerCells()
	{	
		playerCells = new ArrayList<PlayerCellSet>();
		orderingByPlayerId = new HashMap<Integer,Integer>();
		
		int count = 1;
		AbstractMap<Integer,FantasyResult> fantasyResults = getRace().getFantasyResults();
		Iterator<Integer> it = fantasyResults.keySet().iterator();
		while(it.hasNext())
		{
			FantasyResult result = fantasyResults.get(it.next());
			if(result != null)
			{
				FantasyPlayer player = result.getPlayer();
				
				FantasyResultCell[] resultCells = new FantasyResultCell[topHeaderModes.length];
				for(int i = 0; i < topHeaderModes.length; i++)
				{
					resultCells[i] = createFantasyResultCell(cellDimension.width, topHeaderHeight[i]
                        , result, Color.WHITE, player.getBackgroundColor(), Color.WHITE);
					resultCells[i].setMode(topHeaderModes[i]);
				}
				
				FantasyPlayerCell playerHeaderCell = createFantasyPlayerCell(
					cellDimension.width, topHeaderHeight[topHeaderHeight.length - 1]
                    , player.toString(), Color.WHITE, player.getBackgroundColor(), Color.WHITE, false);
				
				FantasyPlayerCell playerCell = createFantasyPlayerCell(cellDimension.width, cellDimension.height
					, player.toString(), Color.WHITE, player.getBackgroundColor(), Color.WHITE);
				
				playerCells.add(new PlayerCellSet(result, resultCells, playerHeaderCell, playerCell));	
				orderingByPlayerId.put(player.getPlayerId(), count);
				
				count++;
			}
		}
	}
	
	public void initTopHeaderRows()
	{
		topHeaderRows = new ArrayList<TableRow>();
		
		Dimension[] cellDimensions;
		
		int i = 0;
		for(i = 0; i < topHeaderModes.length; i++)
		{
			cellDimensions = getTopHeaderCellDimensions(i);
			topHeaderRows.add(new FantasyResultHeaderRow(this, cellDimensions, cellMargin, getRace()
				, topHeaderModes[i], i, playerCells));
		}
		cellDimensions = getTopHeaderCellDimensions(i);
		topHeaderRows.add(new PlayerHeaderRow(this, cellDimensions, cellMargin, getRace(), playerCells));
	}
	
	public void initRows()
	{
		rows = new ArrayList<DriverRow>();
		rowsByCarNo = new HashMap<String,DriverRow>();
		orderingByCarNo = new HashMap<String,Integer>();
		
		Dimension[] cellDimensions = getRowCellDimensions();
		ResultCell.Mode[] resultModes = getResultModes();
		int cellMargin = getCellMargin();
		
		int position = 1;
		AbstractMap<Integer, Result> results = getRace().getResults();
		Iterator<Result> it = results.values().iterator();
		while(it.hasNext())
		{
			Result result = it.next();
			
			DriverRow row = new DriverRow(this, cellDimensions, cellMargin, result, resultModes, playerCells);
			row.setYOffset(getRowPosition(position));
			
			rowsByCarNo.put(result.getCar(), row);
			rows.add(row);
			
			orderingByCarNo.put(result.getCar(), position);
			position++;
		}
	}
	
	
	public void renderTopHeaderRows(Graphics2D g, int xOffset, int yOffset)
	{
		int y = yOffset;
		for(TableRow row : topHeaderRows)
		{
			row.renderTo(g, xOffset, y);
			
			y += row.getHeight() + getCellMargin();
		}
	}
	
	public void renderRows(Graphics2D g)
	{
		int rowCount = 1;
		int windowH = getHeight();
		for(int i = rows.size() - 1; i >= 0; i--)
		{
			DriverRow row = rows.get(i);
			int rowY = row.getYOffset();
			int rowH = row.getHeight();
			if(rowY + rowH > 0 && rowY < windowH)
			{
				row.renderTo(g, 0, rowY);
			}
			
			rowCount++;
		}
		//System.out.println();
	}
	
	public int getRowPosition(int position)
	{
		int y = getTopHeaderTotalHeight() + getCellMargin();
		y += (position - getMinPosition()) * (cellDimension.height + getCellMargin());
			
		return y;
	}
	
	public int getColumnPosition(int position)
	{
		int x = getLeftHeaderTotalWidth() + getCellMargin();
		x += (position - 1) * (cellDimension.width + getCellMargin());
			
		return x;
	}	
	
	
	protected Settings getSettings()
	{
		return getLiveUpdater().getSettings();
	}
}
