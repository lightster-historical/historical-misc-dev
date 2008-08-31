package com.lightdatasys.nascar;

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
	

	public static AbstractMap<Integer,Standing> getStandings(Race race)
	{
		AbstractMap<Integer,Standing> standings = new HashMap<Integer,Standing>();
		Date date = race.getDate();
		
		Connection conn = NASCARData.getSQLConnection();

		Date chaseDate = null;
		try
		{
			Statement sChaseDate = conn.createStatement();
			sChaseDate.execute("SELECT date FROM nascarRace WHERE seasonId=" + race.getSeason().getId() + " ORDER BY date LIMIT 25,1");
			
			ResultSet rsChaseDate = sChaseDate.getResultSet();

			if(rsChaseDate.next())
				chaseDate = rsChaseDate.getDate("date");
		}
		catch(Exception ex)
		{
			System.err.println("The chase date for race " + race.getId() + " cannot be determined.");
			ex.printStackTrace();
		}		
		
		try
		{
			String whereChase = "";
			if(chaseDate != null)
				whereChase = String.format(" AND ra.date<='%1$tY-%1$tm-%1$td'", chaseDate);
			
			Statement sPreChase = conn.createStatement();
			String sqlPreChase = String.format(
					"SELECT d.driverId, COUNT(ra.raceId) starts, SUM(IF(finish=1,1,0)) wins, " +
					"SUM(IF(finish<=5,1,0)) top5s, SUM(IF(finish<=10,1,0)) top10s, " +
					"SUM(IF(finish=1,185,IF(finish<=6, 150+(6-finish)*5,IF(finish<=11, 130+(11-finish)*4,IF(finish<=43, 34+(43-finish)*3,0))))+IF(ledLaps>=1,5,0)+IF(ledMostLaps>=1,5,0)+penalties) AS points " +
					"FROM nascarDriver AS d " +
					"INNER JOIN nascarResult AS re ON d.driverId=re.driverId " +
					"INNER JOIN nascarRace AS ra ON re.raceId=ra.raceId " +
					"WHERE ra.seasonId=%1$d AND ra.date<'%2$tY-%2$tm-%2$td'%3$s " +
					"GROUP BY d.driverId ORDER BY points DESC",
					race.getSeason().getId(), race.getDate(), whereChase);
			sPreChase.execute(sqlPreChase);
			
			ResultSet rsPreChase = sPreChase.getResultSet();
			while(rsPreChase.next())
			{
				int driverId = rsPreChase.getInt("driverId");
				
				Driver driver = Driver.getById(driverId);
				Standing standing = new Standing(driver);
				standings.put(driverId, standing);
				
				standing.starts = rsPreChase.getInt("starts");
				standing.wins = rsPreChase.getInt("wins");
				standing.top10s = rsPreChase.getInt("top10s");
				standing.top5s = rsPreChase.getInt("top5s");
				standing.points = rsPreChase.getInt("points");
			}
			
			if(chaseDate != null && date.compareTo(chaseDate) > 0)
			{
				ArrayList<Standing> sorted = new ArrayList<Standing>();
				sorted.addAll(standings.values());
				Collections.sort(sorted);
				
				for(int i = 0; i < 12; i++)
				{
					sorted.get(i).points = 5000 + sorted.get(i).wins * 10; 
				}
				
				if(chaseDate != null)
					whereChase = String.format(" AND ra.date>'%1$tY-%1$tm-%1$td'", chaseDate);
				
				Statement sPostChase = conn.createStatement();
				String sqlPostChase = String.format(
						"SELECT d.driverId, COUNT(ra.raceId) starts, SUM(IF(finish=1,1,0)) wins, " +
						"SUM(IF(finish<=5,1,0)) top5s, SUM(IF(finish<=10,1,0)) top10s, " +
						"SUM(IF(finish=1,185,IF(finish<=6, 150+(6-finish)*5,IF(finish<=11, 130+(11-finish)*4,IF(finish<=43, 34+(43-finish)*3,0))))+IF(ledLaps>=1,5,0)+IF(ledMostLaps>=1,5,0)+penalties) AS points " +
						"FROM nascarDriver AS d " +
						"INNER JOIN nascarResult AS re ON d.driverId=re.driverId " +
						"INNER JOIN nascarRace AS ra ON re.raceId=ra.raceId " +
						"WHERE ra.seasonId=%1$d AND ra.date<'%2$tY-%2$tm-%2$td'%3$s " +
						"GROUP BY d.driverId ORDER BY points DESC",
						race.getSeason().getId(), race.getDate(), whereChase);
				sPostChase.execute(sqlPostChase);
				
				ResultSet rsPostChase = sPostChase.getResultSet();
				while(rsPostChase.next())
				{
					Standing standing;
					
					int driverId = rsPostChase.getInt("driverId");
					if(standings.containsKey(driverId))
						standing = standings.get(driverId);
					else
					{
						Driver driver = Driver.getById(driverId);
						standing = new Standing(driver);
						standings.put(driverId, standing);
					}
					
					standing.starts += rsPostChase.getInt("starts");
					standing.wins += rsPostChase.getInt("wins");
					standing.top10s += rsPostChase.getInt("top10s");
					standing.top5s += rsPostChase.getInt("top5s");
					standing.points += rsPostChase.getInt("points");
					
					standing.points = -1;
				}
			}
		}
		catch(Exception ex)
		{
			System.err.println("The standings for race " + race.getId() + " cannot be determined.");
			ex.printStackTrace();
		}
		
		return standings;
		
		/*
        
 
        if($raceNo >= 26)
        {
            $i = 0;
            foreach($drivers as $driverId => $driver)
            {
                if($i < 12)
                    $drivers[$driverId]['points'] = 5000 + (10 * $driver['wins']);
                else
                    break;
                $i++;
            }
            
            $query = new Query('SELECT d.*, COUNT(ra.raceId) starts, SUM(IF(finish=1,1,0)) wins, SUM(IF(finish<=5,1,0)) top5s, SUM(IF(finish<=10,1,0)) top10s, SUM(IF(finish=1,185,IF(finish<=6, 150+(6-finish)*5,IF(finish<=11, 130+(11-finish)*4,IF(finish<=43, 34+(43-finish)*3,0))))+IF(ledLaps>=1,5,0)+IF(ledMostLaps>=1,5,0)+penalties) AS points FROM nascarDriver AS d INNER JOIN nascarResult AS re ON d.driverId=re.driverId INNER JOIN nascarRace AS ra ON re.raceId=ra.raceId WHERE ra.seasonId=' . $seasonId . ' AND ra.date<=\'' . $raceDate . '\' AND ra.date>\'' . $chaseDate . '\' GROUP BY d.driverId ORDER BY points DESC');
            $result = $db->execQuery($query);
            while($row = $db->getAssoc($result))
            {
                if(array_key_exists($row['driverId'], $drivers))
                {
                    $drivers[$row['driverId']]['starts'] += $row['starts'];
                    $drivers[$row['driverId']]['wins'] += $row['wins'];
                    $drivers[$row['driverId']]['top5s'] += $row['top5s'];
                    $drivers[$row['driverId']]['top10s'] += $row['top10s'];
                    $drivers[$row['driverId']]['points'] += $row['points'];
                }
                else
                {
                    $drivers[$row['driverId']] = $row; 
                }
            }
        }
        */
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
