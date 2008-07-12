package com.lightdatasys.nascar;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class Season
{
	private static HashMap<Integer,Season> seasonsById = new HashMap<Integer,Season>();
	
	private ArrayList<Race> races;
	
	private int seasonId;
	private int year;

	
	protected Season()
	{
		races = new ArrayList<Race>();
	}
	
	
	public int getId()
	{
		return seasonId;
	}
	
	public int getYear()
	{
		return year;
	}
	
	public Race[] getRaces()
	{
		return races.toArray(new Race[races.size()]);
	}
	
	
	public static Season getById(int seasonId)
	{
		if(seasonsById.containsKey(seasonId))
			return seasonsById.get(seasonId);
		else
			return loadFromDatabase(NASCARData.getSQLConnection(), seasonId);	
	}

	public static Season loadFromDatabase(Connection conn, int seasonId)
	{
		Season season = new Season();
		
		try
		{
			Statement sSeason = conn.createStatement();
			sSeason.execute("SELECT seasonId, year FROM nascarSeason WHERE seasonId=" + seasonId);
			
			ResultSet rsSeason = sSeason.getResultSet();
		
			if(rsSeason.next())
			{
				seasonsById.put(seasonId, season);
				
				season.seasonId = seasonId;
				season.year = rsSeason.getInt("year");

				Statement sRace = conn.createStatement();
				sRace.execute("SELECT raceId FROM nascarRace WHERE seasonId=" + seasonId
					+ " ORDER BY date ASC");
				
				ResultSet rsRace = sRace.getResultSet();
				
				while(rsRace.next())
				{
					season.races.add(Race.getById(rsRace.getInt("raceId")));
				}
				
				return season;
			}
			else
			{
				System.out.println("Unknown seasonId: " + seasonId);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
