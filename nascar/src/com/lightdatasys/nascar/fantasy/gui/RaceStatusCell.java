package com.lightdatasys.nascar.fantasy.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.lightdatasys.nascar.fantasy.Leaderboard;
import com.sportvision.model.Race;

public class RaceStatusCell extends Cell 
{
	private BufferedImage image;
	private int width;
	private int height;
	
	protected boolean updated;
	
	private Leaderboard leaderboard;
	
	
	public RaceStatusCell(int w, int h, Leaderboard leaderboard)
	{
		super(w, h);
		
		this.leaderboard = leaderboard;
		
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		
		width = w;
		height = h;
		
		updated = true;
	}
	
	
	public void render(Graphics2D g)
	{
		if(leaderboard.getRace() != null)
		{
			Race race = leaderboard.getRace();
			if(race.flag == Race.PRE_RACE)
			{
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, width, height);
			}
			else if(race.flag == Race.CHECKERED)
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
				if(race.flag == Race.GREEN)
					g.setColor(Color.GREEN);
				else if(race.flag == Race.RED)
					g.setColor(Color.RED);
				else if(race.flag == Race.WHITE)
					g.setColor(Color.WHITE);
				else if(race.flag == Race.YELLOW)
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
