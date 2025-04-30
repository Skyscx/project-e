package me.skyscx.api

import me.skyscx.api.command.PluginCommands
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @created 30.04.2025
 * @author Skyscx
 **/

@Singleton
class ApiApp @Inject constructor(
	commands: PluginCommands,
)