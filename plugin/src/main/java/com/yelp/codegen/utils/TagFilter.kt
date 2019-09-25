package com.yelp.codegen.utils

import io.swagger.models.Operation
import io.swagger.models.Swagger
import io.swagger.models.Tag
import io.swagger.models.parameters.RefParameter
import io.swagger.models.properties.ArrayProperty
import io.swagger.models.properties.RefProperty

/**
 * Removes definitions and paths from Swagger instance which tagged with any tag of the passed tag list.
 * This enables selective API and model generation by tags
 */
class TagFilter {

    private val filteredModels: ArrayList<String> = ArrayList()
    private lateinit var tags: List<String>

    fun filter(tags: List<String>, swagger: Swagger) {
        this.tags = tags

        swagger.tags = if (tags.isEmpty()) swagger.tags else filterTags(swagger)

        if (tags.isNotEmpty()) {
            filterPaths(swagger)
            filterDefinitions(swagger)
        }
    }

    private fun filterOperation(operation: Operation?, tags: List<Tag>) = operation?.let {
        if (operation.tags.any { name -> tags.map { it.name }.contains(name) }) operation else null
    }

    private fun filterPaths(swagger: Swagger) {
        swagger.paths.forEach { entry ->
            val path = entry.value
            val tags = swagger.tags

            path.operations.forEach { operation -> operation.tags = operation.tags.filter { this.tags.contains(it) } }

            path.delete = filterOperation(path.delete, tags)
            path.get = filterOperation(path.get, tags)
            path.post = filterOperation(path.post, tags)
            path.put = filterOperation(path.put, tags)
            path.patch = filterOperation(path.patch, tags)
            path.head = filterOperation(path.head, tags)
        }
    }

    private fun filterDefinitions(swagger: Swagger) {
        swagger.paths.forEach { entry ->
            val path = entry.value

            path.operations.forEach { operation ->

                operation.parameters.forEach {
                    if (it is RefParameter) {
                        filteredModels.add(it.simpleRef)
                    }
                }

                operation.responses.forEach {
                    val schema = it.value.schema
                    if (schema is RefProperty) {
                        filteredModels.add(schema.simpleRef)
                    }
                }
            }
        }

        val list = filteredModels.toList()
        list.forEach { findModels(it, swagger) }

        swagger.definitions = swagger.definitions.filter { filteredModels.contains(it.key) }
    }

    private fun findModels(ref: String, swagger: Swagger) {
        swagger.definitions.filter { ref == it.key }.forEach {
            it.value.properties.forEach {
                val property = it.value
                if (property is ArrayProperty) {

                    val items = property.items
                    if (items is RefProperty && !filteredModels.contains(items.simpleRef)) {
                        filteredModels.add(items.simpleRef)
                        findModels(items.simpleRef, swagger)
                    }
                } else if (property is RefProperty && !filteredModels.contains(property.simpleRef)) {
                    filteredModels.add(property.simpleRef)
                    findModels(property.simpleRef, swagger)
                }
            }
        }
    }

    private fun filterTags(swagger: Swagger) = swagger.tags.filter { tag -> tags.any { tag.name == it } }
}
