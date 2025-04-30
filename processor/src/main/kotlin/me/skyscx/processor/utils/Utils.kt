package me.skyscx.processor.utils

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import me.skyscx.annotation.commands.ArgInject
import org.apache.commons.lang3.text.WordUtils
import javax.inject.Inject

/**
 * @created 27.04.2025
 * @author Skyscx
 **/

typealias Parameters = Collection<KSValueParameter>

fun <T : Any> KSAnnotation.getArgument(name: String): T {
	return arguments.first { it.name?.asString() == name }.value as T
}

inline fun <reified T> List<KSValueParameter>.findParametersWithAnnotation(): List<KSValueParameter> {
	return filter { it.annotations.findAnnotation<T>() != null }
}

@OptIn(KotlinPoetKspPreview::class)
inline fun <reified T> Sequence<KSAnnotation>.findAnnotation(): KSAnnotation? {
	return find { it.annotationType.toTypeName() == T::class.asTypeName() }
}

@OptIn(KotlinPoetKspPreview::class)
fun KSValueParameter.inject(
	primaryConstructor: FunSpec.Builder,
	classType: TypeSpec.Builder
) {
	val name = name!!.asString()
	val type = type.toTypeName()

	injectParameter(name, type, primaryConstructor, classType)
}

fun injectParameter(
	name: String,
	typeName: TypeName,
	primaryConstructor: FunSpec.Builder,
	classType: TypeSpec.Builder
) {
	if (primaryConstructor.parameters.any { it.name == name }) return

	primaryConstructor.addParameter(name, typeName)

	classType.addProperty(
		PropertySpec.builder(name, typeName)
			.initializer(name)
			.addModifiers(KModifier.PRIVATE)
			.build()
	)
}

fun TypeSpec.Builder.injectProperty(packageName: String, className: String, propertyName: String) {
	val typeName = ClassName(packageName, className)
	injectProperty(typeName, propertyName)
}

@OptIn(KotlinPoetKspPreview::class)
fun TypeSpec.Builder.injectProperty(parameter: KSValueParameter): String {
	val typeClassName = parameter.type.toTypeName()
	val generic = parameter.type.resolve().arguments.firstOrNull()
	val genericName = generic?.type?.resolve()?.declaration?.simpleName?.asString()
	var propertyName = WordUtils.uncapitalize(parameter.name?.asString())
	val annotations = parameter.annotations.filter { it.annotationType.toTypeName() != ArgInject::class.asTypeName() }

	if (genericName != null) {
		propertyName += genericName
	}

	injectProperty(typeClassName, propertyName, annotations)

	return propertyName
}

@OptIn(KotlinPoetKspPreview::class)
fun TypeSpec.Builder.injectProperty(
	typeName: TypeName,
	propertyName: String,
	annotations: Sequence<KSAnnotation> = emptySequence()
) {
	if (propertySpecs.any { it.name == propertyName }) return

	addProperty(
		PropertySpec.builder(propertyName, typeName)
			.addModifiers(KModifier.LATEINIT)
			.mutable(true)
			.addAnnotation(Inject::class)
			.apply {
				annotations.forEach { annotation ->
					val className = annotation.annotationType.resolve().toClassName()
					addAnnotation(
						AnnotationSpec.builder(className)
							.build()
					)
				}
			}
			.build()
	)
}