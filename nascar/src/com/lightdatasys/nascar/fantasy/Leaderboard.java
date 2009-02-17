package com.lightdatasys.nascar.fantasy;

//***REMOVED***
//***REMOVED***
//***REMOVED***
//***REMOVED***



import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.net.Socket;
import java.net.URL;

import javax.swing.JPanel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import com.lightdatasys.nascar.live.gui.LiveUpdater;
import com.sportvision.comm.CCSocket;
import com.sportvision.comm.ControlMessageListener;
import com.sportvision.comm.RaceStatusListener;
import com.sportvision.comm.RaceStatusMessage;
import com.sportvision.comm.ShoutcastSocketv2;
import com.sportvision.gui.chase.ChasePanel;
import com.sportvision.gui.components.AlertDialog;
import com.sportvision.gui.header.RacecastHeader;
import com.sportvision.model.Drivers;
import com.sportvision.model.Race;
import com.sportvision.model.TickerListener;
import com.sportvision.model.TickerMessage;
import com.sportvision.utils.CommonFunctions;
import com.sportvision.utils.GlobalTimer;
import com.sportvision.utils.ImageFactory;
import com.sportvision.utils.MessageParser;
import com.sportvision.utils.PropertiesParser;

//Referenced classes of package com.sportvision.gui.main:
//         CardPanel

public class Leaderboard extends JPanel
	implements ControlMessageListener, RaceStatusListener, TickerListener//, RacecastListener
{
	private static LiveUpdater window;
	private static Leaderboard leaderboard;

	
	public static void main(String args[])
	{
		leaderboard = new Leaderboard();
		window = new LiveUpdater(leaderboard);
		System.out.println("window created");
		Thread guiThread = new Thread(window);
		guiThread.start();
		System.out.println("guiThread created");
	}
	
	
	public Drivers getDrivers()
	{
		return drivers;
	}
	
	public Race getRace()
	{
		return race;
	}
	


 public Leaderboard()
 {
     propertyFileLocation = "";
     host = null;
     alternateShoutcastHost = null;
     portList = null;
     trackFile = null;
     ccSocket = null;
     shoutcastSocket = null;
     messageParser = null;
     useShoutcast = true;
     shoutcastInitialReconnectTime = 1000L;
     shoutcastReconnectTimeIncrement = 1000L;
     shoutcastMaxReconnectTime = 5000L;
     shoutcastSocketTimeout = 5000;
     propParser = null;
     drivers = null;
     race = null;
     header = null;
     chasePanel = null;
     bufferSize = 6000;
     shoutcastSID = "42";
     dataTimeoutValue = 20000;
     doorsOpened = false;
     socketOut = null;
     out = null;
     bufferedReader = null;
     reader = null;
     messageChars = new char[4096];
     useChaseTab = false;
     enableChaseForChaseMode = false;
     chaseCarCount = 10;
     setSize(new Dimension(790, 490));
     setVisible(true);
 }

 public void init(String propFile, boolean start)
 {
     CommonFunctions.dump("Racecast Version Build Version 2.5.14 09/14/2007");
     if(initComponents(propFile) && start)
         initCommunications(host);
 }

 public void destroy()
 {
     closeCommunications();
 }

 private boolean initComponents(String propFile)
 {
     try
     {
         propertyFileLocation = propFile;
         CommonFunctions.dump("Participants File : '" + propertyFileLocation + "'");
         useShoutcast = true;
         System.out.println("shoucast mode: " + useShoutcast);

         shoutcastInitialReconnectTime = 1000L;
         shoutcastReconnectTimeIncrement = 1000L;
         shoutcastMaxReconnectTime = 5000L;
         shoutcastSocketTimeout = 5000;
     }
     catch(Throwable t)
     {
         CommonFunctions.dump("error loading parameters");
         t.printStackTrace();
     }
     if(host == null)
         host = useShoutcast ? "ultravox.nascar.com" : "localhost";
     CommonFunctions.dump("host: " + host);
     String docBase = "";
     //System.out.println(docBase);
     try
     {
    	 ImageFactory.URLImagePath = new URL("file:/Users/lightster/Documents/Workspaces/Dev/nascar/bin/");
     }
     catch(Exception e)
     {
    	 e.printStackTrace();
     }
     //System.out.println(getCodeBase());
     propParser = new PropertiesParser(propertyFileLocation, trackFile);
     UIDefaults uid = UIManager.getLookAndFeelDefaults();
     uid.put("ToolTip.foreground", ImageFactory.YELLOW_FLAG_BACKGROUND_COLOR);
     uid.put("ToolTip.background", ImageFactory.APPLICATION_BACKGROUND_COLOR);
     uid.put("ToolTip.border", new LineBorder(Color.black, 1));
     Color whitish = new Color(200, 200, 200);
     Color whiter = new Color(230, 230, 230);
     uid.put("MenuItem.foreground", whiter);
     uid.put("MenuItem.background", ImageFactory.APPLICATION_BACKGROUND_COLOR);
     uid.put("MenuItem.selectionForeground", whiter);
     uid.put("MenuItem.selectionBackground", ImageFactory.APPLICATION_BACKGROUND_COLOR);
     uid.put("PopupMenu.border", new LineBorder(whitish, 2));
     drivers = propParser.getDrivers();
     drivers.setUseChaseTab(useChaseTab);
     drivers.setChaseCarCount(chaseCarCount);
     drivers.setEnableChaseForChaseMode(enableChaseForChaseMode);
     race = propParser.getRace();
     messageParser = new MessageParser(bufferSize);
     setUpListeners();
     
     messageParser.addControlMessageListener(this);
     messageParser.addTickerListener(this);
     
     return true;
 }


 public void stop()
 {
     CommonFunctions.dump("RaceCastApplet.stop()");
     cleanUp();
 }

 public void start()
 {
     CommonFunctions.dump("RaceCastApplet.start()");
     GlobalTimer.start();
 }

 public void cleanUp()
 {
     CommonFunctions.dump(" *********** CLEANING UP **");
     GlobalTimer.reset();
     if(useShoutcast)
     {
         if(shoutcastSocket != null)
         {
             shoutcastSocket.halt();
             shoutcastSocket.disconnect();
             shoutcastSocket = null;
         }
     } else
     if(ccSocket != null)
     {
         ccSocket.halt();
         ccSocket = null;
     }
 }

 public void tickerMessageReceived(TickerMessage m, int c)
 {
     AlertDialog alert = new AlertDialog(m);
     alert.setBounds(200 / 3, 200 / 3, 394, 203);
     alert.setVisible(true);
 }
 

 public boolean initCommunications(String host)
 {
     CommonFunctions.dump("RaceCastApplet :: initCommunications() :: host = " + host);
     try
     {
         if(!useShoutcast && ccSocket == null)
         {
             CommonFunctions.dump("starting in CC mode");
             ccSocket = new CCSocket(host);
             ccSocket.addRacecastListener(messageParser);
             //ccSocket.addRacecastListener(gp);
             //ccSocket.addRacecastListener(this);
             ccSocket.start();
             GlobalTimer.start();
         } else
         if(useShoutcast && shoutcastSocket == null)
         {
             CommonFunctions.dump("PitCommand Launching in Shoutcast mode");
             shoutcastSocket = new ShoutcastSocketv2(host, alternateShoutcastHost, portList, shoutcastSID, dataTimeoutValue);
             shoutcastSocket.addListener(messageParser);
             //shoutcastSocket.addListener(gp);
             //shoutcastSocket.addListener(this);
             shoutcastSocket.setSocketTimeout(shoutcastSocketTimeout);
             shoutcastSocket.setInitialReconnectTime(shoutcastInitialReconnectTime);
             shoutcastSocket.setReconnectTimeIncrement(shoutcastReconnectTimeIncrement);
             shoutcastSocket.setMaxReconnectTime(shoutcastMaxReconnectTime);
             CommonFunctions.dump("starting connect");
             shoutcastSocket.start();
             GlobalTimer.start();
         }
     }
     catch(Throwable t)
     {
         t.printStackTrace();
         System.err.println("error initializing comm");
     }
     return true;
 }

 public boolean restartCommunications()
 {
     CommonFunctions.dump("RaceCastApplet :: restartCommunications()");
     try
     {
         if(!useShoutcast)
         {
             CommonFunctions.dump("restarting in CC mode");
             ccSocket.reconnect();
             GlobalTimer.start();
         } else
         if(useShoutcast)
         {
             CommonFunctions.dump("PitCommand restarting in Shoutcast mode");
             CommonFunctions.dump("starting connect");
             shoutcastSocket.reconnect();
             GlobalTimer.start();
         }
     }
     catch(Throwable t)
     {
         t.printStackTrace();
         System.err.println("error initializing comm");
     }
     return true;
 }

 public void closeCommunications()
 {
     CommonFunctions.dump("RaceCastApplet :: closeCommunications()");
     if(shoutcastSocket != null)
     {
         shoutcastSocket.halt();
         shoutcastSocket = null;
     }
     if(ccSocket != null)
     {
         ccSocket.halt();
         ccSocket.disconnect();
         ccSocket = null;
     }
 }

 public void haltCommunications()
 {
     CommonFunctions.dump("RaceCastApplet :: haltCommunications()");
     if(shoutcastSocket != null)
         shoutcastSocket.halt();
     if(ccSocket != null)
         ccSocket.halt();
 }

 public void controlMessageReceived(char c, int i)
 {
 }

 public void raceStatusMessageReceived(RaceStatusMessage racestatusmessage, int i)
 {
 }

 private void setUpListeners()
 {
     messageParser.clearRaceAndLapListeners();
     messageParser.addRaceStatusListener(drivers);
     messageParser.addRaceStatusListener(race);
     messageParser.addLapInfoListener(drivers);
     messageParser.addLapInfoListener(race);
     if(useChaseTab)
         messageParser.addCupChaseListener(drivers);
     messageParser.addRaceStatusListener(this);
     messageParser.addControlMessageListener(this);
 }

 public synchronized boolean readFile(String fileName)
 {
     try
     {
         File file = new File(fileName);
         int fileLen = (int)file.length();
         bufferedReader = new BufferedReader(new FileReader(fileName));
         messageChars = new char[fileLen];
         bufferedReader.read(messageChars);
         bufferedReader.close();
         return true;
     }
     catch(Throwable t)
     {
         return false;
     }
 }

 public void resetMessageReceived(String propertiesFileInAString)
 {
     propParser.reinitialize(propertiesFileInAString);
     CommonFunctions.dump("*** Racecast :: recieved INIT signal");
     haltCommunications();
     drivers = propParser.getDrivers();
     int sinceLap = race.flagChangeLap;
     race = propParser.getRace();
     race.flagChangeLap = sinceLap;
     race.dump();
     setUpListeners();
     messageParser.addControlMessageListener(this);
     //messageParser.addRacecastDiagnosticsListener(viewersChoicePanel.trackPanel);
     messageParser.addTickerListener(this);
     header.reinit(race, drivers);
     if(useChaseTab)
         chasePanel.reinit(race, drivers);
     restartCommunications();
 }


 public String propertyFileLocation;
 private String host;
 private String alternateShoutcastHost;
 private String portList;
 private String trackFile;
 private CCSocket ccSocket;
 private ShoutcastSocketv2 shoutcastSocket;
 private MessageParser messageParser;
 private boolean useShoutcast;
 private long shoutcastInitialReconnectTime;
 private long shoutcastReconnectTimeIncrement;
 private long shoutcastMaxReconnectTime;
 private int shoutcastSocketTimeout;
 private PropertiesParser propParser;
 private Drivers drivers;
 private Race race;
 //private int flagState;
 private RacecastHeader header;
 /*private LeaderboardPanel leaderboardPanel;
 private TrackTopTenPanel trackTopTenPanel;
 private ViewersChoicePanel viewersChoicePanel;
 private DualDashboardPanel dualDashboardPanel;*/
 private ChasePanel chasePanel;
 //private int selected;
 private int bufferSize;
 private String shoutcastSID;
 private int dataTimeoutValue;
 boolean doorsOpened;
 public Socket socketOut;
 public DataOutputStream out;
 public BufferedReader bufferedReader;
 public Reader reader;
 public char messageChars[];
 private boolean useChaseTab;
 private boolean enableChaseForChaseMode;
 private int chaseCarCount;







}
