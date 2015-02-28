package com.kyzeragon.fmapoverlay;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import com.mumfrey.liteloader.ChatFilter;
import com.mumfrey.liteloader.OutboundChatListener;
import com.mumfrey.liteloader.PostRenderListener;

public class LiteModFMapOverlay implements OutboundChatListener, ChatFilter, PostRenderListener
{
	private boolean sentCmd;
	private boolean isOn;
	private boolean display;
	
	@Override
	public String getName() { return "Faction Map Overlay"; }

	@Override
	public String getVersion() { return "0.9.0"; }

	@Override
	public void init(File configPath) 
	{
		this.sentCmd = false;
	}

	@Override
	public void upgradeSettings(String version, File configPath, File oldConfigPath) {}

	@Override
	public void onPostRenderEntities(float partialTicks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPostRender(float partialTicks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onChat(S02PacketChat chatPacket, IChatComponent chat, String message) 
	{
		if (this.sentCmd && message.matches(".*nknown.*ommand.*"))
			return false;
		System.out.println("Received message: \n" + message);
		return true;
	}

	@Override
	public void onSendChatMessage(C01PacketChatMessage packet, String message) 
	{
		String[] tokens = message.split(" ");
		if (tokens[0].equalsIgnoreCase("/fmo"))
		{
			this.sentCmd = true;
			if (tokens.length > 1)
			{
				if (tokens[1].equalsIgnoreCase("on"))
				{
					this.isOn = true;
					this.logMessage("Faction Map Overlay: ON");
				}
				else if (tokens[1].equalsIgnoreCase("off"))
				{
					this.isOn = false;
					this.logMessage("Faction Map Overlay: OFF");
				}
				else if (tokens[1].equalsIgnoreCase("clear"))
				{
					// TODO: clear the display
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
				this.logMessage(this.getName() + " [v" + this.getVersion() + "]");
				this.logMessage("Type /fmo help for commands.");
			}
		}
	}
	/**
	 * Logs the message to the user
	 * @param message The message to log
	 */
	private void logMessage(String message)
	{
		ChatComponentText displayMessage = new ChatComponentText(message);
		displayMessage.setChatStyle((new ChatStyle()).setColor(EnumChatFormatting.AQUA));
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(displayMessage);
	}

	/**
	 * Logs the error message to the user
	 * @param message The error message to log
	 */
	private void logError(String message)
	{
		ChatComponentText displayMessage = new ChatComponentText(message);
		displayMessage.setChatStyle((new ChatStyle()).setColor(EnumChatFormatting.RED));
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(displayMessage);
	}
}
