package com.lightdatasys.eltalog;

public class Person 
{
	private String firstName;
	private String lastName;
	
	
	public Person(String first, String last)
	{
		firstName = first;
		lastName = last;
	}
	

	public void setFirstName(String name) {firstName = name;}
	public void setLastName(String name) {lastName = name;}
	
	public String getFirstName() {return firstName;}
	public String getLastName() {return lastName;}
	public String toString() {return firstName + " " + lastName;}
}
