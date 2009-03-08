package com.lightdatasys.nascar.live.gui;

import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import com.lightdatasys.gui.AppWindow;
import com.lightdatasys.gui.QuitHandler;
import com.lightdatasys.nascar.Driver;
import com.lightdatasys.nascar.Race;
import com.lightdatasys.nascar.live.gui.panel.LivePanel;
import com.lightdatasys.nascar.live.setting.Setting;
import com.lightdatasys.nascar.live.setting.Settings;
import com.lightdatasys.nascar.live.setting.SettingsMode;

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

    JPopupMenu popup;
    SettingMenuItem menuItem;
	
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
        
        //...where the GUI is constructed:
        //Create the popup menu.
        popup = new JPopupMenu();
        Settings settings = getLiveUpdater().getSettings();
        ArrayList<Setting<?>> settingsList = settings.getSettingsList();
        Collections.sort(settingsList, 
    		new Comparator<Setting<?>>()
			{
				public int compare(Setting<?> o1, Setting<?> o2)
				{
					return o1.toString().compareTo(o2.toString());
				}
			}
        );
        JMenu topMenu;
        
        topMenu = new JMenu("Individual Settings");
        for(Setting<?> setting : settingsList)
        {
            menuItem = new SettingMenuItem(setting);
            //menuItem.add(new SettingValue)
            //menuItem.addActionListener(this);
            topMenu.add(menuItem);
        }
        popup.add(topMenu);
    
        this.addMouseListener(new PopupListener());
	}
	
	protected class SettingsModeItem extends JRadioButtonMenuItem implements ActionListener
	{
		protected SettingsMode settingsMode;
		protected Settings settings;
		
		public SettingsModeItem(SettingsMode settingsMode, Settings settings)
		{
			super(settingsMode.toString());
			
			this.settingsMode = settingsMode;
			this.settings = settings;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			settingsMode.setSettings(settings);
		}
	}
	
	protected class SettingMenuItem extends JMenu implements ActionListener
	{
		protected Setting<?> setting;
		protected ButtonGroup group;
		
		public SettingMenuItem(Setting<?> setting)
		{
			super(setting.getTitle());
			
			this.setting = setting;
			
			group = new ButtonGroup();
			Setting.Option<?>[] options = setting.getValueSet();
	        Arrays.sort(options, 
        		new Comparator<Setting.Option<?>>()
    			{
    				public int compare(Setting.Option<?> o1, Setting.Option<?> o2)
    				{
    					return o1.value.toString().compareTo(o2.value.toString());
    				}
    			}
            );
			for(Setting.Option<?> option : options)
			{
				SettingValueMenuItem item = new SettingValueMenuItem(setting, option);
				item.addActionListener(this);
				this.add(item);
				group.add(item);
			}
		}

		public void actionPerformed(ActionEvent e)
		{
			SettingValueMenuItem item = (SettingValueMenuItem)e.getSource();
			Setting.Option<?> option = item.getOption();
			setting.setValueUsingKey(option.key);
		}
	}
	
	protected class SettingValueMenuItem extends JRadioButtonMenuItem
	{
		protected Setting<?> setting;
		protected Setting.Option<?> option;
		
		public SettingValueMenuItem(Setting<?> setting, Setting.Option<?> option)
		{
			super(option.value.toString());
			
			this.setting = setting;
			this.option = option;
		}
		
		public Setting.Option<?> getOption()
		{
			return option;
		}
		
		public boolean isSelected()
		{
			if(setting == null || option == null)
				return false; 
			
			return option.value.equals(setting.getValue());
		}
	}
	
	
    class PopupListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) 
        {
            if(e.isPopupTrigger()) 
            {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
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
