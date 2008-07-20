package com.lightdatasys.nascar.fantasy.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Cell 
{
	private BufferedImage image;
	private int width;
	private int height;
	

	private Cell()
	{
	}
	
	public Cell(int w, int h)
	{
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		
		width = w;
		height = h;
	}
	
	
	public void render(Graphics2D g)
	{
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.WHITE);
		g.drawString("Test", 2, height);
	}
	
	public BufferedImage getImage()
	{
		return image;
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
