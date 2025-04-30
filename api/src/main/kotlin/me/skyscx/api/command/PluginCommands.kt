package me.skyscx.api.command

import me.skyscx.api.utils.RestartController
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @created 30.04.2025
 * @author Skyscx
 **/
@Singleton
class PluginCommands @Inject constructor(
	commandController: CommandController,
	private val restartController: RestartController,
) {
	init {
		commandController.registerOpCommand("restart") { sender, _ ->
			sender.sendMessage("Restarting plugin...")
			restartController.restart()
		}
		commandController.registerOpCommand("testm") { sender, _ ->
			sender.sendMessage("Text-message")
		}
	}
}