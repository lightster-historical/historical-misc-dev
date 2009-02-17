package com.lightdatasys.nascar.live.gui;

import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import com.lightdatasys.gui.AppWindow;
import com.lightdatasys.gui.QuitHandler;
import com.lightdatasys.nascar.Driver;
import com.lightdatasys.nascar.Race;
import com.lightdatasys.nascar.live.gui.panel.LivePanel;

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
		
        device.setFullScreenWindow(this);

        createBufferStrategy(GFX_BUFFER_COUNT);
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
