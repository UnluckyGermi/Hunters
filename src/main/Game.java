package main;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Game {
	
	private final List<Player> loggers;
	
	private final List<Player> hunters;
	private final Player runner;
	
	private final ItemStack compass;
	
	private BukkitRunnable gameTask;
	
	private int counter;
	private int time;
	
	private boolean paused;
	private boolean started;
	
	
	private void updateScoreboard() {
		
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = scoreboard.registerNewObjective("game", "dummy", "  §aRunner §6vs §cHunters  ");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		
		objective.getScore("    ").setScore(8);
		objective.getScore("  §6Role: §cHunter").setScore(7);
		objective.getScore("   ").setScore(6);
		objective.getScore("  §dTime").setScore(5);
		objective.getScore("  " + Main.formatSeconds(time)).setScore(4);
		objective.getScore("  ").setScore(3);
		objective.getScore(" ").setScore(2);
		objective.getScore("  §7By UnluckyGermi").setScore(1);
		objective.getScore("").setScore(0);
		
		
		
		
		for(Player p : hunters) {
			p.setScoreboard(scoreboard);
		}
		
		scoreboard.resetScores("  §6Role: §cHunter");
		objective.getScore("  §6Role: §aRunner").setScore(7);
		
		runner.setScoreboard(scoreboard);
	}
	
	
	private ItemStack createCompass() {
		ItemStack compass = new ItemStack(Material.COMPASS);
		ItemMeta im = compass.getItemMeta();
		im.setDisplayName("§6Finder");

		List<String> lore = new ArrayList<>();
		lore.add("§7§oThis will help you to find the runner...");
		lore.add("§7§oIt updates every 5 minutes.");
		
		im.setLore(lore);
		
		compass.setItemMeta(im);
		
		return compass;
	}
	
	public Game(Player runner) {
		
		counter = 5;
		time = 0;
		this.runner = runner;
		hunters = new ArrayList<>(Bukkit.getOnlinePlayers());
		loggers = new ArrayList<>();
		hunters.remove(runner);
		started = false;
		paused = false;
		compass = createCompass();
		updateScoreboard();
		
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
		
		runner.getInventory().addItem(compass); //DEBUG
		
		gameTask = new BukkitRunnable() {

			@Override
			public void run() {
				
				
				if(!paused) {
					if(time % 30 == 0) {
						for(Player p : hunters) {
							if(time == 0) {
								p.getInventory().addItem(compass);
								p.setDisplayName("§c" + p.getName());
							}
							
							p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§6Compass updated!"));
							p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 1);
							p.setCompassTarget(runner.getLocation());
						}
						
						runner.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§6Compass updated!")); //DEBUG
						runner.setCompassTarget(runner.getLocation());	//DEBUG
					}
					
					updateScoreboard();
					time++;
				}
				
			}
		};
		
		gameTask.runTaskTimer(Main.plugin, 0L, 20L);
		
		runner.setDisplayName("§a" + runner.getName());
	}
	
	public void addHunter(Player p) {
		hunters.add(p);
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
				if(counter != 0) {
					for(Player p : hunters) {
						p.sendTitle("§6" + counter, "You're a §cHunter", 0, 21, 0);
					}
					
					runner.sendTitle("§6" + counter, "You're the §aRunner!", 0, 21, 0);
					counter--;
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
			
		}.runTaskTimer(Main.plugin, 0L, 20L);
		
		
	}
	
}
