package com.lightdatasys.nascar.fantasy.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JFrame;

import com.lightdatasys.gui.AppWindow;
import com.lightdatasys.gui.QuitHandler;
import com.lightdatasys.nascar.Driver;
import com.lightdatasys.nascar.Result;
import com.lightdatasys.nascar.event.PositionChangeEvent;
import com.lightdatasys.nascar.event.PositionChangeListener;
import com.lightdatasys.nascar.fantasy.FantasyPlayer;
import com.lightdatasys.nascar.fantasy.FantasyResult;
import com.lightdatasys.nascar.fantasy.Leaderboard;
import com.sportvision.model.Drivers;
import com.sportvision.model.Race;

public class LeaderboardWindow extends AppWindow
	implements QuitHandler, Runnable
{
	// number of buffers to use for graphics rendering
	private final static int NUM_GFX_BUFFERS = 2;
	
	/*
	// updates per second (data/stats updates)
	private final static float UPS = .2f;
	// frames per second
	private final static float FPS = 60.0f;
	
	private final static long SWAP_PERIOD = 1000;*/
	
	private Settings settings;
	
	private Leaderboard leaderboard;
	
	private GraphicsEnvironment env;
	private GraphicsDevice device;
	
	private BufferStrategy bufferStrategy;
	
	private boolean done = false;
	
	//private int count = 0;
	
	public final static Color[] colors = {Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY,
			Color.GRAY, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED,
			Color.YELLOW};
	
	public final static int COLUMNS = 45;
	public final static int ROWS = 13;
	
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
	
	//private Random rand;
	
	private int width;
	private int height;
	
	private int cellMargin;
	private int cellWidth;
	private int cellHeight;

	private int colOffsetPos;
	private long colOffsetTime;
	
	private float xScrollOffset;
	
	//private ResultCell.Mode resultMode;

	private ArrayList<PositionChangeEvent> positionChangeEvents;
	private ArrayList<PositionChangeEvent> fantasyPositionChangeEvents;
	
	private com.lightdatasys.nascar.Race race;
	
	// timing
	long lastUpdateTime;  // time of last update
	long lastRenderTime;  // and last render
	long updateInterval;  // number of milliseconds between each update
	long renderInterval;  // and each render
	
	
	public LeaderboardWindow(Leaderboard leaderboard)
	{
		super("Leaderboard");

		setSize(800, 600);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		this.leaderboard = leaderboard;
		
		setUndecorated(true);
		setIgnoreRepaint(true);

        env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        device = env.getDefaultScreenDevice();
        
        device.setFullScreenWindow(this);

        createBufferStrategy(NUM_GFX_BUFFERS);
        bufferStrategy = getBufferStrategy();
        
        positionChangeEvents = new ArrayList<PositionChangeEvent>();
        fantasyPositionChangeEvents = new ArrayList<PositionChangeEvent>();

		leaderboard.init();
		leaderboard.start();
					
		//add(leaderboard);

		//WindowUtil.centerWindow(this);
		//setVisible(true);
		
		settings = new Settings();
		Thread test = new Thread(new SettingsServer(settings));
		test.start();
		System.out.println("settings thread created");
        
        cellMargin = 2;
        
        xScrollOffset = 0;
        
        //resultMode = ResultCell.Mode.POSITION;

		Rectangle dim = device.getDefaultConfiguration().getBounds();
        width = (int)dim.getWidth();
        height = (int)dim.getHeight();
        
		cellWidth = ((width - cellMargin) / COLUMNS) - cellMargin;
		cellHeight = ((height - cellMargin) / ROWS) - cellMargin;
		
		colOffsetPos = 0;
		colOffsetTime = 0;

		race = com.lightdatasys.nascar.Race.getById(1071);
		
		race.addPositionChangeListener
		(
			new PositionChangeListener()
			{
				public void positionChanged(PositionChangeEvent event)
				{
					positionChangeEvents.add(event);
				}
			}
		);
		race.addFantasyPositionChangeListener
		(
			new PositionChangeListener()
			{
				public void positionChanged(PositionChangeEvent event)
				{
					fantasyPositionChangeEvents.add(event);
				}
			}
		);
		
		AbstractMap<Integer,com.lightdatasys.nascar.Result> results = race.getResults();
		AbstractMap<Integer,FantasyResult> fantasyResults = race.getFantasyResults();

		com.lightdatasys.nascar.Driver.loadAllFromDatabase();
		com.lightdatasys.nascar.Driver[] drivers = com.lightdatasys.nascar.Driver.getDrivers(); 
		
		com.lightdatasys.nascar.fantasy.FantasyPlayer.loadAllFromDatabase();
		com.lightdatasys.nascar.fantasy.FantasyPlayer[] players = com.lightdatasys.nascar.fantasy.FantasyPlayer.getPlayers(); 

		cellWidth = 75;
		cellHeight = 75;
		
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
		
		cells = new Cell[COLUMNS][ROWS];
		//tempCells = new Cell[COLUMNS][ROWS];
		for(int x = 0; x < COLUMNS; x++)
		{
			for(int y = 0; y < ROWS; y++)
			{
				cells[x][y] = null;
				
				if(!((0 <= x && x <= 1) && (0 <= y && y <= 1)))
				{
					int i = (x + y * COLUMNS) % drivers.length;

					//System.out.println()
					if(x == 0 || x == 1)
					{
						FantasyResult result = fantasyResults.get(y - 1);
						FantasyPlayer player = result.getPlayer();
						FantasyResultCell cell = new FantasyResultCell(colSize[x], rowSize[y], result, Color.WHITE,
								player.getBackgroundColor(), Color.WHITE);
						//cell.setMode(FantasyResultCell.Mode.POSITION);
						cells[x][y] = cell;
					}
					/*else if(x == 1)
					{
						FantasyResult result = fantasyResults.get(y - 1);
						FantasyResultCell cell = new FantasyResultCell(colSize[x], rowSize[y], result,
							Color.WHITE, Color.BLACK, Color.WHITE);
						cell.setMode(FantasyResultCell.Mode.DRIVER_RACE_POINTS);
						cells[x][y] = cell;
					}*/
					else if(x == 45)
					{
						if(y <= 1)
							cells[x][y] = null;
						else
						{
							FantasyResult result = fantasyResults.get(y - 1);
							FantasyResultCell cell = new FantasyResultCell(colSize[x], rowSize[y], result,
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
							cells[x][y] = new CarNoCell(colSize[x], rowSize[y], result.getCar(), driver.getFontColor(),
									driver.getBackgroundColor(), driver.getBorderColor());
						}
					}
					else if(y == 1)
					{
						Result result = results.get(x - 1);
						if(result != null)
						{
							cells[x][y] = new ResultCell(colSize[x], rowSize[y], result, 
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
								cells[x][y] = new FantasyPlayerCell(colSize[x], rowSize[y], player.toString(), Color.WHITE,
										player.getBackgroundColor()/*drivers[i].getBackgroundColor()*/, Color.WHITE);
							}
						}
					}
				}
				//else
				
				//tempCells[x][y] = cells[x][y];
			}
		}
		cells[0][0] = new RaceStatusCell(colSize[0]+colSize[1] + cellMargin, rowSize[0]+rowSize[1] + cellMargin, race);
		//tempCells[0][0] = cells[0][0];

		Result leaderResult = race.getResultByFinish(1);
		Driver leader = leaderResult.getDriver();
		bgCell = new CarNoCell(getWidth()-colSize[0]-colSize[1]-cellMargin, getHeight()-rowSize[0]-rowSize[1]-cellMargin, leaderResult.getCar(), leader.getFontColor(),
				leader.getBackgroundColor(), leader.getBorderColor());
		leaderCar = leaderResult.getCar();
		
		colSwaps = new Swap[COLUMNS];
		rowSwaps = new Swap[ROWS];
		
		colSwapMap = new int[COLUMNS];
		rowSwapMap = new int[ROWS];
		initSwapMaps();
        
        setBackground(Color.BLACK);

        BufferedImage img = new BufferedImage(16,16,BufferedImage.TYPE_4BYTE_ABGR);
        Cursor blankCursor = getToolkit().createCustomCursor(img,new Point(0,0),"blankCursor");
        setCursor(blankCursor);

        //rand = new Random();
	}
	
	
	public void update()
	{
		initTiming();
		//race.getResultByFinish(5).setFinish(race.getResultByCarNo("48").getFinish());
		//race.getResultByCarNo("48").setFinish(5);
		/*switch(resultMode)
		{
			case LAPS_LED:
				resultMode = ResultCell.Mode.LEADER_INTERVAL;
				break;
			case LEADER_INTERVAL:
				resultMode = ResultCell.Mode.LOCAL_INTERVAL;
				break;
			case LOCAL_INTERVAL:
				resultMode = ResultCell.Mode.POSITION;
				break;
			case POSITION:
				resultMode = ResultCell.Mode.RACE_POINTS;
				break;
			case RACE_POINTS:
				resultMode = ResultCell.Mode.SEASON_POINTS;
				break;
			case SEASON_POINTS:
				resultMode = ResultCell.Mode.LAPS_LED;
				break;
		}*/
		
		com.lightdatasys.nascar.Race.Flag flag;
		switch(getRace().flag)
		{
			case com.sportvision.model.Race.CHECKERED:
				flag = com.lightdatasys.nascar.Race.Flag.CHECKERED;
				break;
			case com.sportvision.model.Race.GREEN:
				flag = com.lightdatasys.nascar.Race.Flag.GREEN;
				break;
			case com.sportvision.model.Race.PRE_RACE:
				flag = com.lightdatasys.nascar.Race.Flag.PRE_RACE;
				break;
			case com.sportvision.model.Race.RED:
				flag = com.lightdatasys.nascar.Race.Flag.RED;
				break;
			case com.sportvision.model.Race.WHITE:
				flag = com.lightdatasys.nascar.Race.Flag.WHITE;
				break;
			case com.sportvision.model.Race.YELLOW:
				flag = com.lightdatasys.nascar.Race.Flag.YELLOW;
				break;
			default:
				flag = com.lightdatasys.nascar.Race.Flag.PRE_RACE;
				break;
		}
		if(race.getCurrentLap() != getRace().currentLap)
		{
			race.updateFantasyLastLapPositions();
			race.updateLastLapPositions();			
			race.setLapCount(getRace().lapCount);
		}
		race.setCurrentLap(getRace().currentLap);
		race.setFlag(flag);
		race.setLastFlagChange(getRace().flagChangeLap);
		race.setCautionCount(getRace().numberOfCautions);
		race.setLeadChangeCount(getRace().numberOfLeadChanges);
		race.setLeaderCount(getRace().numberOfLeaders);
		
		boolean raceStarted = false;
		if(race.getCurrentLap() > 0)
			raceStarted = true;
		
		Vector<?> drivers = getDrivers().getSortedList();
		//*
		int mostLapsLed = 0;
		for(int i = 0; i < drivers.size(); i++)
		{
			String id = (String)drivers.get(i);

			Result result = race.getResultByCarNo(id);
			if(result != null)
			{
				com.sportvision.model.Driver d = getDrivers().get(id);
				int startPos = d.startPosition;
				int currPos = d.currentPosition;
			
				if(raceStarted && currPos != 0)
				{
					result.setFinish(currPos);
					
					result.setBehindLeader((float)d.getTimeOffLeader());
					result.setLapsDown(d.onLeadLap() ? 0 : -d.getLapsBehindLeader());
					result.setLapsLed(d.lapsLed);
					result.setLedLaps(result.getLapsLed() > 0);
					mostLapsLed = Math.max(mostLapsLed, result.getLapsLed());
					result.setSpeed(d.speed);
				}
				else
					result.setFinish(startPos);
			}
			else
			{
				System.out.println("result==null for id=" + id);
			}
		}
		for(int i = 0; i < drivers.size(); i++)
		{
			String id = (String)drivers.get(i);

			Result result = race.getResultByCarNo(id);
			if(result != null)
			{
				result.setLedMostLaps(mostLapsLed == result.getLapsLed() && mostLapsLed != 0);
			}
		}
		race.updateFantasyFinishPositions();
		//*/
		
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

		Result leaderResult = race.getResultByFinish(1);
		if(leaderCar != leaderResult.getCar())
		{
			Driver leader = leaderResult.getDriver();
			bgCell = new CarNoCell(getWidth()-colSize[0]-colSize[1]-cellMargin, getHeight()-rowSize[0]-rowSize[1]-cellMargin, leaderResult.getCar(), leader.getFontColor(),
					leader.getBackgroundColor(), leader.getBorderColor());
			leaderCar = leaderResult.getCar();
		}
		
		/*
		moveColumn(2, 6);
		moveColumn(3, 2);
		moveColumn(4, 3);
		moveColumn(5, 7);
		moveColumn(6, 4);
		moveColumn(7, 5);*/

		/*
		moveRow(5, 8);
		moveRow(8, 3);
		moveRow(3, 5);
		//*/
		
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
	
	
	public void render(Graphics2D g)
	{	
		if(race.getFlag() == com.lightdatasys.nascar.Race.Flag.YELLOW)
			setBackground(Color.YELLOW);
		else if(race.getFlag() == com.lightdatasys.nascar.Race.Flag.RED)
			setBackground(new Color(0xCC, 0x00, 0x00));
		else if(race.getFlag() == com.lightdatasys.nascar.Race.Flag.WHITE)
			setBackground(Color.WHITE);
		else
			setBackground(Color.BLACK);
		
		g.clearRect(0, 0, width, height);
		
		if(race.getFlag() == com.lightdatasys.nascar.Race.Flag.CHECKERED)
		{
			
		}
		
		/*
		if(race.getFlag() != com.lightdatasys.nascar.Race.Flag.RED
			&& race.getFlag() != com.lightdatasys.nascar.Race.Flag.YELLOW)
		{
			AffineTransform oldTransform = g.getTransform();
			AffineTransform transform = new AffineTransform();
			transform.translate(colPosition[2], rowPosition[2]);
			g.setTransform(transform);
			
			bgCell.render(g);
			
			g.setTransform(oldTransform);
		}
		//*/

		float speed = settings.getScrollSpeed() * .4f;
		//int distance = colPosition[43] - colPosition[2];
		if(speed > 10)
		{
			xScrollOffset = 0;
		}
		else
		{
			xScrollOffset += 1.0f * speed * ((System.currentTimeMillis() - lastRenderTime));
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
			
			for(int y = y0; y < y1; y++)
			{
				Cell cell = cells[x][y];
				
				if(cell != null)
				{		
					if(cell instanceof ResultCell)
					{
						((ResultCell)cell).setMode(settings.getResultMode());
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
						cell.render((Graphics2D)img.getGraphics());
					
					AffineTransform transform = new AffineTransform();

					int yPos = rowPosition[y];//cellMargin + y * (cellHeight + cellMargin);
					if(rowSwaps[y] != null)
						yPos = rowSwaps[y].getPosition();
					
					transform.translate(xPos, yPos);
					
					g.drawRenderedImage(img, transform);
					img = null;
				}
			}
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
	
	
	public void initTiming()
	{
		lastUpdateTime = System.currentTimeMillis();
		lastRenderTime = System.currentTimeMillis();
		
		updateInterval = Math.round(1000.0d / settings.getUPS()); 
		renderInterval = Math.round(1000.0d / settings.getFPS());
	}
	
	public void run()
	{
		initTiming();
		
		//final long MAX_SLEEP_TIME = Math.min(updateInterval, renderInterval);
		//long lastTime = System.currentTimeMillis();
		
		try
		{
			while(!done)
			{
				long renderDiff = (System.currentTimeMillis() - lastRenderTime);
	        	if(renderDiff >= renderInterval)
	        	{
					Graphics2D g = null;
					try
					{
						g = (Graphics2D)bufferStrategy.getDrawGraphics();

				        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				            RenderingHints.VALUE_ANTIALIAS_ON);
				        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				        
			            render(g);
			            
			            /*
			            g.setColor(new Color(255, 255, 255, 100));
			            g.setFont(g.getFont().deriveFont(150.0f));
			            g.drawString((int)(1000.0f / renderDiff) + " fps", 5, 300);
			            //*/
			        }
					finally
					{
			            g.dispose();
					}
		            bufferStrategy.show();
		         
		            lastRenderTime = System.currentTimeMillis();
	        	}
	        	
				long updateDiff = (System.currentTimeMillis() - lastUpdateTime);
				if(updateDiff >= updateInterval)
				{
					update();

		            lastUpdateTime = System.currentTimeMillis();
				}
	        	
	        	long updateSleepTime = updateInterval - (System.currentTimeMillis() - lastUpdateTime);
	        	long renderSleepTime = renderInterval - (System.currentTimeMillis() - lastRenderTime);
	        	long sleepTime = Math.min(Math.min(updateSleepTime, renderSleepTime), 5000);
		        
		        try
		        {
		        	if(sleepTime > 0)
		        	{
		        		Thread.sleep(sleepTime);
		        	}
		        }
		        catch(InterruptedException ex)
		        {
		        	ex.printStackTrace();
		        }
			}
		}
		finally
		{
			device.setFullScreenWindow(null);
		}
	}
	
	
	public Drivers getDrivers()
	{
		return leaderboard.getDrivers();
	}
	
	public Race getRace()
	{
		return leaderboard.getRace();
	}
	
	
	public void handleClose()
	{
        System.out.println("Leaderboard Close Handled");
        
        handleQuit();
	}
    
    public void handleQuit()
    {
        System.out.println("Leaderboard Quit Handled");

        // do quit stuff here

		//leaderboard.stop();
		//leaderboard.destroy();
        
        dispose();
        
        System.exit(0);
    } 
}
