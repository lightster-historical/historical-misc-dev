package com.mephex.grapher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JApplet;
import javax.swing.JFrame;

public class Grapher extends JApplet
{	
	protected Graph graph;
	protected GraphPanel graphPanel;

	
	public Grapher()
	{
	}
	
	public Grapher(File file)
		throws FileNotFoundException
	{
		graph = GMLParser.loadGraphDataModel(file);
		graphPanel = new GraphPanel(graph);
	}
	
	public Grapher(URL url)
		throws FileNotFoundException, IOException
	{
		graph = GMLParser.loadGraphDataModel(url.openStream());
		graphPanel = new GraphPanel(graph);
	}
	
	public GraphPanel getPanel()
	{
		return graphPanel;
	}
	
	
	public void init()
	{
	}
	
	public void start()
	{
		try
		{
			URL url = new URL(getParameter("gml-path"));
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("Cookie", getParameter("cookies"));
			
			System.out.println(url.toString());
			
			Grapher grapher = new Grapher(url);
			add(grapher.getPanel());
			System.out.println(grapher.getModelCount());
		}
		catch(Exception ex)
		{
			System.out.println("huh?");
			ex.printStackTrace();
		}
	}
	
	public void stop()
	{
		
	}
	
	public void destroy()
	{
		
	}
	
	
	public int getModelCount()
	{
		if(graph == null)
			return 0;
		return graph.getModelCount();
	}
	
	public boolean setVisible(int i, boolean visible)
	{
		if(graph == null)
			return false;
		return graph.setVisible(i, visible);
	}
	
	
	public static void main(String args[])
	{	
		try
		{
			File file = new File("TestGraph.xml");
			Grapher grapher = new Grapher(file);
		
			JFrame window = new JFrame("Grapher");
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);      
			window.setSize(800, 600);
			
			window.add(grapher.getPanel());
			
			window.setVisible(true);
		}
		catch(FileNotFoundException ex)
		{
			ex.printStackTrace();
		}
	}
}

