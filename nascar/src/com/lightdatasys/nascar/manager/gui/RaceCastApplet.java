// ***REMOVED***
// ***REMOVED***
// ***REMOVED***
// ***REMOVED***

package com.lightdatasys.nascar.manager.gui;


import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.RootPaneContainer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import com.sportvision.comm.CCSocket;
import com.sportvision.comm.ControlMessageListener;
import com.sportvision.comm.RaceStatusListener;
import com.sportvision.comm.RaceStatusMessage;
import com.sportvision.comm.RacecastListener;
import com.sportvision.comm.ShoutcastSocketv2;
import com.sportvision.gui.about.AboutPanel;
import com.sportvision.gui.chase.ChasePanel;
import com.sportvision.gui.components.AlertDialog;
import com.sportvision.gui.components.DoorGlassPane;
import com.sportvision.gui.components.RacecastButtonLabel;
import com.sportvision.gui.components.RacecastTabbedPane;
import com.sportvision.gui.dashboard.DualDashboardPanel;
import com.sportvision.gui.header.RacecastHeader;
import com.sportvision.gui.help.HelpPanel;
import com.sportvision.gui.leaderboard.LeaderboardPanel;
import com.sportvision.gui.main.CardPanel;
import com.sportvision.gui.top_ten.TrackTopTenPanel;
import com.sportvision.gui.track_panel.TrackPanel;
import com.sportvision.gui.viewers_choice.ViewersChoicePanel;
import com.sportvision.model.Drivers;
import com.sportvision.model.Race;
import com.sportvision.model.TickerListener;
import com.sportvision.model.TickerMessage;
import com.sportvision.utils.CommonFunctions;
import com.sportvision.utils.GlobalTimer;
import com.sportvision.utils.ImageFactory;
import com.sportvision.utils.MessageParser;
import com.sportvision.utils.PropertiesParser;

// Referenced classes of package com.sportvision.gui.main:
//            CardPanel

public class RaceCastApplet extends JApplet
    implements ControlMessageListener, RaceStatusListener, TickerListener, RacecastListener
{
    class PopupTriggerHandler extends MouseAdapter
    {

        public void mouseClicked(MouseEvent e)
        {
            if(e.isPopupTrigger())
                handlePopupTrigger(e);
        }

        public void mousePressed(MouseEvent e)
        {
            if(e.isPopupTrigger())
                handlePopupTrigger(e);
        }

        public void mouseReleased(MouseEvent e)
        {
            if(e.isPopupTrigger())
                handlePopupTrigger(e);
        }

        private void handlePopupTrigger(MouseEvent e)
        {
            Component source = (Component)e.getSource();
            if(source.equals(c1))
            {
                if(c1.isVisible())
                    menu.show(c1, e.getX(), e.getY());
                else
                if(c2.isVisible())
                    menu.show(c2, e.getX(), e.getY());
            } else
            if(source.equals(c2))
                if(c2.isVisible())
                    menu.show(c2, e.getX(), e.getY());
                else
                if(c1.isVisible())
                    menu.show(c1, e.getX(), e.getY());
        }

        private JPopupMenu menu;
        private Component c1;
        private Component c2;

        public PopupTriggerHandler(JPopupMenu menu, Component c1, Component c2)
        {
            super();
            this.menu = menu;
            this.c1 = c1;
            this.c2 = c2;
        }
    }

    class ButtonManager extends MouseAdapter
    {

        public void mousePressed(MouseEvent e)
        {
            RacecastButtonLabel button = (RacecastButtonLabel)e.getSource();
            if(button.equals(selected))
                button.setHot();
        }

        public void mouseReleased(MouseEvent e)
        {
            selected = (RacecastButtonLabel)e.getSource();
            int selectedNdx = 0;
            for(int i = 0; i < tabButtons.length; i++)
            {
                if(popped[i])
                    continue;
                tabButtons[i].setCold();
                tabButtons[i].setVisible(true);
                tabHotButtons[i].setVisible(false);
                if(selected.equals(tabButtons[i]))
                    selectedNdx = i;
            }

            tabHotButtons[selectedNdx].setVisible(true);
            tabButtons[selectedNdx].setVisible(false);
            tabButtonBackgroundLabel.setBounds(selected.getX() - 23, selected.getY() - 2, tabButtonBackgroundIcon.getIconWidth(), tabButtonBackgroundIcon.getIconHeight());
            cardPanel.show(TAB_NAMES[selectedNdx]);
            setPanelMode(selectedNdx);
            repaint();
        }

        public void mouseReleasedOutside(MouseEvent e)
        {
            RacecastButtonLabel button = (RacecastButtonLabel)e.getSource();
            if(!button.equals(selected))
                button.setCold();
        }

        public void mouseEntered(MouseEvent e)
        {
            RacecastButtonLabel button = (RacecastButtonLabel)e.getSource();
            if(!button.equals(selected))
                button.setOver();
        }

        public void mouseExited(MouseEvent e)
        {
            RacecastButtonLabel button = (RacecastButtonLabel)e.getSource();
            if(!button.equals(selected))
                button.setCold();
        }

        private RacecastButtonLabel selected;

        ButtonManager()
        {
            super();
        }
    }


    public RaceCastApplet()
    {
        propertyFileLocation = "";
        host = null;
        shoutcastHost = new String("ultravox.nascar.com");
        alternateShoutcastHost = null;
        portList = null;
        hilite = false;
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
        flagState = 0;
        container = null;
        rootPaneContainer = null;
        gp = null;
        header = null;
        trackPanel = null;
        leaderboardPanel = null;
        trackTopTenPanel = null;
        viewersChoicePanel = null;
        dualDashboardPanel = null;
        chasePanel = null;
        aboutPanel = null;
        helpPanel = null;
        tabPane = null;
        tabButtonBackgroundLabel = null;
        tabButtonBackgroundIcon = new ImageIcon(ImageFactory.getHeaderTabImage("hot_tab_back.png", this));
        TAB_NAMES = new String[0];
        NUM_TABS = TAB_NAMES.length;
        tabButtons = new RacecastButtonLabel[NUM_TABS];
        tabHotButtons = new RacecastButtonLabel[NUM_TABS];
        popped = new boolean[NUM_TABS];
        selected = 1;
        trackMotionTolerance = 40;
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
        cardPanel = new CardPanel();
        setSize(new Dimension(790, 490));
        setVisible(true);
        rootPaneContainer = this;
    }

    public void init()
    {
        CommonFunctions.dump("Racecast Version Build Version 2.5.14 09/14/2007");
        if(initComponents())
            initCommunications(host);
    }

    public void destroy()
    {
        closeCommunications();
    }

    private boolean initComponents()
    {
        try
        {
            if(getParameter("race_properties") != null)
                propertyFileLocation = getParameter("race_properties");
            else
                propertyFileLocation = "LifeLock400_2008.txt";
            CommonFunctions.dump("Participants File : '" + propertyFileLocation + "'");
            if(getParameter("use_shoutcast") != null)
                if(getParameter("use_shoutcast").equals("true"))
                    useShoutcast = true;
                else
                    useShoutcast = false;
            System.out.println("shoucast mode: " + useShoutcast);
            if(getParameter("race_host") != null)
                host = getParameter("race_host");
            if(getParameter("track_file") != null)
            {
                trackFile = getParameter("track_file");
                CommonFunctions.dump("Track Image : '" + trackFile + "'");
            }
            if(getParameter("hilite") != null)
            {
                if(getParameter("hilite").equals("true"))
                    hilite = true;
                else
                    hilite = false;
                CommonFunctions.dump("HiLighting Active");
            }
            if(getParameter("motion") != null)
            {
                trackMotionTolerance = Integer.parseInt(getParameter("motion"));
                CommonFunctions.dump("motion set to " + trackMotionTolerance);
            }
            if(getParameter("shoutcast_backup") != null)
            {
                alternateShoutcastHost = getParameter("shoutcast_backup");
                CommonFunctions.dump("Back Up Shoutcast Host : " + alternateShoutcastHost);
            }
            if(getParameter("port_list") != null)
            {
                portList = getParameter("port_list");
                CommonFunctions.dump("Port List  : '" + portList + "'");
            }
            if(getParameter("buffer_size_ms") != null)
            {
                bufferSize = Integer.parseInt(getParameter("buffer_size_ms"));
                CommonFunctions.dump("buffer size (ms) : " + bufferSize);
            }
            if(getParameter("shoutcast_sid") != null)
            {
                shoutcastSID = getParameter("shoutcast_sid");
                CommonFunctions.dump("shoutcast SID : " + shoutcastSID);
            }
            if(getParameter("data_timeout_ms") != null)
            {
                dataTimeoutValue = Integer.parseInt(getParameter("data_timeout_ms"));
                CommonFunctions.dump("data time out (ms) : " + dataTimeoutValue);
            }
            if(getParameter("use_chase_tab") != null)
            {
                if(getParameter("use_chase_tab").equals("true"))
                    useChaseTab = true;
                else
                    useChaseTab = false;
                CommonFunctions.dump("use_chase_tab : " + useChaseTab);
            }
            if(getParameter("chase_car_count") != null)
            {
                chaseCarCount = Integer.parseInt(getParameter("chase_car_count"));
                CommonFunctions.dump("chase_car_count : " + chaseCarCount);
            }
            if(getParameter("enable_chase_for_chase_mode") != null)
            {
                if(getParameter("enable_chase_for_chase_mode").equals("true"))
                    enableChaseForChaseMode = true;
                else
                    enableChaseForChaseMode = false;
                CommonFunctions.dump("enable_chase_for_chase_mode : " + enableChaseForChaseMode);
            }
            shoutcastInitialReconnectTime = getLongParameter("shoutcast_initial_reconnect_time", 1000L);
            shoutcastReconnectTimeIncrement = getLongParameter("shoutcast_reconnect_time_increment", 1000L);
            shoutcastMaxReconnectTime = getLongParameter("shoutcast_max_reconnect_time", 5000L);
            shoutcastSocketTimeout = getIntParameter("shoutcast_socket_timeout", 5000);
        }
        catch(Throwable t)
        {
            CommonFunctions.dump("error loading parameters");
            t.printStackTrace();
        }
        if(host == null)
            host = useShoutcast ? "ultravox.nascar.com" : "localhost";
        CommonFunctions.dump("host: " + host);
        String docBase = getDocumentBase().getHost().toString();
        ImageFactory.URLImagePath = getCodeBase();
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
        container = getContentPane();
        container.setLayout(null);
        gp = new DoorGlassPane(race);
        header = new RacecastHeader(race, drivers);
        header.setBounds(0, 0, 790, 130);
        if(useChaseTab)
            TAB_NAMES = (new String[] {
                "leaderboard", "track_top10", "viewers_choice", "dashboard", "chase"
            });
        else
            TAB_NAMES = (new String[] {
                "leaderboard", "track_top10", "viewers_choice", "dashboard"
            });
        NUM_TABS = TAB_NAMES.length;
        tabButtons = new RacecastButtonLabel[NUM_TABS];
        tabHotButtons = new RacecastButtonLabel[NUM_TABS];
        popped = new boolean[NUM_TABS];
        for(int i = 0; i < NUM_TABS; i++)
        {
            tabButtons[i] = new RacecastButtonLabel();
            tabButtons[i].setHotIcon(new ImageIcon(ImageFactory.getHeaderTabImage(TAB_NAMES[i] + "_hot.gif", this)));
            tabButtons[i].setColdIcon(new ImageIcon(ImageFactory.getHeaderTabImage(TAB_NAMES[i] + "_cold.gif", this)));
            tabButtons[i].setOverIcon(new ImageIcon(ImageFactory.getHeaderTabImage(TAB_NAMES[i] + "_over.gif", this)));
        }

        cardPanel.setBounds(0, 162, 790, 322);
        cardPanel.setOpaque(false);
        cardPanel.add(TAB_NAMES[0], leaderboardPanel = new LeaderboardPanel(drivers, hilite));
        cardPanel.add(TAB_NAMES[1], trackTopTenPanel = new TrackTopTenPanel(drivers, race, hilite));
        cardPanel.add(TAB_NAMES[2], viewersChoicePanel = new ViewersChoicePanel(race, drivers, hilite));
        cardPanel.add(TAB_NAMES[3], dualDashboardPanel = new DualDashboardPanel(race, drivers));
        buttonManager = new ButtonManager();
        tabButtonBackgroundLabel = new JLabel(tabButtonBackgroundIcon);
        int tempX = 20;
        for(int i = 0; i < tabButtons.length; i++)
        {
            tabButtons[i].setBounds(tempX, 136, tabButtons[i].getColdIcon().getIconWidth(), tabButtons[i].getColdIcon().getIconHeight());
            tabButtons[i].setCold();
            tabButtons[i].addMouseListener(buttonManager);
            tempX = tempX + tabButtons[i].getColdIcon().getIconWidth() + 8;
        }

        cardPanel.show(TAB_NAMES[selected]);
        tempX = 20;
        for(int i = 0; i < NUM_TABS; i++)
        {
            tabHotButtons[i] = new RacecastButtonLabel();
            tabHotButtons[i].setHotIcon(tabButtons[i].getHotIcon());
            tabHotButtons[i].setHot();
            tabHotButtons[i].setBounds(tempX, 136, tabHotButtons[i].getHotIcon().getIconWidth(), tabHotButtons[i].getHotIcon().getIconHeight());
            tabHotButtons[i].setVisible(false);
            container.add(tabHotButtons[i]);
            tempX = tempX + tabHotButtons[i].getHotIcon().getIconWidth() + 8;
        }

        tabButtons[selected].setHot();
        tabButtonBackgroundLabel.setBounds(tabButtons[selected].getX() - 23, tabButtons[selected].getY() - 2, tabButtonBackgroundIcon.getIconWidth(), tabButtonBackgroundIcon.getIconHeight());
        tabHotButtons[selected].setVisible(true);
        container.add(tabButtonBackgroundLabel);
        for(int i = 0; i < NUM_TABS; i++)
            container.add(tabButtons[i]);

        container.add(header);
        container.add(cardPanel);
        container.setBackground(Color.black);
        messageParser.addControlMessageListener(this);
        messageParser.addRacecastDiagnosticsListener(viewersChoicePanel.trackPanel);
        messageParser.addTickerListener(this);
        setupDoorGlassPane();
        if(useChaseTab)
            cardPanel.add(TAB_NAMES[4], chasePanel = new ChasePanel(race, drivers, hilite, enableChaseForChaseMode));
        return true;
    }

    public void removeView(Component view)
    {
        int ndx = -1;
        if(leaderboardPanel.equals(view))
            ndx = 0;
        else
        if(trackTopTenPanel.equals(view))
            ndx = 1;
        else
        if(viewersChoicePanel.equals(view))
            ndx = 2;
        else
        if(dualDashboardPanel.equals(view))
            ndx = 3;
        else
        if(chasePanel.equals(view))
            ndx = 4;
        if(ndx >= 0)
        {
            cardPanel.remove(view);
            popped[ndx] = true;
            tabButtons[ndx].setVisible(false);
            tabHotButtons[ndx].setVisible(false);
            for(int i = 0; i < popped.length; i++)
            {
                if(!popped[i])
                {
                    tabButtonBackgroundLabel.setBounds(tabButtons[i].getX() - 23, tabButtons[i].getY() - 2, tabButtonBackgroundIcon.getIconWidth(), tabButtonBackgroundIcon.getIconHeight());
                    tabHotButtons[i].setVisible(true);
                    tabButtons[i].setVisible(false);
                    cardPanel.show(TAB_NAMES[i]);
                    setPanelMode(i);
                    break;
                }
                if(i == popped.length - 1)
                    tabButtonBackgroundLabel.setVisible(false);
            }

            cardPanel.updateUI();
        }
    }

    public void addView(Component view)
    {
        int ndx = -1;
        if(leaderboardPanel.equals(view))
            ndx = 0;
        else
        if(trackTopTenPanel.equals(view))
            ndx = 1;
        else
        if(viewersChoicePanel.equals(view))
            ndx = 2;
        else
        if(dualDashboardPanel.equals(view))
            ndx = 3;
        else
        if(chasePanel.equals(view))
            ndx = 4;
        if(ndx >= 0)
        {
            for(int i = 0; i < popped.length; i++)
                if(!popped[i])
                {
                    tabHotButtons[i].setVisible(false);
                    tabButtons[i].setVisible(true);
                }

            popped[ndx] = false;
            tabButtonBackgroundLabel.setBounds(tabButtons[ndx].getX() - 23, tabButtons[ndx].getY() - 2, tabButtonBackgroundIcon.getIconWidth(), tabButtonBackgroundIcon.getIconHeight());
            tabHotButtons[ndx].setVisible(true);
            tabButtons[ndx].setVisible(false);
            setPanelMode(ndx);
            cardPanel.add(TAB_NAMES[ndx], view);
            cardPanel.show(TAB_NAMES[ndx]);
            cardPanel.updateUI();
        }
    }

    public void setupDoorGlassPane()
    {
        JPanel glass = (JPanel)rootPaneContainer.getGlassPane();
        glass.setLayout(null);
        glass.setVisible(true);
        glass.setBounds(0, 0, 790, 490);
        gp.setBounds(0, 0, 790, 490);
        //gp.setRaceCastApplet(this);
        glass.add(gp);
    }

    public void setDoorsOpened(boolean doorsOpened)
    {
        if(doorsOpened)
        {
            JPanel glass = (JPanel)rootPaneContainer.getGlassPane();
            glass.remove(gp);
        }
        this.doorsOpened = doorsOpened;
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

    public void setPanelMode(int mode)
    {
        if(flagState != 0 && flagState != 5)
            switch(mode)
            {
            default:
                break;

            case 0: // '\0'
                leaderboardPanel.setDoUpdate(true);
                trackTopTenPanel.setDoUpdate(false);
                viewersChoicePanel.setDoUpdate(false);
                dualDashboardPanel.setDoUpdate(false);
                if(useChaseTab)
                    chasePanel.setDoUpdate(false);
                break;

            case 1: // '\001'
                leaderboardPanel.setDoUpdate(false);
                trackTopTenPanel.setDoUpdate(true);
                viewersChoicePanel.setDoUpdate(false);
                dualDashboardPanel.setDoUpdate(false);
                if(useChaseTab)
                    chasePanel.setDoUpdate(false);
                break;

            case 2: // '\002'
                leaderboardPanel.setDoUpdate(false);
                trackTopTenPanel.setDoUpdate(false);
                viewersChoicePanel.setDoUpdate(true);
                dualDashboardPanel.setDoUpdate(false);
                if(useChaseTab)
                    chasePanel.setDoUpdate(false);
                break;

            case 3: // '\003'
                leaderboardPanel.setDoUpdate(false);
                trackTopTenPanel.setDoUpdate(false);
                viewersChoicePanel.setDoUpdate(false);
                dualDashboardPanel.setDoUpdate(true);
                if(useChaseTab)
                    chasePanel.setDoUpdate(false);
                break;

            case 4: // '\004'
                leaderboardPanel.setDoUpdate(false);
                trackTopTenPanel.setDoUpdate(false);
                viewersChoicePanel.setDoUpdate(false);
                dualDashboardPanel.setDoUpdate(false);
                if(useChaseTab)
                    chasePanel.setDoUpdate(true);
                break;
            }
    }

    public void tickerMessageReceived(TickerMessage m, int c)
    {
        AlertDialog alert = new AlertDialog(m);
        alert.setBounds(getX() + getWidth() / 3, getY() + getHeight() / 3, 394, 203);
        alert.show();
    }

    public void messageReceived(byte message[], int messageType)
    {
        char type = (char)message[6];
        if(type == 'S' && doorsOpened)
        {
            trackTopTenPanel.setDoUpdate(true);
            viewersChoicePanel.setDoUpdate(true);
            dualDashboardPanel.setDoUpdate(true);
            if(useChaseTab)
                chasePanel.setDoUpdate(true);
            setPanelMode(selected);
        }
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
                ccSocket.addRacecastListener(gp);
                ccSocket.addRacecastListener(this);
                ccSocket.start();
                GlobalTimer.start();
            } else
            if(useShoutcast && shoutcastSocket == null)
            {
                CommonFunctions.dump("PitCommand Launching in Shoutcast mode");
                shoutcastSocket = new ShoutcastSocketv2(host, alternateShoutcastHost, portList, shoutcastSID, dataTimeoutValue);
                shoutcastSocket.addListener(messageParser);
                shoutcastSocket.addListener(gp);
                shoutcastSocket.addListener(this);
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
        messageParser.addRacecastDiagnosticsListener(viewersChoicePanel.trackPanel);
        messageParser.addTickerListener(this);
        header.reinit(race, drivers);
        leaderboardPanel.reinit(drivers);
        trackTopTenPanel.reinit(race, drivers);
        viewersChoicePanel.reinit(race, drivers);
        dualDashboardPanel.reinit(race, drivers);
        if(useChaseTab)
            chasePanel.reinit(race, drivers);
        restartCommunications();
    }

    private String getParameter(String name, String defaultValue)
    {
        String value = getParameter(name);
        if(value == null)
            value = defaultValue;
        return value;
    }

    private int getIntParameter(String name, int defaultValue)
    {
        int value = defaultValue;
        String stringValue = getParameter(name);
        if(stringValue != null)
            value = Integer.parseInt(stringValue);
        return value;
    }

    private long getLongParameter(String name, long defaultValue)
    {
        long value = defaultValue;
        String stringValue = getParameter(name);
        if(stringValue != null)
            value = Long.parseLong(stringValue);
        return value;
    }

    private boolean getBooleanParameter(String name, boolean defaultValue)
    {
        boolean value = defaultValue;
        String stringValue = getParameter(name);
        if(stringValue != null)
            value = Boolean.valueOf(stringValue).booleanValue();
        return value;
    }

    public static final int WIDTH = 790;
    public static final int HEIGHT = 490;
    public static final String VERSION = "Build Version 2.5.14 09/14/2007";
    public static final String DEFAULT_SHOUTCAST_HOST = "ultravox.nascar.com";
    private static final String PROPERTY_FILE_PATH = "Sportvision/Racecast/Assets/Config/";
    private static final String DEFAULT_CC_HOST = "localhost";
    private static final int HEARTBEAT_WAIT = 20000;
    public String propertyFileLocation;
    private String host;
    private String shoutcastHost;
    private String alternateShoutcastHost;
    private String portList;
    private boolean hilite;
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
    private int flagState;
    private Container container;
    private RootPaneContainer rootPaneContainer;
    private DoorGlassPane gp;
    private RacecastHeader header;
    private TrackPanel trackPanel;
    private LeaderboardPanel leaderboardPanel;
    private TrackTopTenPanel trackTopTenPanel;
    private ViewersChoicePanel viewersChoicePanel;
    private DualDashboardPanel dualDashboardPanel;
    private ChasePanel chasePanel;
    private AboutPanel aboutPanel;
    private HelpPanel helpPanel;
    private RacecastTabbedPane tabPane;
    private JLabel tabButtonBackgroundLabel;
    private ImageIcon tabButtonBackgroundIcon;
    private String TAB_NAMES[];
    private int NUM_TABS;
    private RacecastButtonLabel tabButtons[];
    private RacecastButtonLabel tabHotButtons[];
    private boolean popped[];
    private int selected;
    private int trackMotionTolerance;
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
    private ButtonManager buttonManager;
    private CardPanel cardPanel;







}
