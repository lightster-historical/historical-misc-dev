package com.mephex.grapher;

import java.awt.Color;
import java.awt.geom.Point2D;

public abstract class GraphDataModel
{	
	protected boolean visible;
	
	public GraphDataModel()
	{
		visible = true;
	}
	
		
	public abstract GraphPoint getPoint(int i);
	public abstract Double getX(int i);
	public abstract Double getY(int i);
	
	public abstract int getCount();

	public abstract double getXMin();
	public abstract double getXMax();
	public abstract double getXFractionMin();
	public abstract double getYMin();
	public abstract double getYMax();
	public abstract double getYFractionMin();
	
	
	public boolean isVisible()
	{
		//if(k == 0)
		//	return false;
		
		return visible;
	}
	
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
	
	public Color getColor()
	{
		return null;
	}
}
