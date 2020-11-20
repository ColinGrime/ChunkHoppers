package me.scill.chunkhoppers;

import me.scill.chunkhoppers.data.HopperManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HopperHandler extends HopperManager {

	private int task;

	public HopperHandler(final ChunkHoppers plugin) {
		super(plugin);
		this.start(plugin);
	}

	/**
	 * Starts the Chunk Hoppers.
	 *
	 * @param plugin the chunk hoppers plugin
	 */
	public void start(final ChunkHoppers plugin) {
		if (Bukkit.getScheduler().isQueued(task))
			Bukkit.getScheduler().cancelTask(task);
		task = Bukkit.getScheduler().runTaskTimer(plugin, this::hopperTick, 0L, plugin.getHopperData().getPullSpeed()).getTaskId();
	}

	/**
	 * This is the main controller of hoppers.
	 *
	 * It will run every X seconds to check if
	 * there are items on the ground that are
	 * in need of pulling.
	 */
	private void hopperTick() {
		hopperLocations.stream()
				.filter(this::isHopper)
				.forEach(hopperLocation -> pullNearbyItems((Hopper) hopperLocation.getBlock().getState()));
	}

	/**
	 * Gets the hopper's chunk, searches for any
	 * item on the ground, and puts it into the
	 * hopper until the hopper's inventory is full.
	 *
	 * @param hopper any hopper
	 */
	private void pullNearbyItems(final Hopper hopper) {
		final List<Item> nearbyItems = getNearbyItems(hopper.getChunk());

		List<ItemStack> hopperItems = new ArrayList<>();
		boolean hopperFull = false;

		for (Item groundItem : nearbyItems) {
			// No space left...
			if (hopper.getInventory().firstEmpty() == -1) {
				// Set the hopperItems list.
				if (!hopperFull) {
					hopperItems = new ArrayList<>(Arrays.asList(hopper.getInventory().getContents()));
					hopperFull = true;
				}
				// Look for leftover items...
				checkForAdditionalStacks(hopperItems, groundItem);
			}

			// Space left in the hopper!
			else {
				hopper.getInventory().addItem(groundItem.getItemStack());
				groundItem.remove();
			}
		}
	}

	/**
	 * Checks each hopper item for additional stacks (e.g. less than x64 for dirt).
	 * If there is extra room, attempt to steal the ground item's stacks if applicable.
	 *
	 * @param hopperItems list of non-max-stacked hopper items
	 * @param groundItem any ground item
	 */
	private void checkForAdditionalStacks(final List<ItemStack> hopperItems, final Item groundItem) {
		for (int i=0; i<hopperItems.size(); i++) {
			final ItemStack hopperItem = hopperItems.get(i);
			// Item stack full? Remove it.
			if (hopperItem.getAmount() == hopperItem.getMaxStackSize())
				hopperItems.remove(hopperItem);
				// Item stack not full AND there's an item? Add it to the stack!
			else if (hopperItem.getType() == groundItem.getItemStack().getType()) {
				final ItemStack groundItemStack = groundItem.getItemStack();
				final int amountTaken = Math.min(hopperItem.getMaxStackSize() - hopperItem.getAmount(), groundItemStack.getAmount());
				// All items taken from ground item? Remove it.
				if (amountTaken == groundItemStack.getAmount())
					groundItem.remove();
					// Remove part of the stack from the ground item.
				else {
					groundItemStack.setAmount(amountTaken);
					groundItem.setItemStack(groundItemStack);
				}
				// Add the extra stack to the hopper item.
				hopperItem.setAmount(hopperItem.getAmount() + amountTaken);
			}
		}
	}

	/**
	 * Retrieves all items in a chunk.
	 *
	 * @param chunk any chunk
	 * @return all valid items in a chunk
	 */
	private List<Item> getNearbyItems(final Chunk chunk) {
		return Arrays.stream(chunk.getEntities()).filter(entity -> entity instanceof Item).map(this::entityToItem).collect(Collectors.toList());
	}

	/**
	 * Turns an entity object into an item object.
	 *
	 * @param entity any entity
	 * @return turns the entity into an object
	 */
	private Item entityToItem(final Entity entity) {
		return (Item) entity;
	}

	/**
	 * Checks to see if the specified location is a hopper.
	 *
	 * @param potentialHopper any location
	 * @return true if the location is a hopper
	 */
	private boolean isHopper(final Location potentialHopper) {
		return potentialHopper.getBlock().getType() == Material.HOPPER;
	}
}
