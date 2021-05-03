package main;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

public class Main extends JavaPlugin{
	
	public static Main plugin;
	
	public static Game game;
	
	public static Scoreboard scoreboard;
	
	
	public static String formatSeconds(int timeInSeconds)
	{
	    int hours = timeInSeconds / 3600;
	    int secondsLeft = timeInSeconds - hours * 3600;
	    int minutes = secondsLeft / 60;
	    int seconds = secondsLeft - minutes * 60;

	    String formattedTime = "";
	    if (hours < 10)
	        formattedTime += "0";
	    formattedTime += hours + ":";

	    if (minutes < 10)
	        formattedTime += "0";
	    formattedTime += minutes + ":";

	    if (seconds < 10)
	        formattedTime += "0";
	    formattedTime += seconds ;

	    return formattedTime;
	}
	
	public void onEnable() {
		this.getCommand("hg").setExecutor(new Executor());
		this.getServer().getPluginManager().registerEvents(new Events(), this);
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		
		plugin = this;
	}
	
	
	
	
}
