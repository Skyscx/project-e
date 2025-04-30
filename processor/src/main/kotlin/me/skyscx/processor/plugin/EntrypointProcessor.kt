package me.skyscx.processor.plugin

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import me.skyscx.annotation.Entrypoint
import me.skyscx.annotation.Load
import me.skyscx.api.utils.PluginHolder
import me.skyscx.processor.plugin.component.COMPONENT_NAME
import me.skyscx.processor.plugin.component.COMPONENT_PACKAGE_NAME
import me.skyscx.processor.utils.findAnnotation
import me.skyscx.processor.utils.getArgument
import me.skyscx.processor.utils.injectProperty
import org.apache.commons.lang3.text.WordUtils
import org.bukkit.plugin.java.JavaPlugin

/**
 * @created 29.04.2025
 * @author Skyscx
 **/

const val PLUGIN_NAME = "BankPlugin"
const val PLUGIN_PACKAGE_NAME = "me.skyscx"

class EntrypointProcessor(
	private val options: Map<String, String>,
	private val codeGenerator: CodeGenerator,
	private val logger: KSPLogger,
) : SymbolProcessor {

	private var plugin: KSClassDeclaration? = null
	private val loadFunctions = arrayListOf<KSFunctionDeclaration>()

	override fun process(resolver: Resolver): List<KSAnnotated> {
		val entrypointSymbols = resolver.getSymbolsWithAnnotation(checkNotNull(Entrypoint::class.qualifiedName))
		val loadSymbols = resolver.getSymbolsWithAnnotation(checkNotNull(Load::class.qualifiedName))

		val validPluginSymbols = entrypointSymbols
			.filterIsInstance<KSClassDeclaration>()
			.filter(KSAnnotated::validate)

		val validLoadSymbols = loadSymbols
			.filterIsInstance<KSFunctionDeclaration>()
			.filter(KSAnnotated::validate)

		validPluginSymbols.firstOrNull()?.let { plugin ->
			this.plugin = plugin
		}

		validLoadSymbols.filter { it.parentDeclaration == null }.forEach { symbol ->
			loadFunctions.add(symbol)
		}

		return entrypointSymbols.filterNot { it in validPluginSymbols }.toList() +
				loadSymbols.filterNot { it in validLoadSymbols }
	}

	override fun finish() {
		val annotation = plugin?.annotations?.findAnnotation<Entrypoint>() ?: return

		val pluginClassName = plugin?.toClassName() ?: return
		val pluginVariableName = WordUtils.uncapitalize(pluginClassName.simpleName)

		val loadFunction = plugin
			?.getAllFunctions()
			?.find { it.annotations.findAnnotation<Load>() != null }
			?: return

		FileSpec.builder(PLUGIN_PACKAGE_NAME, PLUGIN_NAME)
			.addType(
				TypeSpec.classBuilder(PLUGIN_NAME)
					.superclass(JavaPlugin::class)
					.addProperty(
						PropertySpec.builder(pluginVariableName, pluginClassName)
							.addModifiers(KModifier.PRIVATE)
							.initializer("%T()", pluginClassName)
							.build()
					)
					.apply {
						val inject = annotation.getArgument<List<KSType>>("modules")

						injectProperty("me.skyscx.listener", "Listeners", "listeners")

						inject.forEach { type ->
							val typeClassName = type.toClassName()
							val propertyName = WordUtils.uncapitalize(typeClassName.simpleName)
							injectProperty(typeClassName, propertyName)
						}

						val componentClassName = ClassName(COMPONENT_PACKAGE_NAME, "Dagger$COMPONENT_NAME")

						val onEnableFunction = FunSpec.builder("onEnable")
							.addModifiers(KModifier.OVERRIDE)
							.addStatement("%T.plugin = this", PluginHolder::class)
							.addStatement("%T.factory().create(this).inject(this)", componentClassName)

						loadFunctions.forEach { loadFunction ->
							val memberName = MemberName(loadFunction.packageName.asString(), loadFunction.simpleName.asString())
							val parameters = loadFunction.parameters.joinToString(",") { parameter -> injectProperty(parameter) }

							onEnableFunction.addStatement("%M(%L)", memberName, parameters)
						}

						onEnableFunction.addStatement(
							"%L.%L(this)",
							pluginVariableName,
							loadFunction.simpleName.asString()
						)

						addFunction(onEnableFunction.build())
					}.build()
			).build()
			.writeTo(codeGenerator = codeGenerator, aggregating = false)
	}
}