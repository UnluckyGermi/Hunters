package main;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;

import mkremins.fanciful.FancyMessage;
import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;


public class Events implements Listener{

	private boolean checkAll(Player p) {
		return (Main.game != null && Main.game.isStarted() && (Main.game.getHunters().contains(p) || Main.game.getRunner().equals(p)) && Main.game.isPaused());
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if(checkAll(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(checkAll(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onLog(PlayerLoginEvent e) {
		if(Main.game != null && Main.game.isStarted() && !Main.game.getHunters().contains(e.getPlayer()) && !Main.game.getRunner().equals(e.getPlayer()) 
				&& Main.game.getLoggers().get(e.getPlayer().getUniqueId()) == null) {
			
			
			e.setKickMessage("§cYou can't join the server until the game ends.");
			e.setResult(Result.KICK_OTHER);
			
			Main.game.addLogger(e.getPlayer().getUniqueId());
			
			PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a(msg(e.getPlayer().getUniqueId().toString(), e.getPlayer().getName())), ChatMessageType.CHAT, UUID.randomUUID());
			((CraftPlayer) Main.game.getRunner()).getHandle().playerConnection.sendPacket(packet);
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if(Main.game != null) {
			if(!Main.game.isStarted() || Main.game.getLoggers().get(e.getPlayer().getUniqueId()) == true)
			Main.game.getLoggers().remove(e.getPlayer().getUniqueId());
			Main.game.addHunter(e.getPlayer());
			e.setJoinMessage("§e" + e.getPlayer().getName() + " joined as §chunter");
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if(Main.game != null && Main.game.isStarted() && e.getEntity().equals(Main.game.getRunner())) {
			Main.game.end(false);
		}
	}
	
	@EventHandler
	public void onAch(PlayerAdvancementDoneEvent e) {
		if(Main.game != null && Main.game.isStarted() && e.getPlayer().equals(Main.game.getRunner()) && e.getAdvancement().equals(Main.game.getObjective())){
			Main.game.end(true);
		}
	}
	
	
	private String msg(String uuid, String name) {
		return new FancyMessage(name)
				.color(ChatColor.YELLOW)
			.then(" is trying to log. Let him pass? ")
				.color(ChatColor.WHITE)
			.then("[Yes] ")
				.color(ChatColor.GREEN)
				.style(ChatColor.BOLD)
				.command("/hg add " + uuid)
		.toJSONString();
	}
	
	
	
	
}
