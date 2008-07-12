package com.lightdatasys.nascar.manager.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.lightdatasys.nascar.Driver;
import com.lightdatasys.nascar.Race;
import com.lightdatasys.nascar.Result;

public class RaceResultsPanel extends JPanel 
{
	private static final long serialVersionUID = 1L;
	
	
	private JTable resultsTable;
	
	private JPanel buttonPanel;
	private JButton reorderButton;
	
	private Race race;

	
	public RaceResultsPanel()
	{
		super(new BorderLayout());
		
		race = null;
		
		resultsTable = new JTable
		(
			new AbstractTableModel()
			{
				private static final long serialVersionUID = 1L;

				public int getColumnCount()
				{
					return 7;
				}

				public int getRowCount()
				{
					return race.getResults().size();
				}

				public String getColumnName(int columnIndex)
				{
					String[] columnNames =
					{
						"Finish",
						"Start",
						"Car",
						"Driver",
						"Led Laps",
						"Led Most Laps",
						"Penalties"
					};

					if(columnIndex < columnNames.length)
						return columnNames[columnIndex];
					
					return "";
				}

				public Object getValueAt(int rowIndex, int columnIndex)
				{
					if(race.getResults().containsKey(rowIndex + 1))
					{
						Result r = race.getResults().get(rowIndex + 1);
						
						Object[] columns =
						{
							r.getFinish(),
							r.getStart(),
							r.getCar(),
							r.getDriver(),
							r.ledLaps(),
							r.ledMostLaps(),
							r.getPenalties()
						};
	
						if(columnIndex < columns.length)
							return columns[columnIndex];
					}
					
					return "";
				}
				
				public boolean isCellEditable(int rowIndex, int columnIndex)
				{
					return true;
				}
				
			    public Class<?> getColumnClass(int c) 
			    {
			    	if(c == 4 || c == 5)
			    		return getValueAt(0, c).getClass();
			    	else
			    		return super.getColumnClass(c);
			    }
			}
		);
		{		
			resultsTable.getColumnModel().getColumn(0).setPreferredWidth(30);
			resultsTable.getColumnModel().getColumn(1).setPreferredWidth(30);
			resultsTable.getColumnModel().getColumn(2).setPreferredWidth(30);
			resultsTable.getColumnModel().getColumn(3).setPreferredWidth(200);
			resultsTable.getColumnModel().getColumn(4).setPreferredWidth(65);
			resultsTable.getColumnModel().getColumn(5).setPreferredWidth(65);
			resultsTable.getColumnModel().getColumn(6).setPreferredWidth(65);

			JComboBox comboBox = new JComboBox(Driver.getDrivers());
			resultsTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(comboBox));
		}
		add(new JScrollPane(resultsTable), BorderLayout.CENTER);
		
		buttonPanel = new JPanel(new GridLayout(1, 0));
		{
			reorderButton = new JButton("Reorder Finish Positions");
			{
			}
			buttonPanel.add(reorderButton, BorderLayout.PAGE_END);
		}
		//add(buttonPanel, BorderLayout.PAGE_END);
		
	}
	
	
	public void setRace(Race race)
	{
		this.race = race;
		((AbstractTableModel)resultsTable.getModel()).fireTableDataChanged();
		//resultsTable.setModel(race.getTableModel());
	}
	
	
	public String getName()
	{
		return "Race Results";
	}
}
