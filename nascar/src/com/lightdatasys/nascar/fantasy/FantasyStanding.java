package com.lightdatasys.nascar.fantasy;


public class FantasyStanding implements Comparable<FantasyStanding>
{
	public FantasyPlayer player;
	public int points;
	
	
	public FantasyStanding(FantasyPlayer player)
	{
		this.player = player;
		this.points = 0;
	}
	
	public FantasyStanding(FantasyPlayer player, int points)
	{
		this.player = player;
		this.points = points;
	}

	
 	public int compareTo(FantasyStanding obj)
 	{
 		if(points < obj.points)
 			return 1;
 		else if(points == obj.points)
 			return 0;
 		else
 			return -1;
 	}
}
