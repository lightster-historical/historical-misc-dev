 package com.mephex.grapher;

import java.awt.Color;
import java.awt.Graphics2D;

public class Graph
{	
	private GraphDataModel model;
	
	private boolean updated;
	
	private int width;
	private int height;
	
	private int minX;
	private int maxX;
	private int scaleX;
	private int gridMajorX;
	
	protected int[] graphMargin = {50, 50, 50, 150};
	
	
	public Graph(GraphDataModel model)
	{
		if(model == null)
			throw new NullPointerException();
		
		this.model = model;
		
		updated = true;
		
		width = 800;
		height = 600;
	}
	
	
	public void update()
	{		
		updated = false;
	}
	
	public void render(Graphics2D g)
	{
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
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
	
	public int getGraphWidth()
	{
		return width - getGraphLeftMargin() - getGraphRightMargin();
	}
	
	public int getGraphHeight()
	{
		return height - getGraphTopMargin() - getGraphBottomMargin();
	}
	
	public double getXScale()
	{
		return 0;
	}
	
	public double getYScale()
	{
		return 0;
	}
	
	public int getGraphTopMargin()
	{
		return graphMargin[0];
	}
	
	public int getGraphRightMargin()
	{
		return graphMargin[1];
	}
	
	public int getGraphBottomMargin()
	{
		return graphMargin[2];
	}
	
	public int getGraphLeftMargin()
	{
		return graphMargin[3];
	}
	

	public int transformX(double x)
	{
		return getGraphLeftMargin() + (int)Math.round(x * getXScale());
	}
	
	public int transformY(double y)
	{
		// Java graphics: top = 0, bottom = vertical resolution
		// Cartesian coords: top = vertical resolution, bottom = 0
		return getHeight() - getGraphBottomMargin() - (int)Math.round(y * getYScale());
	}
}
