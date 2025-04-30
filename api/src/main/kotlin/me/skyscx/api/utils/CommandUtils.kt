package me.skyscx.api.utils

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

typealias Commands = List<String>
typealias Arguments = Array<out String>

fun registerCommand(
    name: String,
    callback: (CommandSender, Arguments) -> Unit,
) {
    server.commandMap.register("bank", object : Command(name, "", "", emptyList()) {
            override fun execute(sender: CommandSender, var2: String, args: Arguments): Boolean {
                callback(sender, args)
                return true
            }
        }
    )
}

operator fun Commands.invoke(player: Player) {
    invoke("%player%" to player.name)
}

operator fun Commands.invoke(vararg pairs: Pair<String, String?>) {
    forEach { rawCommand ->
        var command = rawCommand
        pairs.forEach { (key, value) ->
            value?.let { arg ->
                command = command.replace(key, arg)
            }
        }
        server.dispatchCommand(server.consoleSender, command)
    }
}