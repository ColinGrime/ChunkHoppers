package me.scill.chunkhoppers.data;

import lombok.Getter;
import me.scill.chunkhoppers.ChunkHoppers;
import me.scill.chunkhoppers.utilities.Utility;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class HopperData  {

	private final String hopperName;
	private final int pullSpeed;
	private final String messageOnPlace, messageOnBreak;
	private final List<String> commandHelp;
	private final String reloadMessage, noPermsError, noValidPlayerError;

	public HopperData(final ChunkHoppers plugin) {
		// Chunk hopper specific.
		hopperName = Utility.color(plugin.getConfig().getString("chunk-hoppers.name"));
		pullSpeed = plugin.getConfig().getInt("chunk-hoppers.pull-speed");
		messageOnPlace = Utility.color(plugin.getConfig().getString("chunk-hoppers.message-on-place"));
		messageOnBreak = Utility.color(plugin.getConfig().getString("chunk-hoppers.message-on-break"));

		commandHelp = plugin.getConfig().getStringList("admin.command-help").stream().map(Utility::color).collect(Collectors.toList());
		reloadMessage = Utility.color(plugin.getConfig().getString("admin.reload"));
		noPermsError = Utility.color(plugin.getConfig().getString("errors.insufficient-permissions"));
		noValidPlayerError = Utility.color(plugin.getConfig().getString("errors.no-valid-player"));
	}
}