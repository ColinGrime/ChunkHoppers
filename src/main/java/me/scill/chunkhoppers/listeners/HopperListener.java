package me.scill.chunkhoppers.listeners;

import de.tr7zw.nbtapi.NBTItem;
import me.scill.chunkhoppers.ChunkHoppers;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class HopperListener implements Listener {

	private final ChunkHoppers plugin;

	public HopperListener(final ChunkHoppers plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onHopperPlace(final BlockPlaceEvent event) {
		if (!event.isCancelled() && isChunkHopper(event.getItemInHand())) {
			plugin.getHopperHandler().addHopper(event.getBlock().getLocation());
			event.getPlayer().sendMessage(plugin.getHopperData().getMessageOnPlace());
		}
	}

	@EventHandler
	public void onHopperBreak(final BlockBreakEvent event) {
		if (!event.isCancelled() && isChunkHopper(event.getBlock())) {
			// Get rid of the chunk hopper.
			plugin.getHopperHandler().removeHopper(event.getBlock().getLocation());
			event.getPlayer().getInventory().addItem(plugin.getHopperHandler().getHopper());
			event.getPlayer().sendMessage(plugin.getHopperData().getMessageOnBreak());

			// Get rid of the block.
			event.setCancelled(true);
			event.getBlock().setType(Material.AIR);
		}
	}

	/**
	 * Checks to see if the specified item stack is a chunk hopper.
	 *
	 * @param potentialChunkHopper any item stack
	 * @return true if the item is a chunk hopper
	 */
	private boolean isChunkHopper(final ItemStack potentialChunkHopper) {
		if (potentialChunkHopper.getType() != Material.HOPPER)
			return false;

		final NBTItem hopperData = new NBTItem(potentialChunkHopper);
		return hopperData.getBoolean("isChunkHopper");
	}

	/**
	 * Checks to see if the specified location is a chunk hopper.
	 *
	 * @param potentialChunkHopper any block
	 * @return true if the block is a chunk hopper
	 */
	private boolean isChunkHopper(final Block potentialChunkHopper) {
		if (potentialChunkHopper.getType() != Material.HOPPER)
			return false;
		return plugin.getHopperHandler().getHopperLocations().contains(potentialChunkHopper.getLocation());
	}
}