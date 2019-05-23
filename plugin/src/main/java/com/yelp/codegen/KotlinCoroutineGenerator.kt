package com.yelp.codegen

open class KotlinCoroutineGenerator : KotlinGenerator() {

    init {
        templateDir = "kotlin-coroutines"
    }

    override fun getName() = "kotlin-coroutines"

    override fun wrapResponseType(imports: MutableSet<String>, responsePrimitiveType: String) = responsePrimitiveType

    override fun getNoResponseType(imports: MutableSet<String>) = "Unit"
}
