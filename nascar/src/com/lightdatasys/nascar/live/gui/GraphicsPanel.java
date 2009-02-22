package com.lightdatasys.nascar.live.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.image.BufferedImage;

public abstract class GraphicsPanel 
{
	private GraphicsDevice gd;
	private BufferedImage image;
	
	protected boolean updated;

	protected int xOffset;
	protected int yOffset;
	protected Swap xSwap;
	protected Swap ySwap;
	
	protected Color background;
	

	public GraphicsPanel()
	{
		this(null);
	}
	
	public GraphicsPanel(GraphicsDevice gd)
	{
		this.gd = gd;
		updated = true;
		
		this.xOffset = 0;
		this.yOffset = 0;
		this.xSwap = null;
		this.ySwap = null;
		
		this.background = null;
	}
	
	
	public BufferedImage getImage()
	{
		if(image == null)
		{
			int w = getWidth();
			int h = getHeight();
			
			if(gd != null)
				image = gd.getDefaultConfiguration().createCompatibleImage(w, h);
			else
				image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		}
		
		return image;
	}

	public void renderTo(Graphics2D g, int x, int y)
	{
		if(isUpdated())
		{
			render();
		}
		
		g.drawImage((Image)image, x, y, null);
	}
	

	public abstract int getWidth();
	public abstract int getHeight();
	
	public abstract void render(Graphics2D g);
	
	public void render()
	{
		Graphics2D g2 = (Graphics2D)getImage().getGraphics();
		
		if(getBackground() != null)
		{
			g2.setColor(getBackground());
			g2.fillRect(0, 0, getWidth(), getHeight());
		}
		
		render(g2);
		
		clearUpdate();
	}
	
	
	public boolean isUpdated()
	{
		return updated;
	}
	
	public void clearUpdate()
	{
		updated = false;
	}
	
	public void triggerUpdate()
	{
		updated = true;
	}

	
	public void setXOffset(int xOffset)
	{
		this.xOffset = xOffset;
	}
	
	public void setYOffset(int yOffset)
	{
		this.yOffset = yOffset;
	}
	
	public void moveToX(int newOffset, long swapPeriod)
	{
		xSwap = new Swap(getXOffset(), newOffset, swapPeriod);
	}
	
	public void moveToY(int newOffset, long swapPeriod)
	{
		ySwap = new Swap(getYOffset(), newOffset, swapPeriod);
	}

	public int getXOffset()
	{
		return getXOffset(false);
	}
	
	public int getXOffset(boolean start)
	{
		if(xSwap != null && !start)
		{
			if(xSwap.isDone())
			{
				xOffset = xSwap.getPosition();
				xSwap = null;
				
				return xOffset;
			}
			else	
				return xSwap.getPosition();
		}
		else
			return xOffset;
	}

	public int getYOffset()
	{
		return getYOffset(false);
	}
	
	public int getYOffset(boolean start)
	{
		if(ySwap != null && !start)
		{
			if(ySwap.isDone())
			{
				yOffset = ySwap.getPosition();
				ySwap = null;
				
				return yOffset;
			}
			else	
				return ySwap.getPosition();
		}
		else
			return yOffset;
	}
	
	
	public void setBackground(Color background)
	{
		if(this.background == null || !this.background.equals(background))
		{
			this.background = background;
			triggerUpdate();
		}
	}
	
	public Color getBackground()
	{
		return background;
	}
}
