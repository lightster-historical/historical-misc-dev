package com.lightdatasys.nascar.fantasy;

import java.awt.Color;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import com.lightdatasys.nascar.Driver;
import com.lightdatasys.nascar.NASCARData;

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
