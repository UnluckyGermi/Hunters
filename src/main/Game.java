package main;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class Game {
	
	private final List<Player> hunters;
	private final Player runner;
	
	private Scoreboard scoreboard;
	
	private final ItemStack compass;
	
	private BukkitRunnable gameTask;
	
	private int counter;
	private int time;
	
	private boolean paused;
	private boolean started;
	
	private void updateScoreboard() {
		Objective objective = scoreboard.registerNewObjective("game", "dummy", "§aRunner §6vs §cHunters");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		objective.getScore("Time: §7" + Main.formatSeconds(time)).setScore(1);
		objective.getScore(" ").setScore(2);
		objective.getScore("§aRunner: §7" + runner.getDisplayName()).setScore(3);
		
	}
	
	
	private ItemStack createCompass() {
		ItemStack compass = new ItemStack(Material.COMPASS);
		ItemMeta im = compass.getItemMeta();
		im.setDisplayName("§6Finder");

		List<String> lore = im.getLore();
		lore.add("§7§oThis will help you to find the runner...");
		lore.add("§7§oIt updates every 5 minutes.");
		
		im.setLore(lore);
		
		return compass;
	}
	
	public Game(Player runner) {
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		counter = 5;
		time = 0;
		this.runner = runner;
		hunters = new ArrayList<>(Bukkit.getOnlinePlayers());
		hunters.remove(runner);
		started = false;
		paused = false;
		compass = createCompass();
	}
	
	public BukkitRunnable getTask() {
		return gameTask;
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public Player getRunner() {
		return runner;
	}
	
	public List<Player> getHunters(){
		return hunters;
	}
	
	private void startTasks() {
		started = true;
		
		gameTask = new BukkitRunnable() {

			@Override
			public void run() {
				
				
				if(time % 60*5 == 0) {
					for(Player p : hunters) {
						if(time == 0) {
							p.getInventory().addItem(compass);
							p.setDisplayName("§c" + p.getName());
						}
						
						p.sendTitle("", "§6Compass updated!", 0, 20, 10);
						p.setCompassTarget(runner.getLocation());
					}
				}
				updateScoreboard();
				time++;
			}
		};
		
		gameTask.runTaskTimer(Main.plugin, 0L, 20L);
		
		runner.setDisplayName("§a" + runner.getDisplayName());
	}
	
	public void pause() {
		paused = !paused;
	}
	
	public void stop() {
		gameTask.cancel();
		Main.game = null;
	}
	
	public void start() {
		new BukkitRunnable() {

			@Override
			public void run() {
				if(!paused) {
					if(counter != 0) {
						for(Player p : hunters) {
							p.sendTitle("§6" + counter, "You're a §cHunter", 0, 21, 0);
						}
						
						runner.sendTitle("§6" + counter, "You're the §aRunner!", 0, 21, 0);
						
					}
					else {
						for(Player p : hunters) {
							p.sendTitle("§cCatch Him!", "You're a §cHunter", 0, 20, 10);
						}
						
						runner.sendTitle("§aGO!", "You're the §aRunner!", 0, 20, 10);
						startTasks();
						this.cancel();
					}	
				}
			}
			
		}.runTaskTimer(Main.plugin, 0L, 20L);
		
		
	}
	
}
