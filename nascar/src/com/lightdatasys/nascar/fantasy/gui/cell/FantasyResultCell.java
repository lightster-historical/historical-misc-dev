package com.lightdatasys.nascar.fantasy.gui.cell;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.Toolkit;

import com.lightdatasys.gui.FontUtility;
import com.lightdatasys.nascar.fantasy.FantasyResult;

public class FantasyResultCell extends Cell 
{
	public enum Mode {POSITION, SEASON_POINTS, RACE_POINTS, DRIVER_RACE_POINTS,
		LAST_LAP_POSITION, POSITION_CHANGE, LEADER_DRIVER_DIFF};
	
	
	private FantasyResult result;
	
	private Mode mode;
	
	private Color border;
	private Color background;
	private Color text;
	
	private Font font;
	
	private String cachedValue;
	
	
	public FantasyResultCell(GraphicsDevice gd, int w, int h, FantasyResult result, Color text, Color bg, Color border)
	{
		super(gd, w, h);
		
		this.result = result;
		this.border = border;
		this.background = bg;
		this.text = text;
		
		cachedValue = "";
		
		setMode(Mode.POSITION);
	}
	
	
	private String getValue()
	{
		if(mode == Mode.SEASON_POINTS)
		{
			return String.format("%s", result.getSeasonPoints());
		}
		else if(mode == Mode.RACE_POINTS)
		{
			return String.format("%s", result.getRacePoints());
		}
		else if(mode == Mode.DRIVER_RACE_POINTS)
		{
			return String.format("%s", result.getDriverRacePoints());
		}
		else if(mode == Mode.LAST_LAP_POSITION)
		{
			return String.format("%s", result.getLastActualFinish());
		}
		else if(mode == Mode.POSITION_CHANGE)
		{
			return String.format("%s", Math.abs(result.getPositionChange()));
		}
		else if(mode == Mode.LEADER_DRIVER_DIFF)
		{
			if(result.getActualFinish() == 1)
				return String.format("%d", result.getDriverRacePoints());
			else
				return String.format("%d", result.getDriverRacePoints() - result.getRace().getFantasyResults().get(1).getDriverRacePoints());
		}
		
		return (new Integer(result.getActualFinish())).toString();
	}
	
	public void setMode(Mode mode)
	{
		if(!mode.equals(this.mode))
		{
			this.mode = mode;
			
			updated = true;
			
			updateFont();
		}
	}
	
	protected void updateFont()
	{
		Graphics2D g = (Graphics2D)getImage().getGraphics();

        int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        font = new Font(null, Font.BOLD, (int)((getHeight() - 10) * dpi / 72f));
		font = FontUtility.getScaledFont(getWidth() - 2, getHeight() - 2, getValue(), font, g);		
	}
	
	
	public boolean isUpdated()
	{
		return updated || !cachedValue.equals(getValue());
	}
	
	
	public void render(Graphics2D g)
	{
		int borderWidth = 2;
		
		if(isUpdated())
			updateFont();

		cachedValue = getValue();
        
        FontMetrics metrics = g.getFontMetrics(font);

		float xOffset = (float)(getWidth() - font.getStringBounds(cachedValue, g.getFontRenderContext()).getWidth()) / 2.0f;
		float yOffset = (getHeight() + metrics.getAscent() - metrics.getDescent()) / 2.0f;

        /*g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);*/

        g.setFont(font);
        
		// border
		//g.setColor(border);
		g.setPaint(new GradientPaint(getWidth()/2, 0,
                Color.BLACK,
                getWidth()/2,
                getHeight()*3.0f/4,
                border));
		if(mode != Mode.POSITION)
			g.fillRect(0, 0, getWidth(), getHeight());

		Color bg = background;
		if(mode == Mode.POSITION_CHANGE)
		{
			if(result.getPositionChange() == 0)
				bg = Color.BLACK;
			else if(result.getPositionChange() > 0)
				bg = new Color(0x00, 0xCC, 0x00);
			else
				bg = Color.RED;
		}
		if(mode == Mode.POSITION)
			g.setColor(Color.BLACK);
		else
			g.setPaint(new GradientPaint(getWidth()/2, 0,
                Color.BLACK,
                getWidth()/2,
                getHeight()*3.0f/4,
                bg));
		g.fillRect(borderWidth, borderWidth, getWidth()-2*borderWidth, getHeight()-2*borderWidth);

		//g.setFont(g.getFont().deriveFont(36.0f).deriveFont(Font.BOLD));
		g.setColor(text);
		g.drawString(cachedValue, xOffset, yOffset);
		
		updated = false;
	}
}
