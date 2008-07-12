package com.lightdatasys.nascar.manager;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.lightdatasys.gui.AppWindow;
import com.lightdatasys.gui.QuitHandler;
import com.lightdatasys.gui.WindowUtil;
import com.lightdatasys.nascar.NASCARData;
import com.lightdatasys.nascar.Race;
import com.lightdatasys.nascar.Season;
import com.lightdatasys.nascar.manager.gui.RaceResultsPanel;

public class Manager extends AppWindow implements QuitHandler 
{
	private static final long serialVersionUID = 1L;
	
	
	private JSplitPane splitRacesData;
		private JPanel raceListPanel;
			private JList raceList;
		private JTabbedPane tabsData; 
			private RaceResultsPanel raceResultsPanel;
	

	public static void main(String[] args)
	{
		new Manager();
	}

	
	public Manager()
	{
		super("Fantasy NASCAR Manager");

		setSize(800, 600);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		initGUI();

		WindowUtil.centerWindow(this);
		setVisible(true);
	}
	
	
	public void initGUI()
	{
		splitRacesData = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		{
			Season season = Season.getById(2);
			Race[] races = season.getRaces();
			raceListPanel = new JPanel(new BorderLayout());
			{
				raceList = new JList(races);
				{				
					raceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					raceList.addListSelectionListener
					(
						new ListSelectionListener()
						{
							public void valueChanged(ListSelectionEvent e)
							{
								raceResultsPanel.setRace((Race)raceList.getSelectedValue());
							}						
						}
					);
				}
				raceListPanel.add(raceList, BorderLayout.CENTER);
				
				JPanel raceListButtonPanel = new JPanel(new GridLayout(1, 0));
				raceListButtonPanel.add(new JButton("+"));
				raceListButtonPanel.add(new JButton("-"));
				raceListPanel.add(raceListButtonPanel, BorderLayout.PAGE_END);
			}
			
			tabsData = new JTabbedPane();
			{
				raceResultsPanel = new RaceResultsPanel();
				{
				}
				tabsData.add(raceResultsPanel);
				
				/*
				tabsData.add(new JPanel()
				{
					public String getName()
					{
						return "Driver Standings";
					}
				}
				);
				*/
			}
		}
		splitRacesData.setLeftComponent(raceListPanel);
		splitRacesData.setRightComponent(tabsData);
		
		raceList.setSelectedIndex(0);
		
		splitRacesData.setEnabled(false);
		splitRacesData.setDividerSize(0);
		splitRacesData.addHierarchyBoundsListener(new HierarchyBoundsAdapter()
			{
				public void ancestorResized(HierarchyEvent e) 
				{
					splitRacesData.setDividerLocation(.2);
				}
			}
		);
		
		getContentPane().add(splitRacesData);
	}
	
	
	public void handleClose()
	{
        System.out.println("Manager Close Handled");
        
        handleQuit();
	}
    
    public void handleQuit()
    {
        System.out.println("Manager Quit Handled");

        // do quit stuff here
        
        dispose();
        
        NASCARData.closeSQLConnection();
        
        System.exit(0);
    } 
}
