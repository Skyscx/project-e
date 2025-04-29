package me.skyscx.processor.listener

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.google.devtools.ksp.visitor.KSDefaultVisitor
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import me.skyscx.annotation.Listener
import me.skyscx.processor.utils.findAnnotation
import me.skyscx.processor.utils.getArgument
import me.skyscx.processor.utils.inject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @created 27.04.2025
 * @author Skyscx
 **/

class ListenerProcessor(
	private val options: Map<String, String>,
	private val codeGenerator: CodeGenerator,
	private val logger: KSPLogger,
) : SymbolProcessor {

	private val configuredName = "Listeners"
	private val packageName = "me.skyscx.bank.listener"

	private val injectParameters = arrayListOf<KSValueParameter>()

	private val listenersData = ListenerData(CodeBlock.builder())

	private val listenerVisitor = ListenerVisitor()

	override fun process(resolver: Resolver): List<KSAnnotated> {
		val listenerSymbols = resolver.getSymbolsWithAnnotation(checkNotNull(Listener::class.qualifiedName))
		logger.info("Найдено $listenerSymbols символов с аннотацией @Listener")

		val validListenerSymbols =
			listenerSymbols.filterIsInstance<KSFunctionDeclaration>().filter(KSAnnotated::validate)

		validListenerSymbols.forEach { symbol ->
			symbol.accept(listenerVisitor, listenersData).forEach { injectParameter ->
				injectParameters.add(injectParameter)
			}
		}

		return listenerSymbols.filterNot { it in validListenerSymbols }.toList()
	}

	@OptIn(KotlinPoetKspPreview::class)
	override fun finish() {
		logger.info("Начинаю генерацию кода для регистрации слушателей")

		val classTypeBuilder = TypeSpec.classBuilder(configuredName).addAnnotation(Singleton::class)

		val primaryConstructorBuilder = FunSpec.constructorBuilder().addAnnotation(Inject::class)

		injectParameters.forEach { parameter ->
			parameter.inject(primaryConstructorBuilder, classTypeBuilder)
		}

		FileSpec.builder(packageName, configuredName).addType(classTypeBuilder.build()).build()
			.writeTo(codeGenerator = codeGenerator, aggregating = false)
		logger.info("Код успешно сгенерирован")

	}

	private inner class ListenerVisitor : KSDefaultVisitor<ListenerData, List<KSValueParameter>>() {
		@OptIn(KotlinPoetKspPreview::class)
		override fun visitFunctionDeclaration(
			function: KSFunctionDeclaration, data: ListenerData
		): List<KSValueParameter> {
			logger.info("Обрабатываю функцию: ${function.simpleName}")
			val injectParameters = function.parameters
			val packageName = function.packageName.asString()
			val name = function.simpleName.asString()
			val receiverType = function.extensionReceiver?.toTypeName()
			val annotation = function.annotations.findAnnotation<Listener>()
			val needFilter = annotation?.getArgument<Boolean>("needFilter")
			val priority = annotation?.getArgument<KSType>("priority")

			val parameters = injectParameters.map { parameter ->
				parameter.name?.asString()
			}.joinToString(",")

			val functionMemberName = MemberName(packageName, name)

			data.codeBlock.apply {
				// Исправлено: передаем только два аргумента в beginControlFlow
				beginControlFlow("subscribe<%T>(%L)", receiverType, needFilter)
				addStatement("%M(%L)", functionMemberName, parameters)
				endControlFlow()
			}

			return injectParameters
		}

		override fun defaultHandler(node: KSNode, data: ListenerData): List<KSValueParameter> {
			return emptyList()
		}
	}

}

data class ListenerData(
	val codeBlock: CodeBlock.Builder,
)