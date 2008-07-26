package com.lightdatasys.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

public class FontUtility 
{
	public static Font getScaledFont(int w, int h, String label, Font font, Graphics2D g)
	{
		float size, fontW, fontH;
		FontMetrics metrics;
		
        int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        font = new Font(null, Font.BOLD, (int)((h - 10) * dpi / 72f));
        FontRenderContext fontRender = g.getFontRenderContext();

        metrics = g.getFontMetrics(font);
        
        fontW = (float)font.getStringBounds(label, fontRender).getWidth();
        fontH = (float)(h - metrics.getLeading());
        
        if(fontW > w)
        {
        	size = font.getSize2D() * w / fontW;
        	font = font.deriveFont(size);
            metrics = g.getFontMetrics(font);
            
        	fontW = (float)font.getStringBounds(label, fontRender).getWidth();
            fontH = (float)(h - metrics.getLeading());
            
    	    //h2 = (int)font.getStringBounds(label, fontRender).getHeight();
        }
        
        if(fontH > h)
        {
        	size = font.getSize2D() * h / fontH;
        	font = font.deriveFont(size);
            metrics = g.getFontMetrics(font);
    	    //h2 = (int)font.getStringBounds(label, fontRender).getHeight();
        }
        
    	while(fontW > w || fontH > h)
    	{
        	fontW = (float)font.getStringBounds(label, fontRender).getWidth();
            fontH = (float)(h - metrics.getLeading());
            
    		size = font.getSize2D();
    		font = font.deriveFont(size * .95f);
            metrics = g.getFontMetrics(font);
    	}
    	
    	return font;
	}
}