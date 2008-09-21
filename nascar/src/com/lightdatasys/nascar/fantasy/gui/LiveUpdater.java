package com.lightdatasys.nascar.fantasy.gui;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Vector;

import com.lightdatasys.nascar.Result;
import com.lightdatasys.nascar.event.PositionChangeEvent;
import com.lightdatasys.nascar.fantasy.FantasyResult;
import com.lightdatasys.nascar.fantasy.Leaderboard;
import com.lightdatasys.nascar.fantasy.gui.panel.ClassicScrollerPanel;
import com.lightdatasys.nascar.fantasy.gui.panel.LivePanel;
import com.lightdatasys.nascar.fantasy.gui.panel.SortableScroller;

public class LiveUpdater//extends AppWindow
	implements Runnable
{
	private final static String PROPERTY_FILE = "CampingWorldRV400_2008.txt";
	private final static int RACE_ID = 1076;
	
	
	private final static boolean ALLOW_UPDATES = true;
	private final static boolean SHOW_FPS = false;
	
	
	// number of buffers to use for graphics rendering
	private Leaderboard leaderboard;
	
	//private GraphicsEnvironment env;
	//private GraphicsDevice device;
	
	//private BufferStrategy bufferStrategy;
	
	private boolean done = false;
	
	public final static int COLUMNS = 45;
	public final static int ROWS = 13;
	
	//private Random rand;
	
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
	
	private com.lightdatasys.nascar.Race race;
	
	// timing
	private long lastUpdateTime;  // time of last update
	private long lastRenderTime;  // and last render
	private long updateInterval;  // number of milliseconds between each update
	private long renderInterval;  // and each render
	
	private Settings settings;
	
	
	private ArrayList<FullScreenWindow> windows;
	
	
	public LiveUpdater(Leaderboard leaderboard)
	{
		//super("Leaderboard");

		//setSize(800, 600);
		//setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		this.leaderboard = leaderboard;
		
		settings = new Settings();
		
		//setUndecorated(true);
		//setIgnoreRepaint(true);

        //env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        //DisplayMode[] modes = env.getDefaultScreenDevice().getDisplayModes();
        //device = env.getDefaultScreenDevice();
        //GraphicsDevice device2 = env.getScreenDevices()[1];
        //System.out.println(device.getType() + " " + device.getIDstring() + " " + device.toString());
        //System.out.println(device2.getType() + " " + device2.getIDstring() + " " + device2.toString());
    	//System.out.println(((float)device.getDisplayMode().getWidth() / device.getDisplayMode().getHeight()) + " " + device.getDisplayMode().getWidth() + " " + device.getDisplayMode().getHeight() + " " + device.getDisplayMode().getRefreshRate() + " " + device.getDisplayMode().getBitDepth());
        
        //for(int i = 0; i < modes.length; i++)
        //	System.out.println(i + " " + ((float)modes[i].getWidth() / modes[i].getHeight()) + " " + modes[i].getWidth() + " " + modes[i].getHeight() + " " + modes[i].getRefreshRate() + " " + modes[i].getBitDepth());

        /*device.setFullScreenWindow(this);
        AppWindow win2 = new AppWindow()
        {
        	public void paint(Graphics g)
        	{
        		g.setColor(Color.RED);
        		g.fillRect(40, 40, 40, 40);
        	}
        };
        win2.setUndecorated(true);
        //win2.setIgnoreRepaint(true);
        //device2.setFullScreenWindow(win2);
        win2.getGraphics().fillRect(0, 0, 40, 40);
        //device.setDisplayMode(modes[13]);
        */

        //createBufferStrategy(NUM_GFX_BUFFERS);
        //bufferStrategy = getBufferStrategy();
        //System.out.println("Page flipping: " + bufferStrategy.getCapabilities().isPageFlipping());
        
        positionChangeEvents = new ArrayList<PositionChangeEvent>();
        fantasyPositionChangeEvents = new ArrayList<PositionChangeEvent>();

		leaderboard.init(PROPERTY_FILE, ALLOW_UPDATES);
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

		/*DisplayMode dim = device.getDisplayMode();
        width = (int)dim.getWidth();
        height = (int)dim.getHeight();*/
        
		cellWidth = ((width - cellMargin) / COLUMNS) - cellMargin;
		cellHeight = ((height - cellMargin) / ROWS) - cellMargin;
		cellWidth = cellHeight;
		
		//colOffsetPos = 0;
		//colOffsetTime = 0;

		race = com.lightdatasys.nascar.Race.getById(RACE_ID);
		
		windows = new ArrayList<FullScreenWindow>();
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		int i = 0;
		for(GraphicsDevice device : env.getScreenDevices())
		{
			FullScreenWindow window = new FullScreenWindow(this, device);
			windows.add(window);
			
			if(i % 2 == 1)
				window.setPanel(new ClassicScrollerPanel(window));
			
			i++;
		}
		
		/*
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
		*/
		
		AbstractMap<Integer,com.lightdatasys.nascar.Result> results = race.getResults();
		AbstractMap<Integer,FantasyResult> fantasyResults = race.getFantasyResults();

		com.lightdatasys.nascar.Driver.loadAllFromDatabase();
		com.lightdatasys.nascar.Driver[] drivers = com.lightdatasys.nascar.Driver.getDrivers(); 
		
		com.lightdatasys.nascar.fantasy.FantasyPlayer.loadAllFromDatabase();
		com.lightdatasys.nascar.fantasy.FantasyPlayer[] players = com.lightdatasys.nascar.fantasy.FantasyPlayer.getPlayers(); 

		//cellWidth = 75;
		//cellHeight = 75;
		

        //rand = new Random();
	}
	
		
	public void update()
	{
		initTiming();
		
		com.lightdatasys.nascar.Race.Flag flag;
		switch(getSportvisionRace().flag)
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
		if(race.getCurrentLap() != getSportvisionRace().currentLap)
		{
			race.updateFantasyLastLapPositions();
			race.updateLastLapPositions();			
			race.setLapCount(getSportvisionRace().lapCount);
		}
		race.setCurrentLap(getSportvisionRace().currentLap);
		race.setFlag(flag);
		race.setLastFlagChange(getSportvisionRace().flagChangeLap);
		race.setCautionCount(getSportvisionRace().numberOfCautions);
		race.setLeadChangeCount(getSportvisionRace().numberOfLeadChanges);
		race.setLeaderCount(getSportvisionRace().numberOfLeaders);
		
		boolean raceStarted = false;
		if(race.getCurrentLap() > 0)
			raceStarted = true;
		
		Vector<?> drivers = getSportvisionDrivers().getSortedList();
		//*
		int mostLapsLed = 0;
		for(int i = 0; i < drivers.size(); i++)
		{
			String id = (String)drivers.get(i);
			
			Result result = race.getResultByCarNo(id);
			if(result != null)
			{
				com.sportvision.model.Driver d = getSportvisionDrivers().get(id);
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
				//else
				//	result.setFinish(startPos);
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
		
		for(FullScreenWindow window : windows)
			window.update();
	}
	
	
	public void render()
	{	
		for(FullScreenWindow window : windows)
			window.render();
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
		
		//try
		//{
			while(!done)
			{
				long renderDiff = (getLastRenderDelta());
	        	if(renderDiff >= renderInterval)
	        	{
					/*Graphics2D g = null;
					try
					{
						g = (Graphics2D)bufferStrategy.getDrawGraphics();

				        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				            RenderingHints.VALUE_ANTIALIAS_ON);
				        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
			                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				        
			            render(g);
			            
			            if(SHOW_FPS)
			            {
				            g.setColor(new Color(255, 255, 255, 100));
				            g.setFont(g.getFont().deriveFont(150.0f));
				            g.drawString((int)(1000.0f / renderDiff) + " fps", 5, 300);
			            }
			        }
					finally
					{
			            g.dispose();
					}
		            bufferStrategy.show();*/
	        		render();
		         
		            lastRenderTime = System.currentTimeMillis();
	        	}
	        	
				long updateDiff = (getLastUpdateDelta());
				if(updateDiff >= updateInterval)
				{
					update();

		            lastUpdateTime = System.currentTimeMillis();
				}
	        	
	        	long updateSleepTime = updateInterval - (getLastUpdateDelta());
	        	long renderSleepTime = renderInterval - (getLastRenderDelta());
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
		/*}
		finally
		{
			device.setFullScreenWindow(null);
		}*/
	}
	
	
	public long getLastRenderDelta()
	{
		return System.currentTimeMillis() - lastRenderTime;
	}
	
	public long getLastUpdateDelta()
	{
		return System.currentTimeMillis() - lastUpdateTime;
	}

	
	public com.sportvision.model.Drivers getSportvisionDrivers()
	{
		return leaderboard.getDrivers();
	}
	
	public com.sportvision.model.Race getSportvisionRace()
	{
		return leaderboard.getRace();
	}
	
	
	public com.lightdatasys.nascar.Race getRace()
	{
		return race;
	}
	
	public com.lightdatasys.nascar.Driver[] getDrivers()
	{
		return com.lightdatasys.nascar.Driver.getDrivers();
	}
	
	public Settings getSettings()
	{
		return settings;
	}
}
