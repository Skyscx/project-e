package me.skyscx.api.utils

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @created 30.04.2025
 * @author Skyscx
 **/

@Singleton
class RestartController @Inject constructor() {

	fun restart() {
		server.setWhitelist(true)
		server.onlinePlayers.forEach(::kick)
		scheduler.after(10 * 20) { Bukkit.shutdown() }
	}

	private fun kick(player: Player) {
		player.kickPlayer("Restart")
	}
}