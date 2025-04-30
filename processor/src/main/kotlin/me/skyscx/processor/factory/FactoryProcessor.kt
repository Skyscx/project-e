package me.skyscx.processor.factory

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.google.devtools.ksp.visitor.KSDefaultVisitor
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import me.skyscx.annotation.Factory
import me.skyscx.processor.utils.getArgument
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @created 27.04.2025
 * @author Skyscx
 **/

class FactoryProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    private val factoryVisitor = FactoryVisitor()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(checkNotNull(Factory::class.qualifiedName))

        val validSymbols = symbols
            .filterIsInstance<KSClassDeclaration>()
            .filter(KSAnnotated::validate)
            .associateWith { symbol ->
                val wrapper: KSType = symbol.annotations.first().getArgument("wrapper")
                val wrapperClass = resolver.getClassDeclarationByName(wrapper.declaration.qualifiedName!!)!!
                wrapperClass.primaryConstructor!!.parameters
            }
            .filterValues { parameters ->
                parameters.all(KSValueParameter::validate)
            }

        validSymbols.forEach { (symbol, parameters) ->
            symbol.accept(factoryVisitor, parameters)
        }

        return symbols.filterNot { it in validSymbols }.toList()
    }

    private inner class FactoryVisitor :
        KSDefaultVisitor<List<KSValueParameter>, Unit>() {

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: List<KSValueParameter>) {
            val annotation = classDeclaration.annotations.first()

            val factoryClassName = classDeclaration.simpleName.asString().substringBefore("Settings")
            val factoryId = classDeclaration.simpleName.asString().substringBefore("Factory")

            val packageName = classDeclaration.packageName.asString()

            val type: KSType = annotation.getArgument("type")
            val receiverType: KSType = annotation.getArgument("receiverType")
            val wrapper: KSType = annotation.getArgument("wrapper")

            val typeClassName = type.toClassName()
            val wrapperClassName = wrapper.toClassName()
            val receiverTypeClassName = receiverType.toClassName()

            val wrappersType = MAP.parameterizedBy(typeClassName, wrapperClassName)
            val receiverTypes = COLLECTION.parameterizedBy(receiverTypeClassName)

            val fileSpec = FileSpec.builder(packageName, factoryClassName)
                .addType(
                    TypeSpec.classBuilder(factoryClassName)
                        .addAnnotation(Singleton::class)
                        .primaryConstructor(
                            FunSpec.constructorBuilder()
                                .addAnnotation(Inject::class)
                                .apply {
                                    data.forEach { parameter ->
                                        val parameterTypeName = parameter.type.toTypeName()
                                        val parameterType = parameter.type.resolve()
                                        if (parameterTypeName == type.toTypeName() || parameterType.isAssignableFrom(type)) return@forEach
                                        val parameterName = parameter.name?.asString()

                                        parameterName?.let {
                                            addParameter(it, parameterTypeName)
                                        }
                                    }
                                }
                                .build()
                        )
                        .apply {
                            val params = data.map { parameter ->
                                val parameterType = parameter.type.resolve()
                                if (parameter.type.toTypeName() == type.toTypeName() || parameterType.isAssignableFrom(type)) return@map "it"
                                val name = parameter.name?.asString()
                                name
                            }.joinToString(",")

                            addProperty(
                                PropertySpec.builder("wrappers", wrappersType)
                                    .initializer(
                                        "%L.entries.associateWith { %L(%L) }",
                                        typeClassName,
                                        wrapperClassName,
                                        params,
                                    )
                                    .build()
                            )
                        }
                        .addFunction(
                            FunSpec.builder("get")
                                .addParameter("type", typeClassName)
                                .returns(receiverTypeClassName)
                                .addStatement(
                                    "return wrappers[type] ?: throw %T(%P)",
                                    Exception::class,
                                    "Unknown $factoryId type \$type"
                                )
                                .build()
                        )
                        .addFunction(
                            FunSpec.builder("getAll")
                                .returns(receiverTypes)
                                .addStatement("return wrappers.values")
                                .build()
                        )
                        .build()
                )
                .build()

            fileSpec.writeTo(codeGenerator = codeGenerator, aggregating = false)
        }

        override fun defaultHandler(node: KSNode, data: List<KSValueParameter>) {

        }
    }
}