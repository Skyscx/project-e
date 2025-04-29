package me.skyscx.processor.plugin.component

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * @created 28.04.2025
 * @author Skyscx
 **/

@AutoService(SymbolProcessorProvider::class)
class EntrypointComponentProcessorProvider : SymbolProcessorProvider {
	override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
		return PluginComponentProcessor(
			options = environment.options,
			codeGenerator = environment.codeGenerator,
			logger = environment.logger,
		)
	}
}