package me.skyscx.bank

import me.skyscx.annotation.Listener
import org.bukkit.event.player.PlayerJoinEvent

/**
 * @created 28.04.2025
 * @author Skyscx
 **/

@Listener
internal fun PlayerJoinEvent.welcome() {
	player.sendMessage("Здарова заебал!")
}