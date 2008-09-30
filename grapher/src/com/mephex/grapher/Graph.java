package com.mephex.grapher;

import java.awt.Graphics2D;

public class Graph
{	
	private GraphDataModel model;
	
	private boolean updated;
	
	private int width;
	private int height;
	
	
	public Graph(GraphDataModel model)
	{
		if(this.model == null)
			throw new NullPointerException();
		
		updated = true;
		
		width = 800;
		height = 600;
	}
	
	
	public void update()
	{
		
	}
	
	public void render(Graphics2D g)
	{
		
	}
	
	
	public void triggerUpdate()
	{
		updated = true;
	}
	
	public void setWidth(int w)
	{
		if(w > 0)
		{
			width = w;
			triggerUpdate();
		}
	}
	
	public void setHeight(int h)
	{
		if(h > 0)
		{
			height = h;
			triggerUpdate();
		}
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
	
	public double getXScale()
	{
		return 0;
	}
	
	public double getYScale()
	{
		return 0;
	}
	
	
}
