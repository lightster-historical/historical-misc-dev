package com.lightdatasys.nascar.fantasy.gui.cell;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.image.BufferedImage;

public class Cell 
{
	private BufferedImage image;
	private int width;
	private int height;
	
	private Color background;
	
	protected boolean updated;
	

	private Cell()
	{
		updated = true;
		background = Color.BLACK;
	}
	
	public Cell(GraphicsDevice gd, int w, int h)
	{
		this();
		
		if(gd != null)
			image = gd.getDefaultConfiguration().createCompatibleImage(w, h);
		else
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
	
	
	public void setBackground(Color color)
	{
		if(!background.equals(color))
			updated = true;
		
		background = color;
	}
	
	
	public boolean isUpdated()
	{
		return updated;
	}
	
	public void triggerRender()
	{
		updated = true;
	}
	
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public BufferedImage getImage()
	{
		return image;
	}
	
	public Color getBackground()
	{
		return background;
	}
}
