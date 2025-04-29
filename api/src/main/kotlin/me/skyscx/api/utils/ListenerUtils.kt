package me.skyscx.api.utils

import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

// Глобальный объект Listener
object GlobalListener : Listener

// Функция для регистрации событий
inline fun <reified T : Event> subscribe(
	needFilter: Boolean = true,
	priority: EventPriority = EventPriority.NORMAL,
	noinline handler: T.() -> Unit
) {
	if (needFilter) {
		listener(priority, handler)
	} else {
		// Если needFilter = false, регистрируем событие без фильтрации
		listener(priority, handler)
	}
}

// Вспомогательная функция для регистрации событий
inline fun <reified T : Event> listener(
	priority: EventPriority = EventPriority.NORMAL,
	noinline handler: T.() -> Unit
) {
	val plugin: JavaPlugin = JavaPlugin.getProvidingPlugin(GlobalListener::class.java)
	plugin.server.pluginManager.registerEvent(
		T::class.java, GlobalListener, priority,
		{ _, event ->
			if (T::class.java.isInstance(event)) {
				handler.invoke(event as T)
			}
		}, plugin
	)
}
