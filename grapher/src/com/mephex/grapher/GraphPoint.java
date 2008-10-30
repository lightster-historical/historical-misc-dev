package com.mephex.grapher;

import java.awt.Color;

public class GraphPoint
{
	protected double x;
	protected double y;
	
	protected Color color;
	
	
	public GraphPoint()
	{
		this(0, 0, null);
	}
	
	public GraphPoint(double x, double y, Color color)
	{
		this.x = x;
		this.y = y;
		
		this.color = color;
	}
	
	
	public void setX(double x)
	{
		this.x = x;
	}
	
	public void setY(double y)
	{
		this.y = y;
	}
	
	public void setColor(Color color)
	{
		this.color = color;
	}
	
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
	
	public Color getColor()
	{
		return color;
	}
}
