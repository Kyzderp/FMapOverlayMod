package com.kyzeragon.fmapoverlay;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class FMapOverlay 
{
	private LinkedList<String> lines;
	private LinkedList<Chunk> toDraw;
	private HashMap<Character, String> factions;
	private boolean isFixed;
	private double fixedY;
	HashMap<String, Integer> colors; 

	public FMapOverlay() 
	{
		this.lines = new LinkedList<String>();
		this.toDraw = new LinkedList<Chunk>();
		this.factions = new HashMap<Character, String>();
		this.isFixed = false;
		this.fixedY = 64;

		this.colors = new HashMap<String, Integer>();
		colors.put("SafeZone", 0xFFAA00);
		colors.put("WarZone", 0xAA0000);
	}

	public void drawOverlay(Tessellator tess)
	{
		for (Chunk chunk: this.toDraw)
		{
			if (this.isFixed)
				chunk.shadeChunk(tess, fixedY);
			else
				chunk.shadeChunk(tess);
		}
	}

	public void addLine(String line)
	{
		lines.addLast(line);
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

		///// Parse the bottom line for the character: faction name /////
		line = lines.get(9);
		line = line.replaceAll("§.?", ""); // /: SafeZone hi \: Kussen
		if (line.length() > 2) // all wilderness! :O except maybe current one
		{
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
		int n = factions.size();
		if (factions.size() % 2 == 0)
			n++;
		for (int i = 0; i < factions.size(); i++)
		{
			String name = (String) factions.values().toArray()[i];
			if (!this.colors.containsKey(name))
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
				{
					System.out.println("Adding " + name + " at " + currX + " " + currZ);
					Chunk toAdd = new Chunk(name, currX, currZ, colors.get(name));
					//					map[x][z] = toAdd;
					if (!this.toDraw.contains(toAdd))
						this.toDraw.addFirst(toAdd);
				}
			}
		}
		return true;
	}

	public void reset()
	{
		this.lines.clear();
		//		this.map = new Chunk[39][8];
		this.toDraw.clear();
	}

	public void clearLines()
	{
		this.lines.clear();
	}

	public void fix()
	{
		this.isFixed = true;
		this.fixedY = Minecraft.getMinecraft().thePlayer.posY - 1.6;
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(1);
		LiteModFMapOverlay.logMessage("§8[§2FMO§8] §aFixing faction map overlay at Y = " + df.format(this.fixedY));
	}

	public void unfix()
	{
		this.isFixed = false;
		LiteModFMapOverlay.logMessage("§8[§2FMO§8] §aUnfixed faction map overlay.");
	}

	public int getSize() { return lines.size(); }

}
