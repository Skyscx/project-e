package me.skyscx.processor.plugin

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * @created 29.04.2025
 * @author Skyscx
 **/

@AutoService(SymbolProcessorProvider::class)
class EntrypointProcessorProvider : SymbolProcessorProvider {

	override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
		return EntrypointProcessor(
			options = environment.options,
			codeGenerator = environment.codeGenerator,
			logger = environment.logger,
		)
	}
}