package com.lightdatasys.nascar.fantasy.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.lightdatasys.nascar.Result;

public class SettingsServer implements Runnable
{
	private Settings settings;
	
	
	public SettingsServer(Settings settings)
	{
		this.settings = settings;
	}
	
	public void run()
	{
		boolean running = true;
		while(running)
		{
			ServerSocket serverSocket = null;
	        try
	        {
	            serverSocket = new ServerSocket(2424);
	        } 
	        catch (IOException e) 
	        {
	            System.err.println("Could not listen on port: 2424.");
	            System.exit(1);
	        }
	
	        Socket clientSocket = null;
	        try 
	        {
	            clientSocket = serverSocket.accept();
	        }
	        catch (IOException e)
	        {
	            System.err.println("Accept failed.");
	            System.exit(1);
	        }
	
	        PrintWriter out;
	        BufferedReader in;
	        try
	        {
	        	out = new PrintWriter(clientSocket.getOutputStream(), true);
	        	in = new BufferedReader(
	        		new InputStreamReader(clientSocket.getInputStream()));
	        	String inputLine, outputLine;

	        	out.println("gogogo");

	        	while ((inputLine = in.readLine()) != null)
	        	{
	        		String[] word = inputLine.split("[ ]+");
	        		boolean done = false;

	        		if(word.length >= 3)
	        		{
	        			if(word[0].toLowerCase().equals("set"))
	        			{
	        				try
	        				{
	        					if(word[1].toLowerCase().equals("fps"))
	        					{
	        						float fps = Float.parseFloat(word[2]);
	        						if(fps > .05)
	        						{
	        							settings.setFPS(fps);
	        							done = true;
	        						}
	        					}
	        					else if(word[1].toLowerCase().equals("ups"))
	        					{
	        						float ups = Float.parseFloat(word[2]);
	        						if(ups > .05)
	        						{
	        							settings.setUPS(ups);
	        							done = true;
	        						}
	        					}
	        					else if(word[1].toLowerCase().equals("swapperiod"))
	        					{
	        						long swapPeriod = Long.parseLong(word[2]);
	        						if(0 <= swapPeriod && swapPeriod < 5000)
	        						{
	        							settings.setSwapPeriod(swapPeriod);
	        							done = true;
	        						}
	        					}
	        					else if(word[1].toLowerCase().equals("scrollspeed"))
	        					{
	        						float scrollSpeed = Float.parseFloat(word[2]);
	        						
        							settings.setScrollSpeed(scrollSpeed);
        							done = true;
	        					}
	        					else if(word[1].toLowerCase().equals("resultmode") ||
        							word[1].toLowerCase().equals("fantasymode1") ||
        							word[1].toLowerCase().equals("fantasymode2"))
	        					{
	        						int value = Integer.parseInt(word[2]);
	        						if(0 <= value && value < 5000)
	        						{
	        							if(word[1].toLowerCase().equals("resultmode"))
	        							{
	        								switch(value)
	        								{
		        								case 1: settings.setResultMode(ResultCell.Mode.LAPS_LED); break;
		        								case 2: settings.setResultMode(ResultCell.Mode.LEADER_INTERVAL); break;
		        								case 3: settings.setResultMode(ResultCell.Mode.LOCAL_INTERVAL); break;
		        								case 4: settings.setResultMode(ResultCell.Mode.RACE_POINTS); break;
		        								case 5: settings.setResultMode(ResultCell.Mode.SEASON_POINTS); break;
		        								case 6: settings.setResultMode(ResultCell.Mode.LAST_LAP_POSITION); break;
		        								case 7: settings.setResultMode(ResultCell.Mode.POSITION_CHANGE); break;
		        								default: settings.setResultMode(ResultCell.Mode.POSITION); break;
	        								}
	        							}
	        							else
	        							{
	        								FantasyResultCell.Mode mode;
	        								switch(value)
	        								{
		        								case 1: mode = FantasyResultCell.Mode.DRIVER_RACE_POINTS; break;
		        								case 2: mode = FantasyResultCell.Mode.RACE_POINTS; break;
		        								case 3: mode = FantasyResultCell.Mode.SEASON_POINTS; break;
		        								case 4: mode = FantasyResultCell.Mode.LAST_LAP_POSITION; break;
		        								case 5: mode = FantasyResultCell.Mode.POSITION_CHANGE; break;
		        								default: mode = FantasyResultCell.Mode.POSITION; break;
	        								}

	        								if(word[1].toLowerCase().equals("fantasymode1"))
	        									settings.setFantasyMode1(mode);
	        								else
	        									settings.setFantasyMode2(mode);
	        							}

	        							done = true;
	        						}
	        					}
	        				}
	        				catch(Exception ex)
	        				{
	        				}
	        			}
	        		}
	        		else if(word.length == 2)
	        		{
	        			if(word[0].toLowerCase().equals("get"))
	        			{
        					if(word[1].toLowerCase().equals("fps"))
        					{
        						out.println(settings.getFPS());
        						done = true;
        					}
        					else if(word[1].toLowerCase().equals("ups"))
        					{
        						out.println(settings.getUPS());
        						done = true;
        					}
        					else if(word[1].toLowerCase().equals("swapperiod"))
        					{
        						out.println(settings.getSwapPeriod());
        						done = true;
        					}
        					else if(word[1].toLowerCase().equals("scrollspeed"))
        					{
        						out.println(settings.getScrollSpeed());
        						done = true;
        					}
        					else if(word[1].toLowerCase().equals("resultmode"))
    						{
        						int value = 0;
								switch(settings.getResultMode())
								{
    								case LAPS_LED: value = 1; break;
    								case LEADER_INTERVAL: value = 2; break;
    								case LOCAL_INTERVAL: value = 3; break;
    								case RACE_POINTS: value = 4; break;
    								case SEASON_POINTS: value = 5; break;
    								case LAST_LAP_POSITION: value = 6; break;
    								case POSITION_CHANGE: value = 7; break;
    								default: value = 0; break;
								}
								
        						out.println(value);
        						done = true;
    						}
        					else if(word[1].toLowerCase().equals("fantasymode1") ||
    							word[1].toLowerCase().equals("fantasymode2"))
        					{
								FantasyResultCell.Mode mode;
								if(word[1].toLowerCase().equals("fantasymode1"))
									mode = settings.getFantasyMode1();
								else
									mode = settings.getFantasyMode2();

        						int value = 0;
								switch(mode)
								{
    								case DRIVER_RACE_POINTS: value = 1; break;
    								case RACE_POINTS: value = 2; break;
    								case SEASON_POINTS: value = 3; break;
    								case LAST_LAP_POSITION: value = 4; break;
    								case POSITION_CHANGE: value = 5; break;
    								default: value = 0; break;
								}
								
        						out.println(value);
        						done = true;
        					}
        				}
        			}

	        		if(done)
	        			out.println("done");
	        		else
	        			out.println("nope");
	        		System.out.println(inputLine + " " + done);
	        	}

	        	out.close();
	        	in.close();
	        	clientSocket.close();
	        	serverSocket.close();
	        }
	        catch(IOException ex)
	        {
	        	ex.printStackTrace();
	        }
	        finally
	        {
	        }
		}
	}
}
