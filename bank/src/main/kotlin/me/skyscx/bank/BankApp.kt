package me.skyscx.bank

import me.skyscx.annotation.Load
import org.bukkit.plugin.java.JavaPlugin
import java.io.PrintWriter
import java.io.StringWriter
import java.util.logging.Handler
import java.util.logging.LogRecord
import java.util.logging.Logger

/**
 * @created 27.04.2025
 * @author Skyscx
 **/

class BankApp {
	@Load
	fun load(plugin: JavaPlugin){
		Logger.getLogger("").addHandler(object : Handler() {
			override fun publish(record: LogRecord) {
				if (record.thrown != null) {
					val errors = StringWriter()
					record.thrown.printStackTrace(PrintWriter(errors))
					// logBot(errors.toString())
				}
			}

			override fun flush() {}

			override fun close() {}
		})
	}
}