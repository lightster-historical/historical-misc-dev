//http://www.nfl.com/liveupdate/scores/scoresPage.json

package com.lightdatasys.nascar.fantasy.gui.panel;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.lightdatasys.nascar.Driver;
import com.lightdatasys.nascar.Race;
import com.lightdatasys.nascar.Result;
import com.lightdatasys.nascar.event.PositionChangeEvent;
import com.lightdatasys.nascar.event.PositionChangeListener;
import com.lightdatasys.nascar.fantasy.FantasyPlayer;
import com.lightdatasys.nascar.fantasy.FantasyResult;
import com.lightdatasys.nascar.fantasy.gui.FullScreenWindow;
import com.lightdatasys.nascar.fantasy.gui.Settings;
import com.lightdatasys.nascar.fantasy.gui.Swap;
import com.lightdatasys.nascar.fantasy.gui.cell.CarNoCell;
import com.lightdatasys.nascar.fantasy.gui.cell.Cell;
import com.lightdatasys.nascar.fantasy.gui.cell.FantasyPlayerCell;
import com.lightdatasys.nascar.fantasy.gui.cell.FantasyResultCell;
import com.lightdatasys.nascar.fantasy.gui.cell.ResultCell;
import com.lightdatasys.nascar.fantasy.gui.cell.ResultCell.Mode;

public class SortableScroller extends LivePanel
{
	public final static int COLUMNS = 11;
	public final static int ROWS = 43;
	
	public final static int DRIVER_RESULT_CELLS = 4;
	
	public final static int COL_HEADERS = 3;
	public final static int ROW_HEADERS = 1 + DRIVER_RESULT_CELLS;
	
	private Color rowAltColor = new Color(0x12, 0x12, 0x12);
	
    float[] colHeaderWeights = {.75f, .75f, .5f};
    float[] rowHeaderWeights = {.8f, .8f, .8f, 1.5f, 1.5f};
	
	private Cell raceStatusCell;
	private AbstractMap<String,Cell> driverCells;
	private AbstractMap<String,Cell>[] driverResultCells;
	private AbstractMap<Integer,Cell> playerHeaderCells;
	private AbstractMap<Integer,Cell> playerCells;
	private AbstractMap<Integer,Cell> playerResult1Cells;
	private AbstractMap<Integer,Cell> playerResult2Cells;
	
	private Cell[][] cells;
	private int[] colSize;
	private int[] rowSize;
	private int[] colPosition;
	private int[] rowPosition;
	
	private Cell[][] rowHeaders;
	private Cell[][] colHeaders;
	private int totalColHeaderSize;
	private int totalRowHeaderSize;
	private int[] colHeaderSize;
	private int[] rowHeaderSize;
	private int[] colHeaderPosition;
	private int[] rowHeaderPosition;
	
	private Cell rowBackground;

	private Swap[] colSwaps;
	private Swap[] rowSwaps;	
	private int[] colSwapMap;
	private int[] rowSwapMap;
	
	private int topRow;
	private int maxRows;
	
	private Cell bgCell;
	private String leaderCar;
	
	private Race.Flag flag;
	
	private int width;
	private int height;
	
	private int cellMargin;
	private int cellWidth;
	private int cellHeight;
	
	private int posMin;
	private int posMax;
	private boolean showHeader;
	
	private int renderCounter;
	
	private long lastScrollTime;
	
	private float xScrollOffset;

	private ArrayList<PositionChangeEvent> positionChangeEvents;
	private ArrayList<PositionChangeEvent> fantasyPositionChangeEvents;
	

	private AbstractMap<Integer,FantasyResult> fantasyResults;
	private AbstractMap<Integer,Result> results;
	private Settings settings;
	
	
	public SortableScroller(FullScreenWindow window, int posMin, int posMax, boolean showHeader)
	{
		super(window);
		
		this.posMin = posMin;
		this.posMax = posMax;
		this.showHeader = showHeader;
		
		renderCounter = 0;

		fantasyResults = getRace().getFantasyResults();
		results = getRace().getResults();
		settings = getLiveUpdater().getSettings();
		
        positionChangeEvents = new ArrayList<PositionChangeEvent>();
        fantasyPositionChangeEvents = new ArrayList<PositionChangeEvent>();

		getRace().addPositionChangeListener
		(
			new PositionChangeListener()
			{
				public void positionChanged(PositionChangeEvent event)
				{
					positionChangeEvents.add(event);
				}
			}
		);
		getRace().addFantasyPositionChangeListener
		(
			new PositionChangeListener()
			{
				public void positionChanged(PositionChangeEvent event)
				{
					fantasyPositionChangeEvents.add(event);
				}
			}
		);
		
        cellMargin = 2;
        
        xScrollOffset = 0;
        
        //resultMode = ResultCell.Mode.POSITION;

        width = getWidth();
        height = getHeight();
        
        calcDimensions();
        int w = (ROW_HEADERS-1)*cellMargin, h = (COL_HEADERS-1)*cellMargin;
        for(int i = 0; i < ROW_HEADERS; i++)
        	w += rowHeaderSize[i];
        for(int i = 0; i < COL_HEADERS; i++)
        	h += colHeaderSize[i];
		initGlobalCells(w, h);
		initDriverCells(colSize[0], rowSize[0]);
		initPlayerCells(colSize[0], rowSize[0], colSize[0]);
		
		maxRows = (height - totalColHeaderSize) / rowSize[0];
		

		cells = new Cell[COLUMNS][ROWS];
		colHeaders = new Cell[COLUMNS][COL_HEADERS];
		rowHeaders = new Cell[ROW_HEADERS][ROWS];
		
		for(int x = 0; x < COLUMNS; x++)
		{
			for(int y = 0; y < ROWS; y++)
			{
				cells[x][y] = null;
				
				Result result = results.get(y + 1);
				if(result != null)
				{
					FantasyResult fantasyResult = fantasyResults.get(x + 1);
					
					if(fantasyResult.getPicks().contains(result.getCar()))
					{
						FantasyPlayer player = fantasyResult.getPlayer();

						cells[x][y] = playerCells.get(player.getPlayerId());
					}
				}
			}
		}
		
		for(int x = 0; x < COLUMNS; x++)
		{
			FantasyResult result = fantasyResults.get(x + 1);
			FantasyPlayer player = result.getPlayer();

			colHeaders[x][0] = playerResult1Cells.get(player.getPlayerId());
			colHeaders[x][1] = playerResult2Cells.get(player.getPlayerId());
			colHeaders[x][2] = playerHeaderCells.get(player.getPlayerId());
		}

		for(int y = 0; y < ROWS; y++)
		{
			Result result = results.get(y + 1);
			
			rowHeaders[0][y] = driverResultCells[0].get(result.getCar());
			rowHeaders[1][y] = driverCells.get(result.getCar());
			for(int i = 1; i < DRIVER_RESULT_CELLS; i++)
			{
				rowHeaders[i + 1][y] = driverResultCells[i].get(result.getCar());
			}
		}
		
		topRow = 0;
		lastScrollTime = System.currentTimeMillis();
		
		colSwaps = new Swap[COLUMNS];
		rowSwaps = new Swap[ROWS];
		
		colSwapMap = new int[COLUMNS];
		rowSwapMap = new int[ROWS];
		initSwapMaps();
        
        getWindow().setBackground(Color.BLACK);

        BufferedImage img = new BufferedImage(16,16,BufferedImage.TYPE_4BYTE_ABGR);
        Cursor blankCursor = getWindow().getToolkit().createCustomCursor(img,new Point(0,0),"blankCursor");
        getWindow().setCursor(blankCursor);
	}
	
	
	public void calcDimensions()
	{
        float colHeaderWeightTotal = 0;
        if(showHeader)
        {
	        for(int i = 0; i < COL_HEADERS; i++)
	        	colHeaderWeightTotal += colHeaderWeights[i];
        }
        float rowHeaderWeightTotal = 0;
        for(int i = 0; i < ROW_HEADERS; i++)
        	rowHeaderWeightTotal += rowHeaderWeights[i];
        
        float colWeightTotal = COLUMNS + rowHeaderWeightTotal;
        float rowWeightTotal = (posMax - posMin + 1) + colHeaderWeightTotal;
        
        totalColHeaderSize = 0;
        totalRowHeaderSize = 0;

        rowHeaderSize = new int[ROW_HEADERS];
        rowHeaderPosition = new int[ROW_HEADERS];
		for(int i = 0; i < ROW_HEADERS; i++)
		{
			rowHeaderSize[i] = Math.round(rowHeaderWeights[i] * (getWidth() - cellMargin * (COLUMNS + ROW_HEADERS)) / colWeightTotal);
			totalRowHeaderSize += rowHeaderSize[i];
			
			if(i > 0)
				rowHeaderPosition[i] = rowHeaderPosition[i - 1] + rowHeaderSize[i - 1] + cellMargin;
			else
				rowHeaderPosition[i] = 0;
		}

		colSize = new int[COLUMNS];
		colPosition = new int[COLUMNS];
		for(int i = 0; i < COLUMNS; i++)
		{
			colSize[i] = Math.round((getWidth() - cellMargin * (COLUMNS + ROW_HEADERS)) / colWeightTotal);
			
			if(i > 0)
				colPosition[i] = colPosition[i - 1] + colSize[i - 1] + cellMargin;
			else
				colPosition[i] = rowHeaderPosition[ROW_HEADERS - 1]
                    + rowHeaderSize[ROW_HEADERS - 1] + cellMargin;
		}

        colHeaderSize = new int[COL_HEADERS];
        colHeaderPosition = new int[COL_HEADERS];
		for(int i = 0; i < COL_HEADERS; i++)
		{
			colHeaderSize[i] = Math.round(colHeaderWeights[i] * colSize[0]);
			totalColHeaderSize += colHeaderSize[i];
			
			if(i > 0)
				colHeaderPosition[i] = colHeaderPosition[i - 1] + colHeaderSize[i - 1] + cellMargin;
			else
				colHeaderPosition[i] = 0;
		}

		rowSize = new int[ROWS];
		rowPosition = new int[ROWS];
		for(int i = 0; i < ROWS; i++)
		{
			//rowSize[i] = Math.round(5.0f * colSize[0] / 8);
			rowSize[i] = Math.round((getHeight() - cellMargin * (posMax - posMin + 1)) / (rowWeightTotal + colHeaderWeightTotal));
			
			if(i > 0)
				rowPosition[i] = rowPosition[i - 1] + rowSize[i - 1] + cellMargin;
			else
				rowPosition[i] = colHeaderPosition[COL_HEADERS - 1]
                    + colHeaderSize[COL_HEADERS - 1] + cellMargin;
		}
	}
	

	public void initGlobalCells(int w, int h)
	{
		raceStatusCell = createRaceStatusCell(w, h, getRace());
		
		Result leaderResult = getRace().getResultByFinish(1);
		Driver leader = leaderResult.getDriver();
		//bgCell = createCarNoCell(getWidth()-w, getHeight()-h, leaderResult.getCar(), leader.getFontColor(),
		//		leader.getBackgroundColor(), leader.getBorderColor());
		leaderCar = leaderResult.getCar();
	}

	public void initDriverCells(int w, int h)
	{
		//driverHeaderCells = new HashMap<String,Cell>();
		driverCells = new HashMap<String,Cell>();
		//driverResult1Cells = new HashMap<String,Cell>();
		
		driverResultCells = new AbstractMap[DRIVER_RESULT_CELLS];

		AbstractMap<Integer,com.lightdatasys.nascar.Result> results = getRace().getResults();
		
		Iterator<Integer> it = results.keySet().iterator();
		while(it.hasNext())
		{
			Result result = results.get(it.next());
			if(result != null)
			{
				Driver driver = result.getDriver();
				
				//ResultCell driverHeaderCell = createResultCell((int)(w * rowHeaderWeights[0]), h, result, 
				//		Color.WHITE, Color.BLACK, Color.WHITE);
				//driverHeaderCell.setMode(Mode.POSITION);
				//driverHeaderCells.put(result.getCar(), driverHeaderCell);
				
				CarNoCell driverCell = createCarNoCell((int)(w * rowHeaderWeights[1]), h, result.getCar(), driver.getFontColor(),
						driver.getBackgroundColor(), driver.getBorderColor());
				driverCells.put(result.getCar(), driverCell);
				
				//ResultCell driverResult1Cell = createResultCell((int)(w * rowHeaderWeights[2]), h, result, 
				//		Color.WHITE, Color.BLACK, Color.WHITE);
				//driverResult1Cells.put(result.getCar(), driverResult1Cell);
				
				for(int i = 0; i < DRIVER_RESULT_CELLS; i++)
				{
					if(driverResultCells[i] == null)
						driverResultCells[i] = new HashMap<String,Cell>();
					
					ResultCell resultCell = createResultCell((int)(w * rowHeaderWeights[(i == 0 ? 0 : i + 1)]), h, result, 
							Color.WHITE, Color.BLACK, Color.WHITE);
					driverResultCells[i].put(result.getCar(), resultCell);
				}
			}
		}
		
		rowBackground = new Cell(getWindow().getDevice(), getWidth(), h)
		{
			public void render(Graphics2D g)
			{
				g.setColor(rowAltColor);
				g.fillRect(0, 0, getWidth(), getHeight());
				
				updated = false;
			}
		};
	}
	
	public void initPlayerCells(int w, int h, int hHeader)
	{
		playerHeaderCells = new HashMap<Integer,Cell>();
		playerCells = new HashMap<Integer,Cell>();
		playerResult1Cells = new HashMap<Integer,Cell>();
		playerResult2Cells = new HashMap<Integer,Cell>();

		AbstractMap<Integer,FantasyResult> fantasyResults = getRace().getFantasyResults();

		Iterator<Integer> it = fantasyResults.keySet().iterator();
		while(it.hasNext())
		{
			FantasyResult result = fantasyResults.get(it.next());
			if(result != null)
			{
				FantasyPlayer player = result.getPlayer();
				
				FantasyPlayerCell playerCell = createFantasyPlayerCell(w, h, player.toString(), Color.WHITE,
						player.getBackgroundColor()/*drivers[i].getBackgroundColor()*/, Color.WHITE);
				playerCells.put(player.getPlayerId(), playerCell);
				
				FantasyResultCell playerResult1Cell = createFantasyResultCell(w, (int)(hHeader * colHeaderWeights[0]), result, Color.WHITE,
						player.getBackgroundColor(), Color.WHITE);
				playerResult1Cell.setMode(FantasyResultCell.Mode.SEASON_POINTS);
				playerResult1Cells.put(player.getPlayerId(), playerResult1Cell);
				
				FantasyResultCell playerResult2Cell = createFantasyResultCell(w, (int)(hHeader * colHeaderWeights[1]), result, Color.WHITE,
						player.getBackgroundColor(), Color.WHITE);
				playerResult2Cell.setMode(FantasyResultCell.Mode.DRIVER_RACE_POINTS);
				playerResult2Cells.put(player.getPlayerId(), playerResult2Cell);
				
				FantasyPlayerCell playerHeaderCell = createFantasyPlayerCell(w, (int)(hHeader * colHeaderWeights[2]), player.toString(), Color.WHITE,
						player.getBackgroundColor()/*drivers[i].getBackgroundColor()*/, Color.WHITE, false);
				playerHeaderCells.put(player.getPlayerId(), playerHeaderCell);
			}
		}
	}
	
	
	protected void initSwapMaps()
	{
		for(int x = 0; x < colSwapMap.length; x++)
		{
			colSwapMap[x] = x;
		}
		
		for(int y = 0; y < rowSwapMap.length; y++)
		{
			rowSwapMap[y] = y;
		}
	}
	
	
	public void moveColumn(int col1, int col2)
	{
		if(0 <= col1 && col1 < cells.length &&
			0 <= col2 && col2 < cells.length &&
			col1 != col2)
		{
			int pos1 = colPosition[col1];//cellMargin + col1 * (cellWidth + cellMargin);
			//if(colSwaps[col1] != null)
			//	pos1 = colSwaps[col1].getPosition();
			
			int pos2 = colPosition[col2];//cellMargin + col2 * (cellWidth + cellMargin);

			colSwaps[col2] = new Swap(pos1, pos2, settings.getSwapPeriod());
			colSwapMap[col2] = col1;
		}
	}

	public void moveRow(int row1, int row2)
	{
		if(0 <= row1 && row1 < cells[0].length &&
			0 <= row2 && row2 < cells[0].length &&
			row1 != row2)
		{
			int pos1 = rowPosition[row1];//cellMargin + row1 * (cellHeight + cellMargin);
			//if(colSwaps[row1] != null)
			//	pos1 = colSwaps[row1].getPosition();
			
			int pos2 = rowPosition[row2];//cellMargin + row2 * (cellHeight + cellMargin);
			
			rowSwaps[row2] = new Swap(pos1, pos2, settings.getSwapPeriod());
			rowSwapMap[row2] = row1;
		}
	}
	
	
	public void update()
	{			
		if(flag != getRace().getFlag())
		{
			if(getRace().getFlag() == Race.Flag.YELLOW)
				rowAltColor = new Color(0x99, 0x99, 0x00);
			else if(getRace().getFlag() == Race.Flag.RED)
				rowAltColor = new Color(0x99, 0x00, 0x00);
			else
				rowAltColor = new Color(0x12, 0x12, 0x12);
			
			rowBackground.triggerRender();
		}
		
		for(PositionChangeEvent event : positionChangeEvents)
		{
			if(event.getOldPosition() - 1 < cells[0].length 
				&& event.getNewPosition() - 1 < cells[0].length)
				moveRow(event.getOldPosition() - 1, event.getNewPosition() - 1);
		}
		positionChangeEvents.clear();
		
		for(PositionChangeEvent event : fantasyPositionChangeEvents)
		{
			if(event.getOldPosition() - 1 < cells.length 
				&& event.getNewPosition() - 1 < cells.length)
				moveColumn(event.getOldPosition() - 1, event.getNewPosition() - 1);
		}
		fantasyPositionChangeEvents.clear();

		Result leaderResult = getRace().getResultByFinish(1);
		if(leaderCar != leaderResult.getCar())
		{
			Driver leader = leaderResult.getDriver();
			//bgCell = createCarNoCell(getWidth()-colSize[0]-colSize[1]-cellMargin, getHeight()-rowSize[0]-rowSize[1]-cellMargin, leaderResult.getCar(), leader.getFontColor(),
			//		leader.getBackgroundColor(), leader.getBorderColor());
			leaderCar = leaderResult.getCar();
		}

		Cell[][] tempCells = new Cell[COLUMNS][ROWS];
		for(int x = 0; x < cells.length; x++)
		{
			for(int y = 0; y < cells[x].length; y++)
			{
				if(rowSwapMap[y] != y && cells[x][y] != null)
					cells[x][y].triggerRender();
				
				tempCells[x][y] = cells[x][y];
			}
		}

		for(int x = 0; x < cells.length; x++)
		{
			for(int y = 0; y < cells[x].length; y++)
			{				
				cells[x][y] = tempCells[colSwapMap[x]][rowSwapMap[y]];
			}
		}
		
		Cell[][] tempRowHeaders = new Cell[ROW_HEADERS][ROWS];
		for(int x = 0; x < rowHeaders.length; x++)
		{
			for(int y = 0; y < rowHeaders[x].length; y++)
			{
				if(rowSwapMap[y] != y && rowHeaders[x][y] != null)
					rowHeaders[x][y].triggerRender();
				
				tempRowHeaders[x][y] = rowHeaders[x][y];
			}
		}

		for(int x = 0; x < rowHeaders.length; x++)
		{
			for(int y = 0; y < rowHeaders[x].length; y++)
			{
				rowHeaders[x][y] = tempRowHeaders[x][rowSwapMap[y]];
			}
		}		
		
		Cell[][] tempColHeaders = new Cell[COLUMNS][COL_HEADERS];
		for(int x = 0; x < colHeaders.length; x++)
		{
			for(int y = 0; y < colHeaders[x].length; y++)
			{
				tempColHeaders[x][y] = colHeaders[x][y];
			}
		}

		for(int x = 0; x < colHeaders.length; x++)
		{
			for(int y = 0; y < colHeaders[x].length; y++)
			{
				colHeaders[x][y] = tempColHeaders[colSwapMap[x]][y];
			}
		}		
		
		initSwapMaps();
	}

	
	public void render(Graphics2D g)
	{
		FullScreenWindow window = getWindow();
		Race race = getLiveUpdater().getRace();
		
		if(race.getFlag() == com.lightdatasys.nascar.Race.Flag.YELLOW)
			window.setBackground(Color.YELLOW);
		else if(race.getFlag() == com.lightdatasys.nascar.Race.Flag.RED)
			window.setBackground(new Color(0xCC, 0x00, 0x00));
		else if(race.getFlag() == com.lightdatasys.nascar.Race.Flag.WHITE)
			window.setBackground(Color.WHITE);
		else
			window.setBackground(Color.BLACK);
		
		g.clearRect(0, 0, getWidth(), getHeight());
		
		/*if(race.getFlag() == com.lightdatasys.nascar.Race.Flag.CHECKERED)
		{
			AffineTransform oldTransform = g.getTransform();
			AffineTransform transform = new AffineTransform();
			transform.translate(colPosition[0], rowPosition[0]);
			g.setTransform(transform);
			
			bgCell.render(g);
			
			g.setTransform(oldTransform);
		}*/

		float speed = settings.getScrollSpeed() * .4f;
		float time = (float)rowSize[0] / speed;

		boolean scrollType = false;
		
		int extraRows = 0;
		
		if(speed > 10)
		{
			xScrollOffset = 0;
		}
		else if(scrollType)
		{
			long delta = System.currentTimeMillis() - lastScrollTime;
			if(delta > time * (maxRows + 2))
			{
				lastScrollTime = System.currentTimeMillis();
				topRow += maxRows;
			}
			delta = System.currentTimeMillis() - lastScrollTime;
			
			if(topRow >= 42)
				topRow = 0; 
			
			xScrollOffset = topRow * (rowSize[0] + cellMargin);
			if(delta > time * maxRows)
			{
				float scrollSpeed = ((float)maxRows * (rowSize[0] + cellMargin)) / (time * 2);
				xScrollOffset += 1.0f * scrollSpeed * (delta - time * maxRows);
			}
			
			extraRows = maxRows + 1 - (43 % maxRows);
		}
		else
		{
			xScrollOffset += 1.0f * speed * ((getLiveUpdater().getLastRenderDelta()));
			extraRows = 2;
		}
		
		int yOffset = rowPosition[0] - rowPosition[posMin - 1];
		if(!showHeader)
		{
			yOffset -= rowPosition[0];
		}
		
		if(xScrollOffset > rowPosition[ROWS-1] + extraRows*rowSize[ROWS-1] + cellMargin - rowPosition[0] + yOffset)
		{
			xScrollOffset -= rowPosition[ROWS-1] + extraRows*rowSize[ROWS-1] + cellMargin - rowPosition[0] + yOffset;
		}
		
		for(int y = 0; y < ROWS; y++)
		{
			int yPos = rowPosition[y];//cellMargin + y * (cellHeight + cellMargin);
			if(rowSwaps != null && rowSwaps[y] != null)
				yPos = rowSwaps[y].getPosition();
			
			//yPos += -xScrollOffset;
			
			BufferedImage rBgImg = rowBackground.getImage();
			if(rowBackground.isUpdated())
			{
				rowBackground.render((Graphics2D)rBgImg.getGraphics());
			}
			
			if(rowSwapMap[y] % 2 == 1)
			{
				g.drawImage((Image)rBgImg, 0, (int)(yPos - xScrollOffset) + yOffset, null);
				g.drawImage((Image)rBgImg, 0, (int)(yPos - xScrollOffset + rowPosition[ROWS-1] + extraRows*rowSize[ROWS-1] + cellMargin - rowPosition[0]) + yOffset, null);
			}
		}
		
		renderCells(g, cells, colPosition, rowPosition, colSwaps, rowSwaps, 0, COLUMNS, 0, ROWS, 0, -xScrollOffset + yOffset);
		//renderCells(g, cells, colPosition, rowPosition, colSwaps, rowSwaps, 0, COLUMNS, 0, ROWS, 0, -xScrollOffset + rowPosition[ROWS-1] + extraRows*(rowSize[ROWS-1] + cellMargin) - rowPosition[0]);
		
		renderCells(g, rowHeaders, rowHeaderPosition, rowPosition, null, rowSwaps, 0, ROW_HEADERS, 0, ROWS, 0, -xScrollOffset + yOffset);
		//renderCells(g, rowHeaders, rowHeaderPosition, rowPosition, null, rowSwaps, 0, ROW_HEADERS, 0, ROWS, 0, -xScrollOffset + rowPosition[ROWS-1] + extraRows*(rowSize[ROWS-1] + cellMargin) - rowPosition[0]);

		if(showHeader)
		{
			g.clearRect(colPosition[0], 0, colPosition[COLUMNS-1]+colSize[COLUMNS-1], colHeaderPosition[COL_HEADERS-1]);
			renderCells(g, colHeaders, colPosition, colHeaderPosition, colSwaps, null, 0, COLUMNS, 0, COL_HEADERS, 0, 0);
		}
		//renderCells(g, colHeaders, colPosition, colHeaderPosition, colSwaps, null, 0, COLUMNS, 0, COL_HEADERS, 0, 0);
		
		for(int x = 0; x < COLUMNS; x++)
		{
			if(colSwaps[x] != null && colSwaps[x].isDone())
			{
				colSwaps[x] = null;
			}
		}
		
		for(int y = 0; y < ROWS; y++)
		{
			if(rowSwaps[y] != null && rowSwaps[y].isDone())
			{
				rowSwaps[y] = null;
			}
		}
		
		//g.clearRect(0, 0, width, rowPosition[2]);
		//renderCells(g, 0, COLUMNS, 0, 2, 0, 0);
		if(showHeader)
		{
			raceStatusCell.render(g);
		}
	}
	
	public void renderCells(Graphics2D g, Cell[][] cells, int[] colPosition, int[] rowPosition, Swap[] colSwaps, Swap[] rowSwaps,  int x0, int x1, int y0, int y1, float xOffset, float yOffset)
	{
		for(int y = y0; y < y1; y++)
		{
			int yPos = rowPosition[y];
			if(rowSwaps != null && rowSwaps[y] != null)
				yPos = rowSwaps[y].getPosition();
			
			yPos += yOffset;
			
			if(0 <= yPos && yPos <= getHeight())
			{
				for(int x = x0; x < x1; x++)
				{
					float xPos = colPosition[x];
					if(colSwaps != null && colSwaps[x] != null)
					{
						xPos = colSwaps[x].getPosition();
					}
		
					xPos += xOffset;
					
					if(xPos <= width && xPos + colSize[x] >= 0)
					{
						Cell cell = cells[x][y];
						
						if(cell != null)
						{		
							if(cell instanceof ResultCell && x == 2)
							{
								((ResultCell)cell).setMode(ResultCell.Mode.SEASON_RANK);
							}
							else if(cell instanceof ResultCell && x == 3)
							{
								((ResultCell)cell).setMode(settings.getResultMode1());
							}
							else if(cell instanceof ResultCell && x == 4)
							{
								((ResultCell)cell).setMode(settings.getResultMode2());
							}
							
							if(cell instanceof FantasyResultCell)
							{
								if(y == 0)
									((FantasyResultCell)cell).setMode(settings.getFantasyMode1());
								else if(y == 1)
									((FantasyResultCell)cell).setMode(settings.getFantasyMode2());
							}
							
							BufferedImage img = cell.getImage();
							
							/*if(y % 2 == 1)
								cell.setBackground(rowAltColor);
							else
								cell.setBackground(Color.BLACK);*/
		
							if(cell.isUpdated())
							{
								Graphics2D g2 = (Graphics2D)img.getGraphics();
								/*if(y % 2 == 1)
									g2.setBackground(rowAltColor);
								else
									g2.setBackground(Color.BLACK);*/
								g2.clearRect(0, 0, cell.getWidth(), cell.getHeight());
								
								cell.render(g2);
							}
							
							g.drawImage((Image)img, (int)xPos, (int)yPos, null);
							img = null;
						}
					}
				}
			}
		}
	}
}
