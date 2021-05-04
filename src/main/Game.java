package main;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
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
	
	private final HashMap<UUID, Boolean> loggers;
	
	private Advancement objective;
	
	private final List<Player> hunters;
	private final Player runner;
	
	private final ItemStack compass;
	
	private BukkitRunnable gameTask;
	
	private int counter;
	private int time;
	
	private boolean paused;
	private boolean started;
	
	private String getPrettyAdvancementString(Advancement a) {
		String s = a.getKey().getKey();
		String pretty = s.split("/")[1].replaceAll("_", " ");
		
		return pretty.substring(0, 1).toUpperCase() + pretty.substring(1);
		
	}
	
	private Scoreboard getScoreboard(boolean runner) {
		Scoreboard sc = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = sc.registerNewObjective("game", "dummy", "  §aRunner §6vs §cHunters  ");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		objective.getScore("     ").setScore(10);
		if(runner) {
			objective.getScore("  §6Role: §aRunner").setScore(9);
		}	
		else {
			objective.getScore("  §6Role: §cHunter").setScore(9);
		}
		objective.getScore("    ").setScore(8);
		objective.getScore("  §dTime").setScore(7);
		objective.getScore("  " + Main.formatSeconds(time)).setScore(6);
		objective.getScore("   ").setScore(5);
		objective.getScore("  §cObjective").setScore(4);
		objective.getScore("  " + getPrettyAdvancementString(this.objective)).setScore(3);
		objective.getScore("  ").setScore(2);
		objective.getScore("  §7By UnluckyGermi").setScore(1);
		objective.getScore("").setScore(0);
		
		return sc;
	}
	
	public void updateScoreboard() {
		
		Scoreboard hunter = getScoreboard(false);
		Scoreboard runner = getScoreboard(true);
			
		this.runner.setScoreboard(runner);
		
		for(Player p : hunters) {
			if(p.isOnline()) p.setScoreboard(hunter);	
		}
	}
	
	public static Advancement getAdvancement(String string) {
		Iterator it = Main.plugin.getServer().advancementIterator();
		while(it.hasNext()) {
			Advancement a = (Advancement) it.next();
			if(a.getKey().getKey().equals(string)) {
				return a;
			}
		}
		
		return null;
	}
	
	public Advancement getObjective() {
		return objective;
	}
	
	
	private ItemStack createCompass() {
		ItemStack compass = new ItemStack(Material.COMPASS);
		ItemMeta im = compass.getItemMeta();
		im.setDisplayName("§6§lTracker");

		List<String> lore = new ArrayList<>();
		lore.add("§7§oThis will help you to find the runner...");
		
		im.setLore(lore);
		
		compass.setItemMeta(im);
		
		return compass;
	}
	
	public Game(Player runner) {
		
		objective = getAdvancement("end/kill_dragon");
		counter = 5;
		time = 0;
		this.runner = runner;
		hunters = new ArrayList<>(Bukkit.getOnlinePlayers());
		loggers = new HashMap<>();
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
	
	public void setObjective(Advancement objective) {
		this.objective = objective;
	}
	
	public List<Player> getHunters(){
		return hunters;
	}
	
	public HashMap<UUID, Boolean> getLoggers(){
		return loggers;
	}
	
	private void revokeAdvancements(Player p) {
		Iterator it = Bukkit.advancementIterator();
		
		while(it.hasNext()) {
			AdvancementProgress progress = p.getAdvancementProgress((Advancement) it.next());
			
			for(String criteria : progress.getAwardedCriteria()) {
				progress.revokeCriteria(criteria);
			}
		}
	}
	
	private void startTasks() {
		started = true;
		
		gameTask = new BukkitRunnable() {

			@Override
			public void run() {		
				if(!paused) {
					for(Player p : hunters) {
						if(time == 0) {
							p.getInventory().addItem(compass);
							p.setDisplayName("§c" + p.getName());
							revokeAdvancements(p);
						}

						p.setCompassTarget(runner.getLocation());
					}

					updateScoreboard();
					time++;
				}
				
			}
		};
		
		gameTask.runTaskTimer(Main.plugin, 0L, 20L);
		
		runner.setDisplayName("§a" + runner.getName());
		revokeAdvancements(runner);
	}
	
	public void addHunter(Player p) {
		hunters.add(p);
	}
	
	public void addLogger(UUID uuid) {
		loggers.put(uuid, false);
	}
	
	public void pause() {
		paused = !paused;
	}
	
	public void stop() {
		gameTask.cancel();
		Main.game = null;
	}
	
	public void end(boolean runnerwin) {
		gameTask.cancel();
		Main.game = null;
		
		for(Player p : hunters) {
			if(runnerwin) {
				p.sendTitle("§cYou lose", "§6Runner reached the goal (§7" + Main.formatSeconds(time) + "§6)", 0, 100, 20);
				p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 100, 0.5f);
			}
			else {
				p.sendTitle("§aYou win!", "§6Runner has died", 0, 60, 20);
				p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 1);
			}
		}
		
		if(runnerwin) {
			runner.sendTitle("§aYou win!", "§6You managed to reach the goal (§7" + Main.formatSeconds(time) + "§6)", 0, 100, 20);
			runner.playSound(runner.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 1);

			Main.plugin.getServer().broadcastMessage("§aRunner §6won the game!");
		}
		else {
			runner.sendTitle("§cYou lose", "§6You died...", 0, 100, 20);
			runner.playSound(runner.getLocation(), Sound.BLOCK_ANVIL_PLACE, 100, 0.5f);
			
			Main.plugin.getServer().broadcastMessage("§cHunters §6won the game!");
		}
	}
	
	public void start() {
		new BukkitRunnable() {

			@Override
			public void run() {
				if(counter != 0) {
					for(Player p : hunters) {
						p.sendTitle("§6" + counter, "You're a §cHunter", 0, 21, 0);
						p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 1);
					}
					
					runner.sendTitle("§6" + counter, "You're the §aRunner!", 0, 21, 0);
					runner.playSound(runner.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 1);
					
					counter--;
				}
				else {
					for(Player p : hunters) {
						p.sendTitle("§cCatch Him!", "You're a §cHunter", 0, 30, 10);
						p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 2);
					}
					
					runner.sendTitle("§aGO!", "You're the §aRunner!", 0, 30, 10);
					runner.playSound(runner.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 2);
					startTasks();
					this.cancel();
				}
			}
		}.runTaskTimer(Main.plugin, 0L, 20L);
	}
}
