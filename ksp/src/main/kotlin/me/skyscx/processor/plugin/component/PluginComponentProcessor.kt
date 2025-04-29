package me.skyscx.processor.plugin.component

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import dagger.BindsInstance
import dagger.Component
import me.skyscx.annotation.PluginComponent
import me.skyscx.processor.plugin.PLUGIN_NAME
import me.skyscx.processor.plugin.PLUGIN_PACKAGE_NAME
import me.skyscx.processor.utils.findAnnotation
import me.skyscx.processor.utils.getArgument
import org.bukkit.plugin.java.JavaPlugin
import javax.inject.Singleton

/**
 * @created 27.04.2025
 * @author Skyscx
 **/

const val COMPONENT_NAME = "PluginComponent"
const val COMPONENT_PACKAGE_NAME = "me.skyscx.di.component"

class PluginComponentProcessor(
	private val options: Map<String, String>,
	private val codeGenerator: CodeGenerator,
	private val logger: KSPLogger,
) : SymbolProcessor {

	private var component: KSClassDeclaration? = null

	override fun process(resolver: Resolver): List<KSAnnotated> {
		val componentSymbols = resolver.getSymbolsWithAnnotation(checkNotNull(PluginComponent::class.qualifiedName))
		logger.info("Processing annotations...")

		val validComponentSymbols = componentSymbols
			.filterIsInstance<KSClassDeclaration>()
			.filter(KSAnnotated::validate)

		validComponentSymbols.firstOrNull()?.let { component ->
			this.component = component
		}

		return componentSymbols.filterNot { it in validComponentSymbols }.toList()
	}

	override fun finish() {
		val annotation = component?.annotations?.findAnnotation<PluginComponent>() ?: return
//		val modules = annotation.getArgument<List<KSType>>("modules")
		logger.info("Finishing code generation...")

//		val moduleClasses = modules.joinToString(",") { module ->
//			"${module.toClassName()}::class"
//		}

		FileSpec.builder(COMPONENT_PACKAGE_NAME, COMPONENT_NAME)
			.addType(
				TypeSpec.interfaceBuilder(COMPONENT_NAME)
					.addAnnotation(Singleton::class)
//					.addAnnotation(
//						AnnotationSpec.builder(Component::class)
//							.addMember("modules = [%L]", moduleClasses)
//							.build()
//					)
					.addFunction(
						FunSpec.builder("inject")
							.addModifiers(KModifier.ABSTRACT)
							.addParameter("plugin", ClassName(PLUGIN_PACKAGE_NAME, PLUGIN_NAME))
							.build()
					)
					.addType(
						TypeSpec.interfaceBuilder("Factory")
							.addAnnotation(Component.Factory::class)
							.addFunction(
								FunSpec.builder("create")
									.addModifiers(KModifier.ABSTRACT)
									.addParameter(
										ParameterSpec.builder("app", JavaPlugin::class)
											.addAnnotation(BindsInstance::class)
											.build()
									)
									.returns(ClassName(COMPONENT_PACKAGE_NAME, COMPONENT_NAME))
									.build()
							).build()
					).build())
			.build()
			.writeTo(codeGenerator = codeGenerator, aggregating = false)
	}
}