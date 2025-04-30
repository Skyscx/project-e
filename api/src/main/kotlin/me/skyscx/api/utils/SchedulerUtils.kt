package me.skyscx.api.utils

import me.skyscx.api.routine.TaskRoutine
import me.skyscx.api.routine.Scheduler


/**
 * @created 30.04.2025
 * @author Skyscx
 **/

val scheduler = Scheduler()

fun nextTick(action: (TaskRoutine) -> Unit) {
	scheduler.after(0, cancellable = false,  action)
}
