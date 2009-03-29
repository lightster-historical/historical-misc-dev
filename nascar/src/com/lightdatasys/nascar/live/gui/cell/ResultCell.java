package com.lightdatasys.nascar.live.gui.cell;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.Toolkit;

import com.lightdatasys.gui.FontUtility;
import com.lightdatasys.nascar.Result;

public class ResultCell extends Cell 
{		
	public static final int BORDER_WIDTH = 3;
	
	
	private Result result;
	
	private Mode mode;
	
	private Color border;
	private Color text;
	
	private Font font;
	
	private String cachedValue;
	private boolean cachedLed;
	private boolean cachedMostLed;
	private int cachedLapsDown;
	private boolean cachedSpeedZero;
	
	private long lastRenderTime;
	
	
	public ResultCell(GraphicsDevice gd, int w, int h, Result result, Color text, Color bg, Color border)
	{
		super(gd, w, h);
		
		this.result = result;
		this.border = border;
		setBackground(bg);
		this.text = text;
		
		cachedValue = "";
		cachedLed = false;
		cachedMostLed = false;
		cachedLapsDown = 0;
		
		setMode(Mode.POSITION);
	}
	
	
	private String getValue()
	{
		updated = true;
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

			if(result.getFinish() == 1)
			{
				return "";
			}
			else
			{
				Result otherResult = result.getRace().getResultByFinish(result.getFinish()-1);
				
				if(otherResult != null)
				{
					if(result.getLapsDown() != 0)
					{
						//int diff = Math.abs(result.getLapsDown() -
						//		otherResult.getLapsDown());
						
						//return String.format("%d", diff);
						return String.format("%d", -result.getLapsDown());
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
		else if(mode == Mode.LAST_LAP_SPEED)
		{
			return String.format("%.2f", result.getLastLapSpeed());
		}
		else if(mode == Mode.LAST_LAP_TIME)
		{
			return String.format("%.2f", result.getLastLapTime());
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
			Result leaderResult = result.getRace().getResultByDriver(result.getRace().getDriverByRank().get(1));
			//result.getRace().getDriverStandingsByRank().get(1).points;
			return String.format("%d", (int)(result.getSeasonPoints() - leaderResult.getSeasonPoints()));
		}
		else if(mode == Mode.LOCAL_POINTS_DIFF)
		{
			if(result == null)
				System.out.println("result is null");
			
			if(result.getRace().isChaseRace())
			{
				Result leaderResult = result.getRace().getResultByDriver(result.getRace().getDriverByRank().get(1));
				Result rank13Result = result.getRace().getResultByDriver(result.getRace().getDriverByRank().get(13));

				if(leaderResult == null)
					System.out.println("leaderResult is null");
				if(rank13Result == null)
					System.out.println("rank13Result is null");
				
				if(result.getSeasonRank() <= 12)
				{
					return String.format("%d", (int)(result.getSeasonPoints() - leaderResult.getSeasonPoints()));
				}
				else
				{
					return String.format("%d", (int)(result.getSeasonPoints() - rank13Result.getSeasonPoints()));
				}
			}
			else
			{
				Result rank12Result = result.getRace().getResultByDriver(result.getRace().getDriverByRank().get(12));
				Result rank13Result = result.getRace().getResultByDriver(result.getRace().getDriverByRank().get(13));

				if(rank12Result == null)
					System.out.println("rank12Result is null, driver12 is " + result.getRace().getDriverByRank().get(12));
				if(rank13Result == null)
					System.out.println("rank13Result is null, driver13 is " + result.getRace().getDriverByRank().get(13));
				
				if(result.getSeasonRank() <= 12)
				{
					return String.format("%d", (int)(result.getSeasonPoints() - rank13Result.getSeasonPoints()));
				}
				else
				{
					return String.format("%d", (int)(result.getSeasonPoints() - rank12Result.getSeasonPoints()));
				}
			}
		}
		else if(mode == Mode.ROW_NUMBER)
		{
			return String.format("%d", result.getRowNumber());
		}
		
		return (new Integer(result.getFinish())).toString();
	}
	
	protected long getDistance(float interval, float speed)
	{
		// (inches per mile / seconds per hour)
		double conversion = 17.6;
		interval = Math.abs(interval);
		return (long)(speed * interval * conversion);
	}
	
	protected Color getColorUsingDistance(long distance, boolean fontColor)
	{
		float carLength = 200;
		float[] ranges = {carLength, carLength * 3, carLength * 10};
		Color color = null;
		
		if(distance <= ranges[0])
		{
			if(fontColor)
				color = Color.BLACK;
			else
			{
				float percent = Math.max(0, Math.min(1, distance / ranges[0]));
				color = new Color(percent, 1, 0);
			}
		}
		else if(distance <= ranges[1])
		{
			if(fontColor)
				color = Color.BLACK;
			else
			{
				float percent = Math.max(0, Math.min(1, (distance - ranges[0]) / ranges[1]));
				color = new Color(1, 1, percent);
			}
		}
		else if(distance <= ranges[2])
		{
			float percent = 1 - Math.max(0, Math.min(1, (distance - ranges[1]) / ranges[2]));
			if(fontColor)
			{
				if(percent < .5)
					color = Color.WHITE;
				else
					color = Color.BLACK;					
			}
			else
				color = new Color(percent, percent, percent);
		}
		else
		{
			if(fontColor)
				color = Color.WHITE;
			else
				color = Color.BLACK;
		}
		
		return color;
	}
	
	
	
	public void setMode(Mode mode)
	{
		if(!mode.equals(this.mode))
		{
			this.mode = mode;
			
			triggerUpdate();
			
			updateFont();
		}
	}
	
	protected void updateFont()
	{
		Graphics2D g = (Graphics2D)getImage().getGraphics();

        int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        font = new Font(null, Font.BOLD, (int)((getHeight() - 10) * dpi / 72f));
		font = FontUtility.getScaledFont(getWidth() - BORDER_WIDTH*5, getHeight() - BORDER_WIDTH*5, getValue(), font, g);		
	}
	
	
	public boolean isUpdated()
	{
		return updated
			|| !cachedValue.equals(getValue())
			|| cachedLed != result.ledLaps()
			|| cachedMostLed != result.ledMostLaps()
		    || cachedLapsDown != result.getLapsDown()
		    || System.currentTimeMillis() - lastRenderTime > 5000;
		   // || (cachedSpeedZero && result.getSpeed() <= .9f)
		   // || (!cachedSpeedZero && result.getSpeed() > .9f);
	}
	
	
	public void render(Graphics2D g)
	{		
		if(isUpdated())
			updateFont();

		cachedValue = getValue();
		cachedLed = result.ledLaps();
		cachedMostLed = result.ledMostLaps();
		cachedLapsDown = result.getLapsDown();
		//cachedSpeedZero = (result.getSpeed() <= .9f);
		
		lastRenderTime = System.currentTimeMillis();
        
        FontMetrics metrics = g.getFontMetrics(font);

		float xOffset = (float)(getWidth() - font.getStringBounds(cachedValue, g.getFontRenderContext()).getWidth()) / 2.0f;
		float yOffset = (getHeight() + metrics.getAscent() - metrics.getDescent()) / 2.0f;

        /*g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);*/

        g.setFont(font);
        
		Color tBorder = new Color(0x33, 0x33, 0x33);
		Color tBackground = Color.BLACK;
		Color tText = Color.WHITE;

		if(mode == Mode.POSITION)
		{
			if(result.getSpeed() > 0.9f)
				tBorder = Color.BLACK;
			else
				tBorder = new Color(0xFF, 0xFF, 0xFF);

			if(result.getLapsDown() != 0)
			{
				if(result.ledMostLaps())
					tBackground = new Color(0x66, 0x33, 0x00);
				else if(result.ledLaps())
					tBackground = new Color(0xFF, 0x99, 0x00);
				else
					tBackground = new Color(0xCC, 0x00, 0x00);
			}
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
		else if(mode == Mode.SEASON_RANK && result.getRace().isChaseRace())
		{
			int rank = result.getSeasonRank();
			if(rank <= 12)
			{
				if(rank == 1)
					tBackground = new Color(0x00, 0xCC, 0x00);
				else
					tBackground = new Color(0xFF, 0xFF, 0x00);
 
				tText = Color.BLACK;
			}
			else if(rank <= 16)
			{
				if(rank == 13)
					tBackground = new Color(0xFF, 0x00, 0x00);
				else
					tBackground = new Color(0x00, 0x00, 0xFF);
				
				tText = Color.WHITE;
			}
			else
			{
				tBackground = Color.BLACK;
				tText = Color.WHITE;
			}
		}
		else if(mode == Mode.LEADER_INTERVAL || mode == Mode.LOCAL_INTERVAL)
		{
			float interval = -1;
			if(mode == Mode.LEADER_INTERVAL)
				interval = Math.abs(result.getBehindLeader());
			else
			{
				Result otherResult = result.getRace().getResultByFinish(result.getFinish()-1);
				
				if(otherResult != null)
				{
					if(result.getLapsDown() == 0)
					{
						interval = Math.abs(result.getBehindLeader() -
								otherResult.getBehindLeader());
					}
				}
			}

			float speed = result.getSpeed();
			if(interval >= 0 && speed > 0)
			{
				long dist = getDistance(interval, speed);
								
				tBackground = getColorUsingDistance(dist, false);
				tText = getColorUsingDistance(dist, true);
			}
		}
		else if(mode == Mode.SPEED)
		{
			double throttle = result.getThrottle();
			double brake = result.getBrake();
			
			if(0 < brake && brake <= 100)
			{
				tBackground = new Color((float)(brake / 100), 0, 0);
				tText = Color.WHITE;
			}
			else if(0 < throttle && throttle <= 100)
			{
				tBackground = new Color(0, (float)(throttle / 100), 0);
				
				if(throttle > .85)
					tText = Color.BLACK;
				else
					tText = Color.WHITE;
			}
		}

		if(mode == Mode.POSITION || mode == Mode.LEADER_INTERVAL || mode == Mode.LOCAL_INTERVAL
			|| mode == Mode.SPEED)
		{
			g.setColor(tBackground);
			g.fillRect(0, 0, getWidth(), getHeight());

			g.setColor(tBorder);
			g.fillRect(BORDER_WIDTH, BORDER_WIDTH, getWidth()-2*BORDER_WIDTH, getHeight()-2*BORDER_WIDTH);
			
			g.setColor(tBackground);
			g.fillRect(BORDER_WIDTH*2, BORDER_WIDTH*2, getWidth()-4*BORDER_WIDTH, getHeight()-4*BORDER_WIDTH);
		}
		else if(mode == Mode.SEASON_RANK)
		{
			g.setColor(tBackground);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		else
		{
			g.setColor(tBorder);
			g.fillRect(0, 0, getWidth(), getHeight());
			
			g.setColor(tBackground);
			g.fillRect(BORDER_WIDTH, BORDER_WIDTH, getWidth()-2*BORDER_WIDTH, getHeight()-2*BORDER_WIDTH);
		}
		
		g.setColor(tText);
		g.drawString(cachedValue, xOffset, yOffset);
		
		updated = false;
	}
	
	
	

	public enum Mode
	{
		POSITION("Position"),
		LAPS_LED("Laps Led"),
		LEADER_INTERVAL("Leader Interval"), 
		LOCAL_INTERVAL("Local Interval"), 
		SEASON_POINTS("Season Points"),
		RACE_POINTS("Race Points"),
		LAST_LAP_POSITION("Last Lap Position"), 
		POSITION_CHANGE("Position Change"),
		SPEED("Speed"), 
		SEASON_RANK("Season Rank"), 
		LEADER_POINTS_DIFF("Leader Points Diff"), 
		LOCAL_POINTS_DIFF("Local Points Diff"),
		ROW_NUMBER("Row Number"),
		LAST_LAP_SPEED("Last Lap Speed"),
		LAST_LAP_TIME("Last Lap Time");
		
		
		protected final String displayValue;
		
		private Mode(String displayValue)
		{
			this.displayValue = displayValue;
		}
		
		public String getStringIndex()
		{
			return (new Integer(ordinal())).toString();
		}
		
		public String toString()
		{
			return displayValue;
		}
		
		
		public Mode getValueUsingIndex(String index)
		{
			try
			{
				int i = Integer.parseInt(index);
				
				if(0 <= i && i < Mode.values().length)
					return Mode.values()[i];
			}
			catch(Exception ex)
			{
			}
			
			return Mode.values()[0];
		}
	};
}
