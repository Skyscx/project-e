package me.skyscx.bank.utils

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.plugin.Plugin

/**
 * @created 27.04.2025
 * @author Skyscx
 **/

val plugin: Plugin get() = PluginHolder.plugin

val server: Server get() = Bukkit.getServer()
