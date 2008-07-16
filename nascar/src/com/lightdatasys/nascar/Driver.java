package com.lightdatasys.nascar;

import java.awt.Color;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class Driver 
{
	private static HashMap<Integer,Driver> driversById = new HashMap<Integer,Driver>();
	
	private int driverId;
	private String firstName;
	private String lastName;
	
	private Color fontColor;
	private Color backgroundColor;
	private Color borderColor;
	
	
	protected Driver()
	{
	}


	public Color getFontColor()
	{
		return fontColor;
	}

	public Color getBackgroundColor()
	{
		return backgroundColor;
	}

	public Color getBorderColor()
	{
		return borderColor;
	}
	
	
	public static Driver getById(int driverId)
	{
		if(driversById.containsKey(driverId))
			return driversById.get(driverId);
		else
			return loadFromDatabase(NASCARData.getSQLConnection(), driverId);	
	}
	
	public static void loadAllFromDatabase()
	{
		Connection conn = NASCARData.getSQLConnection();

		try
		{
			Statement sDriver = conn.createStatement();
			sDriver.execute("SELECT driverId FROM nascarDriver ORDER BY firstName, lastName");
			
			ResultSet rsDriver = sDriver.getResultSet();
			
			while(rsDriver.next())
			{
				try
				{
					Driver.loadFromDatabase(conn, rsDriver.getInt("driverId"));	
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

	public static Driver loadFromDatabase(Connection conn, int driverId)
	{
		Driver driver = new Driver();
		
		try
		{
			Statement sDriver = conn.createStatement();
			sDriver.execute("SELECT driverId, firstName, lastName, color, background, border FROM nascarDriver WHERE driverId=" + driverId);
			
			ResultSet rsDriver = sDriver.getResultSet();
		
			if(rsDriver.next())
			{
				driversById.put(driverId, driver);
				
				driver.driverId = driverId;
				driver.firstName = rsDriver.getString("firstName");
				driver.lastName = rsDriver.getString("lastName");

				driver.fontColor 
					= Driver.getColor(rsDriver.getString("color"), Color.WHITE);
				driver.backgroundColor 
					= Driver.getColor(rsDriver.getString("background"), Color.BLACK);
				driver.borderColor 
					= Driver.getColor(rsDriver.getString("border"), Color.GRAY);
				
				return driver;
			}
			else
			{
				System.out.println("Unknown driverId: " + driverId);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Driver[] getDrivers()
	{
		return driversById.values().toArray(new Driver[driversById.size()]);
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
		return firstName + " " + lastName;
	}
}
