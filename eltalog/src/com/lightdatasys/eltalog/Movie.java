package com.lightdatasys.eltalog;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;

import com.lightdatasys.eltalog.field.DateField;
import com.lightdatasys.eltalog.field.TextField;
import com.lightdatasys.io.InputUtility;
import com.lightdatasys.io.OutputUtility;

public class Movie extends Item
{
	private static final long serialVersionUID = 1L;
	protected final static long CLASS_VERSION = 2008062201L;
	
	private ArrayList<Role> characters; 
	
	
	public Movie()
	{
		super();

		fields.put("title", new TextField("Title"));
		fields.put("releaseDate", new DateField("Date"));
		
		characters = new ArrayList<Role>();
	}
	
	public Movie(String title)
	{
		this();
		
		setTitle(title);
	}
	

	public void setTitle(String title)
	{
		((TextField)fields.get("title")).setValue(title);
	}
	
	public void setReleaseDate(Date date) 
	{
		((DateField)fields.get("releaseDate")).setDate(date);
	}
	
	
	public String getTitle() 
	{
		return ((TextField)fields.get("title")).getValue();
	}
	
	public Date getReleaseDate() 
	{
		return ((DateField)fields.get("releaseDate")).getDate();
	}
	
	public Role[] getCharacters() 
	{
		return characters.toArray(new Role[characters.size()]);
	}

	
	public ItemType<Movie> getItemType()
	{
		return MovieType.getInstance();
	}
	
	
	public Object getColumn(int column)
		throws ArrayIndexOutOfBoundsException
	{
		return getItemType().getColumn(this, column);
	}
	
	
	//@Override
	private void readObject(ObjectInputStream in) 
		throws IOException, ClassNotFoundException 
	{
		//System.out.println("Using Movie.readObject");
		long objVersion = in.readLong();

		if(objVersion == 2008062201L)
		{
			characters = new ArrayList<Role>();
			InputUtility.readCollection(in, characters, Role.class);
		}
		else
		{
			throw new IOException("Unknown version for Movie object: " + objVersion);
		}
	}

	//@Override
	private void writeObject(ObjectOutputStream out)
		throws IOException
	{
		System.out.println("Using Movie.writeObject");
		out.writeLong(2008062201L);
		OutputUtility.writeCollection(out, characters);
	}
}
