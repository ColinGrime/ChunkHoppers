package me.scill.chunkhoppers.data;

import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import me.scill.chunkhoppers.ChunkHoppers;
import me.scill.chunkhoppers.utilities.Utility;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class HopperManager {

	private final ChunkHoppers plugin;
	private final File configFile;
	private final FileConfiguration config;

	@Getter
	protected Set<Location> hopperLocations = new HashSet<>();

	protected HopperManager(final ChunkHoppers plugin) {
		this.plugin = plugin;
		this.configFile = new File(plugin.getDataFolder(), "hoppers.yml");
		this.config = YamlConfiguration.loadConfiguration(configFile);
		this.loadHoppers();
	}

	private void saveConfig() {
		try {
			config.save(configFile);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Loads all the hoppers into a set.
	 */
	private void loadHoppers() {
		if (config.getStringList("hoppers") == null)
			return;
		final Set<String> hopperStringLocations = new HashSet<>(config.getStringList("hoppers"));
		hopperLocations = hopperStringLocations.stream().map(Utility::stringToLocation).collect(Collectors.toSet());
	}

	/**
	 * Saves all the hoppers into a file.
	 */
	public void saveHoppers() {
		final Set<String> hopperStringLocations = hopperLocations.stream().map(Utility::locationToString).collect(Collectors.toSet());
		config.set("hoppers", hopperStringLocations.toArray(new String[hopperLocations.size()]));
		saveConfig();
	}

	/**
	 * Adds a chunk hopper.
	 *
	 * @param location the location of the hopper
	 */
	public void addHopper(final Location location) {
		if (location == null)
			return;
		hopperLocations.add(location);
	}

	/**
	 * Removes a chunk hopper.
	 *
	 * @param location the location of the lock.
	 */
	public void removeHopper(final Location location) {
		if (location == null)
			return;
		hopperLocations.remove(location);
	}

	/**
	 * Gets chunk hoppers.
	 *
	 * @param amount the amount of chunk hoppers
	 * @return chunk hoppers
	 */
	public ItemStack getHopper(final int amount) {
		final ItemStack chunkHopper = new ItemStack(Material.HOPPER, amount);

		// Set hopper meta.
		final ItemMeta hopperMeta = chunkHopper.getItemMeta();
		hopperMeta.setDisplayName(Utility.color(plugin.getHopperData().getHopperName()));
		chunkHopper.setItemMeta(hopperMeta);

		// Set hopper NBT data.
		final NBTItem hopperData = new NBTItem(chunkHopper);
		hopperData.setBoolean("isChunkHopper", true);
		return hopperData.getItem();
	}

	public ItemStack getHopper() {
		return getHopper(1);
	}
}