package com.lightdatasys.nascar.fantasy.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.lightdatasys.gui.FontUtility;
import com.lightdatasys.nascar.Race;

public class RaceStatusCell extends Cell 
{
	private BufferedImage image;
	private int width;
	private int height;
	
	protected boolean updated;
	
	private Race race;
	
	
	public RaceStatusCell(int w, int h, Race race)
	{
		super(w, h);
		
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
			if(race.getFlag() == Race.Flag.PRE_RACE)
			{
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, width, height);
				
				String label = String.format("%d", race.getLapCount() - race.getCurrentLap());
				g.setColor(Color.WHITE);
				
				Font font = FontUtility.getScaledFont(width, height, label, g.getFont(), g);
				FontMetrics metrics = g.getFontMetrics(font);
				g.setFont(font);

				float xOffset = (float)(getWidth() - font.getStringBounds(label, g.getFontRenderContext()).getWidth()) / 2.0f;
				float yOffset = (getHeight() + metrics.getAscent() - metrics.getDescent()) / 2.0f;
				
				
				g.drawString(label, xOffset, yOffset);
			}
			else if(race.getFlag() == Race.Flag.CHECKERED)
			{
				int rows, cols;
				int squareW, squareH;
				
				rows = 7;
				cols = 7;

				squareW = getWidth() / rows;
				squareH = getHeight() / cols;
				
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, width, height);
				
				for(int i = 0; i < rows * cols; i++)
				{
					int row =  i / cols;
					int col = i % cols;
					
					if((row % 2 == 0 && col % 2 == 0) ||
						(row % 2 == 1 && col % 2 == 1))
					{
						g.setColor(Color.WHITE);
						g.fillRect(col * squareW, row * squareH, squareW, squareH);
					}
				}
			}
			else
			{
				if(race.getFlag() == Race.Flag.GREEN)
					g.setColor(Color.GREEN);
				else if(race.getFlag() == Race.Flag.RED)
					g.setColor(Color.RED);
				else if(race.getFlag() == Race.Flag.WHITE)
					g.setColor(Color.WHITE);
				else if(race.getFlag() == Race.Flag.YELLOW)
					g.setColor(Color.YELLOW);
				
				g.fillRect(0, 0, width, height);
			}
		}
		else
		{
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, width, height);
		}
		
		//updated = false;
	}
}
