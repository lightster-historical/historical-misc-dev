package com.mephex.grapher;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;

import javax.swing.JPanel;

public class GraphPanel extends JPanel
	implements ComponentListener
{
	protected Graph graph;
	
	
	public GraphPanel(Graph graph)
	{
		super();
		
		if(graph == null)
			throw new NullPointerException();

		this.graph = graph;
		
		setBackground(Color.WHITE);
		
		addComponentListener(this);
	}
	

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		if(graph.isUpdated())
			graph.update();
		
		graph.render((Graphics2D)g);
	}

	

	public void componentHidden(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	
	public void componentShown(ComponentEvent e)  {}
	{
		if(graph != null)
		{
			graph.triggerUpdate();
			repaint();
		}
	}
	
	public void componentResized(ComponentEvent e)
	{
		if(graph != null)
		{
			graph.triggerUpdate();
			graph.setWidth(getWidth());
			graph.setHeight(getHeight());
			repaint();
		}
	}

}
