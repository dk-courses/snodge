package com.natpryce.snodge.json

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.natpryce.snodge.Mutagen
import com.natpryce.snodge.text.encodedAs
import com.natpryce.snodge.mapped
import java.nio.charset.Charset
import java.util.Random


abstract class JsonMutagen : Mutagen<JsonElement> {
    final override fun invoke(random: Random, original: JsonElement): Sequence<Lazy<JsonElement>> =
        original.walk()
            .flatMap { path -> this.mutationsOfElement(random, original, path, path(original)) }
    
    abstract fun mutationsOfElement(
        random: Random,
        document: JsonElement,
        pathToElement: JsonPath,
        elementToMutate: JsonElement
    ): Sequence<Lazy<JsonElement>>
}


fun Mutagen<JsonElement>.forStrings(): Mutagen<String> {
    val gson = Gson()
    return this@forStrings.mapped({ gson.fromJson(it, JsonElement::class.java) }, { it.toString() })
}

fun Mutagen<JsonElement>.forEncodedStrings(encoding: Charset) =
    this.forStrings().encodedAs(encoding)


/**
 * Constrain a JsonNodeMutagen to apply only to the element at the given path
 */
fun JsonMutagen.atPath(path: JsonPath) = this.atPath { it == path }

/**
 * Constrain a JsonNodeMutagen to apply only to the element at the given path
 */
fun JsonMutagen.atPath(vararg steps: Any) = atPath(JsonPath(*steps))

/**
 * Constrain a JsonNodeMutagen to apply only to elements at paths that match the given predicate
 */
fun Mutagen<JsonElement>.atPath(pathSelector: (JsonPath) -> Boolean) =
    object : JsonMutagen() {
        override fun mutationsOfElement(random: Random, document: JsonElement, pathToElement: JsonPath, elementToMutate: JsonElement): Sequence<Lazy<JsonElement>> =
            if (pathSelector(pathToElement)) {
                this@atPath(random, elementToMutate).map {
                    lazy { pathToElement.replace(document, it.value) }
                }
            }
            else {
                emptySequence()
            }
    }
