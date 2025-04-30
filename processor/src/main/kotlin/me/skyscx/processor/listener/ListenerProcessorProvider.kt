package me.skyscx.processor.listener

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * @created 27.04.2025
 * @author Skyscx
 **/

@AutoService(SymbolProcessorProvider::class)
class ListenerProcessorProvider : SymbolProcessorProvider {
	override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
		return ListenerProcessor(
			options = environment.options,
			codeGenerator = environment.codeGenerator,
			logger = environment.logger,
		)
	}
}