package com.lightdatasys.gui;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.Window;

public class WindowUtil
{   
    public static void centerWindow(Window window)
    {
        int windowWidth, windowHeight;
        windowWidth = (int)window.getSize().getWidth();
        windowHeight = (int)window.getSize().getHeight();
        
        int screenWidth, screenHeight;
        screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        
        window.setLocation((screenWidth - windowWidth) / 2
            , (screenHeight - windowHeight) / 2);
    }
    
    public static void centerWindow(Window window, Window parent)
    {
        int windowWidth, windowHeight;
        windowWidth = (int)window.getSize().getWidth();
        windowHeight = (int)window.getSize().getHeight();
        
        int screenWidth, screenHeight;
        screenWidth = parent.getWidth();
        screenHeight = parent.getHeight();
        
        int screenX, screenY;
        screenX = parent.getX();
        screenY = parent.getY();
        
        window.setLocation(screenX + (screenWidth - windowWidth) / 2
                , screenY + (screenHeight - windowHeight) / 2);
    }
    
    
    public static Window getParentWindow(Component component)
    {
        while(component != null && !(component instanceof Window))
        {
            component = component.getParent();
        }
        
        if(component instanceof Window)
        {
            return (Window)component;
        }
        else
        {
            return null;
        }
    }
}