package com.mephex.grapher;

import java.awt.geom.Point2D;

public abstract class GraphDataModel
{	
	public GraphDataModel()
	{
		
	}
	
		
	public abstract Point2D.Double getPoint(int i);
	public abstract double getX(int i);
	public abstract double getY(int i);
	
	public abstract int getCount();

	public abstract double getXMin();
	public abstract double getXMax();
	public abstract double getYMin();
	public abstract double getYMax();
}
