package me.skyscx.processor.command

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.google.devtools.ksp.visitor.KSDefaultVisitor
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import me.skyscx.annotation.commands.*
import me.skyscx.processor.utils.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @created 27.04.2025
 * @author Skyscx
 **/

class CommandProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    private val configuredName = "Commands"
    private val packageName = "me.skyscx.command"

    private val initCodeBlock = CodeBlock.builder()

    private val injectParameters = arrayListOf<KSValueParameter>()

    private val commandVisitor = CommandVisitor()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val commandSymbols = resolver.getSymbolsWithAnnotation(checkNotNull(Command::class.qualifiedName))
        val argMapperSymbols = resolver.getSymbolsWithAnnotation(checkNotNull(ArgMapper::class.qualifiedName))

        val validCommandSymbols = commandSymbols
            .filterIsInstance<KSFunctionDeclaration>()
            .filter(KSAnnotated::validate)

        val validArgMapperSymbols = argMapperSymbols
            .filterIsInstance<KSFunctionDeclaration>()
            .filter(KSAnnotated::validate)

        validCommandSymbols.forEach { symbol ->
            symbol.accept(commandVisitor, CommandData(initCodeBlock, validArgMapperSymbols))
                .forEach { injectParameter ->
                    injectParameters.add(injectParameter)
                }
        }

        return commandSymbols.filterNot { it in validCommandSymbols }.toList() +
                argMapperSymbols.filter { it in validArgMapperSymbols }
    }

    override fun finish() {
        val classTypeBuilder = TypeSpec.classBuilder(configuredName)
            .addAnnotation(Singleton::class)

        val primaryConstructorBuilder = FunSpec.constructorBuilder()
            .addAnnotation(Inject::class)

        FileSpec.builder(packageName, configuredName)
            .addImport("me.skyscx.api.utils", "registerCommand")
            .addType(
                classTypeBuilder
                    .primaryConstructor(primaryConstructorBuilder.build())
                    .apply {
                        injectParameters.forEach { parameter -> injectProperty(parameter) }
                    }
                    .addInitializerBlock(initCodeBlock.build())
                    .build()
            )
            .build()
            .writeTo(codeGenerator = codeGenerator, aggregating = false)
    }

    private inner class CommandVisitor : KSDefaultVisitor<CommandData, Parameters>() {

        override fun visitFunctionDeclaration(
            function: KSFunctionDeclaration,
            data: CommandData
        ): Parameters {
            val annotation = function.annotations.first()
            val commandName: String = annotation.getArgument("name")
            val checkOp: Boolean = annotation.getArgument("op")
            val senderParameter = function.parameters.findParametersWithAnnotation<Sender>().firstOrNull()
            val argParameters = function.parameters.findParametersWithAnnotation<Arg>()
            val argsParameter = function.parameters.findParametersWithAnnotation<Args>().firstOrNull()
            val argInjectParameters = function.parameters.findParametersWithAnnotation<ArgInject>()

            val parameters = function.parameters.map { parameter ->
                when (parameter) {
                    senderParameter -> "sender"
                    argsParameter -> "args"
                    else -> parameter.name?.asString()
                }
            }.joinToString(",")

            data.codeBlock.apply {
                beginControlFlow("registerCommand(%S) { sender, args ->", commandName)

                if (senderParameter != null) {
                    addStatement("if (sender !is %L) return@registerCommand", senderParameter.type.toTypeName())
                }

                if (checkOp) {
                    addStatement("if (!sender.isOp) return@registerCommand")
                }

                argParameters.forEachIndexed { index, argParameter ->
                    val type = argParameter.type.toTypeName()
                    val mapperType = data.argMappers.find { it.returnType?.toTypeName() == type.copy(nullable = false) }
                        ?: kotlin.run {
                            logger.error("cannot find arg mapper for type $type")
                            return@forEachIndexed
                        }
                    val mapperMemberName = MemberName(
                        packageName = mapperType.packageName.asString(),
                        simpleName = mapperType.simpleName.asString()
                    )

                    val getter = when {
                        type.isNullable -> ".getOrNull(%L)?."
                        else -> "[%L]."
                    }

                    addStatement("val %L = args$getter%M()", argParameter.name!!.asString(), index, mapperMemberName)
                }

                // call command function
                addStatement("%L(%L)", function.qualifiedName!!.asString(), parameters)
                endControlFlow()
            }

            return argInjectParameters
        }

        override fun defaultHandler(node: KSNode, data: CommandData): Parameters {
            return emptyList()
        }
    }
}

data class CommandData(
    val codeBlock: CodeBlock.Builder,
    val argMappers: Sequence<KSFunctionDeclaration>,
)
