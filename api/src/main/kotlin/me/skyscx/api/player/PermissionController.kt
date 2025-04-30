package me.skyscx.api.player

import org.bukkit.entity.Player
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @created 30.04.2025
 * @author Skyscx
 **/

@Singleton
class PermissionController @Inject constructor(){

	fun isStaff(player: Player) = player.isOp

}