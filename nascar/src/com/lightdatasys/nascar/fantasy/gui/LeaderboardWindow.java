package com.lightdatasys.nascar.fantasy.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;

import com.lightdatasys.gui.AppWindow;
import com.lightdatasys.gui.QuitHandler;
import com.lightdatasys.nascar.fantasy.Leaderboard;
import com.sportvision.model.Drivers;
import com.sportvision.model.Race;

public class LeaderboardWindow extends AppWindow
	implements QuitHandler, Runnable
{
	// number of buffers to use for graphics rendering
	private final static int NUM_GFX_BUFFERS = 2;
	
	// updates per second (data/stats updates)
	private final static float UPS = 0.25f;
	// frames per second
	private final static float FPS = 30.0f;
	
	private final static long SWAP_PERIOD = 1000;
	
	private Leaderboard leaderboard;
	
	private GraphicsEnvironment env;
	private GraphicsDevice device;
	
	private BufferStrategy bufferStrategy;
	
	private boolean done = false;
	
	private int count = 0;
	
	public final static Color[] colors = {Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY,
			Color.GRAY, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED,
			Color.YELLOW};
	
	public final static int COLUMNS = 20;
	public final static int ROWS = 14;
	
	private Cell[][] cells;
	//private Cell[][] tempCells;

	private Swap[] colSwaps;
	private Swap[] rowSwaps;	
	private int[] colSwapMap;
	private int[] rowSwapMap;
	
	private Random rand;
	
	private int width;
	private int height;
	
	private int cellMargin;
	private int cellWidth;
	private int cellHeight;
	
	
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

		//leaderboard.init();
		//leaderboard.start();
					
		//add(leaderboard);

		//WindowUtil.centerWindow(this);
		//setVisible(true);
        
        cellMargin = 2;

		Rectangle dim = device.getDefaultConfiguration().getBounds();
        width = (int)dim.getWidth();
        height = (int)dim.getHeight();
        
		cellWidth = ((width - cellMargin) / COLUMNS) - cellMargin;
		cellHeight = ((height - cellMargin) / ROWS) - cellMargin;
		
		com.lightdatasys.nascar.Driver.loadAllFromDatabase();
		com.lightdatasys.nascar.Driver[] drivers = com.lightdatasys.nascar.Driver.getDrivers(); 
        
		cells = new Cell[COLUMNS][ROWS];
		//tempCells = new Cell[COLUMNS][ROWS];
		for(int x = 0; x < COLUMNS; x++)
		{
			for(int y = 0; y < ROWS; y++)
			{
				if(!((0 <= x && x <= 1) && (0 <= y && y <= 1)))
				{
					int i = (x + y * COLUMNS) % drivers.length;
					
					/*if(x % 2 == 0)
						cells[x][y] = new CarNoCell(cellWidth, cellHeight, "24", drivers[i].getFontColor(),
								drivers[i].getBackgroundColor(), drivers[i].getBorderColor());
					else*/
						cells[x][y] = new CarNoCell(cellWidth, cellHeight, "" + x, drivers[i].getFontColor(),
								drivers[i].getBackgroundColor(), drivers[i].getBorderColor());	
				}
				else
					cells[x][y] = null;
				
				//tempCells[x][y] = cells[x][y];
			}
		}
		cells[0][0] = new Cell(cellWidth*2 + cellMargin, cellHeight*2 + cellMargin);
		//tempCells[0][0] = cells[0][0];
		
		colSwaps = new Swap[COLUMNS];
		rowSwaps = new Swap[ROWS];
		
		colSwapMap = new int[COLUMNS];
		rowSwapMap = new int[ROWS];
		initSwapMaps();
        
        setBackground(Color.BLACK);

        rand = new Random();
	}
	
	
	public void update()
	{
		moveColumn(2, 6);
		moveColumn(3, 2);
		moveColumn(4, 3);
		moveColumn(5, 7);
		moveColumn(6, 4);
		moveColumn(7, 5);

		//*
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
		g.clearRect(0, 0, width, height);
		
		for(int x = 0; x < COLUMNS; x++)
		{
			int xPos = cellMargin + x * (cellWidth + cellMargin);
			if(colSwaps[x] != null)
			{
				xPos = colSwaps[x].getPosition();
			}
			
			for(int y = 0; y < ROWS; y++)
			{
				Cell cell = cells[x][y];
				
				if(cell != null)
				{
					BufferedImage img = cell.getImage();
					cell.render((Graphics2D)img.getGraphics());
					
					AffineTransform transform = new AffineTransform();

					int yPos = cellMargin + y * (cellHeight + cellMargin);
					if(rowSwaps[y] != null)
						yPos = rowSwaps[y].getPosition();
					
					transform.translate(xPos, yPos);
					
					g.drawRenderedImage(img, transform);
					g.setColor(Color.WHITE);
					g.drawString(""+x, xPos, 50);
					img = null;
				}
			}
			
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
	}
	

	public void moveColumn(int col1, int col2)
	{
		if(0 <= col1 && col1 < cells.length &&
			0 <= col2 && col2 < cells.length &&
			col1 != col2)
		{
			int pos1 = cellMargin + col1 * (cellWidth + cellMargin);
			//if(colSwaps[col1] != null)
			//	pos1 = colSwaps[col1].getPosition();
			
			int pos2 = cellMargin + col2 * (cellWidth + cellMargin);

			colSwaps[col2] = new Swap(pos1, pos2, SWAP_PERIOD);
			colSwapMap[col2] = col1;
		}
	}

	public void moveRow(int row1, int row2)
	{
		if(0 <= row1 && row1 < cells[0].length &&
			0 <= row2 && row2 < cells[0].length &&
			row1 != row2)
		{
			int pos1 = cellMargin + row1 * (cellHeight + cellMargin);
			//if(colSwaps[row1] != null)
			//	pos1 = colSwaps[row1].getPosition();
			
			int pos2 = cellMargin + row2 * (cellHeight + cellMargin);
			
			rowSwaps[row2] = new Swap(pos1, pos2, SWAP_PERIOD);
			rowSwapMap[row2] = row1;
		}
	}
	
	
	public void run()
	{
		long lastUpdateTime = 0;
		long lastRenderTime = 0;
		
		long updateInterval = Math.round(1000.0d / UPS); 
		long renderInterval = Math.round(1000.0d / FPS);
		
		final long MAX_SLEEP_TIME = Math.min(updateInterval, renderInterval);

		long lastTime = System.currentTimeMillis();
		try
		{
			while(!done)
			{
				long updateDiff = (System.currentTimeMillis() - lastUpdateTime);
				if(updateDiff >= updateInterval)
				{
					update();

		            lastUpdateTime = System.currentTimeMillis();
				}
				
				long renderDiff = (System.currentTimeMillis() - lastRenderTime);
	        	if(renderDiff >= renderInterval)
	        	{
					Graphics2D g = null;
					try
					{
						g = (Graphics2D)bufferStrategy.getDrawGraphics();
			            render(g);
			            g.setColor(Color.WHITE);
			            g.drawString((int)(1000.0f / renderDiff) + " fps", 5, 30);
					}
					finally
					{
			            g.dispose();
					}
		            bufferStrategy.show();
		         
		            lastRenderTime = System.currentTimeMillis();
	        	}
	        	
	        	long updateSleepTime = updateInterval - (System.currentTimeMillis() - lastUpdateTime);
	        	long renderSleepTime = renderInterval - (System.currentTimeMillis() - lastRenderTime);
	        	long sleepTime = Math.min(updateSleepTime, renderSleepTime);
		        
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
