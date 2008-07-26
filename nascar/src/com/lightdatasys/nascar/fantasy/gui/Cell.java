package com.lightdatasys.nascar.fantasy.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Cell 
{
	private BufferedImage image;
	private int width;
	private int height;
	
	protected boolean updated;
	

	private Cell()
	{
		updated = true;
	}
	
	public Cell(int w, int h)
	{
		this();
		
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		
		width = w;
		height = h;
	}
	
	
	public void render(Graphics2D g)
	{
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		
		updated = false;
	}
	
	public BufferedImage getImage()
	{
		return image;
	}
	
	
	public boolean isUpdated()
	{
		return updated;
	}
	
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
}
