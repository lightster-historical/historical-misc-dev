package com.lightdatasys.nascar.fantasy.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;

import com.lightdatasys.nascar.Result;

public class ResultCell extends Cell 
{
	public enum Mode {POSITION, LAPS_LED, LEADER_INTERVAL, LOCAL_INTERVAL, SEASON_POINTS, RACE_POINTS};
	
	
	private Result result;
	
	private static Mode mode;
	
	private Color border;
	private Color background;
	private Color text;
	
	private Font font;
	
	private int w2;
	
	
	public ResultCell(int w, int h, Result result, Color text, Color bg, Color border)
	{
		super(w, h);
		
		this.result = result;
		this.border = border;
		this.background = bg;
		this.text = text;
		
		setMode(Mode.POSITION);
	}
	
	
	private String getValue()
	{
		if(mode == Mode.LAPS_LED)
		{
			
		}
		else
		{
			return (new Integer(result.getFinish())).toString();
		}
		
		return "";
	}
	
	public void setMode(Mode mode)
	{
		this.mode = mode;
		
		int borderWidth = 2;
		
		Graphics2D g = (Graphics2D)getImage().getGraphics();
        
        int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        font = new Font(null, Font.BOLD, (int)((getHeight() - 10) * dpi / 72f));
        FontRenderContext fontRender = g.getFontRenderContext();

	    w2 = (int)(font.getStringBounds(String.format("%s", getValue()), fontRender).getWidth());
        if(w2 > getWidth()-2*5)
        {
            font = new Font(null, Font.BOLD, (int)((2f / getValue().length()) * (getHeight()-2*5) * dpi / 72f));
            w2 = (int)(font.getStringBounds(String.format("%s", getValue()), fontRender).getWidth());
        }
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
		g.drawString(getValue(), (getWidth() - w2)/2, yOffset);
	}
}
