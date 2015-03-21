package com.kyzeragon.fmapoverlay;

import java.io.File;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import com.mumfrey.liteloader.ChatFilter;
import com.mumfrey.liteloader.OutboundChatListener;
import com.mumfrey.liteloader.PostRenderListener;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;

public class LiteModFMapOverlay implements OutboundChatListener, ChatFilter, PostRenderListener, Tickable
{
	private boolean sentCmd;
	private boolean isOn;
	private boolean display;
	private boolean justPressed;
	private FMapOverlay fmap;
	private static KeyBinding loadMapBinding;

	@Override
	public String getName() { return "Faction Map Overlay"; }

	@Override
	public String getVersion() { return "0.9.0"; }

	@Override
	public void init(File configPath) 
	{
		this.sentCmd = false;
		this.justPressed = false;
		this.isOn = false;
		this.fmap = new FMapOverlay();
		this.loadMapBinding = new KeyBinding("key.fmapoverlay.shortcut", Keyboard.KEY_L, "key.categories.litemods");
		LiteLoader.getInput().registerKeyBinding(this.loadMapBinding);
	}

	@Override
	public void upgradeSettings(String version, File configPath, File oldConfigPath) {}

	@Override
	public void onPostRenderEntities(float partialTicks) 
	{
		if (!this.isOn)
			return;

		RenderHelper.disableStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST); // derp
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F); // derp
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		//		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_LINE_SMOOTH); // derp
		GL11.glLineWidth(1.0F); // derp
		GL11.glDepthMask(false);
		//		GL11.glDisable(GL11.GL_DEPTH_TEST);

		//		boolean foggy = GL11.glIsEnabled(GL11.GL_FOG);
		//		GL11.glDisable(GL11.GL_FOG);

		GL11.glPushMatrix();

		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		GL11.glTranslated(-(player.prevPosX + (player.posX - player.prevPosX) * partialTicks),
				-(player.prevPosY + (player.posY - player.prevPosY) * partialTicks),
				-(player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks));

		Tessellator tess = Tessellator.instance;
		this.fmap.drawOverlay(tess);

		GL11.glDepthFunc(GL11.GL_LEQUAL); // derp
		GL11.glPopMatrix();

		// Only re-enable fog if it was enabled before we messed with it.
		// Or else, fog is *all* you'll see with Optifine.
		//		if (foggy)
		//			GL11.glEnable(GL11.GL_FOG);
		//		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		//		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F); // derp

		RenderHelper.enableStandardItemLighting();
	}

	@Override
	public void onPostRender(float partialTicks) {}

	@Override
	public boolean onChat(S02PacketChat chatPacket, IChatComponent chat, String message) 
	{
		if (this.sentCmd && message.matches(".*nknown.*ommand.*"))
			return false;
		if (message.matches("§r§6_+\\.\\[.*"))
		{
//			this.fmap.reset();
			this.fmap.clearLines();
			this.fmap.addLine(message);
		}
		else if (this.fmap.getSize() > 0 && this.fmap.getSize() < 10
				&& message.matches("§r§[0-9a-f]?.?§r§.*"))
			this.fmap.addLine(message);
		else if (this.fmap.getSize() == 9)// && message.matches("§r§[0-9a-f]?.?: .*"))
			this.fmap.addLine(message);
		return true;
	}

	@Override
	public void onSendChatMessage(C01PacketChatMessage packet, String message) 
	{
		String[] tokens = message.split(" ");
		if (tokens[0].equalsIgnoreCase("/fmo"))
		{ // §
			this.sentCmd = true;
			if (tokens.length > 1)
			{
				if (tokens[1].equalsIgnoreCase("on"))
				{
					this.isOn = true;
					this.logMessage("§8[§2FMO§8] §aFaction Map Overlay: §2ON");
				}
				else if (tokens[1].equalsIgnoreCase("off"))
				{
					this.isOn = false;
					this.logMessage("§8[§2FMO§8] §aFaction Map Overlay: §4OFF");
				}
				else if (tokens[1].equalsIgnoreCase("display"))
				{
					if (this.fmap.parseMap())
						this.logMessage("§8[§2FMO§8] §aDisplaying faction map overlay");
					else
						this.logError("Unable to display overlay! Run /f map first");
				}
				else if (tokens[1].equalsIgnoreCase("clear"))
				{
					this.fmap.reset();
					this.logMessage("§8[§2FMO§8] §aFaction map overlay cleared.");
				}
				else if (tokens[1].equalsIgnoreCase("fix"))
				{
					this.fmap.fix();
				}
				else if (tokens[1].equalsIgnoreCase("unfix"))
				{
					this.fmap.unfix();
				}
				else if (tokens[1].equalsIgnoreCase("help"))
				{
					String[] commands = {"on - Turn Faction Map Overlay on",
							"off - Turn Faction Map Overlay off",
							"clear - Clear the overlay display",
					"help - This help message. Hurrdurr."};
					this.logMessage(this.getName() + " [v" + this.getVersion() + "] commands:");
					for (int i = 0; i < commands.length; i++)
						this.logMessage("/fmo " + commands[i]);
				}
				else
				{
					this.logError("Invalid parameters. See /fmo help for help with commands.");
				}
			}
			else
			{
				this.logMessage(this.getName() + " [v" + this.getVersion() + "] by Kyzeragon");
				this.logMessage("Type /fmo help for commands.");
			}
		}
	}

	@Override
	public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) 
	{
		if (this.loadMapBinding.isPressed())
		{
			System.out.println("pressed");
			if (!this.justPressed) // first time pressing
			{
				this.logMessage("§8[§2FMO§8] §aAuto-running /f map... press again to display overlay.");
				minecraft.thePlayer.sendChatMessage("/f map");
				this.justPressed = true;
			}
			else // second time pressing
			{
				this.logMessage("§8[§2FMO§8] §aDisplaying faction map overlay...");
				this.isOn = true;
				this.fmap.parseMap();
				this.justPressed = false;
			}
		}
	}

	/**
	 * Logs the message to the user
	 * @param message The message to log
	 */
	public static void logMessage(String message)
	{// "§8[§2FMO§8] §a" + 
		ChatComponentText displayMessage = new ChatComponentText(message);
		displayMessage.setChatStyle((new ChatStyle()).setColor(EnumChatFormatting.GREEN));
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(displayMessage);
	}

	/**
	 * Logs the error message to the user
	 * @param message The error message to log
	 */
	public static void logError(String message)
	{
		ChatComponentText displayMessage = new ChatComponentText("§8[§4!§8] §c" + message + " §8[§4!§8]");
		displayMessage.setChatStyle((new ChatStyle()).setColor(EnumChatFormatting.RED));
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(displayMessage);
	}
}
