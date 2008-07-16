package com.lightdatasys.nascar.fantasy.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import com.lightdatasys.gui.AppWindow;
import com.lightdatasys.gui.QuitHandler;
import com.lightdatasys.nascar.fantasy.Leaderboard;
import com.sportvision.model.Drivers;
import com.sportvision.model.Race;

public class LeaderboardWindow extends AppWindow
	implements QuitHandler, Runnable
{
	private final static int NUM_BUFFERS = 3;
	
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
	
	//private Swap[] columnSwap;
	private Swap swap;
	
	
	public LeaderboardWindow(Leaderboard leaderboard)
	{
		super("Leaderboard");

		setSize(800, 600);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		this.leaderboard = leaderboard;
		
		swap = null;
		
		setUndecorated(true);
		setIgnoreRepaint(true);

        env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        device = env.getDefaultScreenDevice();
        
        device.setFullScreenWindow(this);

        createBufferStrategy(NUM_BUFFERS);
        bufferStrategy = getBufferStrategy();

		//leaderboard.init();
		//leaderboard.start();
					
		//add(leaderboard);

		//WindowUtil.centerWindow(this);
		//setVisible(true);
        
		int margin = 2;
		Rectangle dim = device.getDefaultConfiguration().getBounds();
		int width = (((int)dim.getWidth() - margin) / COLUMNS) - margin;
		int height = (((int)dim.getHeight() - margin) / ROWS) - margin;
		
		com.lightdatasys.nascar.Driver.loadAllFromDatabase();
		com.lightdatasys.nascar.Driver[] drivers = com.lightdatasys.nascar.Driver.getDrivers(); 
        
		cells = new Cell[COLUMNS][ROWS];
		for(int x = 0; x < COLUMNS; x++)
		{
			for(int y = 0; y < ROWS; y++)
			{
				if(!((0 <= x && x <= 1) && (0 <= y && y <= 1)))
				{
					int i = (x + y * COLUMNS) % drivers.length;
					if(x % 2 == 0)
						cells[x][y] = new CarNoCell(width, height, "24", drivers[i].getFontColor(),
								drivers[i].getBackgroundColor(), drivers[i].getBorderColor());
					else
						cells[x][y] = new CarNoCell(width, height, "124", drivers[i].getFontColor(),
								drivers[i].getBackgroundColor(), drivers[i].getBorderColor());	
				}
				else
					cells[x][y] = null;
			}
		}
		cells[0][0] = new Cell(width*2 + margin, height*2 + margin);
        
        setBackground(Color.BLACK);
	}
	
	
	public void render(Graphics2D g)
	{		
		int margin = 2;
		Rectangle dim = device.getDefaultConfiguration().getBounds();
		
		g.clearRect(dim.x, dim.y, dim.width, dim.height);
		
		int width = (((int)dim.getWidth() - margin) / COLUMNS) - margin;
		int height = (((int)dim.getHeight() - margin) / ROWS) - margin;
		
		if(swap == null)
		{
			swap = new Swap(width + margin, 20);
		}
		
		for(int x = 0; x < COLUMNS; x++)
		{
			for(int y = 0; y < ROWS; y++)
			{
				Cell cell = cells[x][y];
				
				if(cell != null)
				{
					BufferedImage img = cell.getImage();
					cell.render((Graphics2D)img.getGraphics());
					
					AffineTransform transform = new AffineTransform();

					if(x == 4)
						transform.translate(margin + x * (width + margin) + swap.getDisplacement(), margin + y * (height + margin));
					else if(x == 5)
						transform.translate(margin + x * (width + margin) - swap.getDisplacement(), margin + y * (height + margin));
					else
						transform.translate(margin + x * (width + margin), margin + y * (height + margin));
					
					g.drawRenderedImage(img, transform);
					img = null;
				}
			}
		}
		
		swap.increment();
		//System.out.println(swap.getDelta() + " " + swap.getDisplacement());
		if(swap.isDone() && swap.getDelta() - swap.getMaxDelta() > 10)
		{
			swap = null;
			
			Cell[] col = cells[4];
			cells[4] = cells[5];
			cells[5] = col;
		}
	}
	
	public void run()
	{
		try
		{
			while(!done)
			{
		        Graphics2D g = (Graphics2D)bufferStrategy.getDrawGraphics();
		        
		        //if(!bufferStrategy.contentsLost()) 
		        //{
		            render(g);
		            bufferStrategy.show();
		            g.dispose();
		        //}
		        
		        /*
		        try
		        {
			        Thread.sleep(1000);	
		        }
		        catch(InterruptedException ex)
		        {
		        	ex.printStackTrace();
		        }
		        //*/
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
