package me.skyscx.api.routine

class TaskRoutine {
    var id: Int = 0
    var nextPassTime: Long = 0
    var interval: Long = 0
    var action: (TaskRoutine) -> Unit = EMPTY_ACTION
    var killHandler: (TaskRoutine) -> Unit = EMPTY_ACTION
    var pass: Long = 0
    var passLimit: Long = 0

    fun onKill(lambda: (TaskRoutine) -> Unit) {
        killHandler = combineLambda(killHandler, sanitizeAction(lambda))
    }

    fun doLast(lambda: (TaskRoutine) -> Unit) {
        action = combineLambda(action, sanitizeAction(lambda))
    }

    fun doFirst(lambda: (TaskRoutine) -> Unit) {
        action = combineLambda(sanitizeAction(lambda), action)
    }

    fun cancel() {
        killHandler(this)
    }

    private fun combineLambda(lambda1: (TaskRoutine) -> Unit, lambda2: (TaskRoutine) -> Unit): (TaskRoutine) -> Unit {
        if (lambda1 === EMPTY_ACTION) {
            return lambda2
        }
        if (lambda2 === EMPTY_ACTION) {
            return lambda1
        }

        return { r ->
            lambda1(r)
            lambda2(r)
        }
    }

    companion object {
        val EMPTY_ACTION: (TaskRoutine) -> Unit = { }

        fun <T> sanitizeAction(action: (T) -> Unit): (T) -> Unit {
            return { obj ->
                try {
                    action(obj)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        val routine = other as TaskRoutine
        if (id != routine.id) return false
        if (nextPassTime != routine.nextPassTime) return false
        if (interval != routine.interval) return false
        if (action != routine.action) return false
        if (killHandler != routine.killHandler) return false
        if (pass != routine.pass) return false
        if (passLimit != routine.passLimit) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + nextPassTime.hashCode()
        result = 31 * result + interval.hashCode()
        result = 31 * result + action.hashCode()
        result = 31 * result + killHandler.hashCode()
        result = 31 * result + pass.hashCode()
        result = 31 * result + passLimit.hashCode()
        return result
    }

    override fun toString(): String {
        return "Routine(id=$id, nextPassTime=$nextPassTime, interval=$interval, action=$action, killHandler=$killHandler, pass=$pass, passLimit=$passLimit)"
    }
}