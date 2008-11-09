package com.lightdatasys.nascar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class NASCARData
{
	public static final String SQL_HOST = "lightdatasys.light";
	public static final String SQL_DB_NAME = "litesign_alpha";
	public static final String SQL_USERNAME = "litesign_mlight";
	public static final String SQL_PASSWORD = "***REMOVED***";
	
	private static Connection conn;
	
	
	
	protected NASCARData()
	{
	}
	
	
	public static Connection getSQLConnection()
	{
		if(conn == null)
		{
			return openSQLConnection();
		}
		else
			return conn;
	}
	
	protected static Connection openSQLConnection()
	{
        try 
        {
            // The newInstance() call is a work around for some
            // broken Java implementations

            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } 
        catch (Exception ex)
        {
            // handle the error
        	ex.printStackTrace();
        }

		try
		{
			conn = DriverManager.getConnection(
				"jdbc:mysql://" + SQL_HOST + "/" + SQL_DB_NAME + "?user=" + SQL_USERNAME + "&password=" + SQL_PASSWORD);

			return conn;
		} 
		catch (SQLException ex) 
		{
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		 
		return null;
	}
	
	public static void closeSQLConnection()
	{
		try
		{
			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
