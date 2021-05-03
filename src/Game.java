import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Game {
	
	private final List<Player> hunters;
	private final Player runner;
	
	private int counter;
	
	
	private boolean started;
	
	
	
	public Game(Player runner) {
		counter = 5;
		this.runner = runner;
		hunters = new ArrayList<>(Bukkit.getOnlinePlayers());
		hunters.remove(runner);
		started = false;	
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public Player getRunner() {
		return runner;
	}
	
	public List<Player> getHunters(){
		return hunters;
	}
	
	
	
	public void start() {
		new BukkitRunnable() {

			@Override
			public void run() {
				if(counter != 0) {
					for(Player p : hunters) {
						p.sendTitle("§6" + counter, "You're a §cHunter", 0, 21, 0);
					}
					
					runner.sendTitle("§6" + counter, "You're the §aRunner!", 0, 21, 0);
					
				}
				else {
					started = true;
					this.cancel();
				}
				
			}
			
		}.runTaskTimer(Main.plugin, 0L, 20L);
		
		
	}
	
}
