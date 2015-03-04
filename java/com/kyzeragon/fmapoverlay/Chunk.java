package com.kyzeragon.fmapoverlay;

public class Chunk 
{
	public String name;
	private Chunk north;
	private Chunk east;
	private Chunk south;
	private Chunk west;
	
	public Chunk(String name, Chunk n, Chunk e, Chunk s, Chunk w)
	{
		this.name = name;
		this.north = n;
		this.east = e;
		this.south = s;
		this.west = w;
	}
	
	public boolean equals(Chunk other)
	{
		return this.name.equals(other.name);
	}
}
