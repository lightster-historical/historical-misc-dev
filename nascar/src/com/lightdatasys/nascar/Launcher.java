package com.lightdatasys.nascar;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.lightdatasys.gui.AppWindow;
import com.lightdatasys.gui.QuitHandler;
import com.lightdatasys.gui.WindowUtil;
import com.lightdatasys.nascar.manager.Manager;

public class Launcher extends AppWindow implements QuitHandler
{
	private static final long serialVersionUID = 1L;

	
	public static void main(String[] args)
	{
		new Launcher();
	}

	
	public Launcher()
	{
		super("Fantasy NASCAR Launcher Utility");

		setSize(300, 200);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		initGUI();

		WindowUtil.centerWindow(this);
		setVisible(true);
	}
	
	public void initGUI()
	{
		JPanel mainPanel = new JPanel(new GridLayout(0, 1));
		{
			JButton buttonRaceManager = new JButton("Race Manager");
			{
				buttonRaceManager.addActionListener
				(
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e) 
						{
							dispose();
							
							String[] empty = {};
							Manager.main(empty);
						}						
					}
				);
			}
			JButton buttonLeaderboard = new JButton("Leaderboard");
			JButton buttonQuit = new JButton("Quit");

			mainPanel.add(buttonRaceManager);
			mainPanel.add(buttonLeaderboard);
			mainPanel.add(buttonQuit);
		}
		getContentPane().add(mainPanel);
	}	
	

	public void handleClose()
	{
        System.out.println("Launcher Close Handled");
        
        handleQuit();
	}
    
    public void handleQuit()
    {
        System.out.println("Launcher Quit Handled");

        // do quit stuff here
        
        dispose();
        
        System.out.println("Launcher Exiting");
        System.exit(0);
    } 
}
