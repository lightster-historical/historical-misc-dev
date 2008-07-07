package com.lightdatasys.eltalog;

public class Role
{
	private Person person;
	private String title;
	
	
	public Role(Person person, String title)
	{
		this.person = person;
		this.title = title;
	}
	
	
	public void setPerson(Person person) {this.person = person;}
	public void setTitle(String title) {this.title = title;}
	
	public Person getPerson() {return person;}
	public String getTitle() {return title;}
}
