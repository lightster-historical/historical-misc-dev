package com.lightdatasys.eltalog;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class MovieLibrary extends Library<Movie>
{
	private static final long serialVersionUID = 200806181106L;
	

	public MovieLibrary() 
		throws NullPointerException
	{
		super(MovieType.getInstance());
	}
	
	
	public String getName()
	{
		return "Movies";
	}
	
	private void writeObject(ObjectOutputStream out)
		throws IOException
	{
		System.out.println("Using MovieLibrary.writeObject");
	}
}
