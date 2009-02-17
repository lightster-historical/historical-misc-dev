package com.lightdatasys.nascar.live.gui.cell;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.image.BufferedImage;

import com.lightdatasys.nascar.live.gui.GraphicsPanel;

public class Cell extends GraphicsPanel
{
	private BufferedImage image;
	private int width;
	private int height;
	
	private Color background;
	
	protected boolean updated;
	
	
	public Cell(GraphicsDevice gd, int w, int h)
	{
		super(gd);
		
		updated = true;
		background = Color.BLACK;
		
		width = w;
		height = h;
	}
	
	
	public void render(Graphics2D g)
	{
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		updated = false;
	}
	
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	
	public void setBackground(Color color)
	{
		if(!background.equals(color))
			updated = true;
		
		background = color;
	}
	
	public Color getBackground()
	{
		return background;
	}
}
