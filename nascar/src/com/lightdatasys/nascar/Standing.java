package com.lightdatasys.nascar;


public class Standing implements Comparable<Standing>
{
	public Driver driver;
	public int starts;
	public int wins;
	public int top5s;
	public int top10s;
	public int points;
	
	
	public Standing(Driver driver)
	{
		this.driver = driver;
		this.starts = 0;
		this.wins = 0;
		this.top5s = 0;
		this.top10s = 0;
		this.points = 0;
	}
	
	public Standing(Driver driver, int starts, int wins, int top5s, int top10s, int points)
	{
		this.driver = driver;
		this.starts = starts;
		this.wins = wins;
		this.top5s = top5s;
		this.top10s = top10s;
		this.points = points;
	}

	
 	public int compareTo(Standing obj)
 	{
 		if(points < obj.points)
 			return 1;
 		else if(points == obj.points)
 			return 0;
 		else
 			return -1;
 	}
}
