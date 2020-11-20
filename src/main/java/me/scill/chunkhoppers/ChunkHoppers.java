package me.scill.chunkhoppers;

import lombok.Getter;
import me.scill.chunkhoppers.commands.HopperCommand;
import me.scill.chunkhoppers.data.HopperData;
import me.scill.chunkhoppers.listeners.HopperListener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class ChunkHoppers extends JavaPlugin {

	private HopperData hopperData;
	private HopperHandler hopperHandler;

	@Override
	public void onEnable() {
		// Data
		saveDefaultConfig();
		hopperData = new HopperData(this);

		// Hopper handler!
		hopperHandler = new HopperHandler(this);

		// Listeners
		getServer().getPluginManager().registerEvents(new HopperListener(this), this);

		// Commands
		getCommand("chunkhoppers").setExecutor(new HopperCommand(this));
	}

	@Override
	public void onDisable() {
		hopperHandler.saveHoppers();
	}

	/**
	 * Reloads the plugin with new hopper values.
	 */
	public void reload() {
		reloadConfig();
		hopperData = new HopperData(this);
		hopperHandler.start(this);
	}
}