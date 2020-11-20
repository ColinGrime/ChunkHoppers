package me.scill.chunkhoppers.commands;

import me.scill.chunkhoppers.ChunkHoppers;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HopperCommand implements CommandExecutor {
	
	private final ChunkHoppers plugin;
	
	public HopperCommand(final ChunkHoppers plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("chunkhoppers")) {
			// If players don't have permission, send them an error.
			if (!sender.hasPermission("chunkhoppers.give") && !sender.hasPermission("chunkhoppers.give")) {
				sender.sendMessage(plugin.getHopperData().getNoPermsError());
				return true;
			}

			// No args? Send help!
			if (args.length == 0)
				sendHelp(sender);

			// Give command.
			else if (args[0].equalsIgnoreCase("give") && sender.hasPermission("chunkhoppers.give")) {
				if (args.length < 2)
					sendHelp(sender);
				else if (Bukkit.getPlayer(args[1]) == null) {
					sender.sendMessage(plugin.getHopperData().getNoValidPlayerError()
							.replace("%player%", args[1]));
				} else {
					if (args.length > 2 && args[2].matches("-?\\d+"))
						Bukkit.getPlayer(args[1]).getInventory().addItem(plugin.getHopperHandler().getHopper(Integer.parseInt(args[2])));
					else
						Bukkit.getPlayer(args[1]).getInventory().addItem(plugin.getHopperHandler().getHopper());
				}
			}

			// Reload command.
			else if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("chunkhoppers.reload")) {
				plugin.reload();
				sender.sendMessage(plugin.getHopperData().getReloadMessage());
			}

			// Send help if nothing else works!
			else
				sendHelp(sender);

			return true;
		}
		return false;
	}

	/**
	 * Sends the help message to the specified sender.
	 *
	 * @param sender any commandsender
	 */
	private void sendHelp(final CommandSender sender) {
		for (String help : plugin.getHopperData().getCommandHelp())
			sender.sendMessage(help);
	}
}
