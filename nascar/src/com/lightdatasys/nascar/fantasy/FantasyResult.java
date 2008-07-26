package com.lightdatasys.nascar.fantasy;

import java.util.AbstractMap;
import java.util.ArrayList;

import com.lightdatasys.nascar.Driver;
import com.lightdatasys.nascar.Race;
import com.lightdatasys.nascar.Result;
import com.lightdatasys.nascar.Standing;

public class FantasyResult implements Comparable<FantasyResult>
{
	private Race race;
	private FantasyPlayer player;
	private int finish;
	
	private ArrayList<String> picks;
	
	
	public FantasyResult(Race race, FantasyPlayer player, int finish)
	{		
		this.race = race;
		this.player = player;
		this.finish = finish;
		
		this.picks = new ArrayList<String>();
	}
	
	
	public Race getRace()
	{
		return race;
	}
	
	public FantasyPlayer getPlayer()
	{
		return player;
	}
	
	public int getFinish()
	{
		return finish;
	}
	
	public ArrayList<String> getPicks()
	{
		return picks;
	}
	
	public int getSeasonPoints()
	{
		AbstractMap<Integer,FantasyStanding> standings = getRace().getFantasyStandings();
		if(standings.containsKey(player.getUserId()))
		{
			return standings.get(player.getUserId()).points + getRacePoints();
		}
		
		return getRacePoints();
	}
	
	public int getRacePoints()
	{
		int points = 0;
		
		if(finish == 1)
			points = 185;
		else if(finish <= 6)
			points = 150 + (6 - finish) * 5;
		else if(finish <= 11)
			points = 130 + (11 - finish) * 4;
		else if(finish <= 43)
			points = 34 + (43 - finish) * 3;
        
        int max = 100;
        points -= 185 - max;
		
		return points;
	}
	
	public int getDriverRacePoints()
	{
		int points = 0;
		
		for(String car : picks)
		{
			points += getRace().getResultByCarNo(car).getRacePoints();
		}
		
		return points;
	}
	
	
	public void addDriver(Driver driver)
	{
		Result result = getRace().getResultByDriver(driver);
		if(result != null)
		{
			addCar(result.getCar());
		}
	}
	
	public void addCar(String car)
	{
		if(getRace().getResultByCarNo(car) != null && !picks.contains(car))
			picks.add(car);
	}
	
	
	public void setFinish(int finish)
	{
		if(race != null)
			race.setFantasyFinish(player, finish);
		
		this.finish = finish;
	}
	
	
	
 	public int compareTo(FantasyResult obj)
 	{
 		if(getDriverRacePoints() < obj.getDriverRacePoints())
 			return 1;
 		else if(getDriverRacePoints() == obj.getDriverRacePoints())
 			return 0;
 		else
 			return -1;
 	}
}
