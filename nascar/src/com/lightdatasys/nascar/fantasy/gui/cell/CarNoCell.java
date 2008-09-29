package com.lightdatasys.nascar.fantasy.gui.cell;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import com.lightdatasys.gui.FontUtility;

public class CarNoCell extends Cell 
{
	private String label;
	
	private Color border;
	private Color background;
	private Color text;
	
	private float opacity;
	
	private Font font;
	
	
	public CarNoCell(GraphicsDevice gd, int w, int h, String carNo, Color text, Color bg, Color border)
	{
		this(gd, w, h, carNo, text, bg, border, 1.0f);
	}
	
	public CarNoCell(GraphicsDevice gd, int w, int h, String carNo, Color text, Color bg, Color border, float opacity)
	{
		super(gd, w, h);
		
		this.label = carNo;
		this.border = border;
		this.background = bg;
		this.text = text;
		this.opacity = opacity;

		int borderWidth = 2;
		
		Graphics2D g = (Graphics2D)getImage().getGraphics();
		
        int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        font = new Font(null, Font.BOLD, (int)((getHeight() - 10) * dpi / 72f));
		font = FontUtility.getScaledFont(getWidth() - 10, getHeight() - 10, label, font, g);
	}
	
	public void render(Graphics2D g)
	{
		int borderWidth = 2;
        
        FontMetrics metrics = g.getFontMetrics(font);
        
		float xOffset = (float)(getWidth() - font.getStringBounds(label, g.getFontRenderContext()).getWidth()) / 2.0f;
		float yOffset = (getHeight() + metrics.getAscent() - metrics.getDescent()) / 2.0f;

        //*
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //*/

        g.setFont(font);
        
	    //g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        
		// border
		g.setColor(border);
		g.setPaint(new GradientPaint(
				getWidth()/2, 0, 
                border,
                getWidth()/2,
                getHeight()*1.1f,
                Color.BLACK));
		g.fillRect(0, 0, getWidth(), getHeight());

		g.setColor(background);
		g.setPaint(new GradientPaint(
				getWidth()/2, getHeight()*.75f, 
                background,
                getWidth()/2,
                getHeight(),
                Color.BLACK));
		g.fillRect(borderWidth, borderWidth, getWidth()-2*borderWidth, getHeight()*2);

		//g.setFont(g.getFont().deriveFont(36.0f).deriveFont(Font.BOLD));
		g.setColor(text);
		g.drawString(label, xOffset, yOffset);
		
		updated = false;
	}
}
