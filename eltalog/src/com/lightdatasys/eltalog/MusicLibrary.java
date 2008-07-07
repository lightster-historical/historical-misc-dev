package com.lightdatasys.eltalog;

public class MusicLibrary extends Library<Movie>
{
	private static final long serialVersionUID = 200806181106L;
	

	public MusicLibrary() 
		throws NullPointerException
	{
		super(MovieType.getInstance());
	}
	
	
	public String getName()
	{
		return "Music";
	}
}
