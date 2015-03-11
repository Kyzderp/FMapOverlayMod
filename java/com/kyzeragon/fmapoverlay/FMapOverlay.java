package com.kyzeragon.fmapoverlay;

import java.util.HashMap;
import java.util.LinkedList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

public class FMapOverlay 
{
	public boolean doneTakingLines;
	private LinkedList<String> lines;
	private HashMap<Character, String> factions;
	private Chunk[][] map;
	private boolean isFixed;
	private double fixedY;

	public FMapOverlay() 
	{
		this.lines = new LinkedList<String>();
		this.factions = new HashMap<Character, String>();
		this.map = new Chunk[39][8];
		this.doneTakingLines = false;
		this.isFixed = false;
		this.fixedY = 64;
	}

	public void drawOverlay(Tessellator tess)
	{
		for (int z = 0; z < 8; z++)
		{
			for (int x = 0; x < 39; x++)
			{
				if (map[x][z] != null)
				{
					if (this.isFixed)
						map[x][z].shadeChunk(tess, fixedY);
					else
						map[x][z].shadeChunk(tess);
				}
			}	
		}
	}

	public void addLine(String line)
	{
		lines.addLast(line);
		System.out.println("Added line: " + line);
		if (lines.size() >= 10 && this.doneTakingLines)
			this.parseMap();
	}

	public boolean parseMap()
	{
		if (lines.size() < 1)
			return false;
		this.factions.clear();
		this.map = new Chunk[39][8];
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
			line = line.replaceAll("§.?", ""); // /: SafeZone hi \: Kussen
			System.out.println("Line: " + line);
			if (line.length() < 3) // all wilderness! :O except maybe current one
				break;
			String name = "";
			char character = '"';
			String[] words = line.split(" ");
			for (int j = 0; j < words.length; j++) // loop through the words
			{
				if (words[j].matches(".:"))
				{
					if (!name.equals(""))
					{
						factions.put(character, name);
						name = "";
					}
					character = words[j].charAt(0);
				}
				else
					name += words[j];
			}
			factions.put(character,  name);
		}

		///// Add the current chunk's char + name if it's not already inside / isn't wilderness /////
		if (!originFac.equals("Wilderness") && !factions.containsValue(originFac))
			factions.put('+', originFac);

		///// Decide on colors to use /////
		HashMap<String, Integer> colors = new HashMap<String, Integer>();
		int n = factions.size();
		if (factions.size() % 2 == 0)
			n++;
		for (int i = 0; i < factions.size(); i++)
		{
			String name = (String) factions.values().toArray()[i];
			if (name.equals("SafeZone"))
				colors.put(name, 0xFFAA00);
			else if (name.equals("WarZone"))
				colors.put(name, 0xAA0000);
			else
				colors.put(name, 0xFFFFFF / n * (i + 1));
		}

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
				char currChar = currLine.charAt(x * 5 + 4);
				String name = factions.get(currChar);
				if (currZ == originZ && currX == originX)
					name = originFac;
				if (colors.get(name) != null)
					map[x][z] = new Chunk(name, currX, currZ, colors.get(name)); 
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
	
	public void fix()
	{
		this.isFixed = true;
		this.fixedY = Minecraft.getMinecraft().thePlayer.posY - 1.6;
	}
	
	public void unfix()
	{
		this.isFixed = false;
	}

	public int getSize() { return lines.size(); }
}
