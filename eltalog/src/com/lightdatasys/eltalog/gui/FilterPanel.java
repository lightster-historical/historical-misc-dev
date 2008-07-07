package com.lightdatasys.eltalog.gui;

import java.awt.GridLayout;

import javax.swing.JPanel;

public class FilterPanel extends JPanel
{
	private static final long serialVersionUID = 200806181106L;
	
	
	public FilterPanel()
	{
		super(new GridLayout(1, 0));

		add(new FilterList("Genre"));
		add(new FilterList("Company"));
		add(new FilterList("Person"));
	}
	
	
	
}
