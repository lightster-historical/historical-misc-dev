package com.mephex.grapher;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

public class GraphPanel extends JPanel
	implements ComponentListener
{
	protected Graph graph;
	
	
	public GraphPanel(GraphDataModel model)
	{
		super();
		
		if(model == null)
			throw new NullPointerException();

		graph = new Graph(model);
		
		addComponentListener(this);
	}
	

	public void paintComponent(Graphics g)
	{
		graph.render((Graphics2D)g);
	}

	

	public void componentHidden(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	
	public void componentShown(ComponentEvent e)  {}
	{
		if(graph != null)
		{
			graph.triggerUpdate();
		}
	}
	
	public void componentResized(ComponentEvent e)
	{
		if(graph != null)
		{
			graph.setWidth(getWidth());
			graph.setHeight(getHeight());
		}
	}

}
