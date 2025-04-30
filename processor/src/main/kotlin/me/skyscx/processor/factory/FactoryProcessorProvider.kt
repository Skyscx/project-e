package me.skyscx.processor.factory

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * @created 27.04.2025
 * @author Skyscx
 **/

@AutoService(SymbolProcessorProvider::class)
class FactoryProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return FactoryProcessor(
            options = environment.options,
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
        )
    }
}