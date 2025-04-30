package me.skyscx.api.command

import me.skyscx.api.player.PermissionController
import me.skyscx.api.utils.Arguments
import me.skyscx.api.utils.server
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * @created 30.04.2025
 * @author Skyscx
 **/

@Singleton
class CommandController @Inject constructor(
	private val permissionController: Provider<PermissionController>,
) {

	fun registerCommand(vararg commands: String, callback: (Player, Arguments) -> Unit) {
		registerCommand(commands.asList(), callback)
	}

	fun registerConsoleCommand(vararg commands: String, callback: (ConsoleCommandSender, Arguments) -> Unit) {
		registerCommand(commands.asList(), callback)
	}

	fun registerStaffCommand(vararg commands: String, callback: (CommandSender, Arguments) -> Unit) {
		registerCommand(commands.asList(), callback) { sender ->
			sender is ConsoleCommandSender || (sender is Player && permissionController.get().isStaff(sender))
		}
	}

	fun registerOpCommand(vararg commands: String, callback: (CommandSender, Arguments) -> Unit) {
		registerCommand(commands.asList(), callback, CommandSender::isOp)
	}

	private inline fun <reified T : CommandSender> registerCommand(
		commands: List<String>,
		crossinline callback: (T, Arguments) -> Unit,
		crossinline canExecute: (T) -> Boolean = { true },
	) {
		server.commandMap.register("bank", object : Command(commands.first(), "", "", commands.drop(1)) {
			override fun execute(sender: CommandSender, var2: String, args: Arguments): Boolean {
				if (sender !is T || !canExecute(sender)) {
					sender.sendMessage("Not enough rights!")
					return false
				}

				callback(sender, args)
				return true
			}
		})
	}
}