package com.lightdatasys.nascar.fantasy;

import java.awt.Color;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import com.lightdatasys.nascar.Driver;
import com.lightdatasys.nascar.NASCARData;
import com.lightdatasys.nascar.Race;
import com.lightdatasys.nascar.Standing;

public class FantasyPlayer 
{
	private static HashMap<Integer,FantasyPlayer> playersByUserId = new HashMap<Integer,FantasyPlayer>();

	private int playerId;
	private int userId;
	private String name;
	private Color backgroundColor;
	
	
	protected FantasyPlayer()
	{
	}

	
	public int getPlayerId()
	{
		return playerId;
	}
	
	public int getUserId()
	{
		return userId;
	}
	
	public String getName()
	{
		return name;
	}

	public Color getBackgroundColor()
	{
		return backgroundColor;
	}
	
	
	public static FantasyPlayer getByUserId(int userId)
	{
		if(playersByUserId.containsKey(userId))
			return playersByUserId.get(userId);
		else
			return loadFromDatabase(NASCARData.getSQLConnection(), userId);	
	}
	
	public static void loadAllFromDatabase()
	{
		Connection conn = NASCARData.getSQLConnection();

		try
		{
			Statement sPlayer = conn.createStatement();
			sPlayer.execute("SELECT pu.userId FROM player_user AS pu " +
					"INNER JOIN nascarFantPick AS nf ON pu.userId=nf.userId " +
					"INNER JOIN nascarRace AS nra ON nf.raceId=nra.raceId " +
					"WHERE nra.seasonId=2 " +
					"GROUP BY pu.userId");
			
			ResultSet rsPlayer = sPlayer.getResultSet();
			
			while(rsPlayer.next())
			{
				try
				{
					FantasyPlayer.loadFromDatabase(conn, rsPlayer.getInt("userId"));	
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	public static FantasyPlayer loadFromDatabase(Connection conn, int userId)
	{
		FantasyPlayer player = new FantasyPlayer();
		
		try
		{
			Statement sPlayer = conn.createStatement();
			sPlayer.execute("SELECT p.playerId, u.userId, name, bgcolor FROM player AS p "
					+ "INNER JOIN player_user AS pu ON p.playerId=pu.playerId "
					+ "INNER JOIN user AS u ON pu.userId=u.userId WHERE u.userId=" + userId);
			
			ResultSet rsPlayer = sPlayer.getResultSet();
		
			if(rsPlayer.next())
			{
				playersByUserId.put(userId, player);

				player.userId = userId;
				player.playerId = rsPlayer.getInt("playerId");
				player.name = rsPlayer.getString("name");

				player.backgroundColor 
					= Driver.getColor(rsPlayer.getString("bgcolor"), Color.BLACK);
				
				return player;
			}
			else
			{
				System.out.println("Unknown userId: " + userId);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static FantasyPlayer[] getPlayers()
	{
		return playersByUserId.values().toArray(new FantasyPlayer[playersByUserId.size()]);
	}
	

	public static AbstractMap<Integer,FantasyStanding> getStandings(Race race)
	{
		AbstractMap<Integer,FantasyStanding> standings = new HashMap<Integer,FantasyStanding>();
		Date date = race.getDate();
		
		Connection conn = NASCARData.getSQLConnection();
		
		try
		{
			Statement sStandings = conn.createStatement();
			String sqlStandings = /*String.format(
				"SELECT r.raceId, fp.userId, " +
				"SUM(IF(finish=1,185,IF(finish<=6, 150+(6-finish)*5,IF(finish<=11, 130+(11-finish)*4,IF(finish<=43, 34+(43-finish)*3,0))))+IF(ledLaps>=1,5,0)+IF(ledMostLaps>=1,5,0)) AS points " +
			    "FROM nascarFantPick AS fp " +
			    "INNER JOIN nascarRace AS r ON fp.raceId=r.raceId " +
			    "INNER JOIN user AS u ON fp.userId=u.userId " +
			    "LEFT JOIN nascarResult AS re ON r.raceId=re.raceId AND fp.driverId=re.driverId " +
			    "WHERE seasonId=%1$d AND r.date<\'%2$tY-%2$tm-%2$td\' " +
			    "GROUP BY r.raceId, fp.userId " +
			    "ORDER BY points DESC",*/
				String.format(
				"SELECT raceId FROM nascarRace WHERE seasonId=%1$d AND date<\'%2$tY-%2$tm-%2$td\' ORDER BY date ASC",
				race.getSeason().getId(), race.getDate());
			sStandings.execute(sqlStandings);
			
			for(FantasyPlayer player : FantasyPlayer.getPlayers())
			{
				FantasyStanding standing = new FantasyStanding(player);
				standings.put(player.getUserId(), standing);
			}

			ResultSet rsStandings = sStandings.getResultSet();
			while(rsStandings.next())
			{
				int raceId = rsStandings.getInt("raceId");
				Race r = Race.getById(raceId);

				for(FantasyPlayer player : FantasyPlayer.getPlayers())
				{
					FantasyStanding standing = standings.get(player.getUserId());
					standing.points += r.getFantasyResultByPlayer(player).getRacePoints();
				}
			}
			/*
			while(rsStandings.next())
			{
				int userId = rsStandings.getInt("userId");
				
				FantasyPlayer player = FantasyPlayer.getByUserId(userId);
				FantasyStanding standing = new FantasyStanding(player);
				standings.put(userId, standing);
				
				standing.points = rsStandings.getInt("points");
				System.out.println(standing.points);
			}
			*/
		}
		catch(Exception ex)
		{
			System.err.println("The standings for race " + race.getId() + " cannot be determined.");
			ex.printStackTrace();
		}
		
		return standings;
	}
	
	
	private static Color getColor(String encodedColor, Color alternate)
	{
		try
		{
			return Color.decode(encodedColor);
		}
		catch(Exception ex)
		{
		}
		
		return alternate;
	}
	
	
	public String toString()
	{
		return getName();
	}
}
