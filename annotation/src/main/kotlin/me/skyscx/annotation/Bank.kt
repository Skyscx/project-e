package me.skyscx.annotation

import kotlin.reflect.KClass
import me.skyscx.protocol.Module

/**
 * @created 27.04.2025
 * @author Skyscx
 **/

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Bank(
	val modules: Array<KClass<out Module>>,
)
