package com.lightdatasys.eltalog.gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

import com.lightdatasys.eltalog.Item;
import com.lightdatasys.eltalog.Library;

public class LibraryTablePanel<I extends Item> extends JPanel
{
	private static final long serialVersionUID = 200806181106L;
	
	private Library<I> library;
	private JTable table;
	
	
	public LibraryTablePanel(Library<I> library)
	{
		super(new GridBagLayout());
		
		this.library = library;
		
		JScrollPane scrollPane = new JScrollPane();
		
		table = new JTable(library.getTableModel());
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		for (int i = 0; i < table.getModel().getColumnCount(); i++) 
		    table.getColumnModel().getColumn(i).setPreferredWidth(30);

		table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));

		scrollPane.setViewportView(table);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.ipady = 50;      //make this component tall
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		add(scrollPane, c);
	}
	
	
	
	public ArrayList<I> getSelectedItems()
	{
		ArrayList<I> items = new ArrayList<I>();
		
		int[] selectedRows = table.getSelectedRows();
		for(int selected : selectedRows)
		{
			items.add(library.getTableModel().getRowAt(selected));
		}
		
		return items;
	}
	
	
	public void addListSelectionListener(ListSelectionListener listener)
	{
		table.getSelectionModel().addListSelectionListener(listener);
	}
	
	public void removeListSelectionListener(ListSelectionListener listener)
	{
		table.getSelectionModel().removeListSelectionListener(listener);
	}
	
	
	public String getName()
	{
		return library.getName();
	}
}
