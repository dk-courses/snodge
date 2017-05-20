package com.natpryce.snodge.json

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.snodge.json.JsonPath.Companion
import com.natpryce.snodge.json.JsonPath.functions
import org.junit.Assert.assertTrue
import org.junit.Test

class MutagenAtPathTest {
    @Test
    fun canLimitMutagenToPath() {
        val mutator = JsonMutagen(replaceJsonElement(JsonPrimitive("XXX")).atPath(Companion.of("a", "b")))
    
        val doc = obj(
            "a" to obj(
                "b" to 1,
                "c" to 2),
            "d" to obj(
                "b" to 1,
                "c" to 2))
        
        val mutant = mutator(doc).first().value
        
        assertThat(mutant, equalTo<JsonElement>(obj(
            "a" to obj(
                "b" to "XXX",
                "c" to 2),
            "d" to obj(
                "b" to 1,
                "c" to 2))))
    }
    
    @Test
    @Throws(Exception::class)
    fun canLimitMutagenToPathsByPredicate() {
        val mutator = JsonMutagen(replaceJsonElement(JsonPrimitive("XXX")).atPath(functions.endsWith("b")))
        
        val doc = obj(
            "a" to obj(
                "b" to 1,
                "c" to 2),
            "d" to obj(
                "b" to 1,
                "c" to 2))
        
        val mutations = mutator.invoke(doc).take(2).map { it.value }
        
        assertTrue("a.b mutated", mutations.contains(
            obj(
                "a" to obj(
                    "b" to "XXX",
                    "c" to 2),
                "d" to obj(
                    "b" to 1,
                    "c" to 2))))
        
        assertTrue("d.b mutated", mutations.contains(
            obj(
                "a" to obj(
                    "b" to 1,
                    "c" to 2),
                "d" to obj(
                    "b" to "XXX",
                    "c" to 2))))
    }
}
