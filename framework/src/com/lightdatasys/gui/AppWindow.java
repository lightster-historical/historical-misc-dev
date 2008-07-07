package com.lightdatasys.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

import javax.swing.JFrame;

public class AppWindow extends JFrame
	implements WindowListener, WindowFocusListener, WindowStateListener
{
	private static final long serialVersionUID = 200806181106L;
	
	public final static boolean MAC_OS_X = System.getProperty("mrj.version") != null;


	public AppWindow()
	{
		this("");
	}

	public AppWindow(String title)
	{
		super(title);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		setOSXListener();

		addWindowListener(this);
		addWindowFocusListener(this);
		addWindowStateListener(this);
	}


	public void windowClosing(WindowEvent event)
	{
		handleClose();
	}

	public void windowClosed(WindowEvent event) {}
	public void windowOpened(WindowEvent event) {}
	public void windowIconified(WindowEvent event) {}
	public void windowDeiconified(WindowEvent event) {}
	public void windowActivated(WindowEvent event) {}
	public void windowDeactivated(WindowEvent event) {}
	public void windowGainedFocus(WindowEvent event)
	{
		setOSXListener();
	}
	public void windowLostFocus(WindowEvent event) {}
	public void windowStateChanged(WindowEvent event) {}


	private void setOSXListener()
	{
		if(MAC_OS_X)
		{
			try
			{
				Class<?> c = Class.forName("com.lightdatasys.gui.OSXWindowListener");
				OSXWindowListener obj = (OSXWindowListener)c.newInstance();

				Class<?>[] argTypes = {AppWindow.class};
				Object[] args = {this};
				c.getMethod("setListener", argTypes).invoke(obj, args);
			}
			catch(Exception exception)
			{
				exception.printStackTrace();
			}
		}
	}


	public void handleClose() {}
}