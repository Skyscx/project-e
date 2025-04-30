package me.skyscx.api.utils

import org.bukkit.event.Event
import org.bukkit.event.EventPriority

/**
 * @created 27.04.2025
 * @author Skyscx
 **/

inline fun <reified T : Event> listener(
	priority: EventPriority = EventPriority.NORMAL,
	noinline handler: T.() -> Unit
) {
	server.pluginManager.registerEvent(
		T::class.java, Listener, priority,
		{ _, event ->
			if (T::class.java.isInstance(event)) {
				handler.invoke(event as T)
			}
		}, plugin
	)
}

object Listener : org.bukkit.event.Listener
