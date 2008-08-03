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
	private int finish; // with tie breaks (to allow for storing results by a unique finish position)
	private int finishActual; // actual finish (with possible ties)
	private int finishLastActual;
	private int finishChange;
	
	private ArrayList<String> picks;
	
	
	public FantasyResult(Race race, FantasyPlayer player, int finish)
	{		
		this.race = race;
		this.player = player;
		this.finish = finish;
		this.finishActual = finish;
		this.finishLastActual = finish;
		this.finishChange = 0;
		
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
	
	public int getActualFinish()
	{
		return finishActual;
	}
	
	public int getLastActualFinish()
	{
		return finishLastActual;
	}
	
	public int getPositionChange()
	{
		return finishChange;
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
		
		int pos = finishActual;
		if(pos == 1)
			points = 185;
		else if(pos <= 6)
			points = 150 + (6 - pos) * 5;
		else if(pos <= 11)
			points = 130 + (11 - pos) * 4;
		else if(pos <= 43)
			points = 34 + (43 - pos) * 3;
        
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
	
	public void setActualFinish(int finish)
	{
		this.finishActual = finish;
	}
	
	public void setLastActualFinish(int finish)
	{
		this.finishLastActual = finish;
	}
	
	public void setPositionChange(int change)
	{
		this.finishChange = change;
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
