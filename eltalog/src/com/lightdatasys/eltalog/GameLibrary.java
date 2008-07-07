package com.lightdatasys.eltalog;

public class GameLibrary extends Library<Movie>
{
	private static final long serialVersionUID = 200806181106L;
	

	public GameLibrary() 
		throws NullPointerException
	{
		super(MovieType.getInstance());
	}
	
	
	public String getName()
	{
		return "Games";
	}
}
