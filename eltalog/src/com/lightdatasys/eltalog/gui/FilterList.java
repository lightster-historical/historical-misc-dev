package com.lightdatasys.eltalog.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class FilterList extends JPanel
{
	private static final long serialVersionUID = 200806181106L;
	
	
	public FilterList(String filterTitle)
	{
		super(new BorderLayout());
		
		this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
		
		JScrollPane scrollPane = new JScrollPane();
		JList list = new JList();
		scrollPane.setViewportView(list);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		JLabel label = new JLabel(filterTitle, JLabel.CENTER);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		
		add(label, BorderLayout.PAGE_START);
		add(scrollPane, BorderLayout.CENTER);
	}
}
