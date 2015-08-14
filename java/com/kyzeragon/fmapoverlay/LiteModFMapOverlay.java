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
		LiteModFMapOverlay.loadMapBinding = new KeyBinding("key.fmapoverlay.shortcut", Keyboard.KEY_L, "key.categories.litemods");
		LiteLoader.getInput().registerKeyBinding(LiteModFMapOverlay.loadMapBinding);
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
		
		if (this.fmap.getDrawNames())
			this.fmap.drawNames(tess);
		
//		FMapOverlay.drawBillboard(0, 10, 0, 0x80000000, 0xFFFFFFFF, 0.02, "Test Billboard 0 10 0");
//		FMapOverlay.drawBillboard(10, 20, 10, 0x80000000, 0xFFFFFFFF, 0.02, "Test Billboard 10 20 10");

		
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

//	§r§6______________.[ §r§2(-63,13) §r§fPhantom§r§6 ]._________________§r
//	§r§6\N/§r§f\§r§7--§r§f#§r§7---§r§f???§r§7-§r§f???§r§6$§r§f%%=%%%%%%%====%&^^^^^§r
//	§r§6W+E§r§f\§r§7--§r§f???§r§7-§r§f??§r§7-§r§f????§r§6$§r§f?==\\%%%%%======^^^^^§r
//	§r§6/§r§cS§r§6\§r§fA§r§7--§r§f??§r§7---§r§fB§r§7-§r§fBB??§r§6$§r§f?==\%%%C%%====D=^^^^§r§6$§r
//	§r§7--§r§fAAA§r§7---§r§fEEBBB§r§7-§r§fBB=§r§6$§r§f%%%%%C%C%%%%%F==^GG^§r§6$§r
//	AAA\AH§r§7---§r§fEB======§r§6$§r§fJ§r§b+§r§fKK%%CC==%%%FD^^G^^§r§6$§r
//	A§r§7-§r§fAA§r§7-§r§fA§r§7--§r§fEEE=BLL§r§7-§r§fM§r§6$§r§7--§r§fKKK%C%§r§7-§r§fNNOPQD^^G^^§r§6$§r
//	AAA§r§7-§r§fA§r§7---§r§f==EBBBL§r§7-§r§6$$§r§7-§r§fKKRK%%%%N§r§dSSSS§r§fTTTTTT§r§6$§r
//	AAAAA§r§7--§r§fU==BBBBB§r§6$$§r§7-§r§fR§r§7-§r§fRRV§r§7-§r§f=WXN§r§dSSSS§r§fTTTTTT§r§6$§r
//	\: Huracan C: andrei N: Epsilon K: TheArchers W: piolavago J: BestMcPlayer O: Slickerzsx V: otaku B: loyalplayerz H: PHILIPPINES G: Nighthawks Q: GODS =: Craftlopious E: Dr3am R: teammangben T: InsulaColumba A: DiamondMinerz ^: CookieSyndicate U: PVO L: MonedyTDM §r§dS: Casual §r§f?: Crystalyzd M: TheNoobz P: STARE &: Dope /: Innocentio F: whack %: TheTeamExtreme X: PoraMine D: Pixelmon §r§6$: SafeZone §r§f#: xeroworld§r
	
	@Override
	public boolean onChat(S02PacketChat chatPacket, IChatComponent chat, String message) 
	{
		if (this.sentCmd && message.matches(".*nknown.*ommand.*"))
		{
			this.sentCmd = false;
			return false;
		}
		if (message.matches("§r§6_+\\.\\[.*\\(-?[0-9]+.*"))
		{
			this.fmap.clearLines();
			this.fmap.addLine(message);
		}
		else if (this.fmap.getSize() > 0 && this.fmap.getSize() < 10)
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
					LiteModFMapOverlay.logMessage("Faction Map Overlay: §2ON", true);
				}
				else if (tokens[1].equalsIgnoreCase("off"))
				{
					this.isOn = false;
					LiteModFMapOverlay.logMessage("Faction Map Overlay: §4OFF", true);
				}
				else if (tokens[1].equalsIgnoreCase("display"))
				{
					if (this.fmap.parseMap())
						LiteModFMapOverlay.logMessage("Displaying faction map overlay", true);
					else
						LiteModFMapOverlay.logError("Unable to display overlay! Run /f map first");
				}
				else if (tokens[1].equalsIgnoreCase("clear"))
				{
					this.fmap.reset();
					LiteModFMapOverlay.logMessage("Faction map overlay cleared.", true);
				}
				else if (tokens[1].equalsIgnoreCase("lock") || tokens[1].equalsIgnoreCase("fix"))
				{
					this.fmap.fix();
				}
				else if (tokens[1].equalsIgnoreCase("unlock") || tokens[1].equalsIgnoreCase("unfix"))
				{
					this.fmap.unfix();
				}
/*				else if (tokens[1].equalsIgnoreCase("names"))
				{
					if (tokens.length == 3 && tokens[2].equalsIgnoreCase("on"))
						this.fmap.setDrawNames(true);
					else if (tokens.length == 3 && tokens[2].equalsIgnoreCase("off"))
						this.fmap.setDrawNames(false);
					else
						LiteModFMapOverlay.logError("Usage: /fmo names <on|off>");
				}*/
				else if (tokens[1].equalsIgnoreCase("help"))
				{
					String[] commands = {"on - Turn Faction Map Overlay on",
							"off - Turn Faction Map Overlay off",
							"clear - Clear the overlay display",
					"help - This help message. Hurrdurr."};
					LiteModFMapOverlay.logMessage(this.getName() + " [v" + this.getVersion() + "] commands:", false);
					for (int i = 0; i < commands.length; i++)
						LiteModFMapOverlay.logMessage("/fmo " + commands[i], false);
				}
				else
				{
					LiteModFMapOverlay.logError("Invalid parameters. See /fmo help for help with commands.");
				}
			}
			else
			{
				LiteModFMapOverlay.logMessage(this.getName() + " [v" + this.getVersion() + "] by Kyzeragon", false);
				LiteModFMapOverlay.logMessage("Type /fmo help for commands.", false);
			}
		}
	}

	@Override
	public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) 
	{
		if (LiteModFMapOverlay.loadMapBinding.isPressed())
		{
			System.out.println("pressed");
			if (!this.justPressed) // first time pressing
			{
				LiteModFMapOverlay.logMessage("Auto-running /f map... press again to display overlay.", true);
				minecraft.thePlayer.sendChatMessage("/f map");
				this.justPressed = true;
			}
			else // second time pressing
			{
				LiteModFMapOverlay.logMessage("Displaying faction map overlay...", true);
				this.isOn = true;
				if (!this.fmap.parseMap())
					LiteModFMapOverlay.logError("Error in displaying faction map overlay!");
				this.justPressed = false;
			}
		}
	}

	/**
	 * Logs the message to the user
	 * @param message The message to log
	 */
	public static void logMessage(String message, boolean prefix)
	{// "§8[§2FMO§8] §a" + 
		if (prefix)
			message = "§8[§2FMO§8] §a" + message;
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
