package com.lightdatasys.nascar;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractMap;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.table.AbstractTableModel;

public class Race
{
	private static AbstractMap<Integer,Race> racesById = new HashMap<Integer,Race>();
	
	
	private AbstractTableModel resultsTableModel;
	
	private int raceId;
	
	private Season season;
	private Track track;
	
	private String name;
	private String nascarComId;
	private Date date;
	
	private AbstractMap<Integer,Result> results;

	
	public Race()
	{
	}
	
	
	public int getId()
	{
		return raceId;
	}
	
	public Season getSeason()
	{
		return season;
	}
	
	public Track getTrack()
	{
		return track;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getNascarComId()
	{
		return nascarComId;
	}
	
	public Date getDate()
	{
		return date;
	}
	
	public AbstractMap<Integer,Result> getResults()
	{
		return results;
	}
	
	
	/*public AbstractTableModel getTableModel()
	{
		if(resultsTableModel == null)
		{
			resultsTableModel = new AbstractTableModel()
			{
				private static final long serialVersionUID = 1L;

				public int getColumnCount()
				{
					return 7;
				}

				public int getRowCount()
				{
					return results.size();
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
					if(results.containsKey(rowIndex + 1))
					{
						Result r = results.get(rowIndex + 1);
						
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
				
			    public Class<?> getColumnClass(int c) 
			    {
			    	if(getValueAt(0, c) != null)
			    		return getValueAt(0, c).getClass();
			    	else
			    		return "".getClass();
			    }
			};
		}
		
		return resultsTableModel;
	}*/
	
	public void loadResults()
	{
		try
		{
			results = new HashMap<Integer,Result>();
			
			Statement sResults = NASCARData.getSQLConnection().createStatement();
			sResults.execute("SELECT resultId, driverId, car, start, finish, ledLaps, ledMostLaps, penalties FROM nascarResult WHERE raceId=" + raceId
				+ " ORDER BY finish ASC");
			
			ResultSet rsResults = sResults.getResultSet();
			
			while(rsResults.next())
			{
				Driver driver = Driver.getById(rsResults.getInt("driverId"));
				String car = rsResults.getString("car");
				int start = rsResults.getInt("start");
				int finish = rsResults.getInt("finish");
				boolean ledLaps = (rsResults.getInt("ledLaps") == 0) ? false : true;
				boolean ledMostLaps = (rsResults.getInt("ledMostLaps") == 0) ? false : true;
				int penalties = rsResults.getInt("penalties");
				
				Result result = new Result(this, driver, car, start, finish, ledLaps, ledMostLaps, penalties);
				results.put(finish, result);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	
	public static Race getById(int raceId)
	{
		if(racesById.containsKey(raceId))
			return racesById.get(raceId);
		else
			return loadFromDatabase(NASCARData.getSQLConnection(), raceId);		
	}
	
	public static Race loadFromDatabase(Connection conn, int raceId)
	{
		Race race = new Race();
		
		try
		{
			Statement sRace = conn.createStatement();
			sRace.execute("SELECT raceId, trackId, seasonId, name, nascarComId, date FROM nascarRace WHERE raceId=" + raceId);
			
			ResultSet rsRace = sRace.getResultSet();
		
			if(rsRace.next())
			{
				racesById.put(raceId, race);
				
				race.raceId = raceId;
				
				race.season = Season.getById(rsRace.getInt("seasonId"));
				race.track = Track.getById(rsRace.getInt("trackId"));
				
				race.name = rsRace.getString("name");
				race.nascarComId = rsRace.getString("nascarComId");
				race.date = rsRace.getDate("date");
				
				race.loadResults();
				
				return race;
			}
			else
			{
				System.out.println("Unknown raceId: " + raceId);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public String toString()
	{
		return getName();
	}
}
