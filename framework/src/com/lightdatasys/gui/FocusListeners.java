package com.lightdatasys.gui;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.text.JTextComponent;

public class FocusListeners
{    
    public static class SelectAll extends FocusAdapter
    {
        public void focusGained(FocusEvent event)
        {
            if(event.getSource() instanceof JTextComponent)
            {
                JTextComponent textComp = (JTextComponent)event.getSource();
                textComp.selectAll();
            }
        }
    }
}
