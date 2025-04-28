package me.skyscx.annotation

import org.bukkit.event.EventPriority

/**
 * @created 27.04.2025
 * @author Skyscx
 **/

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Listener(
	val needFilter: Boolean = true,
	val priority: EventPriority = EventPriority.NORMAL,
)
