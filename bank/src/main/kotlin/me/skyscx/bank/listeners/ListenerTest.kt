package me.skyscx.bank.listeners

import me.skyscx.annotation.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerJoinEvent

/**
 * @created 28.04.2025
 * @author Skyscx
 **/

@Listener
internal fun PlayerJoinEvent.welcome() {
	player.sendMessage("Здарова заебал!")
	println("${player.name} + connected!")

}

@Listener
internal fun BlockPlaceEvent.blockPlace(){
	player.sendMessage("Block placed")
	isCancelled = true
}