package com.lightdatasys.nascar.fantasy.gui.cell;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.Toolkit;
import java.text.NumberFormat;

import com.lightdatasys.gui.FontUtility;
import com.lightdatasys.nascar.Result;
import com.lightdatasys.nascar.fantasy.gui.cell.FantasyResultCell.Mode;

public class ResultCell extends Cell 
{
	public enum Mode {POSITION, LAPS_LED, LEADER_INTERVAL, LOCAL_INTERVAL, SEASON_POINTS, RACE_POINTS,
		LAST_LAP_POSITION, POSITION_CHANGE, SPEED, SEASON_RANK, LEADER_POINTS_DIFF, LOCAL_POINTS_DIFF};
		
	public static final int BORDER_WIDTH = 2;
	
	
	private Result result;
	
	private Mode mode;
	
	private Color border;
	private Color background;
	private Color text;
	
	private Font font;
	
	private String cachedValue;
	private boolean cachedLed;
	private boolean cachedMostLed;
	private int cachedLapsDown;
	
	
	public ResultCell(GraphicsDevice gd, int w, int h, Result result, Color text, Color bg, Color border)
	{
		super(gd, w, h);
		
		this.result = result;
		this.border = border;
		this.background = bg;
		this.text = text;
		
		cachedValue = "";
		cachedLed = false;
		cachedMostLed = false;
		cachedLapsDown = 0;
		
		setMode(Mode.POSITION);
	}
	
	
	private String getValue()
	{
		if(mode == Mode.LAPS_LED)
		{
			return (new Integer(result.getLapsLed())).toString();
		}
		else if(mode == Mode.LEADER_INTERVAL)
		{
			if(result.getFinish() == 1)
			{
				return "";
			}
			else if(result.getLapsDown() != 0)
			{
				return String.format("%d", -result.getLapsDown());
			}
			else
			{
				float interval = Math.abs(result.getBehindLeader());

				if(interval >= 10)
					return String.format("%.1f", interval);
				else if(interval >= 1)
					return String.format("%.2f", interval);			
				else
					return String.format(".%03d", (int)(interval*1000));
			}
		}
		else if(mode == Mode.LOCAL_INTERVAL)
		{
			Result otherResult = result.getRace().getResultByFinish(result.getFinish()-1);

			if(result.getFinish() == 1)
			{
				return "";
			}
			else if(otherResult != null)
			{
				if(result.getLapsDown() != 0)
				{
					int diff = Math.abs(result.getLapsDown() -
							otherResult.getLapsDown());
					
					return String.format("%d", diff);
				}
				else
				{
					float interval = Math.abs(result.getBehindLeader() -
							otherResult.getBehindLeader());
	
					if(interval >= 10)
						return String.format("%.1f", interval);
					else if(interval >= 1)
						return String.format("%.2f", interval);					
					else
						return String.format(".%03d", (int)(interval*1000));
				}
			}
		}
		else if(mode == Mode.SEASON_POINTS)
		{
			return String.format("%s", result.getSeasonPoints());
		}
		else if(mode == Mode.RACE_POINTS)
		{
			return String.format("%s", result.getRacePoints());
		}
		else if(mode == Mode.LAST_LAP_POSITION)
		{
			return String.format("%s", result.getLastFinish());
		}
		else if(mode == Mode.POSITION_CHANGE)
		{
			return String.format("%s", result.getPositionChange());
		}
		else if(mode == Mode.SPEED)
		{
			return String.format("%d", (int)result.getSpeed());
		}
		else if(mode == Mode.SEASON_RANK)
		{
			return String.format("%d", (int)result.getSeasonRank());
		}
		else if(mode == Mode.LEADER_POINTS_DIFF)
		{
			//result.getRace().getDriverStandingsByRank().get(1).points;
			return String.format("%d", (int)(result.getRace().getDriverStandingsByRank().get(result.getRace().getRankByDriver(result.getDriver())).points - result.getRace().getDriverStandingsByRank().get(1).points));
		}
		else if(mode == Mode.LOCAL_POINTS_DIFF)
		{
			if(result.getRace().isChaseRace())
			{
				if(result.getSeasonRank() <= 12)
				{
					return String.format("%d", (int)(result.getRace().getDriverStandingsByRank().get(result.getSeasonRank()).points - result.getRace().getDriverStandingsByRank().get(1).points));
				}
				else
				{
					return String.format("%d", (int)(result.getRace().getDriverStandingsByRank().get(result.getSeasonRank()).points - result.getRace().getDriverStandingsByRank().get(13).points));
				}
			}
			else
			{
				if(result.getSeasonRank() <= 12)
				{
					return String.format("+%d", (int)(result.getRace().getDriverStandingsByRank().get(result.getSeasonRank()).points - result.getRace().getDriverStandingsByRank().get(13).points));
				}
				else
				{
					return String.format("%d", (int)(result.getRace().getDriverStandingsByRank().get(result.getSeasonRank()).points - result.getRace().getDriverStandingsByRank().get(12).points));
				}
			}
		}
		
		return (new Integer(result.getFinish())).toString();
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
		font = FontUtility.getScaledFont(getWidth() - BORDER_WIDTH*3, getHeight() - BORDER_WIDTH*3, getValue(), font, g);		
	}
	
	
	public boolean isUpdated()
	{
		return updated
			|| !cachedValue.equals(getValue())
			|| cachedLed != result.ledLaps()
			|| cachedMostLed != result.ledMostLaps();
		    //|| !cachedValue.equals(result.getLapsDown());
	}
	
	
	public void render(Graphics2D g)
	{		
		if(isUpdated())
			updateFont();

		cachedValue = getValue();
		cachedLed = result.ledLaps();
		cachedMostLed = result.ledMostLaps();
		cachedLapsDown = result.getLapsDown();
        
        FontMetrics metrics = g.getFontMetrics(font);

		float xOffset = (float)(getWidth() - font.getStringBounds(cachedValue, g.getFontRenderContext()).getWidth()) / 2.0f;
		float yOffset = (getHeight() + metrics.getAscent() - metrics.getDescent()) / 2.0f;

        /*g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);*/

        g.setFont(font);
        
		Color tBorder = border;
		Color tBackground = background;
		Color tText = text;

		if(mode == Mode.POSITION)
		{
			if(result.getSpeed() > 0.9f)
				tBorder = Color.BLACK;
			else
				tBorder = new Color(0xCC, 0x00, 0x00);

			if(result.getLapsDown() != 0)
				tBackground = new Color(0xCC, 0x00, 0x00);
			else if(result.ledMostLaps())
				tBackground = new Color(0x00, 0x99, 0x00);
			else if(result.ledLaps())
				tBackground = new Color(0xFF, 0xFF, 0x00);
			else
				tBackground = Color.BLACK;

			if(result.getLapsDown() != 0)
				tText = Color.WHITE;
			else if(result.ledLaps() && !result.ledMostLaps())
				tText = Color.BLACK;
		}
		else if(mode == Mode.LOCAL_POINTS_DIFF && result.getRace().isChaseRace())
		{
			if(result.getSeasonRank() <= 12)
				tBackground = new Color(0xFF, 0xFF, 0x00);
			else
				tBackground = Color.BLACK;
		}

		if(mode == Mode.POSITION)
		{
			g.setColor(tBackground);
			g.fillRect(0, 0, getWidth(), getHeight());

			g.setColor(tBorder);
			g.fillRect(BORDER_WIDTH, BORDER_WIDTH, getWidth()-2*BORDER_WIDTH, getHeight()-2*BORDER_WIDTH);
			
			g.setColor(tBackground);
			g.fillRect(BORDER_WIDTH*2, BORDER_WIDTH*2, getWidth()-4*BORDER_WIDTH, getHeight()-4*BORDER_WIDTH);
		}
		else
		{
			g.setColor(new Color(0x33, 0x33, 0x33));
			g.drawRect(BORDER_WIDTH, BORDER_WIDTH, getWidth()-2*BORDER_WIDTH, getHeight()-2*BORDER_WIDTH);
		}
		
		g.setColor(tText);
		g.drawString(cachedValue, xOffset, yOffset);
		
		updated = false;
	}
}
