package main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;

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
		if(Main.game != null && Main.game.isStarted() && !Main.game.getHunters().contains(e.getPlayer()) && !Main.game.getRunner().equals(e.getPlayer())) {
			e.setKickMessage("§cYou can't join the server until the game ends.");
			e.setResult(Result.KICK_OTHER);
			
			Main.game.getRunner().sendMessage("§e" + e.getPlayer().getName() + " §fis trying to join.");
			
		}
	}
	
	
	
	
}
