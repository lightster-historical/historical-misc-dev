package com.lightdatasys.gui;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

public class OSXWindowListener extends ApplicationAdapter
{
    private Application app;    
    private AppWindow parentFrame;
    
    public OSXWindowListener()
    {
        parentFrame = null;
    }
    
    public void handleAbout(ApplicationEvent event)
    {
        if(parentFrame != null && parentFrame instanceof AboutHandler)
        {
            event.setHandled(true);
            ((AboutHandler)parentFrame).handleAbout();
        }
    }
    
    public void handlePreferences(ApplicationEvent event)
    {
        if(parentFrame != null && parentFrame instanceof PreferencesHandler)
        {
            event.setHandled(true);
            ((PreferencesHandler)parentFrame).handlePreferences();
        }
    }
    
    public void handleQuit(ApplicationEvent event)
    {
        if(parentFrame != null && parentFrame instanceof QuitHandler)
        {
            event.setHandled(true);
            ((QuitHandler)parentFrame).handleQuit();
        }
    }    
    
    public void setListener(AppWindow frame)
    {
        app = new Application();
        app.addApplicationListener(this);
        
        parentFrame = frame;
        
        if(parentFrame != null)
        {
            app.setEnabledAboutMenu(parentFrame instanceof AboutHandler);
            app.setEnabledPreferencesMenu(parentFrame instanceof PreferencesHandler);
        }
    }
}