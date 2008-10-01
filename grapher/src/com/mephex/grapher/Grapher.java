package com.mephex.grapher;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Grapher
{
	protected Graph graph;
	
	public Grapher()
	{
		JFrame window = new JFrame("Grapher");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);      
		window.setSize(800, 600);
		
		graph = new Graph(new DefaultGraphDataModel());
		
		JPanel panel = new JPanel()
		{
			public void paintComponent(Graphics g)
			{
				graph.render((Graphics2D)g);
			}
		};
		window.add(panel);
		
		window.setVisible(true);
	}
	
	
	public static void main(String args[])
	{
		Grapher grapher = new Grapher();
	}
}
