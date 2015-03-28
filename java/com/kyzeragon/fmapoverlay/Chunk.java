package com.kyzeragon.fmapoverlay;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

public class Chunk 
{
	public String name;
	private int x;
	private int z;
	private int color;
	
	public Chunk(String name, int x, int z, int color)
	{
		this.name = name;
		this.x = x;
		this.z = z;
		this.color = color;
	}
	
	public void shadeChunk(Tessellator tess)
	{
		this.shadeChunk(tess, Minecraft.getMinecraft().thePlayer.posY - 1.6);
	}
	
	public void shadeChunk(Tessellator tess, double y)
	{
		tess.startDrawing(GL11.GL_POLYGON);
		tess.setColorRGBA_I(color, 80);
		tess.addVertex(this.x * 16, y, this.z * 16);
		tess.addVertex(this.x * 16, y, this.z * 16 + 16);
		tess.addVertex(this.x * 16 + 16, y, this.z * 16 + 16);
		tess.addVertex(this.x * 16 + 16, y, this.z * 16);
		tess.draw();
	}
	
	public void drawName(Tessellator tess)
	{
		this.drawName(tess, Minecraft.getMinecraft().thePlayer.posY);
	}
	
	public void drawName(Tessellator tess, double y)
	{
		FMapOverlay.drawBillboard(this.x * 16 + 8, y, this.z * 16 + 8, 0x80000000, 0xFFFFFFFF, 0.04, this.name);
	}
	
	public void lineChunk(Tessellator tess)
	{
		double y = Minecraft.getMinecraft().thePlayer.posY - 1.6;
		GL11.glLineWidth(3.0f);
		tess.startDrawing(GL11.GL_LINE_LOOP);
		tess.setColorRGBA_I(color, 200);
		tess.addVertex(this.x * 16 + 0.1, y, this.z * 16 + 0.1);
		tess.addVertex(this.x * 16 + 0.1, y, this.z * 16 + 15.9);
		tess.addVertex(this.x * 16 + 15.9, y, this.z * 16 + 15.9);
		tess.addVertex(this.x * 16 + 15.9, y, this.z * 16 + 0.1);
		tess.draw();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!this.name.equals(((Chunk)obj).name))
			return false;
		if (this.x != ((Chunk)obj).x)
			return false;
		if (this.z != ((Chunk)obj).z)
			return false;
		return true;
	}
}
