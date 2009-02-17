package com.lightdatasys.nascar.live.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.lightdatasys.nascar.live.gui.cell.FantasyResultCell;
import com.lightdatasys.nascar.live.gui.cell.ResultCell;

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
	        					else if(word[1].toLowerCase().equals("resultmode1") ||
        							word[1].toLowerCase().equals("resultmode2") ||
        							word[1].toLowerCase().equals("fantasymode1") ||
        							word[1].toLowerCase().equals("fantasymode2"))
	        					{
	        						int value = Integer.parseInt(word[2]);
	        						if(0 <= value && value < 5000)
	        						{
	        							if(word[1].toLowerCase().equals("resultmode1") ||
        									word[1].toLowerCase().equals("resultmode2"))
	        							{
	        								ResultCell.Mode mode;
	        								switch(value)
	        								{
		        								case 1: mode = ResultCell.Mode.LAPS_LED; break;
		        								case 2: mode = ResultCell.Mode.LEADER_INTERVAL; break;
		        								case 3: mode = ResultCell.Mode.LOCAL_INTERVAL; break;
		        								case 4: mode = ResultCell.Mode.RACE_POINTS; break;
		        								case 5: mode = ResultCell.Mode.SEASON_POINTS; break;
		        								case 6: mode = ResultCell.Mode.LAST_LAP_POSITION; break;
		        								case 7: mode = ResultCell.Mode.POSITION_CHANGE; break;
		        								case 8: mode = ResultCell.Mode.SPEED; break;
		        								case 9: mode = ResultCell.Mode.SEASON_RANK; break;
		        								case 10: mode = ResultCell.Mode.LEADER_POINTS_DIFF; break;
		        								case 11: mode = ResultCell.Mode.LOCAL_POINTS_DIFF; break;
		        								default: mode = ResultCell.Mode.POSITION; break;
	        								}

	        								if(word[1].toLowerCase().equals("resultmode1"))
	        									settings.setResultMode1(mode);
	        								else
	        									settings.setResultMode2(mode);
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
        					else if(word[1].toLowerCase().equals("resultmode1") ||
    							word[1].toLowerCase().equals("fantasymode2"))
    						{
								ResultCell.Mode mode;
								if(word[1].toLowerCase().equals("resultmode1"))
									mode = settings.getResultMode1();
								else
									mode = settings.getResultMode2();
								
        						int value = 0;
								switch(mode)
								{
    								case LAPS_LED: value = 1; break;
    								case LEADER_INTERVAL: value = 2; break;
    								case LOCAL_INTERVAL: value = 3; break;
    								case RACE_POINTS: value = 4; break;
    								case SEASON_POINTS: value = 5; break;
    								case LAST_LAP_POSITION: value = 6; break;
    								case POSITION_CHANGE: value = 7; break;
    								case SPEED: value = 8; break;
    								case SEASON_RANK: value = 9; break;
    								case LEADER_POINTS_DIFF: value = 10; break;
    								case LOCAL_POINTS_DIFF: value = 11; break;
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
