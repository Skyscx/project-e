package me.skyscx.bank.commands

import me.skyscx.annotation.commands.ArgInject
import me.skyscx.annotation.commands.Command
import me.skyscx.annotation.commands.Sender
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * @created 30.04.2025
 * @author Skyscx
 **/

@Command(
	name = "spawn",
	usage = "/spawn"
)
internal fun spawn(@Sender player: Player) {
	player.teleport(Location(Bukkit.getWorld("world"), 0.0, 100.0, 0.0))
}