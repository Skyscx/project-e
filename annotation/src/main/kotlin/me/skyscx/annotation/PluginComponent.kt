package me.skyscx.annotation

import kotlin.reflect.KClass

/**
 * @created 27.04.2025
 * @author Skyscx
 **/

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class PluginComponent(vararg val modules: KClass<*>)
