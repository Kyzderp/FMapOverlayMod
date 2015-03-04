package com.kyzeragon.fmapoverlay;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;

public class Edge 
{
	private int minX;
	private int maxX;
	private int minZ;
	private int maxZ;
	
	public Edge(int x1, int z1, int x2, int z2)
	{
		this.minX = Math.min(x1, x2);
		this.maxX = Math.max(x1, x2);
		this.minZ = Math.min(z1, z2);
		this.maxZ = Math.max(z1, z2);
	}
	
	public void drawEdge(Tessellator tess, double y, int color)
	{
		tess.startDrawing(GL11.GL_LINE);
		tess.setColorRGBA_I(color, 200);
		tess.addVertex(minX, y, minZ);
		tess.addVertex(maxX, y, maxZ);
		tess.draw();
	}
}
