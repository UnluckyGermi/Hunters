package main;
import java.util.UUID;

import org.bukkit.advancement.Advancement;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Executor implements CommandExecutor{
	
	public static final String[] subcommands = {"new", "start", "stop", "pause", "add", "objective"};
	
	
	private void sendHelp(Player p) {
		p.sendMessage("?6Commands: ");

		for(String s : subcommands) {
			p.sendMessage("?6/hg ?b" + s);
		}
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(label.equalsIgnoreCase("hg") && sender instanceof Player) {
			
			Player player = (Player) sender;
			
			if(args.length == 0) {
				sendHelp(player);
			}
			else {
				if(args[0].equalsIgnoreCase(subcommands[0])) {
					if(args.length == 2) {
						if(Main.game != null && Main.game.isStarted()) {
							player.sendMessage("?cThere is a game already started. Use /hg stop to stop the game.");
							return true;
						}
						
						Player runner = Main.plugin.getServer().getPlayer(args[1]);
						
						if(runner == null || !runner.isOnline()) {
							player.sendMessage("?cThe player must be online!");
							return true;
						}
						
						Main.game = new Game(runner);
						player.sendMessage("?aGame created! Runner: ?b" + runner.getName());
						
					}
					else {
						player.sendMessage("?6Usage: /hg new <Runner> [Objective]");
					}
				}
				else if(args[0].equalsIgnoreCase(subcommands[1])) {
					if(args.length == 1) {
						if(Main.game != null) {
							if(!Main.game.isStarted()) {
								Main.game.start();
								player.sendMessage("?aGame started!");
							}
							else {
								player.sendMessage("?cThere is a game already started.");
							}
						}
						else {
							player.sendMessage("?cYou need to create a game with /hg new");
						}
					}
					else {
						player.sendMessage("?6Usage: /hg start");
					}
				}
				else if(args[0].equalsIgnoreCase(subcommands[2])) {
					if(args.length == 1) {
						if(Main.game != null) {
							if(Main.game.isStarted()) {
								Main.game.stop();
								player.sendMessage("?7Game stopped.");
							}
							else {
								player.sendMessage("?cThere is no game started.");
							}
						}
						else {
							player.sendMessage("?cYou need to create a game with /hg new");
						}
					}
					else {
						player.sendMessage("?6Usage: /hg stop");
					}
				}
				else if(args[0].equalsIgnoreCase(subcommands[3])) {
					if(args.length == 1) {
						if(Main.game != null) {
							Main.game.pause();
							if(Main.game.isPaused()) {
								player.sendMessage("?7Game paused. You can resume it by typing again /hg pause");
								
							}
							else {
								player.sendMessage("?aGame resumed!");
							}
						}
						else {
							player.sendMessage("?cYou need to create a game with /hg new");
						}
					}
					else {
						player.sendMessage("?6Usage: /hg pause");
					}
				}
				else if(args[0].equalsIgnoreCase(subcommands[4])) {
					if(args.length == 2) {
						if(Main.game != null) {
							for(UUID uuid : Main.game.getLoggers().keySet()) {
								if(uuid.toString().equals(args[1])) {
									Main.game.getLoggers().put(uuid, true);
									player.sendMessage("?aThe player can now join.");
									return true;
								}
							}
							player.sendMessage("?cThe player must log in first.");
						}
						else {
							player.sendMessage("?cYou need to create a game with /hg new");
						}
					}
					else {
						player.sendMessage("?6Usage: /hg add <Player>");
					}
				}
				else if(args[0].equalsIgnoreCase(subcommands[5])) {
					if(args.length == 2) {
						if(Main.game != null) {
							Advancement a = Game.getAdvancement(args[1]);
							if(a != null) {
								Main.game.setObjective(a);
							}
							else {
								player.sendMessage("?cThat advancement doesn't exist.");
							}
							
						}
						else {
							player.sendMessage("?cYou need to create a game with /hg new");
						}
					}
					else {
						player.sendMessage("?6Usage: /hg add <Player>");
					}
				}
			}
		}
		
		return true;
	}

}
