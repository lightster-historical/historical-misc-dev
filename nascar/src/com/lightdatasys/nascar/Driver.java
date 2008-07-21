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

	
	public int getId()
	{
		return driverId;
	}
	
	public String getFirstName()
	{
		return firstName;
	}
	
	public String getLastName()
	{
		return lastName;
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
	
	
	public static Color getColor(String encodedColor, Color alternate)
	{
		if(encodedColor.length() == 7 && encodedColor.charAt(0) == '#')
		{
			encodedColor = encodedColor.substring(1);
		}
		
		if(encodedColor.length() == 6 && encodedColor.matches("[0-9a-fA-F]{6,6}"))
		{
			int r = 0, g = 0, b = 0;
			
			r = Integer.parseInt(encodedColor.substring(0, 2), 16);
			g = Integer.parseInt(encodedColor.substring(2, 4), 16);
			b = Integer.parseInt(encodedColor.substring(4, 6), 16);
			
			return new Color(r, g, b);
		}
		
		return alternate;
	}
	
	
	public String toString()
	{
		return firstName + " " + lastName;
	}
}
