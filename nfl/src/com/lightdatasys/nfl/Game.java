package com.lightdatasys.nfl;

public class Game 
{
	protected Team awayTeam;
	protected Team homeTeam;
	protected int[] awayScore;
	protected int[] homeScore;
	
	public Game(Team awayTeam, Team homeTeam)
	{
		if(awayTeam == null && homeTeam == null)
			throw new IllegalArgumentException("awayTeam and homeTeam cannot be null");
		else if(awayTeam == null)
			throw new IllegalArgumentException("awayTeam cannot be null");
		else if(homeTeam == null)
			throw new IllegalArgumentException("homeTeam cannot be null");
		
		this.awayTeam = awayTeam;
		this.homeTeam = homeTeam;
	}
}
