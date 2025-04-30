package me.skyscx.annotation

/**
 * @created 30.04.2025
 * @author Skyscx
 **/

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Factory(
    val type: KClass<*>,
    val receiverType: KClass<*>,
    val wrapper: KClass<*>,
)
