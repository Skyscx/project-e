package me.skyscx.annotation.commands

/**
 * @created 30.04.2025
 * @author Skyscx
 **/

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Command(
    val name: String,
    val usage: String = "",
    val op: Boolean = false,
)
