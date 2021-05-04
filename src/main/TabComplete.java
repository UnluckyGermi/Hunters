package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class TabComplete implements TabCompleter{

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(label.equalsIgnoreCase("hg")) {
			if(args.length == 1) {
				return Arrays.asList(Executor.subcommands);
			}
			else if(args.length == 2) {
				if(args[0].equalsIgnoreCase(Executor.subcommands[5])) {
					List<Advancement> adv = new ArrayList<>();
					List<String> str = new ArrayList<>();
					Bukkit.advancementIterator().forEachRemaining(adv::add);
					for(Advancement a : adv) {
						String s = a.getKey().getKey();
						if(s.startsWith(args[1])) {
							str.add(a.getKey().getKey());
						}
						
					}
					
					return str;
					
				}
			}
		}
		
		
		
		return null;
		
	}

}
