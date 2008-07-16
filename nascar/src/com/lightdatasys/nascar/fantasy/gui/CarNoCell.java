package com.lightdatasys.nascar.fantasy.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;

public class CarNoCell extends Cell 
{
	private String carNo;
	
	private Color border;
	private Color background;
	private Color text;
	
	private Font font;
	
	private int w2;
	
	
	public CarNoCell(int w, int h, String carNo, Color text, Color bg, Color border)
	{
		super(w, h);
		
		this.carNo = carNo;
		this.border = border;
		this.background = bg;
		this.text = text;
		

		int borderWidth = 2;
		
		Graphics2D g = (Graphics2D)getImage().getGraphics();
        
        int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        font = new Font(null, Font.BOLD, (int)((getHeight() - 10) * dpi / 72f));
        FontRenderContext fontRender = g.getFontRenderContext();

	    w2 = (int)(font.getStringBounds(String.format("%s", carNo), fontRender).getWidth());
        if(w2 > getWidth()-2*5)
        {
            font = new Font(null, Font.BOLD, (int)((2f / carNo.length()) * (getHeight()-2*5) * dpi / 72f));
            w2 = (int)(font.getStringBounds(String.format("%s", carNo), fontRender).getWidth());
        }
        
        int h2 = (int)font.getStringBounds(String.format("%s", carNo), fontRender).getHeight();
		int digitWidth = (int)font.getStringBounds("9", fontRender).getWidth();
	}
	
	
	public void render(Graphics2D g)
	{
		int borderWidth = 2;
        
        FontMetrics metrics = g.getFontMetrics(font);

		int xOffset = 5;
		int yOffset = (getHeight() + metrics.getAscent() - metrics.getDescent()) / 2;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        g.setFont(font);
        
		// border
		g.setColor(border);
		g.fillRect(0, 0, getWidth(), getHeight());

		g.setColor(background);
		g.fillRect(borderWidth, borderWidth, getWidth()-2*borderWidth, getHeight()-2*borderWidth);

		//g.setFont(g.getFont().deriveFont(36.0f).deriveFont(Font.BOLD));
		g.setColor(text);
		g.drawString(carNo, (getWidth() - w2)/2, yOffset);
	}
}
