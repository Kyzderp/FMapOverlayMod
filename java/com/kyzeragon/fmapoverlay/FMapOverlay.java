package com.kyzeragon.fmapoverlay;

import java.util.HashMap;
import java.util.LinkedList;

public class FMapOverlay 
{
	public boolean doneTakingLines;
	private LinkedList<String> lines;
	private HashMap<Character, String> factions;
	private Chunk[][] map;

	public FMapOverlay() 
	{
		this.lines = new LinkedList<String>();
		this.factions = new HashMap<Character, String>();
		this.map = new Chunk[39][8];
		this.doneTakingLines = false;
	}

	public void addLine(String line)
	{
		lines.addLast(line);
		//		System.out.println("Added line, size is now " + lines.size());
		if (lines.size() >= 10 && this.doneTakingLines)
			this.parseMap();
	}

	public boolean parseMap()
	{
		if (lines.size() < 1)
			return false;
		///// Read the first line to get the current faction /////
		String line = lines.get(0);
		int originX = Integer.parseInt(line.substring(line.indexOf("(") + 1, line.indexOf(",")));
		int originZ = Integer.parseInt(line.substring(line.indexOf(",") + 1, line.indexOf(")")));
		int nameStart = line.indexOf(")") + 6;
		String originFac = line.substring(nameStart, line.indexOf("§", nameStart));
		System.out.println("Faction " + originFac + " at " + originX + " " + originZ);

		///// Parse the bottom lines for the character: faction name /////
		for (int i = 9; i < lines.size(); i++)
		{ // §r§6/: SafeZone §r§f\: Kussen§r
			line = lines.get(i);
			line = line.replaceAll("§.?", ""); // /: SafeZone \: Kussen
			if (line.length() < 3) // all wilderness! :O except maybe current one
				break;
			for (int j = 0; j < line.length(); j++) // loop through the characters
			{
				
			}
			
		}
		
		///// Add the current chunk's char + name if it's not already inside / isn't wilderness /////
		if (!factions.containsValue(originFac))
			factions.put('+', originFac);
			
		
		///// Parse the actual map and add the chunks to the 2D array /////
		for (int z = 0; z < 8; z++)
		{
			String currLine = this.lines.get(z + 1);
			int currZ = originZ - 4 + z;
			for (int x = 0; x < 39; x++)
			{
				if (x < 3 && z < 3)
					continue;
				int currX = originX - 19 + x;
				
			}
		}
		return true;
	}
	//		§r§6____________.[ §r§2(-102,-43) §r§2Wilderness§r§6 ].______________§r
	//		§r§6\§r§6N§r§6/§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r
	//		§r§6W§r§6+§r§6E§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r
	//		§r§6/§r§cS§r§6\§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r
	//		§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r
	//		§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§a\§r§b+§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r
	//		§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§a\§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r
	//		§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r
	//		§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r§7-§r
	//		§r§a\: Kussen§r

	public void reset()
	{
		this.lines.clear();
	}

	public int getSize() { return lines.size(); }
}
