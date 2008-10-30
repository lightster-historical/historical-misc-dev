 package com.mephex.grapher;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Graph
{	
	private ArrayList<GraphDataModel> models;
	
	private boolean updated;
	
	private int width;
	private int height;
	
	private double minX;
	private double maxX;
	private double scaleX;
	private double gridMajorX;
	private double gridMinorX;
	
	private double minY;
	private double maxY;
	private double scaleY;
	private double gridMajorY;
	private double gridMinorY;

	private int minorPerXMajor = 4;
	private int minorPerYMajor = 4;
	private int pixelsPerXMinor = 15;
	private int pixelsPerYMinor = 15;
	
	protected int[] graphMargin = {10, 10, 40, 70};

	
	public Graph()
	{
		models = new ArrayList<GraphDataModel>();
		
		updated = true;
		
		width = 800;
		height = 600;
	}
	
	public Graph(GraphDataModel model)
	{
		this();
		
		addModel(model);
	}
	
	
	public void addModel(GraphDataModel model)
	{
		if(model == null)
			throw new NullPointerException();
		
		models.add(model);
	}
	
	
	public int getModelCount()
	{
		return models.size();
	}
	
	public boolean setVisible(int i, boolean visible)
	{
		if(0 <= i && i < models.size())
		{
			models.get(i).setVisible(visible);
			return true;
		}
		
		return false;
	}
	
	
	public void update()
	{	
		double xFractionMin = Double.MAX_VALUE;
		double yFractionMin = Double.MAX_VALUE;
		
		minX = 0;
		minY = 0;
		
		maxX = 0;
		maxY = 0;
		for(GraphDataModel model : models)
		{
			maxX = Math.max(maxX, model.getXMax());
			maxY = Math.max(maxY, model.getYMax());
		}

		scaleX = getGraphWidth() / (maxX - minX);
		scaleY = getGraphHeight() / (maxY - minY);
		
		xFractionMin = 1;
		yFractionMin = 1;
		
		//gridMajorX = 5;
		gridMinorX = 1;
		//gridMajorY = 50;
		gridMinorY = 10;
		
		/*

		gridMajorX = (minorPerXMajor + 1) * pixelsPerXMinor / scaleX;
		gridMajorX = roundFraction(gridMajorX);
		gridMinorX = gridMajorX / (minorPerXMajor + 1);
		gridMajorY = (minorPerYMajor + 1) * pixelsPerYMinor / scaleY;
		gridMajorY = roundFraction(gridMajorY);
		gridMinorY = gridMajorY / (minorPerYMajor + 1);
		
		if(gridMinorX < xFractionMin)
		{
			gridMinorX = xFractionMin;
			gridMajorX = gridMinorX * (minorPerXMajor + 1);
		}
		
		if(gridMinorY < yFractionMin)
		{
			gridMinorY = yFractionMin;
			gridMajorY = gridMinorY * (minorPerYMajor + 1);
		}
		*/
		
		updated = false;
	}
	
	public void render(Graphics2D g)
	{
		int x0 = transformX(0);
		int y0 = transformY(0);
		
		int xLabelMaxHeight = 0;
		int yLabelMaxWidth = 0;

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	            RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setColor(Color.black);

		g.drawLine(x0, y0, x0, transformY(maxY));
		g.drawLine(x0, y0, transformX(maxX), y0);
		
		final double ERROR = 1E-10; 
		
        Font font = g.getFont();
        FontRenderContext fontRender = g.getFontRenderContext();
        FontMetrics metrics = g.getFontMetrics(font);

		int i = 0;
		double x = 0;
		while(x <= maxX)
		{   
			int xi = (int)x;
			String xs;
			if(xi - ERROR <= x && x <= xi + ERROR)
				xs = String.format("%s", xi);
			else
				xs = String.format("%s", xi);
			
			Rectangle2D bounds = font.getStringBounds(xs, fontRender);
			int fontW = (int)bounds.getWidth();
		    int fontH = (int)bounds.getHeight();
		    xLabelMaxHeight = Math.max(fontH, xLabelMaxHeight);
			
			int xt = transformX(x);
			g.drawLine(xt, y0, xt, y0 + 7);
			if(i % (minorPerXMajor + 1) == 0)
				g.drawString(xs, xt - fontW/2, y0 + 5 + fontH);
			
			x += gridMinorX;
			i++;
		}
		
		i = 0;
		double y = 0;
		while(y <= maxY)
		{
			int yi = (int)y;
			String ys;
			if(yi - ERROR <= y && y <= yi + ERROR)
				ys = String.format("%s", yi);
			else
				ys = String.format("%s", yi);
			
			Rectangle2D bounds = font.getStringBounds(ys, fontRender);
			int fontW = (int)bounds.getWidth();
		    int fontH = (int)bounds.getHeight();
		    yLabelMaxWidth = Math.max(fontW, yLabelMaxWidth);
			
			int yt = transformY(y);
			g.drawLine(x0, yt, x0 - 5, yt);
			if(i % (minorPerYMajor + 1) == 0)
				g.drawString(ys, x0 - fontW - 10, yt - metrics.getDescent() + (fontH)/2);
			
			y += gridMinorY;
			i++;
		}

		for(GraphDataModel model : models)
		{
			if(model.isVisible())
			{
				int lastX = -1, lastY = -1;
				if(model.getColor() != null)
					g.setColor(model.getColor());
				else
					g.setColor(Color.black);
				
				for(i = 0; i < model.getCount(); i++)
				{
					int xi = transformX(model.getX(i));
					int yi = transformY(model.getY(i));
					
					if(lastX != -1 && lastY != -1)
					{
						g.drawLine(xi, yi, lastX, lastY);
					}
	
					g.fillRect(xi-2, yi-2, 5, 5);
					
					lastX = xi;
					lastY = yi;
				}
			}
		}

		
		int fontW, fontH;
		Rectangle2D bounds;
		
		g.setColor(Color.BLACK);
		
		bounds = font.getStringBounds(getXLabel(), fontRender);
		fontW = (int)bounds.getWidth();
	    fontH = (int)bounds.getHeight();
		g.drawString(getXLabel(), x0 + getGraphWidth()/2 - fontW/2, y0 + 5 + xLabelMaxHeight + fontH);
		
		AffineTransform transform = new AffineTransform();
		bounds = font.getStringBounds(getYLabel(), fontRender);
		fontW = (int)bounds.getWidth();
	    fontH = (int)bounds.getHeight();
		transform.translate(x0 - 20 - yLabelMaxWidth - fontH, y0 - getGraphHeight()/2 - fontW/2);
		transform.rotate(Math.PI/2);
	    g.setTransform(transform);
		g.drawString(getYLabel(), 0, 0);
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
	
	

	public double getXMin()
	{
		return minX;
	}
	
	public double getXMax()
	{
		return maxX;
	}
	
	public double getXScale()
	{
		return scaleX;
	}
	
	public double getXGridMajor()
	{
		return gridMajorX;
	}
	
	public double getXGridMinor()
	{
		return gridMinorX;
	}
	

	public double getYMin()
	{
		return minY;
	}
	
	public double getYMax()
	{
		return maxY;
	}
	
	public double getYScale()
	{
		return scaleY;
	}
	
	public double getYGridMajor()
	{
		return gridMajorY;
	}
	
	public double getYGridMinor()
	{
		return gridMinorY;
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
	
	
	public String getXLabel()
	{
		return "Week";
	}
	
	public String getYLabel()
	{
		return "Points Behind Leader";
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
	
	
	public static double roundFraction(double mixed)
	{
		if(mixed <= 0)
			return 1;
		else if(mixed < 1)
		{
			double majX = mixed;
			long d = 1;
			while(majX < 1)
			{
				majX *= 10;
				d *= 10;
			}
			return Math.ceil(majX) / d;
		}
		else
		{
			return Math.ceil(mixed);
		}
	}
}
