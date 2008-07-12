package com.lightdatasys.nascar;

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
	
	
	protected Driver()
	{
	}
	

	
	
	public static Driver getById(int driverId)
	{
		if(driversById.containsKey(driverId))
			return driversById.get(driverId);
		else
			return loadFromDatabase(NASCARData.getSQLConnection(), driverId);	
	}

	public static Driver loadFromDatabase(Connection conn, int driverId)
	{
		Driver driver = new Driver();
		
		try
		{
			Statement sDriver = conn.createStatement();
			sDriver.execute("SELECT driverId, firstName, lastName FROM nascarDriver WHERE driverId=" + driverId);
			
			ResultSet rsDriver = sDriver.getResultSet();
		
			if(rsDriver.next())
			{
				driversById.put(driverId, driver);
				
				driver.driverId = driverId;
				driver.firstName = rsDriver.getString("firstName");
				driver.lastName = rsDriver.getString("lastName");
				
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
	
	
	public String toString()
	{
		return firstName + " " + lastName;
	}
}
