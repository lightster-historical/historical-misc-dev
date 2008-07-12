package com.lightdatasys.nascar;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class Track
{
	private static HashMap<Integer,Track> tracksById = new HashMap<Integer,Track>();
	
	private int trackId;
	private String name;
	private String location;
	private float length;

	
	protected Track()
	{
	}
	
	
	public int getId()
	{
		return trackId;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getLocation()
	{
		return location;
	}
	
	public float getLength()
	{
		return length;
	}
	
	
	public static Track getById(int trackId)
	{
		if(tracksById.containsKey(trackId))
			return tracksById.get(trackId);
		else
			return loadFromDatabase(NASCARData.getSQLConnection(), trackId);	
	}

	public static Track loadFromDatabase(Connection conn, int trackId)
	{
		Track track = new Track();
		
		try
		{
			Statement statement = conn.createStatement();
			statement.execute("SELECT trackId, name, location, length FROM nascarTrack WHERE trackId=" + trackId);
			
			ResultSet resultSet = statement.getResultSet();
		
			if(resultSet.next())
			{
				tracksById.put(trackId, track);
				
				track.trackId = trackId;
				track.name = resultSet.getString("name");
				track.location = resultSet.getString("location");
				track.length = resultSet.getFloat("length");
				
				return track;
			}
			else
			{
				System.out.println("Unknown trackId: " + trackId);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
