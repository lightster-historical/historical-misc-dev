package com.lightdatasys.nascar.live.gui.cell;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.image.BufferedImage;

import com.lightdatasys.gui.FontUtility;
import com.lightdatasys.nascar.Race;

public class RaceStatusCell extends Cell 
{
	private BufferedImage image;
	private int width;
	private int height;
	
	private Race race;
	
	
	public RaceStatusCell(GraphicsDevice gd, int w, int h, Race race)
	{
		super(gd, w, h);
		
		this.race = race;
		
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		
		width = w;
		height = h;
		
		updated = true;
	}
	
	
	public void render(Graphics2D g)
	{	
		if(race != null)
		{			
			long flagDelta = System.currentTimeMillis() - race.getFlagChange();
			
			if(race.getFlag() == Race.Flag.PRE_RACE)
			{
				g.setColor(new Color(0xCC, 0xCC, 0xCC));
				g.fillRect(0, 0, width, height);
				
				g.setColor(Color.BLACK);
			}
			else if(race.getFlag() == Race.Flag.CHECKERED)
			{
				long cycle = 200;
				
				if(flagDelta > 5000 || (flagDelta / cycle) % 4 < 3)
				{
					int rows, cols;
					int squareW, squareH;
					
					rows = 7;
					cols = 13;
	
					squareW = getWidth() / cols;
					squareH = getHeight() / rows;
					
					g.setColor(Color.BLACK);
					g.fillRect(0, 0, width, height);
					
					for(int x = 0; x < cols; x++)
					{
						for(int y = 0; y < rows; y++)
						{
							if((x % 2 == 0 && y % 2 == 0) ||
								(x % 2 == 1 && y % 2 == 1))
							{
								g.setColor(Color.WHITE);
								g.fillRect(x * squareW, y * squareH, squareW, squareH);
							}
						}
					}
				}
				else
				{
					g.setColor(Color.BLACK);	
					g.fillRect(0, 0, width, height);
				}
			}
			else
			{
				long cycle = 200;
				
				if(race.getFlag() == Race.Flag.GREEN)
				{
					if(flagDelta > 5000 || (flagDelta / cycle) % 2 == 0)
						g.setColor(new Color(0x00, 0x99, 0x00));
					else
						g.setColor(Color.BLACK);
				}
				else if(race.getFlag() == Race.Flag.RED)
				{
					if(flagDelta > 5000 || (flagDelta / cycle) % 2 == 0)
						g.setColor(new Color(0xCC, 0x00, 0x00));
					else
						g.setColor(Color.BLACK);
				}
				else if(race.getFlag() == Race.Flag.WHITE)
					g.setColor(Color.WHITE);
				else if(race.getFlag() == Race.Flag.YELLOW)
				{					
					if(flagDelta > 5000 || (flagDelta / cycle) % 2 == 0)
						g.setColor(Color.YELLOW);
					else
						g.setColor(Color.BLACK);
				}
				
				g.fillRect(0, 0, width, height);

				if(race.getFlag() == Race.Flag.GREEN)
					g.setColor(Color.WHITE);
				else if(race.getFlag() == Race.Flag.RED)
					g.setColor(Color.WHITE);
				else if(race.getFlag() == Race.Flag.WHITE)
					g.setColor(Color.BLACK);
				else if(race.getFlag() == Race.Flag.YELLOW)
				{
					if(flagDelta > 5000 || (flagDelta / cycle) % 2 == 0)
						g.setColor(Color.BLACK);
					else
						g.setColor(Color.WHITE);
				}
			}
			
			String label = String.format("%d", race.getLapCount() - race.getCurrentLap());
			
			Font font = FontUtility.getScaledFont(width, height - 25, label, g.getFont(), g);
			FontMetrics metrics = g.getFontMetrics(font);
			g.setFont(font);

			float xOffset = (float)(getWidth() - font.getStringBounds(label, g.getFontRenderContext()).getWidth()) / 2.0f;
			float yOffset = (getHeight() + metrics.getAscent() - metrics.getDescent()) / 2.0f;
			
			if(race.getFlag() != Race.Flag.CHECKERED)
				g.drawString(label, xOffset, yOffset);
		}
		else
		{
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, width, height);
		}
		
		//updated = false;
	}
}
