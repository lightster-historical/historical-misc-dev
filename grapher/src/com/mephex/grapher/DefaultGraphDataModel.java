package com.mephex.grapher;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;


public class DefaultGraphDataModel extends GraphDataModel
{
	protected ArrayList<GraphPoint> points;
	
	
	protected Color color;

	protected double xMin;
	protected double xMax;
	protected double xFractionMin;
	
	protected double yMin;
	protected double yMax;
	protected double yFractionMin;
	
	
	public DefaultGraphDataModel()
	{
		points = new ArrayList<GraphPoint>();
		
		color = null;
		
		updateStats();
	}
	
	
	protected void updateStats()
	{
		xMin = Double.MAX_VALUE;
		xMax = Double.MIN_VALUE;
		xFractionMin = Double.MAX_VALUE;
		
		yMin = Double.MAX_VALUE;
		yMax = Double.MIN_VALUE;
		yFractionMin = Double.MAX_VALUE;
		
		for(GraphPoint point : points)
		{
			xMin = Math.min(xMin, point.getX());
			xMax = Math.max(xMax, point.getX());
			
			yMin = Math.min(yMin, point.getY());
			yMax = Math.max(yMax, point.getY());
			
			double yTemp = Math.abs(point.getY());
			double yFractionTemp = yTemp;
			double yTemp2 = yTemp;
			double yTens = 10;
			while(yTemp > 0)
			{
				yTemp -= (int)yTemp;
				yTemp *= 10;
				
				yTens /= 10;
			}
			
			//System.out.print(yTemp2 + "," + yTens + " ");
			yFractionMin = Math.min(yFractionMin, (int)(yTemp2 * yTens) / yTens);
		}

		//System.out.println(yFractionMin);
	}
	
	
	@Override
	public Color getColor()
	{
		return color;
	}
	

	@Override
	public int getCount()
	{
		return points.size();
	}

	@Override
	public GraphPoint getPoint(int i)
	{
		if(!(0 <= i && i < getCount()))
			return null;
		
		return points.get(i);
	}

	@Override
	public Double getX(int i)
	{
		if(!(0 <= i && i < getCount()))
				return null;
		
		return points.get(i).getX();
	}

	@Override
	public double getXMin()
	{
		return 1;
	}

	@Override
	public double getXMax()
	{
		return getCount();
	}
	
	public double getXFractionMin()
	{
		return 1;
	}
	

	@Override
	public Double getY(int i)
	{
		if(!(0 <= i && i < getCount()))
			return null;

		return points.get(i).getY();
	}

	@Override
	public double getYMin()
	{
		return yMin;
	}

	@Override
	public double getYMax()
	{
		return yMax;
	}
	
	public double getYFractionMin()
	{
		return yFractionMin;
	}
	
	
	
	
	public void addPoint(GraphPoint point)
	{
		if(point != null)
		{
			points.add(point);
			updateStats();
		}
	}
	

	public void setColor(Color color)
	{
		this.color = color;
	}
}
