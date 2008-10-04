package com.lightdatasys.nascar.fantasy.gui;

import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import com.lightdatasys.gui.AppWindow;
import com.lightdatasys.gui.QuitHandler;
import com.lightdatasys.nascar.Driver;
import com.lightdatasys.nascar.Race;
import com.lightdatasys.nascar.fantasy.gui.panel.ClassicScrollerPanel;
import com.lightdatasys.nascar.fantasy.gui.panel.LivePanel;
import com.lightdatasys.nascar.fantasy.gui.panel.SortableScroller;

public class FullScreenWindow extends AppWindow
	implements QuitHandler
{
	// number of buffers to use for graphics rendering
	private final static int GFX_BUFFER_COUNT = 2;
	
	//private Settings settings;
	
	private LiveUpdater liveUpdater;
	
	private GraphicsDevice device;
	
	//private BufferStrategy bufferStrategy;
	
	private LivePanel panel;
	
	private boolean done = false;
	
	public final static int COLUMNS = 45;
	public final static int ROWS = 13;
	
	
	public FullScreenWindow(LiveUpdater liveUpdater, GraphicsDevice device)
	{
		panel = null;

		setSize(800, 600);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		this.liveUpdater = liveUpdater;
		this.device = device;
		
		setUndecorated(true);
		setIgnoreRepaint(true);
		
		//settings = new Settings();
		
		panel = new SortableScroller(this, 1, 43, true);//
		//panel = new ClassicScrollerPanel(this);

        //DisplayMode[] modes = device.getDisplayModes();
        //GraphicsDevice device2 = env.getScreenDevices()[1];
        //System.out.println(device.getType() + " " + device.getIDstring() + " " + device.toString());
        //System.out.println(device2.getType() + " " + device2.getIDstring() + " " + device2.toString());
    	//System.out.println(((float)device.getDisplayMode().getWidth() / device.getDisplayMode().getHeight()) + " " + device.getDisplayMode().getWidth() + " " + device.getDisplayMode().getHeight() + " " + device.getDisplayMode().getRefreshRate() + " " + device.getDisplayMode().getBitDepth());
        
        //for(int i = 0; i < modes.length; i++)
        //	System.out.println(i + " " + ((float)modes[i].getWidth() / modes[i].getHeight()) + " " + modes[i].getWidth() + " " + modes[i].getHeight() + " " + modes[i].getRefreshRate() + " " + modes[i].getBitDepth());

        device.setFullScreenWindow(this);

        createBufferStrategy(GFX_BUFFER_COUNT);
        //bufferStrategy = getBufferStrategy();
        System.out.println("Page flipping: " + getBufferStrategy().getCapabilities().isPageFlipping());
	}
	
	
	public LiveUpdater getLiveUpdater()
	{
		return liveUpdater;
	}
	
	/*public Settings getSettings()
	{
		return settings;
	}*/
	
	public GraphicsDevice getDevice()
	{
		return device;
	}
	
	
	public void setPanel(LivePanel panel)
	{
		this.panel = panel;
	}
	
	
	public void update()
	{
		if(panel != null)
		{
			panel.update();
		}
	}
		
	public void render()
	{
		if(panel != null)
		{
			Graphics2D g = null;
			BufferStrategy bufferStrategy = getBufferStrategy();

			try
			{
				g = (Graphics2D)bufferStrategy.getDrawGraphics();
	
		        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		            RenderingHints.VALUE_ANTIALIAS_ON);
		        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
	                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		        
		        panel.render(g);
			}
			finally
			{
				g.dispose();
			}
			
			try
			{
				if(isVisible())
					bufferStrategy.show();
			}
			catch(IllegalStateException ex)
			{
				System.err.println("Caused by sync issues. Window closed in middle of rendering:");
				ex.printStackTrace(System.err);
			}
		}
	}
	
	
	public Race getRace()
	{
		return liveUpdater.getRace();
	}
	
	public Driver[] getDrivers()
	{
		return liveUpdater.getDrivers();
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
        done = true;
        
        dispose();
        
        System.exit(0);
    } 
}
