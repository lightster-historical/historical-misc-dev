package com.lightdatasys.eltalog;

public class BookLibrary extends Library<Movie>
{
	private static final long serialVersionUID = 200806181106L;
	

	public BookLibrary() 
		throws NullPointerException
	{
		super(MovieType.getInstance());
	}
	
	
	public String getName()
	{
		return "Books";
	}
}
