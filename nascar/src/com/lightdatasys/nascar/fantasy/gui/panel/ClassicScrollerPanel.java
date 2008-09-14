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
import com.lightdatasys.nascar.fantasy.gui.cell.RaceStatusCell;
import com.lightdatasys.nascar.fantasy.gui.cell.ResultCell;

public class ClassicScrollerPanel extends LivePanel
{
	public final static int COLUMNS = 45;
	public final static int ROWS = 13;
	
	private Cell raceStatusCell;
	//private AbstractMap<String,Cell> driverHeaderCells;
	private AbstractMap<String,Cell> driverCells;
	private AbstractMap<String,Cell> driverResult1Cells;
	private AbstractMap<Integer,Cell> playerHeaderCells;
	private AbstractMap<Integer,Cell> playerCells;
	private AbstractMap<Integer,Cell> playerResult1Cells;
	private AbstractMap<Integer,Cell> playerResult2Cells;
	
	private Cell[][] cells;
	private int[] colSize;
	private int[] rowSize;
	private int[] colPosition;
	private int[] rowPosition;
	//private Cell[][] tempCells;

	private Swap[] colSwaps;
	private Swap[] rowSwaps;	
	private int[] colSwapMap;
	private int[] rowSwapMap;
	
	private Cell bgCell;
	private String leaderCar;
	
	private int width;
	private int height;
	
	private int cellMargin;
	private int cellWidth;
	private int cellHeight;

	//private int colOffsetPos;
	//private long colOffsetTime;
	
	private float xScrollOffset;
	
	//private ResultCell.Mode resultMode;

	private ArrayList<PositionChangeEvent> positionChangeEvents;
	private ArrayList<PositionChangeEvent> fantasyPositionChangeEvents;
	
	//private com.lightdatasys.nascar.Race race;
	
	// timing
	private long lastUpdateTime;  // time of last update
	private long lastRenderTime;  // and last render
	private long updateInterval;  // number of milliseconds between each update
	private long renderInterval;  // and each render
	

	private AbstractMap<Integer,FantasyResult> fantasyResults;
	private AbstractMap<Integer,Result> results;
	private Settings settings;
	
	
	public ClassicScrollerPanel(FullScreenWindow window)
	{
		super(window);

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
        
		cellWidth = ((width - cellMargin) / COLUMNS) - cellMargin;
		cellHeight = ((height - cellMargin) / ROWS) - cellMargin;
		cellWidth = cellHeight;
		
		colSize = new int[COLUMNS];
		colPosition = new int[COLUMNS];
		colSize[0] = cellWidth; colSize[1] = cellWidth;
		for(int i = 2; i < COLUMNS; i++)
		{
			colSize[i] = cellWidth;
		}
		colPosition[0] = cellMargin;
		for(int i = 1; i < COLUMNS; i++)
		{
			colPosition[i] = colPosition[i - 1] + colSize[i - 1] + cellMargin;
		}

		rowSize = new int[ROWS];
		rowPosition = new int[ROWS];
		rowSize[0] = cellHeight; rowSize[1] = cellHeight;
		for(int i = 2; i < ROWS; i++)
		{
			rowSize[i] = cellHeight;
		}
		rowPosition[0] = cellMargin;
		for(int i = 1; i < ROWS; i++)
		{
			rowPosition[i] = rowPosition[i - 1] + rowSize[i - 1] + cellMargin;
		}

		initGlobalCells(cellWidth*2 + cellMargin);
		initDriverCells(cellWidth, cellWidth);
		initPlayerCells(cellWidth, cellWidth);
		
		cells = new Cell[COLUMNS][ROWS];
		//tempCells = new Cell[COLUMNS][ROWS];
		for(int x = 0; x < COLUMNS; x++)
		{
			for(int y = 0; y < ROWS; y++)
			{
				cells[x][y] = null;
				
				if(!((0 <= x && x <= 1) && (0 <= y && y <= 1)))
				{
					int i = (x + y * COLUMNS) % getDrivers().length;

					//System.out.println()
					if(x == 0 || x == 1)
					{
						FantasyResult result = fantasyResults.get(y - 1);
						FantasyPlayer player = result.getPlayer();
						/*FantasyResultCell cell = new FantasyResultCell(colSize[x], rowSize[y], result, Color.WHITE,
								player.getBackgroundColor(), Color.WHITE);
								*/
						//cell.setMode(FantasyResultCell.Mode.POSITION);
						cells[x][y] = playerResult1Cells.get(player.getPlayerId());
					}
					else if(x == 45)
					{
						if(y <= 1)
							cells[x][y] = null;
						else
						{
							FantasyResult result = fantasyResults.get(y - 1);
							FantasyResultCell cell = createFantasyResultCell(colSize[x], rowSize[y], result,
								Color.WHITE, Color.BLACK, Color.WHITE);
							cell.setMode(FantasyResultCell.Mode.POSITION_CHANGE);
							cells[x][y] = cell;
						}
					}
					else if(y == 0)
					{
						Result result = results.get(x - 1);
						if(result != null)
						{
							Driver driver = result.getDriver();
							cells[x][y] = createCarNoCell(colSize[x], rowSize[y], result.getCar(), driver.getFontColor(),
									driver.getBackgroundColor(), driver.getBorderColor());
						}
					}
					else if(y == 1)
					{
						Result result = results.get(x - 1);
						if(result != null)
						{
							cells[x][y] = createResultCell(colSize[x], rowSize[y], result, 
									Color.WHITE, Color.BLACK, Color.WHITE);
						}
					}
					/*else if(x < 2)
					{
						cells[x][y] = new CarNoCell(colSize[x], rowSize[y], "" + x, 
								Color.WHITE, Color.BLACK, Color.WHITE);
					}*/
					else if(x >= 2 && y >= 2)// && (y == 3 || y == 5 || y == 7))//x >= 2 && y >= 2 && !(y == 3 && x == 5))
					{
						Result result = results.get(x - 1);
						if(result != null)
						{
							FantasyResult fantasyResult = fantasyResults.get(y - 1);
							
							if(fantasyResult.getPicks().contains(result.getCar()))
							{
								FantasyPlayer player = fantasyResult.getPlayer();

								cells[x][y] = playerCells.get(player.getPlayerId());
								//cells[x][y] = new FantasyPlayerCell(colSize[x], rowSize[y], player.toString(), Color.WHITE,
								//		player.getBackgroundColor()/*drivers[i].getBackgroundColor()*/, Color.WHITE);
							}
						}
					}
				}
				//else
				
				//tempCells[x][y] = cells[x][y];
			}
		}
		cells[0][0] = createRaceStatusCell(colSize[0]+colSize[1] + cellMargin, rowSize[0]+rowSize[1] + cellMargin, getRace());
		//tempCells[0][0] = cells[0][0];

		Result leaderResult = getRace().getResultByFinish(1);
		Driver leader = leaderResult.getDriver();
		bgCell = createCarNoCell(getWidth()-colSize[0]-colSize[1]-cellMargin, getHeight()-rowSize[0]-rowSize[1]-cellMargin, leaderResult.getCar(), leader.getFontColor(),
				leader.getBackgroundColor(), leader.getBorderColor());
		leaderCar = leaderResult.getCar();
		
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
	

	public void initGlobalCells(int dim)
	{
		raceStatusCell = createRaceStatusCell(dim, dim, getRace());
		
		Result leaderResult = getRace().getResultByFinish(1);
		Driver leader = leaderResult.getDriver();
		bgCell = createCarNoCell(getWidth()-dim, getHeight()-dim, leaderResult.getCar(), leader.getFontColor(),
				leader.getBackgroundColor(), leader.getBorderColor());
	}

	public void initDriverCells(int dim, int resultWidth)
	{
		driverCells = new HashMap<String,Cell>();
		driverResult1Cells = new HashMap<String,Cell>();

		AbstractMap<Integer,com.lightdatasys.nascar.Result> results = getRace().getResults();
		
		Iterator<Integer> it = results.keySet().iterator();
		while(it.hasNext())
		{
			Result result = results.get(it.next());
			if(result != null)
			{
				Driver driver = result.getDriver();
				
				Cell driverCell = createCarNoCell(dim, dim, result.getCar(), driver.getFontColor(),
						driver.getBackgroundColor(), driver.getBorderColor());
				driverCells.put(result.getCar(), driverCell);
				
				Cell driverResult1Cell = createResultCell(resultWidth, dim, result, 
						Color.WHITE, Color.BLACK, Color.WHITE);
				driverCells.put(result.getCar(), driverResult1Cell);
			}
		}
	}
	
	public void initPlayerCells(int dim, int headerHeight)
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
				
				FantasyPlayerCell playerHeaderCell = createFantasyPlayerCell(dim, headerHeight, player.toString(), Color.WHITE,
						player.getBackgroundColor()/*drivers[i].getBackgroundColor()*/, Color.WHITE);
				playerHeaderCells.put(player.getPlayerId(), playerHeaderCell);
				
				FantasyPlayerCell playerCell = createFantasyPlayerCell(dim, dim, player.toString(), Color.WHITE,
						player.getBackgroundColor()/*drivers[i].getBackgroundColor()*/, Color.WHITE);
				playerCells.put(player.getPlayerId(), playerCell);
				
				FantasyResultCell playerResult1Cell = createFantasyResultCell(dim, dim, result, Color.WHITE,
						player.getBackgroundColor(), Color.WHITE);
				playerResult1Cells.put(player.getPlayerId(), playerResult1Cell);
				
				FantasyResultCell playerResult2Cell = createFantasyResultCell(dim, dim, result, Color.WHITE,
						player.getBackgroundColor(), Color.WHITE);
				playerResult2Cell.setMode(FantasyResultCell.Mode.POSITION_CHANGE);
				playerResult2Cells.put(player.getPlayerId(), playerResult2Cell);
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
		for(PositionChangeEvent event : positionChangeEvents)
		{
			if(event.getOldPosition() + 1 < cells.length 
				&& event.getNewPosition() + 1 < cells.length)
				moveColumn(event.getOldPosition() + 1, event.getNewPosition() + 1);
		}
		positionChangeEvents.clear();
		
		for(PositionChangeEvent event : fantasyPositionChangeEvents)
		{
			if(event.getOldPosition() + 1 < cells[0].length 
				&& event.getNewPosition() + 1 < cells[0].length)
				moveRow(event.getOldPosition() + 1, event.getNewPosition() + 1);
		}
		fantasyPositionChangeEvents.clear();

		Result leaderResult = getRace().getResultByFinish(1);
		if(leaderCar != leaderResult.getCar())
		{
			Driver leader = leaderResult.getDriver();
			bgCell = createCarNoCell(getWidth()-colSize[0]-colSize[1]-cellMargin, getHeight()-rowSize[0]-rowSize[1]-cellMargin, leaderResult.getCar(), leader.getFontColor(),
					leader.getBackgroundColor(), leader.getBorderColor());
			leaderCar = leaderResult.getCar();
		}
		
		Cell[][] tempCells = new Cell[COLUMNS][ROWS];
		for(int x = 0; x < cells.length; x++)
		{
			for(int y = 0; y < cells[x].length; y++)
			{
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
		
		if(race.getFlag() == com.lightdatasys.nascar.Race.Flag.CHECKERED)
		{
			AffineTransform oldTransform = g.getTransform();
			AffineTransform transform = new AffineTransform();
			transform.translate(colPosition[2], rowPosition[2]);
			g.setTransform(transform);
			
			bgCell.render(g);
			
			g.setTransform(oldTransform);
		}

		float speed = settings.getScrollSpeed() * .4f;
		//int distance = colPosition[43] - colPosition[2];
		if(speed > 10)
		{
			xScrollOffset = 0;
		}
		else
		{
			xScrollOffset += 1.0f * speed * ((getLiveUpdater().getLastRenderDelta()));
		}

		if(xScrollOffset > colPosition[COLUMNS-1] + 2*colSize[COLUMNS-1] + cellMargin - colPosition[2])
		{
			xScrollOffset -= colPosition[COLUMNS-1] + 2*colSize[COLUMNS-1] + cellMargin - colPosition[2];
			//colOffsetTime = System.currentTimeMillis();
			/*System.out.println("yep");
			g.setColor(Color.YELLOW);
			g.fillRect(100, 100, 100, 100);*/
		}
		renderCells(g, 2, COLUMNS, 0, ROWS, -xScrollOffset, 0);
		renderCells(g, 2, COLUMNS, 0, ROWS, -xScrollOffset + colPosition[COLUMNS-1] + 2*colSize[COLUMNS-1] + cellMargin - colPosition[2], 0);
		
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
		
		g.clearRect(0, 0, colPosition[2], height);
		renderCells(g, 0, 2, 0, ROWS, 0, 0);
	}
	
	public void renderCells(Graphics2D g, int x0, int x1, int y0, int y1, float xOffset, float yOffset)
	{
		for(int x = x0; x < x1; x++)
		{
			float xPos = colPosition[x];//cellMargin + x * (cellWidth + cellMargin);
			if(colSwaps[x] != null)
			{
				xPos = colSwaps[x].getPosition();
			}
			
			if(x >= 2)
				xPos += xOffset;
			
			if(xPos <= width && xPos + colSize[x] >= 0)
			{
				for(int y = y0; y < y1; y++)
				{
					Cell cell = cells[x][y];
					
					if(cell != null)
					{		
						if(cell instanceof ResultCell)
						{
							((ResultCell)cell).setMode(settings.getResultMode1());
						}
						
						if(cell instanceof FantasyResultCell)
						{
							if(x == 0)
								((FantasyResultCell)cell).setMode(settings.getFantasyMode1());
							else if(x == 1)
								((FantasyResultCell)cell).setMode(settings.getFantasyMode2());
						}
						
						BufferedImage img = cell.getImage();
	
						if(cell.isUpdated())
						{
							cell.render((Graphics2D)img.getGraphics());
						}
						
						//AffineTransform transform = new AffineTransform();
	
						int yPos = rowPosition[y];//cellMargin + y * (cellHeight + cellMargin);
						if(rowSwaps[y] != null)
							yPos = rowSwaps[y].getPosition();
						
						//transform.translate(xPos, yPos);
						
						g.drawImage((Image)img, (int)xPos, (int)yPos, null);
						//g.drawRenderedImage(img, transform);
						img = null;
					}
				}
			}
			//else if(!(xPos <= width))
			//	x = x1;
		}
	}
}
