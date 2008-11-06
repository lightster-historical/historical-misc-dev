package com.lightdatasys.nfl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LiveScoreParser 
{
	//{gc:[["scores","update",[["29631",["OAK","BLT","V"],["02:48","4"],[3,10,"OPP",36],[0,0,10,0,,10],[2,17,3,7,,29],[["J.Russell",12,27,206],["C.Schilens",3,76]],[["J.Flacco",12,24,140],["D.Williams",1,70]],0],["29632",["ARZ","CAR","H"],["03:52","4"],[2,11,"OWN",36],[3,7,13,0,,23],[0,3,21,3,,27],[["K.Warner",35,49,381],["L.Fitzgerald",7,115]],[["J.Delhomme",19,27,231],["S.Smith",5,117]],0],["29633",["TB","DAL","V"],["00:34","4"],[2,7,"OPP",20],[6,0,3,0,,9],[0,10,3,0,,13],[["J.Garcia",27,41,226],["A.Bryant",7,46]],[["B.Johnson",19,33,122],["M.Barber",25,71]],1],["29634",["WAS","DET","H"],["00:52","4"],[3,3,"OWN",45],[3,3,10,9,,25],[7,3,0,7,,17],[["J.Campbell",23,28,328],["S.Moss",9,140]],[["D.Orlovsky",20,33,221],["S.McDonald",5,68]],0],["29635",["BUF","MIA","V"],["01:56","4"],[3,19,"OWN",49],[3,6,7,0,,16],[7,0,10,8,,25],[["T.Edwards",20,34,208],["L.Evans",7,116]],[["C.Pennington",22,30,314],["T.Ginn",7,175]],0],["29636",["SL","NE","V"],["01:26","4"],[3,15,"OPP",38],[3,7,3,3,,16],[7,6,0,10,,23],[["M.Bulger",17,32,294],["D.Avery",6,163]],[["M.Cassel",21,33,267],["R.Moss",7,102]],0],["29637",["SD","NO","H"],["07:21","4"],[1,10,"OWN",19],[3,14,3,10,,30],[3,20,7,7,,37],[["P.Rivers",21,31,282],["L.Tomlinson",18,103]],[["D.Brees",28,37,322],["L.Moore",5,88]],0],["29638",["KC","NYJ","H"],["01:00","4"],[1,10,"OWN",30],[0,14,3,7,,24],[7,7,7,7,,28],[["T.Thigpen",23,32,256],["D.Bowe",5,87]],[["B.Favre",28,40,290],["J.Cotchery",9,102]],0],["29639",["ATL","PHI","V"],["03:55","4"],[1,10,"OWN",30],[0,7,0,7,,14],[0,10,7,3,,20],[["M.Ryan",19,37,239],["R.White",7,103]],[["D.McNabb",18,33,245],["B.Westbrook",18,129]],0],["29640",["CLV","JAX",],["15:00","Pregame"],[,,,],[,,,,,],[,,,,,],[["D.Anderson",0,0,0],["J.Lewis",0,0]],[["D.Garrard",0,0,0],["F.Taylor",0,0]],0],["29641",["NYG","PIT",],["15:00","Pregame"],[,,,],[,,,,,],[,,,,,],[["E.Manning",0,0,0],["B.Jacobs",0,0]],[["B.Roethlisberger",0,0,0],["M.Moore",0,0]],0],["29642",["SEA","SF",],["15:00","Pregame"],[,,,],[,,,,,],[,,,,,],[["S.Wallace",0,0,0],["J.Jones",0,0]],[["J.O'Sullivan",0,0,0],["M.Robinson",0,0]],0],["29662",["CIN","HST",],["15:00","Pregame"],[,,,],[,,,,,],[,,,,,],[["R.Fitzpatrick",0,0,0],[,,]],[["M.Schaub",0,0,0],["S.Slaton",0,0]],0]]]]}
	public static void parseFile(File file)
	{
		try
		{
			StringBuffer buff = new StringBuffer();
			String str;
			
			BufferedReader istream = new BufferedReader(new FileReader(file));
			while((str = istream.readLine()) != null)
			{
				buff.append(str);
			}
			
			parseScore(buff.toString());
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static void parseScore(String str)
	{
		Pattern pattern = Pattern.compile("\\{gc:\\[\\[\"\\w*?\",\"\\w*?\",\\[(.*?)\\]\\]\\]\\}");
		Matcher matcher = pattern.matcher((CharSequence)str);

		if(matcher.matches())
		{
			String games = matcher.group(1);
			System.out.println(games);
			parseGames(games);
		}
		else
		{
			System.err.println("Input score string is not in the correct format. ");
			System.err.println("\tInput string: " + str);
			System.err.println("\tValid pattern: " + pattern.pattern());
		}
	}
	
	public static void parseGames(String str)
	{
		String sub = str;
		Pattern pattern = Pattern.compile(",?" +
				"(\\[" +
					"\"([0-9]*?)\"," +
					"\\[\"(\\w*?)\",\"(\\w*?)\",(\"[VH]?\")?\\]," +
					"\\[\"([0-9]*?:[0-9]*?)\",\"(\\w*?)\"\\]," +
					"\\[([0-9]*?),([0-9]*?),(?:\"(OWN|OPP)?\")?,([0-9]*?)\\]," +
					"\\[([0-9]*?),([0-9]*?),([0-9]*?),([0-9]*?),([0-9]*?),([0-9]*?)\\]," +
					"\\[([0-9]*?),([0-9]*?),([0-9]*?),([0-9]*?),([0-9]*?),([0-9]*?)\\]," +
					"(\\[\\[(?:\".*?\")?,[0-9]*?,[0-9]*?,[0-9]*?\\],\\[(?:\".*?\")?,[0-9]*?,[0-9]*?\\]\\],\\[\\[(?:\".*?\")?,[0-9]*?,[0-9]*?,[0-9]*?\\],\\[(?:\".*?\")?,[0-9]*?,[0-9]*?\\]\\]),(.*?)\\])(.*?)");
		//System.out.println(pattern.pattern());

		Matcher matcher = null;
		do
		{
			matcher = pattern.matcher((CharSequence)sub);
			
			if(matcher.matches())
			{
				//String games = matcher.group(1);
				for(int i = 0; i <= matcher.groupCount(); i++)
					System.out.println(matcher.group(i));
				//System.out.println(games);
				//parseGamesString(games);
				
				sub = matcher.group(matcher.groupCount());
			}
			else
			{
				System.err.println("Input game string is not in the correct format. ");
				System.err.println("\tInput string: " + sub);
				System.err.println("\tValid pattern: " + pattern.pattern());
				matcher = null;
			}
		}
		while(!sub.trim().equals("") && matcher != null);
		
		//gameId
		//awayTeam
		//homeTeam
		//possession: visitor/home (V/H)
		//time (mm:ss)
		//quarter
		//down
		//yards to go
		//field position (OWN/OPP)
		//yard line
		//q1,q2,q3,q4,OT, and total away score
		//q1,q2,q3,q4,OT, and total home score
		//away QB, completions, attempts, yards
		//away receiver, receptions, yards
		//home QB, completions, attempts, yards
		//home receiver, receptions, yards
	}
	
	
	public static void main(String args[])
	{
		parseFile(new File("scoresPage3.json"));
	}
}
