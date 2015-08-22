package com.kyzeragon.fmapoverlay;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class FMapOverlay //TODO: work with /f map on
{
	private LinkedList<String> lines;
	private LinkedList<Chunk> toDraw;
	private HashMap<Character, String> factions;
	private boolean isFixed;
	private double fixedY;
	private boolean drawNames;
	HashMap<String, Integer> colors; 

	public FMapOverlay() 
	{
		this.lines = new LinkedList<String>();
		this.toDraw = new LinkedList<Chunk>();
		this.factions = new HashMap<Character, String>();
		this.isFixed = false;
		this.fixedY = 64;
		this.drawNames = false;

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
	
	public void drawNames(Tessellator tess)
	{
		for (Chunk chunk: this.toDraw)
		{
			if (this.isFixed)
				chunk.drawName(tess, fixedY + 1.6);
			else
				chunk.drawName(tess);
		}
	}

	public void addLine(String line)
	{
		lines.addLast(line);
	}

	public boolean parseMap()
	{
		if (lines.size() < 10)
			return false;
		///// Read the first line to get the current faction /////
//		§r§6______________.[ §r§2(-63,13) §r§fPhantom§r§6 ]._________________§r
//		§r§6______________.[ §r§2(-16,9) §r§6SafeZone ]._________________§r
//		§r§6___________.[ §r§2(-108,-242) Wilderness§r§6 ].______________§r
		String line = lines.get(0);
		int originX = Integer.parseInt(line.substring(line.indexOf("(") + 1, line.indexOf(",")));
		int originZ = Integer.parseInt(line.substring(line.indexOf(",") + 1, line.indexOf(")")));
		int nameStart = line.indexOf(")") + 1;
		String originFac = line.substring(nameStart, line.indexOf("]", nameStart));
		originFac = originFac.replaceAll("§.?", "");
		originFac = originFac.trim();

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
			if (!this.colors.containsKey(name) && !name.equals("SafeZone") && !name.equals("WarZone"))
				colors.put(name, 0xFFFFFF / n * (i + 1));
		}

		///// Parse the actual map and add the chunks to the 2D array /////
		for (int z = 0; z < 8; z++)
		{
			String currLine = this.lines.get(z + 1);
			currLine = currLine.replaceAll("§.?", "");
			if (currLine.length() < 39)
			{
				LiteModFMapOverlay.logError("currLine: " + currLine);
				return false;
			}
			
			int currZ = originZ - 4 + z;
			for (int x = 0; x < 39; x++)
			{
				if (x < 3 && z < 3)
					continue;
				int currX = originX - 19 + x;
				char currChar = currLine.charAt(x);
				String name = factions.get(currChar);
				if (currZ == originZ && currX == originX)
					name = originFac;
				if (colors.get(name) != null)
				{
					Chunk toAdd = new Chunk(name, currX, currZ, colors.get(name));
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
		LiteModFMapOverlay.logMessage("Locked faction map overlay at Y = " + df.format(this.fixedY), true);
	}

	public void unfix()
	{
		this.isFixed = false;
		LiteModFMapOverlay.logMessage("Unlocked faction map overlay.", true);
	}

	/**
	 * Entire method code stolen from totemo's Watson mod \o/ Added some derps.
	 * 
	 * Draw a camera-facing text billboard in three dimensions.
	 * 
	 * @param x the x world coordinate.
	 * @param y the y world coordinate.
	 * @param z the z world coordinate.
	 * @param bgARGB the background colour of the billboard, with alpha in the top
	 *          8 bits, then red, green, blue in less significant octets (blue in
	 *          the least significant 8 bits).
	 * @param fgARGB the foreground (text) colour of the billboard, with alpha in
	 *          the top 8 bits, then red, green, blue in less significant octets
	 *          (blue in the least significant 8 bits).
	 * @param scaleFactor a scale factor to adjust the size of the billboard. Try
	 *          0.02.
	 * @param text the text on the billboard.
	 */
	public static void drawBillboard(double x, double y, double z, int bgARGB,
			int fgARGB, double scaleFactor, String text)
	{
//		x = x * 2;
//		y = y * 2;
//		z = z * 2;
		
		RenderManager renderManager = RenderManager.instance;
		FontRenderer fontRenderer = renderManager.getFontRenderer();
		if (fontRenderer == null)
			return;

		Minecraft mc = Minecraft.getMinecraft();
		// (512 >> mc.gameSettings.renderDistance) * 0.8;
		double far = mc.gameSettings.renderDistanceChunks * 16;
		double dx = x - RenderManager.renderPosX + 0.5d;
		double dy = y - RenderManager.renderPosY + 0.5d;
		double dz = z - RenderManager.renderPosZ + 0.5d;
		
		dx *= 2;
		dy *= 2;
		dz *= 2;
		
		double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
		double dl = distance;
		if (dl > far)
		{
			double d = far / dl;
			dx *= d;
			dy *= d;
			dz *= d;
			dl = far;
		}

		GL11.glPushMatrix();

		double scale = (0.05 * dl + 1.0) * scaleFactor;
		GL11.glTranslated(dx, dy, dz);
		GL11.glRotatef(-renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(
				mc.gameSettings.thirdPersonView != 2 ? renderManager.playerViewX
						: -renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
		GL11.glScaled(-scale, -scale, scale);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Tessellator tessellator = Tessellator.instance;

		int textWidth = fontRenderer.getStringWidth(text) >> 1;
		if (textWidth != 0)
		{
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);

			// Draw background plate.
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(bgARGB & 0x00FFFFFF, (bgARGB >>> 24) & 0xFF);
			tessellator.addVertex(-textWidth - 1, -6, 0.0);
			tessellator.addVertex(-textWidth - 1, 4, 0.0);
			tessellator.addVertex(textWidth + 1, 4, 0.0);
			tessellator.addVertex(textWidth + 1, -6, 0.0);
			tessellator.draw();

			// Draw text.
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			fontRenderer.drawString(text, -textWidth, -5, fgARGB);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(true);
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	} // drawBillboard

	public int getSize() { return lines.size(); }

	public boolean getDrawNames() { return this.drawNames; }
	
	public void setDrawNames(boolean drawName) { this.drawNames = drawName; }
}
